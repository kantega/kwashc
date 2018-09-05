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

import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import sun.security.ssl.SSLSocketFactoryImpl;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


/**
 * Test if SSL/TLS based communication allows insecure/weak ciphers and supports the best available Perfect Forward
 * Secrecy ciphers.
 * <p>
 * Oracle has done a good job in disabling bad/weak ciphers in recent versions. Unfortunately that makes testing for
 * them a bit harder. To do this we need to override some settings in JDK_HOME/lib/security/java.security. To make this
 * work properly we need to start the server using jetty:run-forked. Alternatively this could have been done using
 * MAVEN_OPTS="-Djava.security.properties=jvm/java.security" mvn jetty:run.
 * <p>
 * The actual test goes as follows:
 * <p>
 * 1. Checks for availability of insecure, anonymous, weak ciphers
 * 2. Checks if Perfect Forward Secrecy ciphers are available.
 * <p>
 * Solution, part 1:
 * <p>
 * Exclude the following ciphers
 * <p>
 * <excludeCipherSuites>
 * <excludeCipherSuite>SSL_RSA_WITH_3DES_EDE_CBC_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_DHE_RSA_WITH_DES_CBC_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_DHE_DSS_WITH_DES_CBC_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_RSA_WITH_NULL_MD5</excludeCipherSuite>
 * <excludeCipherSuite>SSL_RSA_WITH_NULL_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_RSA_WITH_RC4_128_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_RSA_WITH_RC4_128_MD5</excludeCipherSuite>
 * <excludeCipherSuite>SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA</excludeCipherSuite>
 * <excludeCipherSuite>SSL_DH_anon_EXPORT_WITH_RC4_40_MD5</excludeCipherSuite>
 * <excludeCipherSuite>TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5</excludeCipherSuite>
 * <excludeCipherSuite>TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA</excludeCipherSuite>
 * <excludeCipherSuite>TLS_DH_anon_WITH_AES_128_GCM_SHA256</excludeCipherSuite>
 * <excludeCipherSuite>TLS_ECDHE_ECDSA_WITH_RC4_128_SHA</excludeCipherSuite>
 * <excludeCipherSuite>TLS_ECDHE_RSA_WITH_RC4_128_SHA</excludeCipherSuite>
 * <excludeCipherSuite>TLS_ECDH_ECDSA_WITH_RC4_128_SHA</excludeCipherSuite>
 * <excludeCipherSuite>TLS_ECDH_RSA_WITH_RC4_128_SHA</excludeCipherSuite>
 * </excludeCipherSuites>
 * <p>
 * Solution, part 2:
 * <p>
 * Make sure only Perfect Forward Secrecy ciphers with DH key length of > 1024 are available.
 * <p>
 * Referanses:
 * <p/>
 * http://wiki.eclipse.org/Jetty/Howto/CipherSuites
 * http://www.techstacks.com/howto/j2se5_ssl_cipher_strength.html
 * https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientCustomSSL.java
 * http://cephas.net/blog/2007/10/02/using-a-custom-socket-factory-with-httpclient/
 * https://blogs.oracle.com/java-platform-group/entry/java_8_will_use_tls
 * https://www.ssllabs.com/ssltest/viewClient.html?name=Java&version=8u31
 * server/cipher/*.txt: Full list of ciphers supported by different Java versions.
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class CipherSuiteTest extends AbstractSSLTest {

    public CipherSuiteTest() {

        try {
            verifyCipherSuites(ciphers);
            verifyCipherSuites(ciphers2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] ciphers = new String[]{
            "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
            "SSL_DHE_RSA_WITH_DES_CBC_SHA",
            "SSL_DHE_DSS_WITH_DES_CBC_SHA",
            "SSL_RSA_WITH_NULL_MD5",
            "SSL_RSA_WITH_NULL_SHA",
            "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA",
            "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",
            "SSL_RSA_WITH_RC4_128_SHA",
            "SSL_RSA_WITH_RC4_128_MD5",
            "TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5",
            "TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA",
            "TLS_DH_anon_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",
            "TLS_ECDHE_RSA_WITH_RC4_128_SHA",
            "TLS_ECDH_ECDSA_WITH_RC4_128_SHA",
            "TLS_ECDH_RSA_WITH_RC4_128_SHA"
    };

    private String[] ciphers2 = new String[]{
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA"
    };

    private String[] ciphers3 = new String[]{
            "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
    };

    @Override
    public String getName() {
        return "SSL/TLS Connection Cipher Strength";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION_SECURE_COMMUNICATION + "<br><br> A cipher suite is a named combination of authentication, encryption" +
                ", message authentication code (MAC) and key exchange algorithms used to negotiate the security settings" +
                " in and SSL/TLS based communication. To ensure that the communication is secure it is important to only " +
                "support strong and secure cipher suites. ";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Transport_Layer_Protection_Cheat_Sheet";
    }

    @Override
    public String getExploit(Site site) {
        return null;
    }

    @Override
    public String getHint() {
        return "Create a blacklist of disabled SSL/TLS cipher suites using <excludeCipherSuites> in <configuration> of " +
                "the jetty-maven-plugin in pom.xml. A blacklist might be good, but you should also consider a whitelist.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        int httpsPort;
        try {
            httpsPort = new Integer(site.getSecureport());
        } catch (NumberFormatException e) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("No HTTPS port specified, test not run!");
            setDuration(testResult, startTime);
            return testResult;
        }

        try {

            if (checkClient(site, httpsPort, new String[]{"TLSv1.1", "TLSv1.2"}, ciphers) == 200) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Your application accepts weak/anonymous SSL/TLS cipher suites!");
            }

        } catch (NoSuchAlgorithmException e) {
            return exitMissingCipherSuites(testResult);
        } catch (KeyManagementException e) {
            return exitIncorrectCertificate(testResult);
        } catch (SSLHandshakeException e) {
            if (e.getMessage().contains("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)")
                    || ((e.getMessage().contains("Received fatal alert: handshake_failure")))) {

                try {

                    if (checkClient(site, httpsPort, new String[]{"TLSv1.1", "TLSv1.2"}, ciphers2) == 200) {

                        try {
                            if (checkClient(site, httpsPort, new String[]{"TLSv1.1", "TLSv1.2"}, ciphers3) == 200) {
                                testResult.setResultEnum(ResultEnum.passed);
                                testResult.setMessage("Awesome!! No weak/anonymous ciphers, Perfect Forward Secrecy and highly secure and high performance ciphers!");
                                return testResult;
                            }
                        } catch (NoSuchAlgorithmException e1) {
                            return exitMissingCipherSuites(testResult);
                        } catch (KeyManagementException e1) {
                            return exitIncorrectCertificate(testResult);
                        } catch (IOException e1) {
                            testResult.setResultEnum(ResultEnum.partial);
                            testResult.setMessage("Almost there, no weak/anonymous ciphers and supporting Perfect Forward Secrecy ciphers are present, but you are still not supporting ciphers with TLS_*_SHA384.");
                            return testResult;
                        }
                    } else {
                        exitWrongHttpCode(testResult);
                    }
                    return testResult;
                } catch (NoSuchAlgorithmException e1) {
                    return exitMissingCipherSuites(testResult);
                } catch (KeyManagementException e1) {
                    return exitIncorrectCertificate(testResult);
                } catch (IOException e1) {
                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("Almost there, no weak/anonymous ciphers and allows Perfect Forward Secrecy, but some of your ciphers require DSA keys, which are effectively limited to 1024 bits!");
                    return testResult;
                }

            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }
        } finally {
            setDuration(testResult, startTime);
        }

        return testResult;
    }

    private void exitWrongHttpCode(TestResult testResult) {
        testResult.setResultEnum(ResultEnum.failed);
        testResult.setMessage("The server did not respond with an HTTP 200 when it should, contact tutor.");
    }

    private TestResult exitIncorrectCertificate(TestResult testResult) {
        testResult.setResultEnum(ResultEnum.failed);
        testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
        return testResult;
    }

    private TestResult exitMissingCipherSuites(TestResult testResult) {
        testResult.setResultEnum(ResultEnum.failed);
        testResult.setMessage("Cipher suites used for connection not available in local environment, contact tutor.");
        return testResult;
    }

    private void verifyCipherSuites(String[] ciphers) throws Exception {
        javax.net.ssl.SSLSocketFactory sslSocket = new SSLSocketFactoryImpl();
        String[] supportedCiphers = sslSocket.getSupportedCipherSuites();
        List<String> supportedCiphersList = Arrays.asList(supportedCiphers);
        for (String s : ciphers) {
            if (!supportedCiphersList.contains(s)) {
                throw new RuntimeException(String.format("Cipher %s is not supported in the running Java environment. " +
                        "Please fix by enabling obsolete/disabled ciphers via -Djava.security.properties=jvm/java.security or check if the cipher is actually valid.", s));
            }
        }
    }

}
