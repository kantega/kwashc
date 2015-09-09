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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Site extends AbstractPersistable<Long> implements Comparable<Site> {

    @Size(min = 3, max = 25)
    @Column(unique = true)
    private String name;

    @Size(min = 4, max = 25)
    private String owner;

    @Size(min = 5, max = 50)
    @Column(nullable = false)
    @Pattern(regexp = ".*/")
    private String address;

	@Past
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(nullable = true)
	private Date completed;

    @Past
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date lastPassed;

	@Digits(integer = 6, fraction = 0)
    private String secureport = "8443";

    @Column(nullable = false)
    private String secret;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "site", orphanRemoval = true)
    private List<TestResult> testResults = new ArrayList<TestResult>();

    /**
     * Number of passed tests
     */
    private int score = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<TestResult> getTestResults() {
	    if (testResults == null) {
		    testResults = new ArrayList<TestResult>(0);
	    }
        return testResults;
    }

	public Date getCompleted() {
		return completed;
	}

	public void setCompleted(Date completed) {
		this.completed = completed;
	}

	public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
        score = 0;
        for (TestResult test : testResults) {
	        // just to avoid NPE when tests fail horribly:
	        if (test != null && test.getResultEnum() == ResultEnum.passed) {
		        score++;
	        }
        }
		if (completed == null && score == testResults.size()) {
			completed = new Date();
		} else if (score < testResults.size()) {
			completed = null;
		}
	}

	public int getScore() {
		return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSecureport() {
        return secureport;
    }

    public void setSecureport(String secureport) {
        this.secureport = secureport;
    }

    public Date getLastPassed() {
        return lastPassed;
    }

    public void setLastPassed(Date lastPassed) {
        this.lastPassed = lastPassed;
    }

	/**
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
	 *
	 * @throws ClassCastException if the specified object's type prevents it
	 *         from being compared to this object.
	 */
	public int compareTo(Site o) {
		if (o == null) return 1;
		if (score > o.getScore()) return 1;
		else if (score < o.getScore()) return -1;
		else {
			if (completed.before(o.getCompleted())) return 1;
			if (completed.after(o.getCompleted())) return -1;
			else return 0;
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
				append("name", name).
				append("owner", owner).
				append("address", address).
				append("completed", completed).
				append("secureport", secureport).
				append("secret", secret).
				append("testResults", testResults).
				append("score", score).
				toString();
	}
}
