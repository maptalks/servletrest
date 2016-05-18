package org.maptalks.servletrest.config.exceptions;

public class InvalidURLPatternException extends RuntimeException {
	public InvalidURLPatternException() {
		super();
	}

	public InvalidURLPatternException(final String msg) {
		super(msg);
	}
}