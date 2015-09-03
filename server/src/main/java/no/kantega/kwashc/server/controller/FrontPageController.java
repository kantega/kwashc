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

package no.kantega.kwashc.server.controller;

import no.kantega.kwashc.server.model.ResultEnum;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestRun;
import no.kantega.kwashc.server.repository.SiteRepository;
import no.kantega.kwashc.server.repository.TestRunRepository;
import no.kantega.kwashc.server.test.AbstractTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class FrontPageController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private TestRunRepository testRunRepository;

    private static Map<String, List<String>> helpMap = new HashMap<String, List<String>>();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showFrontPage(Model model) {
        model.addAttribute("tests", TestRepository.getTests());

	    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "score"), new Sort.Order(Sort.Direction.ASC, "completed"));
        Iterable<Site> sites = siteRepository.findAll(sort);

        model.addAttribute("sites", sites);
		model.addAttribute("numberOfTests", TestRepository.getTests().size());
        return "frontpage";
    }

    @RequestMapping(value = "/display", method = RequestMethod.GET)
    public String showDisplayPage(Model model) {
	    
	    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "score"), new Sort.Order(Sort.Direction.ASC, "completed"));
        Iterable<Site> sites = siteRepository.findAll(sort);

        model.addAttribute("sites", sites);
		model.addAttribute("numberOfTests", TestRepository.getTests().size());

	    // testname -> number of runs
	    Map<String, Integer> testCloud = new HashMap<String, Integer>();

	    // create test cloud
	    List<TestRun> testRuns = testRunRepository.findAll();
	    for (TestRun testRun : testRuns) {
		    String testIdentifikator = testRun.getTestIdentifikator();
		    if (testCloud.containsKey(testIdentifikator)) {
			    testCloud.put(testIdentifikator, testCloud.get(testIdentifikator) + 1);
		    } else {
			    testCloud.put(testIdentifikator, 1);
		    }
	    }
        model.addAttribute("testCloud", testCloud);
        return "display";
    }
    @RequestMapping(value="/gettingstarted", method = RequestMethod.GET)
    public String showGettingStartedPage() {
        return "gettingstarted";
    }

    @RequestMapping(value="/needshelp", method = RequestMethod.GET)
    public String showNeedsHelpPage(Model model) {

        List<TestRun> testRuns = testRunRepository.findAll();

        for(TestRun testRun : testRuns) {
            if(testRun.getResultEnum() == ResultEnum.passed) {
                String testRunSite = testRun.getSite();
                String test = testRun.getTestIdentifikator();
                List<String> remainingTests = helpMap.get(testRunSite);
                if(remainingTests == null) {
                    populateHelpMap(testRunSite);
                }
                else {
                    if(remainingTests.contains(test)) {
                        remainingTests.remove(test);
                        Site site = siteRepository.findByName(testRunSite);
                        site.setLastPassed(testRun.getTimeRun());
                        siteRepository.save(site);
                    }
                }
            }
        }

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "lastPassed"));
        Iterable<Site> sites = siteRepository.findAll(sort);
        model.addAttribute("sites", sites);

        return "needshelp";
    }

    private void populateHelpMap(String site) {
        Map<String, AbstractTest> tests = TestRepository.getTests();

        List<String> siteTests = new ArrayList<String>();
        for(String test : tests.keySet()) {
            siteTests.add(test);
        }
        helpMap.put(site, siteTests);
    }
}
