package org.maptalks.servletrest.config.exceptions;

public class InvalidServletMethodException extends RuntimeException {
	public InvalidServletMethodException(final Exception e) {
		super(e);
	}
}
