package org.maptalks.servletrest.config;

import static org.junit.Assert.fail;

import javax.servlet.http.HttpServlet;

import org.junit.Assert;
import org.junit.Test;

import org.maptalks.servletrest.ServletFactory;
import org.maptalks.servletrest.config.exceptions.InvalidServletModeException;
import org.maptalks.servletrest.servlets.UServlet;

public class ServletPatternTest {

	@Test
	public void testGetServlet() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("/u/111");
		Assert.assertNotNull(servlet);
		Assert.assertTrue(servlet instanceof UServlet);
		Assert.assertEquals("111", ((UServlet) servlet).getId());
	}

	@Test
	public void testGetServlet2() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}/");
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("/u/111");
		Assert.assertNotNull(servlet);
		Assert.assertTrue(servlet instanceof UServlet);
		Assert.assertEquals("111", ((UServlet) servlet).getId());
	}

	@Test
	public void testGetServlet3() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("/u/111/");
		Assert.assertNotNull(servlet);
		Assert.assertTrue(servlet instanceof UServlet);
		Assert.assertEquals("111", ((UServlet) servlet).getId());
	}

	@Test
	public void testGetServletWithInstanceMode() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setMode(Const.SERVLET_INSTANCE_MODE);
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("/u/112");
		Assert.assertNotNull(servlet);
		Assert.assertTrue(servlet instanceof UServlet);
		Assert.assertEquals("112", ((UServlet) servlet).getId());
	}

	@Test
	public void testUnHitServlet() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setMode(Const.SERVLET_INSTANCE_MODE);
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("/u/112/a");
		Assert.assertNull(servlet);
	}

	@Test
	public void testUnHitServlet2() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setMode(Const.SERVLET_INSTANCE_MODE);
		pattern.setServlet(new UServlet());
		final HttpServlet servlet = pattern.getServlet("u/112/a");
		Assert.assertNull(servlet);
	}

	@Test
	public void testSingletonMode() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
        pattern.setMode(Const.SERVLET_SINGLETON_MODE);
		pattern.setServlet(new UServlet());

		final HttpServlet servlet1 = pattern.getServlet("/u/111");
		final HttpServlet servlet2 = pattern.getServlet("/u/112");
		Assert.assertEquals(servlet1, servlet2);
	}

	@Test
	public void testInstanceMode() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setServlet(new UServlet());
		pattern.setMode(Const.SERVLET_INSTANCE_MODE);
		final HttpServlet servlet1 = pattern.getServlet("/u/111");
		final HttpServlet servlet2 = pattern.getServlet("/u/112");
		Assert.assertNotSame(servlet1, servlet2);
	}

	@Test
	public void testInvalidMode() {
		final ServletPattern pattern = new ServletPattern();
		pattern.setPattern("/u/{id}");
		pattern.setServlet(new UServlet());
		for (int i = 0; i < Const.SERVLET_MODES.length; i++) {
			pattern.setMode(Const.SERVLET_MODES[i]);
		}
		try {
			pattern.setMode("invalidMode");
			fail();
		} catch (final InvalidServletModeException ex) {

		}
	}
}
