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
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
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

		SelectChannelConnector selectChannelConnector = new SelectChannelConnector();

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStore(findServerBaseDir().getAbsolutePath() + "/src/test/resources/jetty-ssl.keystore");
		sslContextFactory.setKeyStorePassword("owaspJetty");
		sslContextFactory.setCertAlias("owaspJetty");
		sslContextFactory.setKeyStoreType("JKS");
		assert sslContextFactory.checkConfig();

		SslSocketConnector sslSocketConnector = new SslSocketConnector(sslContextFactory);

		server.setConnectors(new Connector[]{selectChannelConnector, sslSocketConnector});

		WebAppContext webAppContext = new WebAppContext(warFile.getAbsolutePath(), contextpath);
		webAppContext.setTempDirectory(FileUtils.getTempDirectory());
		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[]{webAppContext});
		server.setHandler(handlerList);
		server.start();

		Site site = new Site();
		site.setAddress("http://localhost:" + selectChannelConnector.getLocalPort() + "/");
		site.setSecureport(Integer.toString(sslSocketConnector.getLocalPort()));
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
