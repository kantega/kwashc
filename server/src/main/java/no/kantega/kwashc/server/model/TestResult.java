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

package no.kantega.kwashc.server.model;

import no.kantega.kwashc.server.test.AbstractTest;
import no.kantega.kwashc.server.test.TestRepository;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * TODO andbat: Description/responsibilities.
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
@Entity
public class TestResult extends AbstractPersistable<Long> {

	@Basic
    private String testIdentifikator;

	@Basic
    private boolean passed;

	@Basic
    private String message;

    @ManyToOne
    private Site site;

	public TestResult(AbstractTest test) {
		this.testIdentifikator = test.getIdentifikator();
	}

	protected TestResult() {
		// for Hibernate
	}

	public AbstractTest getTest() {
		return TestRepository.getTests().get(testIdentifikator);
	}

	public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
