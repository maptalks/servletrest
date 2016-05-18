package org.maptalks.servletrest.config.exceptions;

public class XmlReadFailedException extends RuntimeException {
	public XmlReadFailedException() {

	}

	public XmlReadFailedException(final Exception e) {
		super(e);
	}
}
