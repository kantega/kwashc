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
import no.kantega.kwashc.server.model.TestRun;
import no.kantega.kwashc.server.repository.SiteRepository;
import no.kantega.kwashc.server.repository.TestRunRepository;
import no.kantega.kwashc.server.test.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FrontPageController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private TestRunRepository testRunRepository;

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
    
    @RequestMapping(value="/needshelp", method = RequestMethod.GET)
    public String showNeedsHelpPage(Model model) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "score"), new Sort.Order(Sort.Direction.ASC, "completed"));
        Iterable<Site> sites = siteRepository.findAll(sort);

        Map<String, Map<String, Integer>> dateMap = new HashMap<String, Map<String, Integer>>();

        for(Site site : sites) {
            dateMap.put(site.getName(), null);
        }
        
        List<TestRun> testRuns = testRunRepository.findAll();

        Date threshold = new Date();
        threshold.setTime(threshold.getTime() - 600000);
        
        Date dateNow = new Date();
        
        for(TestRun testRun : testRuns) {
            String site = testRun.getSite();
            String test = testRun.getTestIdentifikator();
            Date date = testRun.getTimeRun();
            int minutes = (int)((dateNow.getTime() - date.getTime()) / 60000);
            if(testRun.isPassed() && dateMap.containsKey(site)) {
                if(dateMap.get(site) == null && date.before(threshold)) {
                    Map<String, Integer> testMap= new HashMap<String, Integer>();
                    testMap.put(test, minutes);
                    dateMap.put(site, testMap);
                }
                else {
                    if(dateMap.get(site).get(test) == null && date.before(threshold)) {
                        dateMap.get(site).put(test, minutes);
                    }
                    else if(minutes < dateMap.get(site).get(test) && date.before(threshold)) {
                        dateMap.get(site).put(test, minutes);
                    }
                    else {
                        dateMap.get(site).remove(test);
                    }
                }
            }
        }

        model.addAttribute("helpMap", dateMap);

        return "needshelp";
    }
}
