package org.maptalks.servletrest.config.exceptions;

public class ServletFactoryNotReadyException extends Exception {
	public ServletFactoryNotReadyException() {
		super("Servlet Factory is initialing");
	}
}
