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

import java.io.IOException;

public class SiteExistsTest extends AbstractTest {

	public String getName() {
		return "Existence test";
	}

	public String getDescription() {
		return "Tests that the site exists.";
	}

	@Override
	public String getInformationURL() {
		return null;
	}

	protected TestResult testSite(Site site, final TestResult result) throws IOException {

		WebTester tester = new WebTester();
		tester.beginAt(site.getAddress());

		int response = tester.getTestingEngine().getServerResponseCode();

		if (response == 200) {
			result.setPassed(true);
			result.setMessage("Server at " + site.getAddress() + " did respond with 200.");
			return result;
		} else {
			result.setPassed(false);
			result.setMessage("Server at " + site.getAddress() + " did NOT respond with 200. It responded: " + response + ".");
			return result;
		}
	}
}
