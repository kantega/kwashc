package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

/**
 * Test if the backdoor placed in LoginServlet by a malicious programmer is present.
 *
 * Solution: Find and remove the unicode trick in one of the comments in LoginServlet. While it will be displayed as a
 * comment in any IDE, the java compiler will decode this as "if(password.equals("backdoor")) password = user.getPassword();"
 *
 * See http://obfuscat.ion.land
 *
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
class BackdoorTest extends AbstractTest {

    @Override
    public String getName() {
        return "Backdoor test";
    }

    @Override
    public String getDescription() {
        return "A malicious programmer has created a backdoor allowing him to log in as any user. This test checks if it is present";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Malicious_Developers_and_Enterprise_Java_Rootkits";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();
        final String backdoorPassword = "backdoor";

        String responseBody;

            WebTester tester = new WebTester();
            tester.setIgnoreFailingStatusCodes(true);
            tester.beginAt(site.getAddress());
            tester.clickLinkWithExactText("Admin");
            tester.setTextField("username", "username");
            tester.setTextField("password", backdoorPassword);
            tester.clickButton("formSubmitButton");

            responseBody = tester.getPageSource();

            if(!responseBody.contains("You asked for a protected resource. Please log in:")) {
                testResult.setPassed(false);
                testResult.setMessage("It's possible to log in with the special password 'backdoor' for any user. No points!");
            } else {
                testResult.setPassed(true);
                testResult.setMessage("The backdoor doesn't work anymore! Good work!");
            }
            setDuration(testResult, startTime);
            return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.securityFeature;
    }
}
