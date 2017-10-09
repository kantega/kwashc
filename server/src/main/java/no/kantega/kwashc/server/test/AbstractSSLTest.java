package no.kantega.kwashc.server.test;

import no.kantega.kwashc.server.model.Site;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractSSLTest extends AbstractTest {

    protected int checkClient(Site site, int httpsPort, String[] protocols, String[] ciphers) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLContext sslContext = builder.build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, protocols, ciphers, new NoopHostnameVerifier());
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(1000)
                .build();

        URL url = new URL(site.getAddress());
        HttpHost target = new HttpHost(url.getHost(), httpsPort, "https");
        HttpGet request = new HttpGet("https://" + url.getHost() + ":" + site.getSecureport() + url.getPath() + "blog");
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultRequestConfig(config).build();
            CloseableHttpResponse execute = client.execute(target, request);
            StatusLine statusLine = execute.getStatusLine();
            return statusLine.getStatusCode();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public TestCategory getTestCategory() {
        return TestCategory.crypto;
    }



}