package org.maptalks.servletrest.test;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import org.maptalks.servletrest.ServletFactory;
import org.maptalks.servletrest.config.Const;
import org.maptalks.servletrest.config.ServletPattern;
import org.maptalks.servletrest.config.exceptions.DuplicateUrlPatternException;
import org.maptalks.servletrest.config.exceptions.InvalidURLPatternException;

public class ServletFactoryTest {

	@Test
	public void testInit() throws ServletException {
		final URL classUrl = this.getClass().getResource("");
		final String configFilePath = classUrl.getFile() + "servletconfig.xml";
		// configFilePath = "servletconfig.xml";
		final ServletFactory factory = new ServletFactory();
		factory.init(configFilePath, null);
		final List<ServletPattern> patterns = factory.getServletPatterns();
		Assert.assertNotNull(patterns);
		Assert.assertEquals(3, patterns.size());
		Assert.assertEquals("/b/{id}/a", patterns.get(2).getPattern());
		Assert.assertEquals(patterns.get(2).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.FServlet");
		Assert.assertEquals(Const.SERVLET_INSTANCE_MODE, patterns.get(2)
				.getMode());
		Assert.assertEquals("/u/{id}", patterns.get(0).getPattern());
		Assert.assertEquals(patterns.get(0).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.UServlet");
		Assert.assertEquals(Const.SERVLET_INSTANCE_MODE, patterns.get(0)
				.getMode());
		Assert.assertEquals("/z/{id}", patterns.get(1).getPattern());
		Assert.assertEquals(patterns.get(1).getServletClass().getName(),
				"org.maptalks.servletrest.servlets.FServlet");
		Assert.assertEquals(Const.SERVLET_SINGLETON_MODE, patterns.get(1)
				.getMode());
	}

	@Test
	public void testMultiConfigs() throws ServletException {
		final URL classUrl = this.getClass().getResource("");
		String configFilePath = classUrl.getFile() + "servletconfig.xml";
		configFilePath = configFilePath + "," + classUrl.getFile()
				+ "servletconfig2.xml";
		// configFilePath = "servletconfig.xml";
		final ServletFactory factory = new ServletFactory();
		factory.init(configFilePath, null);
		final List<ServletPattern> patterns = factory.getServletPatterns();
		Assert.assertNotNull(patterns);
		Assert.assertEquals(6, patterns.size());
	}

	@Test
	public void testDupConfig() throws ServletException {
		final URL classUrl = this.getClass().getResource("");
		final String configFilePath = classUrl.getFile()
				+ "dupservletconfig.xml";
		// configFilePath = "servletconfig.xml";
		final ServletFactory factory = new ServletFactory();
		try {
			factory.init(configFilePath, null);
			fail();
		} catch (final DuplicateUrlPatternException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testConflictConfig() throws ServletException {
		final URL classUrl = this.getClass().getResource("");
		final String configFilePath = classUrl.getFile()
				+ "conflictservletconfig.xml";
		// configFilePath = "servletconfig.xml";
		final ServletFactory factory = new ServletFactory();
		try {
			factory.init(configFilePath, null);
			fail();
		} catch (final InvalidURLPatternException ex) {
			Assert.assertTrue(true);
		}
	}
}
