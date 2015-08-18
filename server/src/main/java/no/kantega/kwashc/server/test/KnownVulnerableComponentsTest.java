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
    public String getDescription()
    {
        return "Testing for known vulnerable components used in the web application.";
    }

    @Override
    public String getInformationURL()
    {
        return "https://www.owasp.org/index.php/Top_10_2013-A9-Using_Components_with_Known_Vulnerabilities";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable
    {
        long startTime = System.nanoTime();
        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());
        String source = tester.getPageSource();

        if(!source.contains("$(location.hash)"))
        {
            testResult.setPassed(false);
            testResult.setMessage("jQuery functionality removed from source. Remember: do not change functionality!");
            setDuration(testResult, startTime);
            return testResult;
        }

        IElement elem = tester.getTestingEngine().getElementByID("jquery");
        if(elem == null){
            testResult.setPassed(false);
            testResult.setMessage("Do not remove the id attribute from the script tags in header.jsp! Do you think we are using unicorns to test this?");
            setDuration(testResult, startTime);
            return testResult;
        }

        String src = elem.getAttribute("src");
        Pattern pattern = Pattern.compile(".*jquery-(.*)\\.min\\.js");
        Matcher matcher = pattern.matcher(src);

        if(!matcher.matches())
        {
            testResult.setPassed(false);
            testResult.setMessage("Unable to find script-tag for jquery in html. Include jquery by using the \"src\"-attribute");
            setDuration(testResult, startTime);
            return testResult;
        }

        String version = matcher.group(1);
        if(version.equalsIgnoreCase("1.6.2"))
        {
            testResult.setPassed(false);
            testResult.setMessage("The application includes vulnerable components. " +
                    "Try to search for known vulnerable components that is used in the application.");
        } else if(version.matches("2.*"))
        {
            testResult.setPassed(false);
            testResult.setMessage("The blog needs to support older versions of IE, try using the latest version 1.x of jquery.");
        } else if(version.equalsIgnoreCase("1.11.3"))
        {
            testResult.setPassed(true);
            testResult.setMessage("jQuery is successfully updated to the latest version!");
        } else
        {
            testResult.setPassed(false);
            testResult.setMessage("Seems like jquery has been tried updated, but you should update to the latest 1.x version of jquery.");
        }

        setDuration(testResult, startTime);
        return testResult;
    }
}
