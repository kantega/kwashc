/*
 * Copyright 2012 Kantega AS
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

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;
import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * In this test we switch session cookie right before we submit the form, and asserts that the post is not accepted (that it is not visible
 * on the front page).
 *
 * Solution: Couple the hidden field in the form to the session. The simplest solution is to use the session identifier in the hidden field,
 * and check on that in BlogServlet.
 *
 * OBS: Newlines in the hidden field will break other tests.
 *
 * @author Anders Båtstrand, (www.kantega.no)
 */
public class CSRFTest extends AbstractTest {

    private static final String EXPLOIT =
            "<form id=\"commentForm\" action=\"http://localhost:8080/blog\" method=\"POST\" style=\"display:none\">\n" +
            "            <input class=\"title\" type=\"text\" size=\"40\" id=\"title\" name=\"title\" value=\"This is CSRF!\"/>\n" +
            "            <input class=\"homepage\" type=\"text\" size=\"40\" id=\"homepage\" name=\"homepage\"/>\n" +
            "            <input class=\"text\" id=\"comment\" " +
            "name=\"comment\" value=\"This is a CSRF comment posted from the test server.\"/>\n" +
            "</form>" +
            "<input type='button' onclick='document.forms[\"commentForm\"].submit()' value='Click here'/>";

    @Override
    public String getName() {
        return "Cross Site Request Forgery";
    }

    @Override
    public String getDescription() {
        return "Cross Site Request Forgery (CSRF) happens when http requests to your service are predictable. An " +
                "attacker can generate a request to preform some action, which a victim can be fooled to execute" +
                " by clicking a link or visiting a malicious third party site. The victim's web browser will execute " +
                "the request, happily including all session cookies from the existing session." +
                "<br><br>The key to solving CSRF is adding unpredictability to the requests.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29";
	}

    @Override
    public String getExploit(Site site) {
        return "This test server is in effect a third party site. It should not be able to make your browser post data " +
                "on your behalf to your blog. This could be hidden and automated, but for testing purposes you can " +
                "click this button:<br> "+ EXPLOIT;
    }

    @Override
    public String getHint() {
        return "Create a session scoped cryptographically secure random CSRF token using SecureRandom, and add it to " +
                "the session when the page is loaded. Include it in the form as a hidden variable, and check if the " +
                "two matches when a comment is posted.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        if(allowsPostingWithoutTokens(site)) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("The blog has a CSRF vulnerability allowing an attacker to fabricate arbitrary " +
                    "comments which victims can be tricked into posting just by visiting a malicious third party site!");
        } else if (allowsPostingWithTokenFromOtherSession(site)) {
            testResult.setResultEnum(ResultEnum.partial);
            testResult.setMessage("It's possible to steal a form token created in another session, and use that in the " +
                    "CSRF attack against other users. Tokens must be unique for the session!");
        } else {
            testResult.setResultEnum(ResultEnum.passed);
            testResult.setMessage("No CSRF vulnerabilities found.");
        }

        setDuration(testResult, startTime);
        return testResult;
    }
    private boolean allowsPostingWithoutTokens(Site site) throws IOException {

        String randomPost = UUID.randomUUID().toString();

        HttpClient httpclient = HttpClientUtil.getHttpClient();
        HttpPost httppost = new HttpPost(site.getAddress() + "blog");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("title", "CSRF post!"));
        formparams.add(new BasicNameValuePair("comment", randomPost));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httppost.setEntity(entity);

        httpclient.execute(httppost);

        String frontPage = HttpClientUtil.getPageText(site.getAddress() + "blog");

        return frontPage.contains(randomPost);
    }

    private boolean allowsPostingWithTokenFromOtherSession(Site site) throws IOException {
        String sessionRandomPost = UUID.randomUUID().toString();

        // create two sessions:
        WebTester tester1 = new WebTester();
        tester1.beginAt(site.getAddress());

        WebTester tester2 = new WebTester();
        tester2.beginAt(site.getAddress());

        // Replace sessions by replacing cookies:
        WebClient client1 = ((HtmlUnitTestingEngineImpl)tester1.getTestingEngine()).getWebClient();
        WebClient client2 = ((HtmlUnitTestingEngineImpl)tester2.getTestingEngine()).getWebClient();

        CookieManager cookieManager2 = client2.getCookieManager();
        Cookie jsessionIdCookie = cookieManager2.getCookie("JSESSIONID");

        if(jsessionIdCookie != null){
            CookieManager newCookieManager = new CookieManager();
            newCookieManager.addCookie(jsessionIdCookie);
            client1.setCookieManager(newCookieManager);
        }

        // fill in form
        tester1.setTextField("title", "CSRF post");
        tester1.setTextField("comment", "Using another form token: " + sessionRandomPost);

        try {
            tester1.clickButton("commentFormSubmit");
        } catch (Exception e) {
            // ignore
        }
        String frontPage = HttpClientUtil.getPageText(site.getAddress() + "blog");

        return frontPage.contains(sessionRandomPost);
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.csrf;
    }
}
