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

package no.kantega.kwashc.server.util;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class CipherAndProcotolTest {

    public static void main(String[] args) throws Exception {
        // Get the SSLServerSocket
        SSLServerSocketFactory ssl;
        SSLServerSocket sslServerSocket;
        ssl = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        sslServerSocket = (SSLServerSocket) ssl.createServerSocket();

        // Get the list of all supported cipher suites.
        String[] cipherSuites = sslServerSocket.getSupportedCipherSuites();
        for (String suite : cipherSuites)
            System.out.println(suite);

        // Get the list of all supported protocols.
        String[] protocols = sslServerSocket.getSupportedProtocols();
        for (String protocol : protocols)
            System.out.println(protocol);

    }

}
