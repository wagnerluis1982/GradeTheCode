package org.gtc.test;

public class TestMethodResult {

	private TestStatus status;
	private Long elapsedTime;

	public TestMethodResult(TestStatus status, Long elapsedTime) {
		this.status = status;
		this.elapsedTime = elapsedTime;
	}

	public void setStatus(TestStatus status) {
		this.status = status;
	}

	public TestStatus getStatus() {
		return this.status;
	}

	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Long getElapsedTime() {
		return elapsedTime;
	}

}
