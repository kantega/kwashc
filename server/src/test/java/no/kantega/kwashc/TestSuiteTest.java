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

package no.kantega.kwashc;

import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import no.kantega.kwashc.server.test.AbstractTest;
import no.kantega.kwashc.server.test.SSLCipherSuiteTest;
import no.kantega.kwashc.server.test.SiteExistsTest;
import no.kantega.kwashc.server.test.SiteWorksTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A test to test the test suite! :-)
 *
 * @author Anders B�strand, (www.kantega.no)
 */
public class TestSuiteTest {

	private static Site site;

	@BeforeClass
	public static void startSite() throws Exception {
		site = WebappJettyStarter.start();
	}

	@Test
	public void siteExistsNotTest() {
		SiteExistsTest siteExistsTest = new SiteExistsTest();
		Site site2 = new Site();
		// wrong port:
		site2.setAddress("http://localhost:22/");
		assertTestNotOK(siteExistsTest.testSite(site2));
		// wrong address:
		site2.setAddress("http://doesnotexist:22/");
		assertTestNotOK(siteExistsTest.testSite(site2));
	}

	/*
	 * Timeout: 2 minutes
	 */
	@Ignore("Vi må fikse Exception: Caused by: com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException: 500 /WEB-INF/jsp/blog.jsp(1,63) PWC6188: The absolute uri: http://j\n" +
			"ava.sun.com/jsp/jstl/core cannot be resolved in either web.xml or the jar files deployed with this application")
	@Test(timeout = 2 * 60 * 1000)
	public void testAllTest() throws Exception {

		for (AbstractTest test : TestRepository.getTests().values()) {
			if (test instanceof SiteWorksTest || test instanceof SiteExistsTest) {
				assertTestOK(test.testSite(site));
			} else if (test instanceof SSLCipherSuiteTest) {
				// test does not work, error in test system
			} else {
				assertTestNotOK(test.testSite(site));
			}
		}
	}

	private void assertTestOK(TestResult testResult) {
		assertTrue("The test '" + testResult.getTest().getName() + "' failed: " + testResult.getMessage(), testResult.getResultEnum() == ResultEnum.passed);
	}

	private void assertTestNotOK(TestResult testResult) { //TODO: Test partial
		assertFalse("The test '" + testResult.getTest().getName() + "' should have failed: " + testResult.getMessage(), testResult.getResultEnum() == ResultEnum.passed);
	}
}
