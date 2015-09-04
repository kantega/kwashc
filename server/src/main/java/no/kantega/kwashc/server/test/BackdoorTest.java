package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

/**
 * Test if the backdoor placed in LoginServlet by a malicious programmer is present.
 * <p/>
 * Solution: Find and remove the unicode trick in one of the comments in LoginServlet. While it will be displayed as a
 * comment in any IDE, the java compiler will decode this as "if(password.equals("backdoor")) password = user
 * .getPassword();"
 * <p/>
 * See http://obfuscat.ion.land
 *
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
class BackdoorTest extends AbstractTest {

    @Override
    public String getName() {
        return "Backdoor";
    }

    @Override
    public String getDescription() {
        return "A malicious programmer has created a backdoor, making it possible for him (or the NSA) to log in as "
                + "any user at any time, without knowing the user's actual password. ";
    }

    @Override
    public String getInformationURL() {
        return "http://obfuscat.ion.land";
    }

    @Override
    public String getExploit(Site site) {
        return "The special password 'backdoor' works for any user, regardless of what the actual password is. Try "
                + "logging in as <i>username</i> or <i>system</i> (click <a href='" + getBaseUrl(site) + "admin' target=" +
                "'_blank'>'Admin' at the bottom of the blog</a>).";
    }

    @Override
    public String getHint() {
        return "The obfuscated backdoor is hidden in LogInServlet. Your IDE might be as confused as you are.";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        String responseBody;

        WebTester tester = new WebTester();
        tester.setIgnoreFailingStatusCodes(true);
        tester.beginAt(site.getAddress());
        tester.clickLinkWithExactText("Admin");
        tester.setTextField("username", "username");
        tester.setTextField("password", "backdoor");
        tester.clickButton("formSubmitButton");

        responseBody = tester.getPageSource();

        if (!responseBody.contains("You asked for a protected resource. Please log in:")) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("A malicious programmer has created a backdoor making it possible to log in as any " +
                    "user with the special password 'backdoor'");
        } else {
            testResult.setResultEnum(ResultEnum.passed);
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
