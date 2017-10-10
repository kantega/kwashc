package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

/**
 * http://michael-coates.blogspot.no/2011/03/enabling-browser-security-in-web.html
 * http://www.jtmelton.com/2012/01/17/year-of-security-for-java-week-3-session-cookie-secure-flag/
 * http://www.jtmelton.com/2012/01/25/year-of-security-for-java-week-4-session-cookie-httponly-flag/
 */
public class ContentSecurityPolicyTest extends AbstractCSPTest {

    @Override
    public String getName() {
        return "Content Security Policy Test";
    }

    @Override
    public String getDescription() {
        return "Tests if the site has set any Content Security Policy headers.";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Content_Security_Policy";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();
        WebTester tester = new WebTester();

        tester.beginAt(site.getAddress());

        String cspHeader1 = tester.getHeader(CSP1);
        String cspHeader2 = tester.getHeader(CSP2);
        String cspHeader3 = tester.getHeader(CSP3);

        testResult.setPassed(false);
        testResult.setMessage("");

        setDuration(testResult, startTime);
        return testResult;
    }
}
