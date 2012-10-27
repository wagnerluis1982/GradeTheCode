package org.gtc.compiler;

/**
 * Exception throwed when occur a method call error
 *
 * @author Wagner Macedo
 */
public class CallMethodException extends Exception {

	public CallMethodException(String message) {
		super(message);
	}

	public CallMethodException(String message, Throwable e) {
		super(message, e);
	}

}
