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
		add(new SiteExistsTest());
		add(new SiteWorksTest());
		add(new OutputEncoding());
		add(new BasicXSRFTest());
		add(new XSSTest());
		add(new FailureToRestrictUrlTest());
		add(new SessionXSRFTest());
		add(new InputValidationTest());
		// This test is currently excluded, as it has no theoretical basis. Should you find a link backing this test, please feel free to re-add it
		// add(new DirectAdminLinkTest());

		add(new ClickjackingTest());
		add(new APITest());
		add(new InsecureDirectObjectReferenceTest());
		add(new InvalidatedRedirectTest());
		add(new ImproperErrorHandlingTest());
        add(new KnownVulnerableComponentsTest());
        add(new BackdoorTest());
		add(new InsecureCryptographicStorageTest());
		// Test only works with a server with JVM > 6:
		if (getMajorJVMVersion() > 6) add(new SSLProtocolTest());
		add(new SSLCipherSuiteTest());
	}

	private static void add(AbstractTest test) {
		tests.put(test.getIdentifikator(), test);
	}

	public static Map<String, AbstractTest> getTests() {
		return tests;
	}

	public static int getMajorJVMVersion() {
		String[] s = {
				"java.lang.Object",
				"java.rmi.Remote",
				"java.util.List",
				"java.lang.reflect.Proxy",
				"java.nio.Buffer",
				"java.lang.Enum",
				"java.util.Deque",
				"java.util.Objects"
		};

		for (int i = s.length - 1; i >= 0; i--) {
			try {
				Class cls = Class.forName(s[i]);
				return i;
			} catch (ClassNotFoundException e) {
				// ignore
			}
		}
		return 0;
	}
}
