/*
 * Copyright 2013 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.kwashc.server.test;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests if passwords in the webapp is stored a cryptographic secure way. To accomplish this a backdoor in the webapp
 * has been created. The test cannot determine if the password is stored securely, but I can inform the about the
 * most known failures of creating secure passwords.
 *
 * The test check the following:
 * Output must be ASCII characters only
 * Output should not contain the original password as a substring
 * Output size must be larger then 56 char (224 bit)
 * Output cannot be just an MD2, MD5, SHA-1, SHA-224 or SHA-256 hash
 *
 * Solution:
 * Store password with PBKDF2WithHmacSHA1 or better, alternately any "function" with output size of 56 char or more (bad solution)
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class InsecureCryptographicStorageTest extends AbstractTest {

    @Override
    public String getName(){
        return "Insecure Cryptographic Storage";
    }

    @Override
    public String getDescription(){
        return "Password storage must be done in a very specific way in order to be secure. There are several ways a " +
                "password database can be lost (SQL injection, misconfiguration, lost backup tapes, disgruntled DB " +
                "admins etc), and our goal is to store the passwords in such a way that it is difficult and time " +
                "consuming to extract the users' passwords even if the DB is stolen." +
                "<br><br>You probably should be querying a dedicated authentication server, rather than storing the " +
                "passwords yourself. The blog does however use its internal DB, so you will need to:<ol><li>Use a " +
                "<i>computationally expensive and cryptographically secure</i> one-way function (\"hash\"). Standard " +
                "hashing functions like SHA are designed to be <i>fast</i>, allowing an attacker to try <i>hundreds " +
                "of billions</i> of potential passwords every second, using cheap over the counter home computers.</li>" +
                "<li>Use an <i>individual</i> salt, unique for each user. This is a random value which is appended to " +
                "the password before hashing. Individual salts forces the attacker to try password candidates for " +
                "each user individually since two identical passwords will create two unique hashes. Without salts " +
                "the attacker can compute one value and search the DB for <i>any</i> user which has used this " +
                "password.</li></ol>." +
                "A 10 minute Norwegian talk about proper password storage can be watched " +
                "<a href='https://vimeo.com/49485270'>here</a> (<a href='https://jonare.github.io/jz12/passwords/'>" +
                "slides</a>).";
    }

    @Override
    public String getExploit(Site site) {
        return "User databases are usually stolen through other security vulnerabilities, such as SQL Injection. The " +
                "blog's DB is just a map hard coded in Database.java, so it isn't directly exploitable from running " +
                "code. ";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Insecure_Storage";
	}

    @Override
    public String getHint() {
        return "A real world application should use an algorithm especially suited for password storage, like PBKDF2 " +
                "or scrypt. For the workshop we should avoid the overhead caused by importing new frameworks or tools: " +
                "<ol><li>Create individual salt: Use SecureRandom to create a random byte array, and " +
                "org.apache.commons.codec.digest.DigestUtils to create an ASCII digest (hash) of this value.</li>" +
                "<li>Use DigestUtils to create a cryptographically secure hash (e.g. SHA512) of this salt and the " +
                "password. Rehash the result a suitable number of times (e.g. 512). This adds computational expense " +
                "for anyone trying to crack the passwords.</li></ol>";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        final String originalUsernamePassword = "password";
        final String originalAnotherUserPassword = "guest";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseBody;

        try {
            WebTester tester = new WebTester();
            tester.setIgnoreFailingStatusCodes(true);
            tester.beginAt(site.getAddress());
            tester.clickLinkWithExactText("Admin");
            tester.setTextField("username", "username");
       	    tester.setTextField(originalUsernamePassword, "password");
            tester.clickButton("formSubmitButton");

            tester.gotoPage(site.getAddress() + "admin?checkIfMyPasswordStoredSecurely");
            responseBody = tester.getPageSource();

            if(responseBody.contains("You asked for a protected resource. Please log in:")){
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("It's not possible to login to your application with the original username/password, no points for you!");
            } else if(responseBody.contains("P1:") && responseBody.contains("P2:")){

                int start = responseBody.indexOf("P1:")+3;
                int stop = responseBody.indexOf(":P1",start);
                int start2 = responseBody.indexOf("P2:")+3;
                int stop2 = responseBody.indexOf(":P2", start2);
                String usernamePassword = responseBody.substring(start, stop).trim();
                String anotherUserPassword = responseBody.substring(start2,stop2).trim();

                if (usernamePassword.equalsIgnoreCase("") || anotherUserPassword.equalsIgnoreCase("")) {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("You have tampered with the super secure cryptographic storage checker, no points for you!");
                } else if (!usernamePassword.matches("\\A\\p{ASCII}*\\z") || !anotherUserPassword.matches("\\A\\p{ASCII}*\\z")) {

                    String add = "";
                    try{
                        URL feedSource = new URL("http://www.schneierfacts.com/rss/random");
                        SyndFeedInput input = new SyndFeedInput();
                        SyndFeed feed = input.build(new XmlReader(feedSource));
                        List<SyndEntry> entries = feed.getEntries();
                        if (entries != null) {
                            add = " "+entries.get(0).getDescription().getValue();
                        }
                    } catch (Exception e){
                        // nothing really matters
                    }

                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("Passwords should only be stored using ASCII characters!"+ add);
                } else if (usernamePassword.contains(originalUsernamePassword) || anotherUserPassword.contains(originalAnotherUserPassword)) {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("Your application stores users' passwords in an insecure manner");
                } else if (isPasswordCreatedWithInsecureHashAlgorithm(usernamePassword, originalUsernamePassword) ||
                    isPasswordCreatedWithInsecureHashAlgorithm(anotherUserPassword, originalAnotherUserPassword)) {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("Your application has insecure cryptographic storage!");
                } else if (usernamePassword.length() < 56 || anotherUserPassword.length() < 56) {
                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("The output size of your cryptographic function seems a bit small, might not withstand a brute-force or rainbow table attack!");
                } else if (!isPasswordCreatedWithInsecureHashAlgorithm(usernamePassword, originalUsernamePassword) ||
                    !isPasswordCreatedWithInsecureHashAlgorithm(anotherUserPassword, originalAnotherUserPassword)) {
                    testResult.setResultEnum(ResultEnum.passed);
                    testResult.setMessage("Ok, your application has some degree of secure cryptographic storage, hopefully...!");
                } else {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("You have tampered with the super secure cryptographic storage checker, no points for you!");
                }
            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("You have tampered with the super secure cryptographic storage checker, no points for you!");

            }

        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        setDuration(testResult, startTime);
        return testResult;

    }

    private boolean isPasswordCreatedWithInsecureHashAlgorithm(String storedPassword, String originalPassword){

	    if (storedPassword.equals(originalPassword)) {
		    // passwords should not saved without some kind of encryption og hashing
		    return true;
	    }

        List<String> hashAlgorithms = Arrays.asList("MD2", "MD5", "SHA-1", "SHA-224", "SHA-256");
        for (String algorithm : hashAlgorithms) {
            String hash = generateHash(algorithm, originalPassword);
            if (hash.equals(storedPassword)) return true;
        }
        return false;

    }

    private String generateHash(String algorithm, String password){

        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.reset();
            messageDigest.update(password.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            return new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            //
            return "";
        }
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.crypto;
    }
}
