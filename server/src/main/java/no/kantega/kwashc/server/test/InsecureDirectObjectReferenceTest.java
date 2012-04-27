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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests if an unauthenticated user can edit a blog post, by sending a GET-request with a commentID as a variable.
 *
 * The test checks the following:
 * Whether an unauthenticated user can edit a blog post
 * Whether the parameter, commentID, is properly validated
 *
 * Solution:
 * Add the edit servlet to the login filter
 * Validate the input parameter commentID
 *
 * @author Øystein Øie, (www.kantega.no)
 */

public class InsecureDirectObjectReferenceTest extends AbstractTest {

    @Override
    public String getName(){
        return "Insecure Direct Object Reference";
    }

    @Override
    public String getDescription(){
        return "Testing for Insecure Direct Object Reference by editing a post on the blog. Users should log in to be able to edit posts.";
    }

	@Override
	public String getInformationURL() {
		return "https://www.owasp.org/index.php/Top_10_2010-A4-Insecure_Direct_Object_References";
	}

	@Override
    protected TestResult testSite(Site site, TestResult testResult){

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet getReq = new HttpGet(site.getAddress() + "edit?commentID=0");
            HttpResponse resp = httpclient.execute(getReq);
            int statusCode = resp.getStatusLine().getStatusCode();
            String html = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
            //EntityUtils.consume(resp.getEntity());
            if (statusCode == 200  && html.contains("Post a comment:")) {
                testResult.setPassed(false);
                testResult.setMessage("Unauthorised editing completed ");
            }

            else if (statusCode != 200) {
                testResult.setPassed(false);
                testResult.setMessage("Unauthorised editing is possible, but no matching comment found");
            }
            
            else if (statusCode == 200 && html.contains("j_security_check")) {
                HttpGet getReqAdmin = new HttpGet(site.getAddress() + "j_security_check?username=guest&password=guest");
                resp = httpclient.execute(getReqAdmin);
                getReq.addHeader(resp.getFirstHeader("Set-Cookie"));
                EntityUtils.consume(resp.getEntity());

                getReq.setURI(new URI(site.getAddress() + "edit?commentID=fdsa"));
                resp =httpclient.execute(getReq);
                statusCode = resp.getStatusLine().getStatusCode();
                //html = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");

                if(statusCode == 500) {
                    testResult.setPassed(false);
                    testResult.setMessage("Almost there, but invalid commentID should not generate HTTP 500");
                }
                else {
                    testResult.setPassed(true);
                    testResult.setMessage("Congratulation");
                }
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(URISyntaxException URIse) {
            URIse.printStackTrace();
        }
        finally {
            httpclient.getConnectionManager().shutdown();
        }

        return testResult;

    }
}
