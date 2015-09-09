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

/**
 * Tests if it is possible to embed the blog in a page on the server.
 * <p/>
 * Solution: Add frame-ancestors directive in Content Security Policy (CSP) and X-Frame-Options to the header;
 * BlogServlet.class or add a filter with:
 *  resp.setHeader("X-FRAME-OPTIONS", "deny");
 *  resp.setHeader("Content-Security-Policy", "frame-ancestors");
 *
 * and write a short javascript that checks if the site is embedded. Something like this:
 * <p/>
 * <script>if (top!=self) top.location.href=self.location.href</script>
 * <p/>
 * Read more at https://www.owasp.org/index.php/Clickjacking_Defense_Cheat_Sheet
 *
 * @author Anders Båstrand, (www.kantega.no)
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class ClickjackingTest extends AbstractTest {

	@Override
	public String getName() {
		return "Click-jacking";
	}

	@Override
	public String getDescription() {
		return "Click-jacking is an easy and effective attack for tricking a legitimate user to perform certain " +
				"actions on your page. This is done by creating a malicious page designed to make the user click on " +
				"certain parts or do certain button presses, while an invisible copy of your site is placed on top. " +
				"The victim sees the specially crafted attacker page, while in reality interacting with your page. ";
	}

	@Override
	public String getInformationURL() {
		return "http://en.wikipedia.org/wiki/Clickjacking";
	}

	@Override
	public String getExploit(Site site) {
		if(site != null) {
            return "Visit <a href=\"/site/" + site.getId() + "/clickjacking\" target=\"_blank\">this page</a>. The " +
                    "blog would be invisible in a real world attack, while the overlaying content would be placed " +
                    "strategically to make the victim perform actions on the blog.";
		}
		return null;
	}

	@Override
	public String getHint() {
		return "See the Wikipedia article.";
	}

	@Override
	protected TestResult testSite(final Site site, final TestResult testResult) throws Throwable {
		long startTime = System.nanoTime();
		WebTester tester = new WebTester();

		tester.beginAt(site.getAddress());

		String frameOptionsHeader = tester.getHeader("X-Frame-Options");
		String contentSecurityPolicyHeader1 = tester.getHeader("Content-Security-Policy");
		String contentSecurityPolicyHeader2 = tester.getHeader("X-Content-Security-Policy");
		String contentSecurityPolicyHeader3 = tester.getHeader("X-WebKit-CSP");


		boolean deprecatedHeaderSolution = false;
		boolean contentSecurityPolicySolution = false;
		boolean javascriptSolution = false;

		if (frameOptionsHeader != null && (frameOptionsHeader.equalsIgnoreCase("deny") || frameOptionsHeader.equalsIgnoreCase("sameorigin"))) {
			deprecatedHeaderSolution = true;
		}

		// tries to go to page showing iframe instead
		tester.beginAt("http://localhost:9090/site/" + site.getId() + "/clickjacking");
		if (!tester.getPageSource().contains("Here we mix your site with new content. Beware!")) {
			javascriptSolution = true;
		}

		if(checkContentSecurityPolicy(contentSecurityPolicyHeader1) || checkContentSecurityPolicy(contentSecurityPolicyHeader2) || checkContentSecurityPolicy(contentSecurityPolicyHeader3)){
			contentSecurityPolicySolution = true;
		}

		try {
			if (javascriptSolution && deprecatedHeaderSolution && contentSecurityPolicySolution) {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("You jumped out of the evil iframe, added an frame-ancestors directive in Content Security Policy (CSP) AND set an X-Frame-Options to '" + frameOptionsHeader + "'. Triple protection. Excellent!!");
                return testResult;
            } else if (javascriptSolution && contentSecurityPolicySolution) {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("You jumped out of the evil iframe, AND added an frame-ancestors directive in Content Security Policy (CSP). Double protection. Excellent!");
                return testResult;
            } else if (contentSecurityPolicySolution && deprecatedHeaderSolution) {
                testResult.setResultEnum(ResultEnum.partial);
                testResult.setMessage("Good! You added an frame-ancestors directive in Content Security Policy (CSP), AND set an X-Frame-Options to '" + frameOptionsHeader + "'. But what if the user is using an old browser?");
                return testResult;
            } else if (javascriptSolution && deprecatedHeaderSolution) {
                testResult.setResultEnum(ResultEnum.partial);
                testResult.setMessage("You jumped out of the evil iframe, AND set an X-Frame-Options to '" + frameOptionsHeader + "'. Unfortunately X-Frame-Options is deprecated, what about using some Content Security Policy (CSP)?");
                return testResult;
            } else if (javascriptSolution) {
                testResult.setResultEnum(ResultEnum.partial);
                testResult.setMessage("You jumped out of the evil iframe. Well done! But what if the user does not have javascript?");
                return testResult;
            } else if (deprecatedHeaderSolution) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("You're on the right track. You have set X-Frame-Options to " + frameOptionsHeader + ". Unfortunately X-Frame-Options is deprecated, and what if the user is using an old browser?");
                return testResult;
            } else if (contentSecurityPolicySolution) {
                testResult.setResultEnum(ResultEnum.partial);
                testResult.setMessage("Good! You have set the frame-ancestors directive in Content Security Policy (CSP). But what if the user is using an old browser?");
                return testResult;
            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("It's possible to embed the blog in an iframe. This creates a click-jacking vulnerability, " +
						"as users can be tricked into performing actions on the blog, while they are shown something else.");
                return testResult;
            }
		} finally {
			setDuration(testResult, startTime);
		}
	}

	private boolean checkContentSecurityPolicy(String contentSecurityPolicyHeader) {
        return contentSecurityPolicyHeader != null && contentSecurityPolicyHeader.contains("frame-ancestors");
    }

	@Override
	public TestCategory getTestCategory() {
		return TestCategory.assorted;
	}
}
