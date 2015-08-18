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
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

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
public class SessionXSRFTest extends AbstractTest {

    @Override
    public String getName() {
        return "Session Cross Site Request Forgery Test";
    }

    @Override
    public String getDescription() {
        return "Tests that the site does not accept POST based on a different session or form.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29";
	}

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        String random = UUID.randomUUID().toString();

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

        if(jsessionIdCookie == null){
            testResult.setPassed(false);
            testResult.setMessage("The JSESSIONID cookie must be present when initilizing a user session, zero points!");
            setDuration(testResult, startTime);
            return testResult;

        } else {
            CookieManager newCookieManager = new CookieManager();
            newCookieManager.addCookie(jsessionIdCookie);
            client1.setCookieManager(newCookieManager);

            // fill in form
            tester1.setTextField("title", "CSRF post");
            tester1.setTextField("comment", "Using another form token: " + random);

            try {
                tester1.clickButton("commentFormSubmit");
            } catch (Exception e) {
                // ignore
            }
            String frontPage = HttpClientUtil.getPageText(site.getAddress() + "blog");

            if (frontPage.contains(random)) {
                testResult.setPassed(false);
                testResult.setMessage("You allowed posting using a form generated in another session!");
                setDuration(testResult, startTime);
                return testResult;
            }

            testResult.setPassed(true);
            testResult.setMessage("No attacks found.");
            setDuration(testResult, startTime);
            return testResult;
        }

    }
}
