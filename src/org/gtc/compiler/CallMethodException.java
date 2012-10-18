package org.gtc.compiler;

public class CallMethodException extends Exception {

	public CallMethodException(String message) {
		super(message);
	}

	public CallMethodException(String message, Throwable e) {
		super(message, e);
	}

}
