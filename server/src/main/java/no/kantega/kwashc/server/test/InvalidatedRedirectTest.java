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
 * Tests if the redirect mechanism in the webapp opens up for redirection to other domains.
 *
 * Solution: Make redirect functionality evaluate redirect urls
 *
 * @author Frode Standal, (Kantega AS)
 */
public class InvalidatedRedirectTest extends AbstractTest {

    @Override
    public String getName() {
        return "Invalidated Redirect Test";
    }

    @Override
    public String getDescription() {
        return "Test if webapp is vulnerable to phishing attacks.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2013-A10-Unvalidated_Redirects_and_Forwards";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseBody = "";

        try {
            HttpGet request = new HttpGet(site.getAddress() + "/redirect?url=http://www.kantega.no");
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);

	        // OBS: In case we are sent to the front page, we must check for something more specific than the wprd Kantega
            if (responseBody.contains("Nesten litt magisk - Kantega")) {
                testResult.setPassed(false);
                testResult.setMessage("Your application is vulnerable to phishing attacks due to invalidated redirects");
            } else {
                testResult.setPassed(true);
                testResult.setMessage("Ok, your application validates redirects properly.");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return testResult;
    }
}
