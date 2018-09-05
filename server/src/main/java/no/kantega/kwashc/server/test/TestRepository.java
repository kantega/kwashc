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

import java.util.LinkedHashMap;
import java.util.Map;

public final class TestRepository {

	private static final Map<String, AbstractTest> tests = new LinkedHashMap<String, AbstractTest>();

	static {
		//happy day tests
		add(new SiteExistsTest());
		add(new SiteWorksTest());

		//XSS
		add(new OutputEncodingTest());
		add(new DOMXSSTest());

		//CSRF
		add(new CSRFTest());

		//Security features
		add(new InputValidationTest());
		//add(new InsecureDirectObjectReferenceTest()); //currently identical to failure to restrict URL. Must be
		// rewritten to make sense. E.g. ownership to posts, and authorization for editing them.
		add(new BackdoorTest());
		add(new UnvalidatedRedirectTest());

		//Misconfiguration
		add(new APITest());
		add(new FailureToRestrictUrlTest());
		add(new ImproperErrorHandlingTest());

		//Assorted
		add(new ContentSecurityPolicyTest());
		add(new ClickjackingTest());
		add(new KnownVulnerableComponentsTest());

		//Crypto
		add(new InsecureCryptographicStorageTest());
		add(new CipherSuiteTest());

		// Injection, not finished
		//add(new SQLInjectionTest());
	}

	private static void add(AbstractTest test) {
		tests.put(test.getIdentifikator(), test);
	}

	public static Map<String, AbstractTest> getTests() {
		return tests;
	}

}
