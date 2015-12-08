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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Tests if the redirect mechanism in the webapp opens up for redirection to other domains.
 *
 * Solution: Make redirect functionality evaluate redirect urls
 *
 * @author Frode Standal, (Kantega AS)
 */
public class UnvalidatedRedirectTest extends AbstractTest {

    @Override
    public String getName() {
        return "Unvalidated redirects";
    }

    @Override
    public String getDescription() {
        return "Unvalidated redirects and forwards is a type of vulnerability which exploits the victim's trust in " +
                "your domain name or website. It's seldom a direct attack on your site, but on your users.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2013-A10-Unvalidated_Redirects_and_Forwards";
	}

    @Override
    public String getExploit(Site site) {
        return "Visit <a href='" + getBaseUrl(site) + "redirect?somePadding=thiIsSomePaddingWhichDoesNotDuAnything&url" +
                "=https://secure.eicar.org/eicar.com.txt&morePadding=neitherDoesThisItOnlyMakesItDifficultToReadAndPossiblyTruncated" +
                "&morePadding=morePaddingmorePaddingmorePaddingmorePaddingmorePaddingmorePaddingmorePaddingmorePaddingmorePadding'>" +
                "your perfectly safe blog</a>. This link could be sent to the victim using social media or an email.";
    }

    @Override
    public String getHint() {
        return "Create a whitelist of domains you trust in RedirectServlet. For this test, hardcoding " +
                "localhost and motherfuckingwebsite.com will be ok. java.net.URL will give you some useful tools for parsing the URL.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseBody = "";

        try {
            HttpGet request = new HttpGet(site.getAddress() + "/redirect?url=http://www.kantega.no");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);

	        // OBS: In case we are sent to the front page, we must check for something more specific than the word Kantega
            if (responseBody.contains("I Kantega brenner vi for godt design og elegant teknologi")) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("The blog can be used in phishing attacks, since it has a redirect service " +
                        "which doesn't discriminate what URLs it redirects to. An attacker might trick a victim into " +
                        "thinking he's visiting your trusted blog, while in reality being forwarded to something " +
                        "malicious.");
            } else {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("Ok, your application validates redirects properly.");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.securityFeature;
    }
}
