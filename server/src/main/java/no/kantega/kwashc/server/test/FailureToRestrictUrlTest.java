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
 * Tests triggers a NumberFormatException in an unrestricted part of the admin servlet
 *
 * The actual restrict url error is because of missing protection of the /blog/admin url in web.xml.
 *
 * Solution: Restrict url behind LoginServlet.
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class FailureToRestrictUrlTest extends AbstractTest {

	@Override
	public String getName() {
		return "Failure to Restrict URL Test";
	}

	@Override
	public String getDescription() {
		return "Test if the blog webapp restricts URL's properly. Be careful not to give away any information to a potential attacker!";
	}

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2010-A8-Failure_to_Restrict_URL_Access";
	}

	@Override
	protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
		long startTime = System.nanoTime();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String responseBody = "";

		try {
			HttpGet request = new HttpGet(site.getAddress() + "/blog/admin?commentToDelete=00121212123123123123123123123123123343435436456745675647456564444444454554");
			HttpResponse response = httpclient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			responseBody = EntityUtils.toString(entity);

			if (statusCode == 500 && responseBody.contains("Exception")) {
				testResult.setPassed(false);
				testResult.setMessage("Your application fails to restrict URL's properly!");
			} else if (statusCode == 500) {
				testResult.setPassed(false);
				testResult.setMessage("Your application fails to restrict URL's properly!");
			} else if (statusCode == 404) {
				testResult.setPassed(false);
				testResult.setMessage("Your application restricts URL's properly, but there is still some improper error handling!");
			} else if (statusCode == 200 && responseBody.contains("You asked for a protected resource")) {
				testResult.setPassed(true);
				testResult.setMessage("Ok, your application restricts URL's properly.");
			} else {
				testResult.setPassed(false);
				testResult.setMessage("Your application fails to restrict URL's properly!");
			}
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		setDuration(testResult, startTime);
		return testResult;
	}

	@Override
	public TestCategory getTestCategory() {
		return TestCategory.misconfiguration;
	}
}
