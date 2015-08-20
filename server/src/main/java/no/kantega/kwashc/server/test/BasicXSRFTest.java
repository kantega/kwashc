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
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test that you do not allow to simply send title and comments as a standalone POST-request, and makes it appear as a post on the blog
 *
 * Solution: Requires an extra parameter in the form.
 *
 * @author Anders Båtstrand, (www.kantega.no)
 */
public class BasicXSRFTest extends AbstractTest {

    @Override
    public String getName() {
        return "Basic Cross Site Request Forgery Test";
    }

    @Override
    public String getDescription() {
        return "Tests that the site does not accept a standalone POST request.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        String random = UUID.randomUUID().toString();

        HttpClient httpclient = HttpClientUtil.getHttpClient();
        HttpPost httppost = new HttpPost(site.getAddress() + "blog");

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("title", "CSRF post!"));
        formparams.add(new BasicNameValuePair("comment", random));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httppost.setEntity(entity);

        httpclient.execute(httppost);

        String frontPage = HttpClientUtil.getPageText(site.getAddress() + "blog");

        if (frontPage.contains(random)) {
            testResult.setPassed(false);
            testResult.setMessage("You allowed posting from outside the site!");
            setDuration(testResult, startTime);
            return testResult;
        }

        testResult.setPassed(true);
        testResult.setMessage("No attacks found.");
        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.csrf;
    }
}
