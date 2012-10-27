package org.gtc.compiler;

/**
 * Exception throwed when an invalid target dir is passed to Compiler
 *
 * A dir could be invalid due to lack of write permissions or doesn't exist.
 *
 * @author Wagner Macedo
 */
public class InvalidTargetException extends CompilerException {

	public InvalidTargetException(String message) {
		super(message);
	}

}
