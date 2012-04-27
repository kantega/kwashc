/*
 * Copyright 2012 Kantega AS
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
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * test if SSL based communication allows insecure ciphers..
 *
 * Solution:
 *
 * Exclude the following ciphers
 *
 * <excludeCipherSuites>
 *     <excludeCipherSuite>SSL_RSA_WITH_3DES_EDE_CBC_SHA</excludeCipherSuite>
 *     <excludeCipherSuite>SSL_DHE_RSA_WITH_DES_CBC_SHA</excludeCipherSuite>
 *     <excludeCipherSuite>SSL_DHE_DSS_WITH_DES_CBC_SHA</excludeCipherSuite>
 * </excludeCipherSuites>
 *
 * Alternate solution is to only include the ones needed. This might be a very restrictive solution, as you might
 * exclude accessfor a lot of clients. The following list solves the test (from http://www.mozilla.org/projects/security/pki/nss/ssl/fips-ssl-ciphersuites.html):
 *
 * <includeCipherSuites>
 * <includeCipherSuite>TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_DHE_DSS_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_DHE_DSS_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_DHE_RSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_DHE_RSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_RSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_RSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_RSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_RSA_WITH_AES_128_CBC_SHA</includeCipherSuite>
 * <includeCipherSuite>TLS_ECDH_RSA_WITH_AES_256_CBC_SHA</includeCipherSuite>
 * </includeCipherSuites>
 *
 * Referanses:
 *
 * http://wiki.eclipse.org/Jetty/Howto/CipherSuites
 * http://www.techstacks.com/howto/j2se5_ssl_cipher_strength.html
 * http://cephas.net/blog/2007/10/02/using-a-custom-socket-factory-with-httpclient/
 *
 * @author Espen A. Fossen, (www.kantega.no)
 *
 */
public class SSLCipherSuiteTest extends AbstractTest {

    @Override
    public String getName() {
        return "SSL Connection Cipher Strength Test";
    }

    @Override
    public String getDescription() {
        return "Test if a weak Cipher is  allowed for an SSL connection.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Transport_Layer_Protection_Cheat_Sheet";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult) throws Throwable {

        int httpsPort;
        try{
            httpsPort = new Integer(site.getSecureport());
        } catch(NumberFormatException e) {
            testResult.setPassed(false);
            testResult.setMessage("No HTTPS port specified, test not run!");
            return testResult;
        }

        HttpClient httpclient = HttpClientUtil.getHttpClient();

        try {

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {allowAllTrustManager}, null);

            SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);

            SSLSocket socket = (SSLSocket) sf.createSocket(params);
            socket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA"});

            URL url = new URL(site.getAddress());

            InetSocketAddress address = new InetSocketAddress(url.getHost(), httpsPort);
            sf.connectSocket(socket, address, null, params);

            Scheme sch = new Scheme("https", httpsPort, sf);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);

            HttpGet request = new HttpGet("https://" + url.getHost() + ":"+site.getSecureport() +url.getPath() + "blog");

            HttpResponse response = httpclient.execute(request);

            if(response.getStatusLine().getStatusCode() == 200){
                testResult.setPassed(false);
                testResult.setMessage("Your application accepts weak SSL cipher suites!");
            }

        } catch (NoSuchAlgorithmException e) {
            testResult.setPassed(false);
            testResult.setMessage("Cipher suites used for connection not available in local environment!");
            return testResult;
        } catch (KeyManagementException e) {
            testResult.setPassed(false);
            testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
            return testResult;
        } catch (IOException e) {
            if(e.getMessage().contains("peer not authenticated")){
                testResult.setPassed(true);
                testResult.setMessage("Ok, application does not allow SSL connection with weak ciphers.");
                return testResult;
            }else{
                testResult.setPassed(false);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return testResult;
    }

    TrustManager allowAllTrustManager = new X509TrustManager() {


        public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    };

}
