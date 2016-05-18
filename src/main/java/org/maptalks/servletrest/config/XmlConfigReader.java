package org.maptalks.servletrest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.maptalks.servletrest.config.exceptions.XmlReadFailedException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.maptalks.servletrest.config.exceptions.InvalidServletException;

public class XmlConfigReader extends DefaultHandler {
	private List<ServletPattern> patterns;
	private ServletPattern servletPattern;
	private String servletClassName;
	private String servletMode;

	public void read(final InputStream xmlStream) throws IOException,
			ParserConfigurationException, SAXException {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		parser.parse(xmlStream, this);
	}

	/**
	 * 获取结果
	 * 
	 * @return
	 * @throws XmlReadFailedException
	 */
	public List<ServletPattern> getResult() {
		return patterns;
	}

	@Override
	public void startDocument() throws SAXException {
		patterns = new ArrayList<ServletPattern>();
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		if (Const.SERVLET_NODE.equalsIgnoreCase(qName)) {
			servletClassName = attributes.getValue("class");


		} else if (Const.PATTERN_NODE.equalsIgnoreCase(qName)) {
            servletMode = attributes.getValue("mode");
			servletPattern = new ServletPattern();
			final String urlPattern = attributes.getValue("value");
			servletPattern.setPattern(urlPattern);
			servletPattern.setMode(servletMode);
			try {
				final Class c = Class.forName(servletClassName);
				final Object o = c.newInstance();
				if (!(o instanceof HttpServlet)) {
					throw new InvalidServletException();
				}
				servletPattern.setServlet((HttpServlet) o);
			} catch (final Exception e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new XmlReadFailedException(e);
			}
		}
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		if (Const.SERVLET_NODE.equalsIgnoreCase(qName)) {
			servletPattern = null;
		} else if (Const.PATTERN_NODE.equalsIgnoreCase(qName)) {
			if (servletPattern != null
					&& servletPattern.getPattern() != null
					&& servletPattern.getPattern().length() > 0) {
				patterns.add(servletPattern);
				servletPattern = null;
			}
		}

	}

}
