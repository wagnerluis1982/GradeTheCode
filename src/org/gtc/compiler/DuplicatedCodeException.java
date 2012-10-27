package org.gtc.compiler;

/**
 * Unchecked exception throwed when adding SourceCode objects in a Compiler set
 *
 * @author Wagner Macedo
 */
public class DuplicatedCodeException extends RuntimeException {

	public DuplicatedCodeException(String message) {
		super(message);
	}

}
