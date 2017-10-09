package no.kantega.kwashc.server.test;

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;

/**
 * SQL Injection test
 *
 * <p>Attempt logging in with a SQL injection. If successful, we will short-circuit
 * the SQL query by introducing an <tt>OR true</tt> condition.</p>
 */
public class SQLInjectionTest extends AbstractTest {

    @Override
    public String getName() {
        return "SQL injection";
    }

    @Override
    public String getDescription() {
        return "<p>SQL injection attacks allow attackers to spoof identity, tamper with existing data, cause repudiation issues such as voiding transactions or changing balances, allow the complete disclosure of all data on the system, destroy the data or make it otherwise unavailable, and become administrators of the database server.</p>"
                + "<p align=\"center\"><img src=\"http://www.securityidiots.com/post_images/sqli_everywhere.png\"/></p>";
    }

    @Override
    public String getInformationURL() {
        return "http://www.unixwiz.net/techtips/sql-injection.html";
    }

    @Override
    public String getExploit(final Site site) {
        if (site == null) {
            return null;
        }
        return "<p>Someone can bypass your login check simply by using a password such as &quot;<tt>FOOBAR' OR 'a'='a</tt>&quot; "
                + "Not because it is the correct password, but because it alters the meaning "
                + "of the SQL-statement we use to check the credentials.</p>"
                + "<p>Anyone can run SQL statements from your login form. Oh no, what else can they do to your blog?</p>";
    }

    @Override
    public String getHint() {
        return "<p>We talk to the database using SQL. When we create our own SQL strings, we will need to sanitize all "
                + "input-parameters. This is hard work, often described as tedious, tiresome, dull, or uncool. "
                + "Also, it's very hard to get right.</p>" + "<p>Instead, put your SQL query in a PreparedStatement and "
                + "substitute input-parameters with <tt>?</tt>-placeholders.</p>" + "<pre>SELECT * FROM users WHERE "
                + "userid=? AND password=?</pre>" + "<p>Then call the <tt>PreparedStatement#setString</tt>-method (or "
                + "<tt>setInteger</tt> or <tt>setWhatever</tt>) to set your parameter.</p>" + "<p>No string concatenation, "
                + "no double- single-quote mixups. And no SQL injection.</p>";
    }

    @Override
    protected TestResult testSite(final Site site, final TestResult testResult) throws Throwable {
        long startTime = System.nanoTime();

        String responseBody;

        WebTester tester = new WebTester();
        tester.setIgnoreFailingStatusCodes(true);
        tester.beginAt(site.getAddress());
        tester.clickLinkWithExactText("Admin");
        tester.setTextField("username", "username");
        tester.setTextField("password", "FOOBAR' OR 'a'='a");
        tester.clickButton("formSubmitButton");

        responseBody = tester.getPageSource();

        if (responseBody.contains("You asked for a protected resource. Please log in:")) {
            testResult.setResultEnum(ResultEnum.passed);
            testResult.setMessage("SQL injection no longer work! Good job!");
        } else {
            testResult.setResultEnum(ResultEnum.failed);
            testResult.setMessage("Someone can steal you users and passwords");
        }
        setDuration(testResult, startTime);
        return testResult;

    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.injection;
    }
}
