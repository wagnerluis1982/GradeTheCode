package org.gtc.test;

public class TestMethodResult {

	private boolean status;
	private long elapsedTime;

	public TestMethodResult(boolean status, long elapsedTime) {
		this.status = status;
		this.elapsedTime = elapsedTime;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean getStatus() {
		return this.status;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

}
