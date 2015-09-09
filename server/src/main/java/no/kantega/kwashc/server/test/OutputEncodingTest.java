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
 * Tests that certain characters is not reflected back to the user from blog comments
 *
 * Solution: Use <c:out value="${...}"/> instead of  ${..}
 *
 * @author Anders Båtstrand, (www.kantega.no)
 */
public class OutputEncodingTest extends AbstractTest {

    @Override
    public String getName() {
        return "Output encoding";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION_XSS + "<br><br>Some user supplied variables in the comments section are included directly " +
                "in the html without any escaping of html characters. This creates a XSS vulnerability." +
                "<br><br>Validating the fields for illegal values <i>could</i> also help, but might be hampered by " +
                "functional constraints. It is clearly legitimate for normal users to be discussing JavaScript or " +
                "maths, and banning potentially &quot;dangerous&quot; characters like &lt;, &gt;, ' or &quot; isn't " +
                "acceptable.";
    }

    @Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet";
	}

    @Override
    public String getExploit(Site site) {
        return "Missing output encoding of the comment and title variables allows an attacker to include fully " +
                "functional html, including active content. Write a comment or a title containing <i>&lt;img src=x " +
                "onerror=\"alert('This is a malicious script excecuting')\"&gt;</i>." +
                "<br><br>Some browsers will filter basic XSS attacks like this. Try Firefox.";
    }

    @Override
    public String getHint() {
        return "The JSTL tag library already included can do basic html escaping. Use &lt;c:out value=.../&gt;";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws IOException {
        long startTime = System.nanoTime();

        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());

        tester.setTextField("title", "<div id=\"evilDiv\">Title</div>");
        tester.setTextField("comment", "<div id=\"evilDiv\">Comment</div>");

        tester.clickButton("commentFormSubmit");

        // this might be reflected back in the form, or on the front page
        if (tester.getTestingEngine().hasElement("evilDiv")) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("User input is included directly in the html without any " +
                    "escaping of html characters. This creates a XSS vulnerability.");
            setDuration(testResult, startTime);
            return testResult;
        }

        tester.beginAt(site.getAddress());
        tester.assertElementNotPresent("evilDiv");

        if (tester.getTestingEngine().hasElement("evilDiv")) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("User input is included directly in the html without any " +
                    "escaping of html characters. This creates a XSS vulnerability.");
            setDuration(testResult, startTime);
            return testResult;
        }

        testResult.setResultEnum(ResultEnum.passed);
        testResult.setMessage("No errors found.");

        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.xss;
    }
}
