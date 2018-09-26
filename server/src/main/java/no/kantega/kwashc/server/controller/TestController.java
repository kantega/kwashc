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

package no.kantega.kwashc.server.controller;


import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import no.kantega.kwashc.server.model.TestRun;
import no.kantega.kwashc.server.repository.SiteRepository;
import no.kantega.kwashc.server.repository.TestRunRepository;
import no.kantega.kwashc.server.test.AbstractTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SiteRepository siteRepository;

	@Autowired
	private TestRunRepository testRunRepository;

    public final static boolean SHOW_HINTS = false;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String viewTests(Model model) {
        model.addAttribute("tests", TestRepository.getTests());
        return "test/viewAll";
    }

    @RequestMapping(value = "/{testName}", method = RequestMethod.GET)
    public String viewTests(Model model, @PathVariable String testName) {
        model.addAttribute("test", TestRepository.getTests().get(testName));
        model.addAttribute("showHints", SHOW_HINTS);
        return "test/viewTest";
    }

    @RequestMapping("/site/{siteId}/executeAll")
    public String executeTests(@PathVariable Long siteId) {
        Site site = siteRepository.findById(siteId).orElse(null);

        Map<String, AbstractTest> tests = TestRepository.getTests();

        List<TestResult> results = site.getTestResults();

        // remove old tests
        results.clear();

        for (AbstractTest test : tests.values()) {
	        TestResult testResult = test.testSite(site);
	        results.add(testResult);

	        // save the run:
	        TestRun testRun = new TestRun(testResult);
	        testRunRepository.save(testRun);
        }

        site.setTestResults(results);
        siteRepository.save(site);

        return "redirect:/site/" + siteId + "/";
    }

	@RequestMapping(value = "/site/{siteId}/execute={testName}", method = RequestMethod.GET)
	public String executeTest(Model model, @PathVariable Long siteId, @PathVariable String testName) {
        Site site = siteRepository.findById(siteId).orElse(null);

		AbstractTest test = TestRepository.getTests().get(testName);
		TestResult result = test.testSite(site);

		// save the run:
		TestRun testRun = new TestRun(result);
		testRunRepository.save(testRun);

		model.addAttribute("test", test);
		model.addAttribute("site", site);
		model.addAttribute("result", result);
        model.addAttribute("showHints", SHOW_HINTS);

		return "test/viewTest";
	}

}
