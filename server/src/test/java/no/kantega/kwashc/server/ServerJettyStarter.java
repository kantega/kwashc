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

package no.kantega.kwashc.server;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * To start the webapp
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
public class ServerJettyStarter {

	public static String warFolder = "src/main/webapp";
	public static String contextpath = "/";

	private static WebAppContext webAppContext;

	// returns port
	public static int start() throws Exception {
		Server server = new Server();
		File baseDir = findServerBaseDir();
		File warFile = new File(baseDir, warFolder);

		SelectChannelConnector selectChannelConnector = new SelectChannelConnector();

		server.setConnectors(new Connector[]{selectChannelConnector});

		webAppContext = new WebAppContext(warFile.getAbsolutePath(), contextpath);
		webAppContext.setTempDirectory(FileUtils.getTempDirectory());
		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[]{webAppContext});
		server.setHandler(handlerList);
		server.start();

		int localPort = selectChannelConnector.getLocalPort();
		System.out.println("Server started on port: " + localPort);
		return localPort;
	}

	public static void main(String[] args) throws Exception {
		ServerJettyStarter.start();
	}

	public static <T> T getSpringBean(Class<T> beanClass) {
		ServletContext servletContext = webAppContext.getServletHandler().getServletContext();
		WebApplicationContext springWebAppContext =
				WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return springWebAppContext.getBean(beanClass);
	}

	private static File findServerBaseDir() {
		File baseDir = new File("server");
		if (!baseDir.exists()) {
			baseDir = new File(".");
		}
		return baseDir;
	}
}
