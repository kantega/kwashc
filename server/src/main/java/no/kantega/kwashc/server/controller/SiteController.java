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

import net.sourceforge.jwebunit.junit.WebTester;
import no.kantega.kwashc.server.model.Site;
import no.kantega.kwashc.server.model.TestResult;
import no.kantega.kwashc.server.repository.SiteRepository;
import no.kantega.kwashc.server.test.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.UUID;

@Controller
@RequestMapping("/site")
@SessionAttributes(types = Site.class)
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

	/**
	 * During internal tests, the site secret must be controlled. Set this
	 * value, and that will be used instead of a random string
	 */
	private static String siteSecret;

	public static void setSiteSecret(final String siteSecret) {
		SiteController.siteSecret = siteSecret;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveSite(@Valid Site site, BindingResult result, Model model) {

        validateSite(site, result);

        if (result.hasErrors()) {
	        System.out.print("Site rejected due to validation errors: " + result.getAllErrors());
            model.addAttribute("site", site);
            return "site/editSite";
        }

        site = siteRepository.save(site);
        return "redirect:/site/" + site.getId() + "/";
    }

	@RequestMapping(value = "/{id}/clickjacking")
	public String viewClickJacking(@PathVariable Long id, Model model) {
        Site site = siteRepository.findById(id).orElse(null);
        model.addAttribute("site", site);
		return "site/clickJacking";
	}

	@RequestMapping(value = "/{id}/delete")
	public String deleteSite(@PathVariable Long id) {
		siteRepository.deleteById(id);
		return "redirect:/site/";
	}

	@RequestMapping(value = "/{id}/resetTests")
	public String resetTests(@PathVariable Long id) {
		Site site = siteRepository.findById(id).orElse(null);
		site.setTestResults(Collections.<TestResult>emptyList());
        siteRepository.save(site);
        return "redirect:/site/" + site.getId() + "/";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String registerSite(Model model, HttpServletRequest request) {
	    Site site = new Site();
	    site.setSecret(createNewSiteSecret());
	    site.setAddress("http://" + request.getRemoteAddr() + ":8080/");
	    model.addAttribute("site", site);
	    return "site/editSite";
    }

	@RequestMapping(value = "/{id}/")
    public String view(@PathVariable Long id, Model model) {
        Site site = siteRepository.findById(id).orElse(null);
        model.addAttribute("site", site);
		model.addAttribute("numberOfTests", TestRepository.getTests().size());
        return "site/viewSite";
    }

    @RequestMapping(value = "/")
    public String view(Model model) {
        model.addAttribute("sites", siteRepository.findAll());
		model.addAttribute("numberOfTests", TestRepository.getTests().size());
        return "site/viewAll";
    }

    @RequestMapping(value = "/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Site site = siteRepository.findById(id).orElse(null);
        model.addAttribute("site", site);
        return "site/editSite";
    }

	private String createNewSiteSecret() {
		if (siteSecret != null) {
			return siteSecret;
		} else {
			return UUID.randomUUID().toString();
		}
	}

	private void validateSite(Site site, BindingResult result) {
		if (site.getAddress() != null) {
            String header = getHeaderValue(site, result);

            if (header == null || header.trim().isEmpty()) {
                result.rejectValue("address", "", "Could not get meta header!");
            } else if (!header.equals(site.getSecret())) {
                result.rejectValue("address", "", "The secret meta header did not match the one found on page!");
            }
        }

        if (site.getId() == null) {
            // new site, check for uniquess
            if (siteRepository.findByName(site.getName()) != null) {
                result.rejectValue("name", "", "Name already taken.");
            }
        }
    }

    private String getHeaderValue(Site site, BindingResult result) {
        String header = "";
        try {
            WebTester tester = new WebTester();
	        tester.beginAt(site.getAddress());
			tester.getTestingEngine().gotoRootWindow();
	        header = tester.getElementAttributeByXPath("/html/head/meta[@name=\"no.kantega.kwashc\"]", "content");
        } catch (Throwable t) {
	        result.rejectValue("address", "", "Exception (" + t.getClass().getSimpleName() + ") getting the header value: " + t.getMessage());
        }
	    return header;
    }

}
