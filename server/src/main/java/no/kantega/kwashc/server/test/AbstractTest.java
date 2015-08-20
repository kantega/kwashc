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

	public abstract TestCategory getTestCategory();

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
			result.setPassed(false);
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

}
