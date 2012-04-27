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

/**
 * Tests that certain characters is not reflected back to the user from blog comments
 *
 * Solution: Use <c:out value="${...}"/> instead of  ${..}
 *
 * @author Anders Båtstrand, (www.kantega.no)
 */
public class OutputEncoding extends AbstractTest {

    @Override
    public String getName() {
        return "Output encoding";
    }

    @Override
    public String getDescription() {
        return "Tests the blog for output encoding errors.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws IOException {

        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());

        tester.setTextField("title", "<div id=\"evilDiv\">Title</div>");
        tester.setTextField("comment", "<div id=\"evilDiv\">Comment</div>");

        tester.clickButton("commentFormSubmit");

        // this might be reflected back in the form, or on the front page
        if (tester.getTestingEngine().hasElement("evilDiv")) {
            testResult.setPassed(false);
            testResult.setMessage("You allowed html tags to be reflected back on the user!");
            return testResult;
        }

        tester.beginAt(site.getAddress());
        tester.assertElementNotPresent("evilDiv");

        if (tester.getTestingEngine().hasElement("evilDiv")) {
            testResult.setPassed(false);
            testResult.setMessage("You allowed html tags to be reflected back on all users!");
            return testResult;
        }

        testResult.setPassed(true);
        testResult.setMessage("No errors found.");

        return testResult;

    }
}
