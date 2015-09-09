/*
 * Copyright 2013 Kantega AS
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

/**
 * Test if various XSS vulnerabilities are present. This test is a bit fragile and can give false negatives if the JS is
 * rewritten without fixing the vulnerabilities.
 *
 * Solution: Remove or fix the offending JS calls:
 * 1) The name parameter is URI decoded, and then written to the DOM as html, rather than text. Fix either, or better yet both.
 * 2) Never call setTimeout() with user supplied data! The function calls eval(), which will execute any JS, even if escaped!
 *
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
public class DOMXSSTest extends AbstractTest {
	
    @Override
    public String getName() {
        return "DOM and JavaScript based cross-site scripting (XSS)";
    }

    @Override
    public String getDescription() {
        return DESCRIPTION_XSS +
				"<br><br>JavaScript functions can create XSS vulnerabilities in two ways:<ol><li>Writing user input " +
				"as html to the DOM<li>" +
				"Using user input in certain unsafe functions, such as functions based on <i>eval(): " +
				"setInterval(), setTimeout()</i> and <i>new Function()</i>. These functions parse and execute any " +
				"javascript contained in the input.</li></ol>";
	}

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet";
	}

	@Override
	public String getExploit(Site site) {
		String link = getBaseUrl(site);

		return "Some browsers will filter these basic attacks. Try using Firefox. " +
				"<ol><li><a href='" + link + "blog?name=%3Cimg%20src=x%20onerror=alert%281%29%3E' target" +
				"='_blank'>" + link + "blog?name=&lt;img src=x onerror=alert(1)&gt;</a></li>" +
				"<li><a href='" + link + "blog?timeout=alert%282%29' target='_blank'>" +
				link + "blog?timeout=alert(2)</a></li>" +
				"</ol>";
	}

	@Override
	public String getHint() {
		return "<ol><li>The name parameter is URI decoded, and then written to the DOM as html, rather than text. " +
				"Fix either, or better yet both.</li>" +
				"<li>Never call setTimeout() with user supplied data! The function calls eval(), which will execute " +
				"any JS, even if escaped! Remember: Eval is evil!</li></ol>";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
		long startTime = System.nanoTime();

		WebTester tester = new WebTester();
		tester.beginAt(site.getAddress());
		tester.assertTextPresent("Posts");

		String source = tester.getPageSource();
		boolean isNameVulnerable = isNameVulnerable(source);
		boolean isTimeoutVulnerable = isTimeoutVulnerable(source);
		//the jQuery $(document.hash) vulnerability is covered by the KnownVulnerableComponentsTest.

		if(isNameVulnerable && isTimeoutVulnerable){
			testResult.setResultEnum(ResultEnum.failed);
			testResult.setMessage("It's possible to create arbitrary html elements using the name parameter, or use " +
					"the timeout parameter to execute arbitrary javascript directly. Both lead to XSS vulnerabilities.");
		}
		else if(isNameVulnerable) {
			testResult.setResultEnum(ResultEnum.partial);
			testResult.setMessage("It's possible to create arbitrary html elements using the name parameter. This is " +
					"a XSS vulnerability.");
		} else if(isTimeoutVulnerable){
			testResult.setResultEnum(ResultEnum.partial);
			testResult.setMessage("It's possible to use the timeout parameter to execute arbitrary javascript " +
					"directly. This is a XSS vulnerability.");
		} else{
			testResult.setResultEnum(ResultEnum.passed);
			testResult.setMessage("No errors, we hope." +
					"You should have fixed blog.jsp: 1) Removed the explicit URI decoding of the parameter " +
					"(decodeURIComponent()) and/or used $('#someId').text(name) rather than document.write(name). 2) " +
					"Parsed 'timeout' as an int before calling setTimeout(theParsedInt). Escaping won't work here! " +
					"Better yet, you should have deleted it, as it isn't used anywhere.");
		}
		setDuration(testResult, startTime);
		return testResult;
    }

	private boolean isTimeoutVulnerable(String source) {
		String[] vulnerableStrings = {"timeout = getParameter(\"timeout\"", "setTimeout(timeout)"};
		return sourceContainsAllStrings(source, vulnerableStrings);
	}

	private boolean isNameVulnerable(String source){
		String[] vulnerableStrings = {"name = getParameter(\"name\")", "document.write(name", "return decodeURIComponent(name"};
		return sourceContainsAllStrings(source, vulnerableStrings);
	}

	private boolean sourceContainsAllStrings(String source, String[] strings){
		for(String string : strings){
			if(!source.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public TestCategory getTestCategory() {
		return TestCategory.xss;
	}
}
