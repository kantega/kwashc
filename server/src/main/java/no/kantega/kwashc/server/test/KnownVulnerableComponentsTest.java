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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests if jquery is updated to the latest version. The version being used (1.6.2), is vulnerable to
 * Cross Site Scripting (ref. http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2011-4969&cid=2).
 *
 * Example of exploit: http://localhost:8080/blog#<img src=/ onerror=alert(1)>
 *
 * Solution:
 * Update to latest version of jQuery (https://jquery.com/download/).
 * Requiring latest version is just for simplicity, updating to version 1.6.3 or later is sufficient.
 *
 * @author Øystein Øie, (www.kantega.no)
 */
public class KnownVulnerableComponentsTest extends AbstractTest
{

    @Override
    public String getName()
    {
        return "Known Vulnerable Components";
    }

    @Override
    public String getDescription() {
        return "Modern development means using quite a lot of third party software, both server and client side. Like " +
                "any software, these components and libraries are susceptible to programming mistakes and " +
                "vulnerabilities.";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Top_10_2013-A9-Using_Components_with_Known_Vulnerabilities";
    }

    @Override
    public String getExploit(Site site) {
        return "Click <a href=\"" + getBaseUrl(site) + "#<img src=x onerror=%22alert('jQuery used to" +
                " use createElement() in selectors. This can cause XSS.')%22/>\" target=\"_blank\")\">here</a>.";
    }

    @Override
    public String getHint() {
        return "Old jQuery versions had an issue where jQuery selectors ($(something)) used the native JS function " +
                "createElement(). Allowing an attacker to create arbitrary html elements creates a XSS vulnerability. " +
                "See the exploit.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable
    {
        long startTime = System.nanoTime();
        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());


        IElement elem = tester.getTestingEngine().getElementByID("jquery");
        if(elem == null){
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Do not remove the id attribute from the script tags in header.jsp! Do you think we are using unicorns to test this?");
            setDuration(testResult, startTime);
            return testResult;
        }

        String src = elem.getAttribute("src");
        Pattern pattern = Pattern.compile(".*jquery-(.*)\\.min\\.js");
        Matcher matcher = pattern.matcher(src);

        if(!matcher.matches())
        {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Unable to find script-tag for jquery in html. Include jquery by using the \"src\"-attribute");
            setDuration(testResult, startTime);
            return testResult;
        }

        String source = tester.getPageSource();
        boolean offendingLocationHashRemoved = !source.contains("$(location.hash)");

        String version = matcher.group(1);
        try {
            if(isApprovedVersion(version)) {
                testResult.setResultEnum(ResultEnum.passed);
                testResult.setMessage("jQuery has been updated to a new version!");
            } else if(offendingLocationHashRemoved){
                testResult.setResultEnum(ResultEnum.partial);
                testResult.setMessage("Good! Removing the pointless $(location.hash} is good practice, but you still " +
                        "should update jQuery to a newer version. Something else might go wrong.");
            } else {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("The application includes vulnerable components. " +
                        "Try to search for known vulnerable components that is used in the application.");
            }
        } catch (Exception e) {
            testResult.setResultEnum(ResultEnum.partial);
            testResult.setMessage("Well, this is embarrassing. We can't make out what version of jQuery you are using " +
                    "(" + version + "). How about a nice conventional version number, like 1.8.0?");
        }

        setDuration(testResult, startTime);
        return testResult;
    }

    boolean isApprovedVersion(String version) {

        int[] minVersionInts = {1, 6, 2};
        String[] versionTokens = version.split("\\.");

        for(int i = 0; i < 3 && i < versionTokens.length; i++) {
            if(minVersionInts[i] < Integer.parseInt(versionTokens[i])) {
                return true;
            }
        }
        return false;

    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.assorted;
    }
}
