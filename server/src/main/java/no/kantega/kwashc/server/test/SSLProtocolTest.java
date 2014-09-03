/*
 * Copyright 2013 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.kwashc.server.test;

import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Test if the web application servlet container and underlying SSL/TLS Protocol implementation support TLS 1.2.
 * <p/>
 * Solution: Running Jetty 8+ with oracle-jdk 1.7+. Old versions of oracle/sun jdk do not support it.
 * <p/>
 * References:
 * http://en.wikipedia.org/wiki/Transport_Layer_Security#Browser_implementations
 *
 * @author Espen A. Fossen, (www.kantega.no)
 */
public class SSLProtocolTest extends AbstractTest {

    @Override
    public String getName() {
        return "Secure SSL/TLS Protocol Test";
    }

    @Override
    public String getDescription() {
        return "Test if secure SSL/TLS Protocols are supported.";
    }

    @Override
    public String getInformationURL() {
        return "https://www.owasp.org/index.php/Transport_Layer_Protection_Cheat_Sheet";
    }

    @Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {

        int httpsPort;
        try {
            httpsPort = new Integer(site.getSecureport());
        } catch (NumberFormatException e) {
            testResult.setPassed(false);
            testResult.setMessage("No HTTPS port specified, test not run!");
            return testResult;
        }

        HttpClient httpclient = HttpClientUtil.getHttpClient();

        try {

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{allowAllTrustManager}, null);

            SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SSLSocket socket = (SSLSocket) sf.createSocket();
            socket.setEnabledProtocols(new String[]{"TLSv1.2"});

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);

            URL url = new URL(site.getAddress());

            InetSocketAddress address = new InetSocketAddress(url.getHost(), httpsPort);
            sf.connectSocket(socket, address, null, params);

            Scheme sch = new Scheme("https", httpsPort, sf);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);

            HttpGet request = new HttpGet("https://" + url.getHost() + ":" + site.getSecureport() + url.getPath() + "blog");

            HttpResponse response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                testResult.setPassed(true);
                testResult.setMessage("That`s better, you application supports secure SSL/TLS protocol TLSv1.2!");
            }
        } catch (KeyManagementException e) {
            testResult.setPassed(false);
            testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
            return testResult;
        } catch (IOException e) {
            if (e.getMessage().contains("peer not authenticated")) {
                testResult.setPassed(false);
                testResult.setMessage("Your application does not support secure SSL/TLS Protocols!");
                return testResult;
            } else {
                testResult.setPassed(false);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return testResult;
    }

    TrustManager allowAllTrustManager = new X509TrustManager() {


        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };
}
