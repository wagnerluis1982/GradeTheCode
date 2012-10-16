package org.gtc.test;

import java.util.Hashtable;
import java.util.Map;

public class TestResult {

	private String name;
	private Map<String, Boolean> results;
	private long elapsedTime;

	public void setName(String name) {
		this.name = name;
		this.results = new Hashtable<String, Boolean>();
	}

	public void setElapsedTime(long nanos) {
		this.elapsedTime = nanos;
	}

	public void addResult(String methodName, boolean passed) {
		this.results.put(methodName, passed);
	}

	public String getName() {
		return name;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public Map<String, Boolean> getResults() {
		return results;
	}

}
