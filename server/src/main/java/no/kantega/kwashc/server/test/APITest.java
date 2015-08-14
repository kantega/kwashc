package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

import java.util.UUID;

/**
 * Tests if the RESTful API at /blog/api/comments/list leaks passwords, or if the JSON is served with the incorrect
 * Content-type: text/html. This would allow XSS on some browsers, as the JSON is parsed as HTML.
 *
 * Solution:
 * 1) Remove passwords from the JSON serialization. This should often be done by creating simpler POJO representations
 * of the domain objects, which is then serialized. Here we can add @JsonIgnore to the getPassword() method in User.
 * 2) CommentsAPIService explicitly overrides Content-Type on the response. Fix ("application/json") or remove.
 *
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
class APITest extends AbstractTest {

    @Override
    public String getName() {
        return "API test";
    }

    @Override
    public String getDescription() {
        return "Tests if the RESTful API is securely configured";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/REST_Security_Cheat_Sheet";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();
        String sensitiveInfo = "assword\":\"";
        String apiUrl = site.getAddress() + "blog/api/comments/list/";

        String random = UUID.randomUUID().toString();

        WebTester tester = new WebTester();
        tester.beginAt(site.getAddress());

        tester.setTextField("title", random);
        String commentTest = "RESTAPI";
        tester.setTextField("comment", commentTest);
        tester.clickButton("commentFormSubmit");

        tester.beginAt(apiUrl);
        tester.assertTextPresent("\"comment\":\""+commentTest+"\"");

        String source = tester.getPageSource();

        boolean containsSensitiveInfo = source.contains(sensitiveInfo);
        boolean isHtmlContentType = tester.getHeader("Content-type").contains("html");

        if (containsSensitiveInfo){
            testResult.setPassed(false);
            testResult.setMessage("The RESTFul API leaks very sensitive info! See if you can " +
                    "<a href='" + apiUrl + "' target=\"_blank\">spot it</a>!");

        } else if(isHtmlContentType) {
            testResult.setPassed(false);
            testResult.setMessage("Good! It looks like your API doesn't leak passwords or password hashes anymore. " +
                    "But the http response has Content-Type: text/html. Some browsers will parse the JSON response as " +
                    "HTML. This allows for stored XSS if a victim is fooled to visit the " +
                    "<a href='" + apiUrl + "' target=\"_blank\">API directly</a>.");
        } else {
            testResult.setPassed(true);
            testResult.setMessage("No problems found. Good work!");
        }
        setDuration(testResult, startTime);
        return testResult;
    }

}
