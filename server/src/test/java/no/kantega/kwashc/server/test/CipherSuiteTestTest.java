package no.kantega.kwashc.server.test;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CipherSuiteTestTest {

    @Test
    public void name() throws Exception {

        CipherSuiteTest cipherSuiteTest = new CipherSuiteTest();
        assertThat("", "SSL/TLS Connection Cipher Strength", is(cipherSuiteTest.getName()));
    }
}