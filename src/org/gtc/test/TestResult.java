package org.gtc.test;

import java.util.Hashtable;
import java.util.Map;

public class TestResult {

	private String name;
	private Map<String, TestMethodResult> methodResults;
	private long elapsedTime;

	public void setName(String name) {
		this.name = name;
		this.methodResults = new Hashtable<String, TestMethodResult>();
	}

	public void setElapsedTime(long nanos) {
		this.elapsedTime = nanos;
	}

	public void addMethodResult(String methodName, TestMethodResult methodResult) {
		this.methodResults.put(methodName, methodResult);
	}

	public String getName() {
		return name;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public Map<String, TestMethodResult> getMethodResults() {
		return methodResults;
	}

}
