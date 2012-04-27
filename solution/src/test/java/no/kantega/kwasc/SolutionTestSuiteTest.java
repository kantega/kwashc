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

import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import no.kantega.kwashc.server.test.AbstractTest;
import no.kantega.kwashc.server.test.SSLCipherSuiteTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * A test to test the test suite! :-)
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
public class SolutionTestSuiteTest {

	private static Site site;

	@BeforeClass
	public static void startSite() throws Exception {
		site = SolutionWebappJettyStarter.start();
	}

	/*
	 * Timeout: 2 minutter
	 */
	@Ignore
	@Test(timeout = 2 * 60 * 1000)
	public void testAllTestOnSOlution() throws Exception {

		for (AbstractTest test : TestRepository.getTests().values()) {
			if (test instanceof SSLCipherSuiteTest) {
				// test does not work, error in test system
			} else {
				assertTestOK(test.testSite(site));
			}
		}
	}

	private void assertTestOK(TestResult testResult) {
		assertTrue("The test '" + testResult.getTest().getName() + "' failed: " + testResult.getMessage(), testResult.isPassed());
	}
}
