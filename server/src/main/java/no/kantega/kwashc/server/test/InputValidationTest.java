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
 * The blog allows the user to provide a link to their homepage in their comments. It tries to html escape this output
 * through <c:out...>, but this is the wrong context, since it is used as an html attribute. It can can be used as a XSS
 * vector using "javascript:alert('Evil script')". The attribute is also not properly contained in brackets in the jsp,
 * so an input of "abc onmouseover=alert('evil script')" will also work.
 *
 * Solution: The easiest solution is to validate the user supplied URLs by parsing them as the strictly typed
 * java.net.URL.
 *
 * You should also 1) contain the attribute in quotes, and 2) properly escape the HTML attribute. The latter is
 * difficult without introducing a framework such as OWASP ESAPI, which has tools for escaping data for a number of
 * different contexts. JSTL's fn.escapeAsXml() *might* provide some coverage, and *might* not break valid URLs. The test
 * doesn't cover these two subjects.
 *
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
public class InputValidationTest extends AbstractTest {

    @Override
    public String getName() {
        return "Validation test";
    }

    @Override
    public String getDescription() {
        return "Tests if the blog handles user specified URLs safely";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws IOException {
        long startTime = System.nanoTime();

        String evilJS = "javascript:alert(1)";

        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());
        tester.setTextField("homepage", evilJS);
        tester.clickButton("commentFormSubmit");

        if (tester.getTestingEngine().getPageSource().contains(evilJS)) {
            testResult.setPassed(false);
            testResult.setMessage("Your commenting system allowed an illegal home page URL containing JavaScript! Try entering the homepage " +
                    "URL 'javascript:alert(1)', and click on the link. Or 'abc onmouseover=alert(2)', and move the " +
                    "mouse pointer over the link.");
        } else {
            testResult.setPassed(true);
            testResult.setMessage("No errors found. Hopefully you parsed the homepage parameter as a java.net.URL." +
                    "Blacklisting scary input like 'javascript:' isn't very effective in the real world.");
        }

        setDuration(testResult, startTime);
        return testResult;
    }
}
