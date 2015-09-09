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

package no.kantega.kwashc.server;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.WebappJettyStarter;
import no.kantega.kwashc.server.controller.SiteController;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.repository.SiteRepository;
import no.kantega.kwashc.server.test.ClickjackingTest;
import no.kantega.kwashc.server.test.SiteExistsTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Anders Båtstrand, (Kantega AS)
 */
public class SiteRegistrationTest {

	private Site site;
	private int serverPort;

	@Before
	public void startSite() throws Exception {
		site = WebappJettyStarter.start();
		assertTrue(new SiteExistsTest().testSite(site).getResultEnum() == ResultEnum.passed);
		serverPort = ServerJettyStarter.start();
	}

	@Test
	@Ignore("Vi må fikse Exception: Caused by: com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException: 500 /WEB-INF/jsp/blog.jsp(1,63) PWC6188: The absolute uri: http://j\n" +
			"ava.sun.com/jsp/jstl/core cannot be resolved in either web.xml or the jar files deployed with this application")
	public void serverStartsTest() {
		WebTester tester = new WebTester();
		tester.getTestingEngine().setIgnoreFailingStatusCodes(true);
		tester.beginAt("http://localhost:" + serverPort);
		tester.assertResponseCode(200);
		tester.assertTextPresent("Score board");
	}

	@Test
	@Ignore("Vi må fikse Exception: Caused by: com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException: 500 /WEB-INF/jsp/blog.jsp(1,63) PWC6188: The absolute uri: http://j\n" +
			"ava.sun.com/jsp/jstl/core cannot be resolved in either web.xml or the jar files deployed with this application")
	public void registerSiteTest() {
		WebTester tester = new WebTester();
		tester.getTestingEngine().setIgnoreFailingStatusCodes(true);
		tester.beginAt("http://localhost:" + serverPort);
		tester.assertResponseCode(200);
		tester.assertTextPresent("Score board");

		// we need to access the site before Spring creates the controller beans:
		SiteController.setSiteSecret(site.getSecret());

		tester.assertLinkPresent("addSiteLink");
		tester.clickLink("addSiteLink");
		tester.assertTextPresent("meta name=\"no.kantega.kwashc\" content=\"" + site.getSecret());

		// checks test values
		tester.assertTextFieldEquals("address", "http://127.0.0.1:8080/");
		tester.assertTextFieldEquals("secureport", "8443");

		tester.setTextField("name", site.getName());
		tester.setTextField("owner", site.getOwner());
		tester.setTextField("address", site.getAddress());
		tester.setTextField("secureport", site.getSecureport());

		tester.clickButton("saveSiteButton");

		// test if site is saved to the database
		SiteRepository siteRepository = ServerJettyStarter.getSpringBean(SiteRepository.class);
		assertNotNull(siteRepository);

		Site savedSite = siteRepository.findByName(site.getName());
		assertNotNull(savedSite);
		assertEquals(site.getName(), savedSite.getName());
		assertEquals(site.getSecret(), savedSite.getSecret());
		assertEquals(site.getAddress(), savedSite.getAddress());
		assertEquals(site.getOwner(), savedSite.getOwner());
		assertEquals(site.getSecureport(), savedSite.getSecureport());

		// checks if any tests have been run
		assertTrue(savedSite.getTestResults().isEmpty());
		assertEquals(0, savedSite.getScore());

		tester.assertTextPresent("Test results");
		tester.assertTextPresent(site.getName());
		tester.assertTextPresent(site.getOwner());

		// page registered, time to run som tests:
		tester.clickLink("executeAllTestsLink");

		savedSite = siteRepository.findByName(site.getName());
		assertNotNull(savedSite);
		assertTrue("SiteWorks and exists, should get at least two points", savedSite.getScore() >= 2);

		assertEquals(TestRepository.getTests().values().size(), savedSite.getTestResults().size());

		// we run the tests once more
		tester.clickLink("executeAllTestsLink");

		savedSite = siteRepository.findByName(site.getName());
		assertNotNull(savedSite);
		assertTrue("SiteWorks and exists, should get at least two points", savedSite.getScore() >= 2);

		assertEquals(TestRepository.getTests().values().size(), savedSite.getTestResults().size());

		tester.assertTextPresent(new ClickjackingTest().getName());

		// clicks into a single test
		tester.clickLink("execute" + new ClickjackingTest().getIdentifikator() + "Link");

		tester.assertTextPresent(new ClickjackingTest().getInformationURL());
		tester.assertTextPresent(new ClickjackingTest().getDescription());

		// check if site startup page is available
		tester.gotoPage("http://localhost:" + serverPort);
		// header + 1 row
		tester.assertTableRowCountEquals("scoreTable", 2);
		tester.assertTableEquals("scoreTable", new String[][]{
				{"Site:",           "Team:",            "Score:"},
				{site.getName(),    site.getOwner(),    String.valueOf(savedSite.getScore() + "/" + String.valueOf(TestRepository.getTests().size()))}
		});

		// deletes
		tester.clickLink("viewSite" + savedSite.getId());
		tester.clickLink("deleteSite");
		// should arrive av the overview of test
		tester.assertTextPresent("Currently registered sites");

		tester.gotoPage("http://localhost:" + serverPort);
		tester.assertTablePresent("scoreTable");
		// just header
		tester.assertTableRowCountEquals("scoreTable", 1);

		// verify if site was deleted
		savedSite = siteRepository.findByName(site.getName());
		assertNull(savedSite);
	}

}
