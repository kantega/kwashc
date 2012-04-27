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

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

/**
 * Tests if it is possible to embed the blog in a page on the server.
 * <p/>
 * Solution: Add X-Frame-Options to the header, and write a short javascript that checks if the site is embedded. Something like this:
 * <p/>
 * <script>if (top!=self) top.location.href=self.location.href</script>
 * <p/>
 * Note: This solution is NOT recommended, but is enough to beat the test for now. Read more at https://www.owasp.org/index.php/Clickjacking#Limitations
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
public class ClickjackingTest extends AbstractTest {

	@Override
	public String getName() {
		return "Click-jacking test";
	}

	@Override
	public String getDescription() {
		return "Tests the site for embedding in an iframe";
	}

	@Override
	public String getInformationURL() {
		return "http://en.wikipedia.org/wiki/Clickjacking";
		// This has the solution too easy to find: ;-)
		// return "https://www.owasp.org/index.php/Clickjacking";
	}

	@Override
	protected TestResult testSite(final Site site, final TestResult testResult) throws Throwable {
		WebTester tester = new WebTester();

		tester.beginAt(site.getAddress());

		String header = tester.getHeader("X-Frame-Options");

		boolean headerSolution = false;
		boolean javascriptSolution = false;

		if (header != null && (header.equalsIgnoreCase("deny") || header.equalsIgnoreCase("sameorigin"))) {
			headerSolution = true;
		}

		// tries to go to page showing iframe instead
		tester.beginAt("http://localhost:9090/site/" + site.getId() + "/clickjacking");
		if (!tester.getPageSource().contains("Here we mix your site with new content. Beware!")) {
			javascriptSolution = true;
		}

		if (javascriptSolution && headerSolution) {
			testResult.setPassed(true);
			testResult.setMessage("You jumped out of the evil iframe, AND used the header value (you used '" + header + "'). Double protection. " +
					"Excellent!");
			return testResult;
		} else if (javascriptSolution) {
			testResult.setPassed(false);
			testResult.setMessage("You jumped out of the evil iframe. Well done! But what if the user does not have javascript?");
			return testResult;
		} else if (headerSolution) {
			testResult.setPassed(false);
			testResult.setMessage("Good! You have set X-Frame-Options to " + header + ". But what if the user is using an old browser?");
			return testResult;
		} else {
			testResult.setPassed(false);
			testResult.setMessage("You have made it possible to embedd your site in an iframe. See for yourself at " +
					"<a href=\"/site/" + site.getId() + "/clickjacking\">this page</a>.");
			return testResult;
		}
	}
}
