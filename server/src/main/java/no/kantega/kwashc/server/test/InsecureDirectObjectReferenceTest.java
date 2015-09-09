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

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

import java.util.UUID;

/**
 * Tests if an unauthenticated user can edit a blog post, by sending a GET-request with a commentID as a variable.
 *
 * The test checks the following:
 * Whether an unauthenticated user can edit a blog post
 *
 * Solution:
 * Add the edit servlet to the login filter
 *
 * @author Øystein Øie, (www.kantega.no)
 */

public class InsecureDirectObjectReferenceTest extends AbstractTest {

    @Override
    public String getName(){
        return "Insecure Direct Object Reference";
    }

    @Override
    public String getDescription(){
        return "Testing for Insecure Direct Object Reference by editing a post on the blog. Users should log in to be able to edit posts.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2013-A4-Insecure_Direct_Object_References";
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
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable{
        long startTime = System.nanoTime();

        String random = UUID.randomUUID().toString();

        WebTester tester = new WebTester();
        tester.beginAt(site.getAddress());

        tester.setTextField("title", random);
        tester.setTextField("comment", "Edit post");
        tester.clickButton("commentFormSubmit");

        String editHref = getCommentToAnonymise(site, random);

        tester.gotoPage(site.getAddress() + editHref);
        tester.gotoPage(site.getAddress() + "/blog");

        String frontpage = tester.getPageSource();

        if(frontpage.contains(random + " - edited")) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Unauthorised editing possible. Users should log in to be able to edit posts.");
        } else
        {
            testResult.setResultEnum(ResultEnum.passed);
            testResult.setMessage("You successfully prevented unauthorised editing!");
        }

        setDuration(testResult, startTime);
        return testResult;

    }

    private String getCommentToAnonymise(Site site, String title){
        WebTester admin = new WebTester();
        admin.beginAt(site.getAddress() + "/admin");

        admin.setTextField("username", "username");
        admin.setTextField("password", "password");
        admin.clickButton("formSubmitButton");

        IElement elem = admin.getElementById("edit." + title);
        return elem.getAttribute("href");
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.securityFeature;
    }
}
