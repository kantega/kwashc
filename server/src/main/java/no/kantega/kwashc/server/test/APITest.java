package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

import java.util.UUID;

/**
 * Tests if the RESTful API at /blog/api/comments/list leaks passwords, or if the JSON is served with the incorrect
 * Content-type: text/html. This would allow XSS on some browsers, as the JSON is parsed as HTML.
 * <p/>
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

        return DESCRIPTION_SECURITY_MISCONFIGURATION + "<br><br>The blog exposes the comments as a REST service at " +
                "blog/api/comments/list/. This is done by the Jackson framework in CommentsAPIService, which by magic" +
                " (and insecure defaults) serializes everything it can get its hands on.";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/REST_Security_Cheat_Sheet";
    }

    @Override
    public String getExploit(Site site) {
        return "The REST service has two major misconfiguration issues: <br><ol><li>Information leakage: See the raw " +
                "JSON response from " + getHref(site) + ", and look for information an attacker could be interested in" +
                ".<br><li> The Content-type header is incorrectly set to text/html. Some browsers will try to parse " +
                "this a html file. This could be a XSS vulnerability. Try entering the comment <i>&lt;img src=x " +
                "onerror=alert(2)&gt;</i>, and open " + getHref(site) + " in Internet Explorer. You will " +
                "need to restart your server afterwards, to clear the comment from the DB, as this payload might " +
                "cause other tests to fail.</ol>";
    }

    @Override
    public String getHint() {
        return "<ol><li>The Comment and subsequently the User objects are serialized to JSON by Jackson in " +
                "CommentsAPIService. You could use the <i>@JsonIgnore</i> annotation to make Jackson skip sensitive " +
                "variables." +
                "<li>CommentsAPIService forces Content-Type. The correct type for JSON is 'application/json'." +
                "</ol>";
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
        tester.assertTextPresent("\"comment\":\"" + commentTest + "\"");

        String source = tester.getPageSource();

        boolean containsSensitiveInfo = source.contains(sensitiveInfo);
        boolean isHtmlContentType = tester.getHeader("Content-type").contains("html");

        if (containsSensitiveInfo) {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("The RESTFul API leaks very sensitive info!");

        } else if (isHtmlContentType) {
            testResult.setResultEnum(ResultEnum.partial);
            testResult.setMessage("Good! It looks like your API doesn't leak passwords or password hashes anymore. " +
                    "But the http response has Content-Type: text/html. Some browsers will parse the JSON response as" +
                    "HTML. This allows for stored XSS if a victim is fooled to visit " + getHref(site) + " directly.");
        } else {
            testResult.setResultEnum(ResultEnum.passed);
            testResult.setMessage("No problems found. Good work!");
        }
        setDuration(testResult, startTime);
        return testResult;
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.misconfiguration;
    }

    private String getHref(Site site) {
        String link = "blog/api/comments/list/";
        if(site != null && site.getAddress() != null) {
            link = "<a href='" + site.getAddress() + "blog/api/comments/list/' target='_blank'>blog/api/comments/list/</a>";
        }
        return link;
    }
}
