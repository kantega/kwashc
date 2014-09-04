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
 * test if SSL based communication allows insecure and supports the latest Java 8 ciphers.
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
 * The best solution is to only include the ones needed. This might be a very restrictive solution, as you might
 * exclude access for a lot of clients. The following list solves the test (from http://www.mozilla.org/projects/security/pki/nss/ssl/fips-ssl-ciphersuites.html):
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

            String[] ciphers = new String[]{"SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_WITH_NULL_MD5", "SSL_RSA_WITH_NULL_SHA", "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", "TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", "TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", "TLS_DH_anon_WITH_AES_128_GCM_SHA256"};

            HttpResponse response = checkClientForCiphers(site, httpsPort, httpclient, ciphers);

            if(response.getStatusLine().getStatusCode() == 200){
                testResult.setPassed(false);
                testResult.setMessage("Your application accepts weak SSL/TLS cipher suites!");
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

                HttpClient httpclient2 = HttpClientUtil.getHttpClient();
                try {
                    String[] ciphers = new String[]{"TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"};
                    HttpResponse response = checkClientForCiphers(site, httpsPort, httpclient2, ciphers);

                    if(response.getStatusLine().getStatusCode() == 200){

                        HttpClient httpclient3 = HttpClientUtil.getHttpClient();
                        try {
                            String[] ciphers2 = new String[]{"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256"};

                            HttpResponse response2 = checkClientForCiphers(site, httpsPort, httpclient3, ciphers2);

                            if(response2.getStatusLine().getStatusCode() == 200){
                                testResult.setPassed(true);
                                testResult.setMessage("Top score, you do not support weak ciphers and the best available Perfect Forward Secrecy ciphers are present.");
                            } else {
                                testResult.setPassed(false);
                                testResult.setMessage("The server did not respond with an HTTP 200 when it should, contact tutor.");
                            }
                            return testResult;
                        } catch (NoSuchAlgorithmException e1) {
                            testResult.setPassed(false);
                            testResult.setMessage("Cipher suites used for connection not available in local environment!");
                            return testResult;
                        } catch (KeyManagementException e1) {
                            testResult.setPassed(false);
                            testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
                            return testResult;
                        } catch (IOException e1) {
                            testResult.setPassed(false);
                            testResult.setMessage("Almost there, your application does not allow SSL/TLS connection with weak ciphers and allows Perfect Forward Secrecy, but there are still stronger ciphers available!");
                            return testResult;
                        } finally {
                            httpclient3.getConnectionManager().shutdown();
                        }
                    } else {
                        testResult.setPassed(false);
                        testResult.setMessage("The server did not respond with an HTTP 200 when it should, contact tutor.");
                    }
                    return testResult;
                } catch (NoSuchAlgorithmException e1) {
                    testResult.setPassed(false);
                    testResult.setMessage("Cipher suites used for connection not available in local environment!");
                    return testResult;
                } catch (KeyManagementException e1) {
                    testResult.setPassed(false);
                    testResult.setMessage("Certificate configuration does not seem to be correct, check certificate on remote environment!");
                    return testResult;
                } catch (IOException e1) {
                    testResult.setPassed(false);
                    testResult.setMessage("Looking better, your application does not allow SSL/TLS connection with weak ciphers, but does not supportPerfect Forward Secrecy!");
                    return testResult;
                } finally {
                    httpclient2.getConnectionManager().shutdown();
                }

            }else{
                testResult.setPassed(false);
                testResult.setMessage("Actual testing failed, check that the connection is working!");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return testResult;
    }

    private HttpResponse checkClientForCiphers(Site site, int httpsPort, HttpClient httpclient, String[] ciphers) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] {allowAllTrustManager}, null);

        SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);

        SSLSocket socket = (SSLSocket) sf.createSocket(params);
        socket.setEnabledCipherSuites(ciphers);

        URL url = new URL(site.getAddress());

        InetSocketAddress address = new InetSocketAddress(url.getHost(), httpsPort);
        sf.connectSocket(socket, address, null, params);

        Scheme sch = new Scheme("https", httpsPort, sf);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch);

        HttpGet request = new HttpGet("https://" + url.getHost() + ":"+site.getSecureport() +url.getPath() + "blog");

        return httpclient.execute(request);
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
