package org.gtc.compiler;

/**
 * Exception for when no source codes was added at compilation job
 *
 * @author Wagner Macedo
 */
public class NoSourceCodesException extends RuntimeException {

	public NoSourceCodesException(String message) {
		super(message);
	}

}
