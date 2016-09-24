package no.kantega.kwashc.server.test;

/**
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
public enum TestCategory {
    happyDay("Happy day"), xss("Cross Site Scripting (XSS)"), csrf("Cross Site Request Forgery (CSRF)"),
    misconfiguration("Misconfiguration"), securityFeature("Security feature"), assorted("Assorted"),
    crypto("Cryptography"), injection("Injection");

    private final String name;

    TestCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
