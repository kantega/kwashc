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

import com.gargoylesoftware.htmlunit.WebClient;
import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;
import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

import java.util.UUID;

/**
 *
 * This test is currently excluded, as it has no general basis. Should you find a link backing this test, please feel free to re-add it
 *
 * Tester om det er mulig å ta med parametre inn under en pålogging.
 * <p/>
 * Løsning: For eksempel:
 * <p/>
 * I SecurityFilter.java, endre
 * <p/>
 * <p/>
 * session.setAttribute(LogInServlet.TARGET_PAGE_SESSION_ATTRIBUTE, request.getRequestURI() + "?" + request.getQueryString());
 * <p/>
 * til
 * session.setAttribute(LogInServlet.TARGET_PAGE_SESSION_ATTRIBUTE, request.getRequestURI());
 */
public class DirectAdminLinkTest extends AbstractTest {

	@Override
	public String getName() {
		return "Direct Admin Link Test";
	}

	@Override
	public String getDescription() {
		return "The checks if an attacker could trick admins to do actions just by following a link.";
	}

	@Override
	public String getInformationURL() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected TestResult testSite(Site site, TestResult testResult) throws Throwable {

		// create two sessions:
		WebTester tester = new WebTester();
		tester.beginAt(site.getAddress());

		WebTester tester2 = new WebTester();
		tester2.beginAt(site.getAddress());

		String title = UUID.randomUUID().toString();

		// first, make a post
		tester.beginAt(site.getAddress());

		tester.setTextField("title", title);
		tester.setTextField("comment", "This comment is about to be deleted by a user unaware of his actions.");

		tester.clickButton("commentFormSubmit");

		tester.gotoPage(site.getAddress());
		tester.assertTextPresent(title);

		// new session
		tester.beginAt(site.getAddress() + "admin");

		tester.setTextField("username", "username");
		tester.setTextField("password", "password");

		tester.clickButton("formSubmitButton");

		// nå er vi på forsiden, og bytter sesjon før vi klikker slett på kommentaren vår
		WebClient client1 = ((HtmlUnitTestingEngineImpl) tester.getTestingEngine()).getWebClient();
		WebClient client2 = ((HtmlUnitTestingEngineImpl) tester2.getTestingEngine()).getWebClient();
		// Replace sessions by replacing cookies:
		client1.setCookieManager(client2.getCookieManager());

		tester.clickLink("delete." + title);

		// we are redirected to log inn page
		tester.setTextField("username", "username");
		tester.setTextField("password", "password");

		tester.clickButton("formSubmitButton");

		// we should be redirected straight to doing delete action
		String frontPage = HttpClientUtil.getPageText(site.getAddress() + "blog");

		if (!frontPage.contains(title)) {

			testResult.setPassed(false);
			testResult.setMessage("You let an innocent (admin) user click a link, get a login page, and, after login, deleting a comment without knowing it.");
			return testResult;

		}

		testResult.setPassed(true);
		testResult.setMessage("No attacks found.");
		return testResult;
	}
}
