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

import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * This test triggers an Utf8Appendable$NotUtf8Exception, which is actually an error in the current version of Jetty.
 * This shows that even if you application handles all exceptions properly, the servlet container might not.
 * <p/>
 * Solution:
 *
 * Add the following to web.xml
 *
 * <error-page>
 *  <error-code>404</error-code>
 *  <location>/WEB_INF/jsp/error.jsp</location>
 * </error-page>
 * <error-page>
 *  <error-code>500</error-code>
 *  <location>/WEB_INF/jsp/error.jsp</location>
 * </error-page>
 * <error-page>
 *  <exception-type>java.lang.Throwable</exception-type>
 *  <location>/WEB_INF/jsp/error.jsp</location>
 * </error-page>
 *
 * Create an error.jsp that does NOT leak ANY information about the application or the server.
 * Alternate solution is to upgrade Jetty to 8.0.3+.
 *
 * Alternate url for provoking an error might be site.getAddress()/edit
 *
 * References:
 * http://www.jtmelton.com/2012/01/10/year-of-security-for-java-week-2-error-handling-in-web-xml/
 *
 * @author Espen A. Fossen, (Kantega AS)
 */
public class ImproperErrorHandlingTest extends AbstractTest {

    @Override
    public String getName() {
        return "Improper Error Handling Test";
    }

    @Override
    public String getDescription() {
        return "Test if webapp handles errors properly when application is forced to throw a stack trace.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2013-A5-Security_Misconfiguration";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseBody = "";
        String responseBody2 = "";

        try {
            HttpGet request = new HttpGet(site.getAddress() + "j_security_check?username=username&password=%E6%E6%27");
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);

            if (responseBody.contains("Exception") || responseBody.contains("exception")
                    || responseBody.contains("Caused by") || responseBody.contains("caused by")) {
                testResult.setPassed(false);
                testResult.setMessage("Your application has improper error handling!");
            } else if(statusCode == 500 || statusCode == 200){

                HttpGet request2 = new HttpGet(site.getAddress() + "...");
                HttpResponse response2 = httpclient.execute(request2);
                int statusCode2 = response2.getStatusLine().getStatusCode();
                HttpEntity entity2 = response2.getEntity();
                responseBody2 = EntityUtils.toString(entity2);
                if(responseBody2.toLowerCase().contains("jetty")){
                    testResult.setPassed(false);
                    testResult.setMessage("Your application has improved error handling, but still leaks information about the web server used!");
                } else if(statusCode2 == 404 || statusCode2 == 200){
                    testResult.setPassed(true);
                    testResult.setMessage("Ok, your application handles errors codes and tries not to leak information!");
                }
            } else {
                testResult.setPassed(false);
                testResult.setMessage("The test didn't work properly, are you providing a proper and secure error handling?");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        setDuration(testResult, startTime);
        return testResult;
    }
}

