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

import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

import javax.net.ssl.SSLHandshakeException;
import java.security.KeyManagementException;

/**
 * Test if the web application servlet container and underlying SSL/TLS Protocol implementation support TLS 1.2, and
 * does not allow usage of SSL 3.0 protocol.
 *
 * Solution, part 1: Either use Oracle JDK 8u31, JDK 7u75 or JDK 6u91 which has SSLv3.0 disabled by default, or add the
 * following to jetty-maven-plugin config:
 *
 * <excludeProtocols>
 *     <excludeProtocol>SSLv3</excludeProtocol>
 * </excludeProtocols>
 *
 * Solution, part 2: Running Jetty 8+ with oracle-jdk 1.7+. Old versions of oracle/sun jdk do not support TLSv 1.2.
 *
 *
 * References:
 * http://en.wikipedia.org/wiki/Transport_Layer_Security#Browser_implementations
 * http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class SSLProtocolTest extends AbstractSSLTest {

    @Override
    public String getName() {
        return "Secure SSL/TLS Protocol";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION_SECURE_COMMUNICATION + "<br><br>To ensure that the communication is secure it is " +
                "important to only support strong versions of the protocols.";
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
        return "SSLv3 has been disabled by default in Oracle JDK 7u75 or JDK 8u31 or newer, upgrading might be in order.";
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

            if (checkClient(site, httpsPort, new String[]{"SSLv3"}, null) == 200) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Your application accepts an insecure SSL protocol!");
            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }

        } catch (KeyManagementException e) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
            return testResult;
        } catch (SSLHandshakeException e) {
            if (e.getMessage().contains("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)") || e.getMessage().contains("Received fatal alert: handshake_failure")) {

                if (checkClient(site, httpsPort, new String[]{"TLSv1.2"}, null) == 200) {
                    testResult.setResultEnum(ResultEnum.passed);
                    testResult.setMessage("That`s better, you application supports secure SSL/TLS protocol TLSv1.2!");
                } else {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("Your application does not support secure SSL/TLS Protocols!");
                }
                return testResult;

            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }
        } finally {
            setDuration(testResult, startTime);
        }

        return testResult;
    }

}
