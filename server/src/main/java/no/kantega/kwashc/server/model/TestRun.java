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

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

/**
 * All test runs are saved here, for statistics. No connections to other tables, to allow deletion there.
 *
 * @author Anders B�strand, (www.kantega.no)
 */
@Entity
public class TestRun extends AbstractPersistable<Long> {

	@Basic
	private String site;

	@Basic
	private String testIdentifikator;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timeRun;

	@Enumerated(EnumType.STRING)
	private ResultEnum resultEnum;

	@Basic
	private String message;

	public TestRun(TestResult testResult) {
		this.testIdentifikator = testResult.getTest().getIdentifikator();
		this.site = testResult.getSite().getName();
		this.timeRun = new Date();
		this.resultEnum = testResult.getResultEnum();
		this.message = testResult.getMessage();
	}

	protected TestRun() {
		// for hibernate
	}

	public String getSite() {
		return site;
	}

	public String getTestIdentifikator() {
		return testIdentifikator;
	}

	public Date getTimeRun() {
		return timeRun;
	}

	public ResultEnum getResultEnum() {
		return resultEnum;
	}

	public String getMessage() {
		return message;
	}
}
