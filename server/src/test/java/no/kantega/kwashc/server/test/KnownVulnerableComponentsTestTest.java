package no.kantega.kwashc.server.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Jon Are Rakvaag (Politiets IKT-tjenester)
 */
public class KnownVulnerableComponentsTestTest {

    KnownVulnerableComponentsTest knownVulnerableComponentsTest = new KnownVulnerableComponentsTest();

    @Test
    public void testValidVersions() throws Exception {
        String[] valid = {"1.6.3", "2.0.0", "2", "2.0-alpha"};

        for (String string : valid) {
            assertTrue("jQuery version " + string + " should be approved",
                    knownVulnerableComponentsTest.isApprovedVersion(string));
        }
    }

    @Test
    public void testInvalidVersions() throws Exception {
        String[] invalid = {"1.6.1", "1"};

        for (String string : invalid) {
            assertFalse("jQuery version " + string + " should not be approved", knownVulnerableComponentsTest
                    .isApprovedVersion(string));
        }
    }

    @Test(expected = NumberFormatException.class)
    public void testUnexpectedVersion() throws Exception {
        knownVulnerableComponentsTest.isApprovedVersion("bob");
    }
}