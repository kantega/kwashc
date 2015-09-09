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
import no.kantega.kwashc.server.model.ResultEnum;
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

    /* FUTURE PLANS
     1.
        a. Extend this test to include validation of other user input (title, comment)
        b. Create a test which uses http headers in unsafe manner
     2. Canonicalization/force charset
    */
    @Override
    public String getName() {
        return "Input validation";
    }

    @Override
    public String getDescription() {
        return "Input validation of user supplied data is the first line of defense against most forms of attacks. " +
                "You should always check if ANY AND ALL USER SUPPLIED DATA appears to be valid. This test only checks" +
                " if the homepage variable is validated as a legal URL.";
    }

    @Override
	public String getInformationURL() {
		return "https://www.securityninja.co.uk/secure-development/input-validation/";
	}

    @Override
    public String getExploit(Site site) {
        return "In this instance, missing validation and improper escaping creates a XSS-vulnerability. Using " +
                "Firefox, enter a comment with the homepage <i>javascript:alert(1)</i>. Click the link, and the " +
                "embedded javascript will execute. This only shows a popup on the 'victim' browser, but it could be " +
                "any sort of malicious javascript, stealing or modifying data, hijacking sessions or installing " +
                "malware." +
                "<br><br>Note that blog.jsp attempts to escape the URL using &lt;a href=&lt;c:out value=\"${comment" +
                ".homepage}\"/&gt;. This fails as the variable is used as an html attribute, while c:out escapes for " +
                "plain html. This incorrect context means that some special characters for an html attribute and URL " +
                "(like ':') aren't correctly escaped.";
    }

    @Override
    public String getHint() {
        return "doPost() in BlogServlet reads the 'homepage' parameter as a String, and stores it directly as a " +
                "String variable in Comment. Comment should store the value as a rich java.net.URL object, rather " +
                "than as a String. URL will parse and validate the input string during object creation using new URL" +
                "(String stringRepresentation), and possibly throw an MalformedURLException. A real life application " +
                "would give the user the opportunity to fix the illegal URL, but here you can safely discard any " +
                "illegal values.";
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
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Input validation of user supplied data is a core security feature for any " +
                    "application. The blog currently accepts a homepage URL as part of the comments, without ensuring" +
                    " that this is in fact a proper URL.");
        } else {
            testResult.setResultEnum(ResultEnum.passed);
            testResult.setMessage("You appear to be validating the user supplied URLs! Hopefully you parsed the " +
                    "homepage parameter as a java.net.URL.Blacklisting scary input like 'javascript:' isn't very " +
                    "effective in the real world.");
        }

        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.securityFeature;
    }
}
