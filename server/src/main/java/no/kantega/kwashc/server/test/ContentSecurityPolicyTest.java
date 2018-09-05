package no.kantega.kwashc.server.test;

import com.shapesecurity.salvation.Parser;
import com.shapesecurity.salvation.data.Policy;
import com.shapesecurity.salvation.data.URI;
import com.shapesecurity.salvation.directiveValues.None;
import com.shapesecurity.salvation.directives.DefaultSrcDirective;
import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static no.kantega.kwashc.server.test.TestCategory.assorted;

/*
 * Test if CSP header is present, and that it has some decent directives.
 *
 * Solution: Add Content-Security-Policy header with "default-src 'self'"
 */
public class ContentSecurityPolicyTest extends AbstractCSPTest {

    @Override
    public String getName() {
        return "Content Security Policy Test";
    }

    @Override
    public String getDescription() {
        return "Content Security Policy (CSP) is an optional policy that web sites can offer the client browser to " +
                "improve security. The policy among others specifies from which location and/or which type of " +
                "resources are allowed to be loaded as part of the web site.";
    }

    @Override
    public String getExploit(Site site) {
        return null;
    }

    @Override
    public String getHint() {
        return "Use the awesome http://cspisawesome.com/ to help with generating a sane policy for your site.";
    }

    @Override
    public TestCategory getTestCategory() {
        return assorted;
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

        String cspHeader = tester.getHeader(CSP1);
        String cspHeaderReport = tester.getHeader(CSPReport);

        if (cspHeader == null || cspHeader.isEmpty()) {
            Document document = Jsoup.parse(tester.getPageSource());
            cspHeader = document.select("meta[http-equiv=Content-Security-Policy]").stream()
                    .findFirst()
                    .map(doc -> {
                        try {
                            return doc.attr("content");
                        } catch (Exception e) {
                            return null;
                        }

                    }).orElse(null);
        }

        if (cspHeaderReport == null || cspHeaderReport.isEmpty()) {
            Document document = Jsoup.parse(tester.getPageSource());
            cspHeaderReport = document.select("meta[http-equiv=Content-Security-Policy-Report]").stream()
                    .findFirst()
                    .map(doc -> {
                        try {
                            return doc.attr("content");
                        } catch (Exception e) {
                            return null;
                        }

                    }).orElse(null);
        }


        try {
            if (cspHeader == null || cspHeader.isEmpty()) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("No Content Security Policy header found, this is bad!");
            } else if (cspHeaderReport != null) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Found Content Security Policy Report Only header, great for testing, but we want the real Mccoy!");
            } else if (!cspHeader.contains("default-src")) {
                testResult.setResultEnum(ResultEnum.failed);
                testResult.setMessage("Your Content Security Policy has no default-src directive, zero points!");
            } else {
                Policy csp = Parser.parse(cspHeader, site.getAddress());
                DefaultSrcDirective defaultSrcDirective = csp.getDirectiveByType(DefaultSrcDirective.class);

                if(defaultSrcDirective != null && ( defaultSrcDirective.values().count() == 0 || defaultSrcDirective.contains(None.INSTANCE))) {
                    testResult.setResultEnum(ResultEnum.failed);
                    testResult.setMessage("Looking better, but your default-src directive has no or 'none' value, effectivly blocking loading of sources from your own site!");
                }else if (csp.allowsScriptFromSource(URI.parse("http://evil.doers.com"))) {
                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("Looking better, but the policy allows script from external sources like http://evil.doers.com!");
                } else if (csp.allowsUnsafeInlineScript()) {
                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("Looking better, the header is present and you have a default directive, but the policy allows unsafe inline scripts!");
                } else if (csp.allowsUnsafeInlineStyle()) {
                    testResult.setResultEnum(ResultEnum.partial);
                    testResult.setMessage("Looking better, the header is present and you have a default diretive, but the policy allows unsafe inline styles!");
                } else {
                    testResult.setResultEnum(ResultEnum.passed);
                    testResult.setMessage("Great work, no unsafe inlines, and a good default policy!");
                }
            }
        } finally {
            setDuration(testResult, startTime);
        }

        return testResult;
    }
}
