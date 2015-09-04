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

import java.util.UUID;

public class SiteWorksTest extends AbstractTest {
	
    @Override
    public String getName() {
        return "Happy day test";
    }

    @Override
    public String getDescription() {
        return "This test checks the behaviour of the site, and will fail if you change functionality.";
    }

	@Override
	public String getInformationURL() {
		return null;
	}

	@Override
	public String getExploit(Site site) {
		return null;
	}

	@Override
	public String getHint() {
		return null;
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        WebTester tester = new WebTester();
        tester.beginAt(site.getAddress());
	    tester.assertTextPresent("Posts");

	    tester.assertLinkPresentWithExactText("Admin");
	    tester.clickLinkWithExactText("Home");
	    tester.assertTextPresent("Posts");

	    tester.assertFormElementPresent("title");
	    tester.assertFormElementPresent("comment");
	    tester.assertButtonPresent("commentFormSubmit");

	    String titleUUID = UUID.randomUUID().toString();
	    String commentUUID = UUID.randomUUID().toString();

	    tester.setTextField("title", "Happy day test: " + titleUUID);
	    tester.setTextField("comment", "Happy day comment: " + commentUUID);

	    tester.clickButton("commentFormSubmit");

	    tester.assertLinkPresentWithExactText("Home");
	    tester.assertLinkPresentWithExactText("Admin");

	    tester.assertTextPresent("Posts");
	    tester.assertTextPresent("Happy day test: " + titleUUID);
	    tester.assertTextPresent("Happy day comment: " + commentUUID);

	    String titleUUID2 = UUID.randomUUID().toString();
	    String commentUUID2 = UUID.randomUUID().toString();

	    tester.setTextField("title", "Happy day test: " + titleUUID2);
	    tester.setTextField("comment", "Happy day comment: " + commentUUID2);

	    tester.clickButton("commentFormSubmit");

	    tester.assertTextPresent("Posts");
	    tester.assertTextPresent("Happy day test: " + titleUUID);
	    tester.assertTextPresent("Happy day comment: " + commentUUID);
	    tester.assertTextPresent("Happy day test: " + titleUUID2);
	    tester.assertTextPresent("Happy day comment: " + commentUUID2);

	    // blog tested OK, testing admin section:

	    tester.assertTextNotPresent("You are currently browsing the site as admin!");

	    tester.clickLinkWithExactText("Admin");
	    tester.assertTextNotPresent("Posts");
	    tester.assertTextPresent("You asked for a protected resource. Please log in:");

	    tester.assertFormElementPresent("username");
	    tester.assertFormElementPresent("password");

	    tester.assertButtonPresent("formSubmitButton");

	    tester.setTextField("username", "does_not_exist");
	    tester.setTextField("password", "does_not_exist");

	    tester.clickButton("formSubmitButton");

	    tester.assertTextPresent("You asked for a protected resource. Please log in:");
	    tester.assertTextNotPresent("You are currently browsing the site as admin!");

	    tester.setTextField("username", "username");
	    tester.setTextField("password", "password");

	    tester.clickButton("formSubmitButton");

	    tester.assertTextPresent("You are currently browsing the site as admin!");

	    tester.clickLink("delete." + "Happy day test: " + titleUUID);

	    tester.assertTextPresent("Posts");
	    tester.assertTextNotPresent("Happy day test: " + titleUUID);
	    tester.assertTextNotPresent("Happy day comment: " + commentUUID);
	    tester.assertTextPresent("Happy day test: " + titleUUID2);
	    tester.assertTextPresent("Happy day comment: " + commentUUID2);

		//Checking the RESTful APIkwrite
		tester.gotoPage(site.getAddress() + "blog/api/comments/list/");
		tester.assertTextPresent(commentUUID2);

        //Checking the redirect servlet
		tester.gotoPage(site.getAddress() + "/redirect?url=http://motherfuckingwebsite.com/");
		tester.assertTextPresent("This is a motherfucking website.");

		testResult.setResultEnum(ResultEnum.passed);
	    testResult.setMessage("Site works like a charm!");

		setDuration(testResult, startTime);
        return testResult;
    }
	@Override
	public TestCategory getTestCategory() {
		return TestCategory.happyDay;
	}
}
