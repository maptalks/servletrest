package org.maptalks.servletrest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import org.maptalks.servletrest.config.Util;
import org.maptalks.servletrest.config.exceptions.InvalidURLPatternException;

public class UtilTest {

	@Test
	public void testCheckPattern() {
		try {
			Util.checkPattern("///");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.checkPattern("/{id}//");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.checkPattern("/a{id}//");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.checkPattern("/{id}/}}/");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.checkPattern("/{id}/{{/");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.checkPattern("/{id}/{}/");
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		Assert.assertEquals(1, Util.checkPattern("/{id}/u/"));
		Assert.assertEquals(2, Util.checkPattern("/{id}/u/{weiboid}"));
	}

	@Test
	public void testComparePattern() throws InvalidURLPatternException {
		assertEquals(1, Util.comparePattern("/u/{id}/a", "/z/{id}/"));
		assertEquals(1, Util.comparePattern("/z/{id}/a", "/z/{id}/"));
		assertEquals(1, Util.comparePattern("/u/{id}/a", "/u/"));
		assertEquals(1, Util.comparePattern("/u/{id}/a", "/u/{id}/"));
		assertEquals(1, Util.comparePattern("/u/{id}/a", "/l/{id}/"));
		//		try {
		assertEquals(1, Util.comparePattern("/u/{id}/a", "/u/a/"));
		//			fail();
		//		} catch (final Exception ex) {
		//			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		//		}
		//		try {
		assertEquals(-1, Util.comparePattern(
				"/rest/{mapDbName}/map/{reqType}/{method}",
				"/rest/{mapDbName}/map/layer/field/{method}"));

		assertEquals(0, Util.comparePattern("/u/{id}/a", "/u/{id}/a"));
		assertEquals(-1, Util.comparePattern("/u/", "/u/{id}/a"));
		assertEquals(1, Util.comparePattern("/u/", null));
		assertEquals(-1, Util.comparePattern(null, "/u/"));
		assertEquals(0, Util.comparePattern(null, null));
		assertEquals(1, Util.comparePattern("/u/", ""));
		assertEquals(-1, Util.comparePattern("", "/u/"));
		assertEquals(0, Util.comparePattern("", ""));
		assertEquals(0, Util.comparePattern("", null));
	}

	@Test
	public void testComparePatternAndUrl() throws InvalidURLPatternException {
		assertEquals(1, Util.comparePatternAndUrl("/u/{id}/a", "/z/1111/"));
		assertEquals(1, Util.comparePatternAndUrl("/z/{id}/a", "/z/1111/"));
		assertEquals(1, Util.comparePatternAndUrl("/u/{id}/a", "/u/"));
		assertEquals(1, Util.comparePatternAndUrl("/u/{id}/a", "/u/1111/"));
		assertEquals(1, Util.comparePatternAndUrl("/u/{id}/a", "/l/1111/"));
		assertEquals(-1, Util.comparePatternAndUrl("/u/{id}/a", "/x/1111/a"));
		assertEquals(0, Util.comparePatternAndUrl("/u/{id}/a", "/u/1111/a"));
		assertEquals(-1, Util.comparePatternAndUrl("/u/", "/u/1111/a"));
		assertEquals(1, Util.comparePatternAndUrl("/u/", null));
		assertEquals(-1, Util.comparePatternAndUrl(null, "/u/"));
		assertEquals(0, Util.comparePatternAndUrl(null, null));
		assertEquals(1, Util.comparePatternAndUrl("/u/", ""));
		assertEquals(-1, Util.comparePatternAndUrl("", "/u/"));
		assertEquals(0, Util.comparePatternAndUrl("", ""));
		assertEquals(0, Util.comparePatternAndUrl("", null));
		assertEquals(-1, Util.comparePatternAndUrl(
				"/rest/{mapDbName}/map/{reqType}/{method}",
				"/rest/testdb/map/layer/field/add"));
	}

	@Test
	public void testValidatePatterns() {
		try {
			Util.validatePattern("/u/112/{var}", "/u/{id}/var", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.validatePattern("/u/112/", "/u/{id}/", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.validatePattern("/{mapDbName}/layer/{method}",
					"/manage/map/create", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {
			Util.validatePattern("/u/112/{var}", "/u/112/var", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {//
			Util.validatePattern("/u/112/{var}", "/u/{id}/var", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}
		try {//
			Util.validatePattern("/u/112/{var}", "/u/{id}/", '{', '}');
			fail();
		} catch (final Exception ex) {
			Assert.assertTrue(ex instanceof InvalidURLPatternException);
		}

	}

}
