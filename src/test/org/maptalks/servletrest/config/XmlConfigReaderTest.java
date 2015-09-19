package org.maptalks.servletrest.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlConfigReaderTest {

	@Test
	public void testRead() throws IOException, ParserConfigurationException,
			SAXException {
		final XmlConfigReader reader = new XmlConfigReader();
		final URL classUrl = this.getClass().getResource("");
		final String path = classUrl.getFile() + "servletconfig.xml";
		final FileInputStream xmlStream = new FileInputStream(new File(path));
		reader.read(xmlStream);
		final List<ServletPattern> patterns = reader.getResult();
		Assert.assertNotNull(patterns);
		Assert.assertEquals(3, patterns.size());
		Assert.assertEquals("/z/{id}", patterns.get(0).getPattern());
	Assert.assertEquals(patterns.get(0).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.FServlet");
		Assert.assertEquals(Const.SERVLET_INSTANCE_MODE, patterns.get(0)
				.getMode());
		Assert.assertEquals("/b/{id}/a", patterns.get(1).getPattern());
		Assert.assertEquals(patterns.get(1).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.FServlet");
		Assert.assertEquals(Const.SERVLET_INSTANCE_MODE, patterns.get(1)
				.getMode());
		Assert.assertEquals("/u/{id}", patterns.get(2).getPattern());
		Assert.assertEquals(patterns.get(2).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.UServlet");
		Assert.assertEquals(Const.SERVLET_INSTANCE_MODE, patterns.get(2)
				.getMode());
	}

}
