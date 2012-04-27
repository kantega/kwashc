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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

public class HttpClientUtil {

    static String getPageText(String address) throws IOException {
        HttpClient httpclient = getHttpClient();
        HttpGet request = new HttpGet(address);
        return httpclient.execute(request, new BasicResponseHandler());
    }

	static HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
		// timeout: 5 seconds
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5 * 1000);
		return httpClient;
	}

}
