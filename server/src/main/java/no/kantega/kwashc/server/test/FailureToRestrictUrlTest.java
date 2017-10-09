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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Tests triggers a NumberFormatException in an unrestricted part of the admin controller
 * <p/>
 * The actual restrict url error is because of missing protection of the /blog/admin url in web.xml.
 * <p/>
 * Solution: Restrict url behind LoginServlet.
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class FailureToRestrictUrlTest extends AbstractTest {

    @Override
    public String getName() {
        return "Failure to Restrict URLs";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION_SECURITY_MISCONFIGURATION + "<br><br>SecurityFilter is supposed to protect the blog's " +
                "admin functionality as configured in web.xml, by being invoked when someone tries to access the " +
                "admin URLs. Filters like this, or frameworks such as Spring Security works beautifully, but requires" +
                " careful configuration to avoid leaving alternative unprotected routes to the functionality.";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Top_10_2010-A8-Failure_to_Restrict_URL_Access";
    }

    @Override
    public String getExploit(Site site) {
        return "Go to <a href='" + getBaseUrl(site) + "blog/admin?commentToDelete=123'>" + getBaseUrl(site) + "blog/admin" +
                "?commentToDelete=123</a> using an unauthenticated session. You should not be able to access this URL" +
                " without logging in.";
    }

    @Override
    public String getHint() {
        return "web.xml configures which URLs are restricted by SecurityFilter, and which URLs AdminServlet is " +
				"accessible from.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        String responseBody = "";

        try {
            HttpGet request = new HttpGet(site.getAddress() +
					"/blog/admin?commentToDelete" +
					"=00121212123123123123123123123123123343435436456745675647456564444444454554");
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
            String generalError = "Your application fails to restrict privileged URLs properly!";

            if (statusCode == 500) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage(generalError);
            } else if (statusCode == 404) {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("Your application restricts URLs properly, but can you be sure no one was using " +
						"/blog/admin?");
            } else if (statusCode == 200 && responseBody.contains("You asked for a protected resource")) {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("Your application restricts URLs properly!");
            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage(generalError);
            }
        } finally {
            httpclient.close();
        }

        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.misconfiguration;
    }
}
