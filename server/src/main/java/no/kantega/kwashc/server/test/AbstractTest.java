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

import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;


/**
 * All tests should extend this
 *
 * @author Anders Båtstrand, (www.kantega.no)
 */
public abstract class AbstractTest {

	protected final Logger logger = Logger.getLogger(getClass());

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getExploit(Site site);

	public abstract String getHint();

	public abstract TestCategory getTestCategory();

	protected final static String DESCRIPTION_XSS = "Cross Site Scripting (XSS) vulnerabilities allows an attacker to" +
			" inject malicious scripts into your web site, which are then executed by the victim's browser as if they " +
			"were part of the part of the page you intended. This happens when unvalidated user input is included in the" +
			" html without proper escaping, or when it used in certain dangerous JavaScript functions. XSS currently " +
			"ranks #3 in the OWASP Top 10 application security risks";

	protected final static String DESCRIPTION_SECURITY_MISCONFIGURATION = "Security misconfiguration covers all " +
			"security issues arising from improper configuration and setup of the application, frameworks, application" +
			" server, web server, database server, or platform. Security misconfiguration currently ranks #5 in the " +
			"OWASP Top 10 application security risks. ";

	protected static final String DESCRIPTION_SECURE_COMMUNICATION = "Communication between a client and a server " +
			"utilities the HTTP protocol. To add security to the HTTP protocol the SSL (Secure Sockets Layer) was " +
			"introduced, aka. as HTTPS. In later revisions the protocol has changed name to TLS (Transport Layer Security).";

	/**
	 * Give some information on the threat behind the text. Might be null
	 *
	 * @return some information on the threat behind the text. Might be null
	 */
	public abstract String getInformationURL();

	public TestResult testSite(Site site) {
		TestResult result = new TestResult(this);
		result.setSite(site);
		try {
			result = testSite(site, result);
		} catch (Throwable e) {
			logger.info("Error during test.", e);
			result.setResultEnum(ResultEnum.failed);
			result.setMessage("Error: " + e.getMessage());
		}
		return result;
	}

	public String getIdentifikator() {
		return getClass().getSimpleName();
	}

	protected abstract TestResult testSite(final Site site, final TestResult testResult) throws Throwable;

	protected void setDuration(TestResult testResult, long startTime) {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		final long sec = TimeUnit.NANOSECONDS.toSeconds(duration);
		final long ms = TimeUnit.NANOSECONDS.toMillis(duration - TimeUnit.SECONDS.toMillis(sec));
		testResult.setDuration(String.format("%01d.%03d", sec, ms));
	}

	protected void appendDuration(TestResult testResult, long startTime) {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		final long sec = TimeUnit.NANOSECONDS.toSeconds(duration);
		final long ms = TimeUnit.NANOSECONDS.toMillis(duration - TimeUnit.SECONDS.toMillis(sec));

		if(testResult.getDuration() == null){
			testResult.setDuration(String.format(" %01d.%03d", sec, ms));
		} else {
			testResult.setDuration(testResult.getDuration() + String.format(" %01d.%03d", sec, ms));

		}
	}

	protected String getBaseUrl(Site site) {
		if(site != null && site.getAddress() != null && !site.getAddress().isEmpty()){
			return site.getAddress();
		} else {
			return "http://localhost:8080/"; //fallback to default
		}
	}

}
