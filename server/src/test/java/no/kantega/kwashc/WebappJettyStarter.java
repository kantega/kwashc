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

package no.kantega.kwashc;

import no.kantega.kwashc.server.model.Site;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * To start the webapp
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
public class WebappJettyStarter {

	public static String warFolder = "src/main/webapp";
	public static String contextpath = "/";

	public static Site start() throws Exception {
		Server server = new Server();
		File baseDir = findWebappBaseDir();
		File warFile = new File(baseDir, warFolder);

		ServerConnector connector = new ServerConnector(server);

		HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(findServerBaseDir().getAbsolutePath() + "/src/test/resources/jetty-ssl.keystore");
		sslContextFactory.setKeyStorePassword("owaspJetty");
		sslContextFactory.setCertAlias("owaspJetty");
		sslContextFactory.setKeyStoreType("JKS");

		ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
		server.setConnectors(new Connector[]{connector, sslConnector});

		WebAppContext webAppContext = new WebAppContext(warFile.getAbsolutePath(), contextpath);
		webAppContext.setTempDirectory(FileUtils.getTempDirectory());
		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[]{webAppContext});
		server.setHandler(handlerList);
		server.start();

		Site site = new Site();
		site.setAddress("http://localhost:" + connector.getLocalPort() + "/");
		site.setSecureport(Integer.toString(sslConnector.getLocalPort()));
		site.setSecret("insert-your-secret-here");
		site.setName("One blog to rule them all");
		site.setOwner("Bruce Schneier");

		System.out.println("Started site: " + site);

		return site;
	}

	public static void main(String[] args) throws Exception {
		WebappJettyStarter.start();
	}

	private static File findWebappBaseDir() {
		File baseDir = new File("webapp");
		if (!baseDir.exists()) {
			baseDir = new File("../webapp");
		}
		return baseDir;
	}

	private static File findServerBaseDir() {
		File baseDir = new File("server");
		if (!baseDir.exists()) {
			baseDir = new File(".");
		}
		return baseDir;
	}
}
