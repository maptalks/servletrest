package org.maptalks.servletrest.test;

import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import junit.framework.Assert;
import org.maptalks.servletrest.ServletFactory;
import org.maptalks.servletrest.config.exceptions.ServletFactoryNotReadyException;

public class EfficiencyTest {

	public void test() throws ServletFactoryNotReadyException, ServletException {
		final URL classUrl = this.getClass().getResource("");
		final String configFilePath = classUrl.getFile() + "mass.xml";
		// configFilePath = "servletconfig.xml";
		final ServletFactory factory = new ServletFactory();
		factory.init(configFilePath, null);
		HttpServlet servlet = factory.getServletByUrl("/a/b/2342342/a7");
		/*
		 * HttpServlet servlet = factory
		 * .getServletByUrl("/asd/bsdfs/2342342/ssaa7");
		 */
		Assert.assertNotNull(servlet);

		final int exeCount = 100000;
		System.out.println("测试开始，循环次数：" + exeCount / 10000 + "万");
		System.out
				.println("----------------------------------------------------------------------------------");
		System.out.println("singleton模式：");
		long currTime = System.currentTimeMillis();
		for (int i = 0; i < exeCount; i++) {
			servlet = factory.getServletByUrl("/a/b/2342342/a7");
		}
		System.out.println("执行结束，耗时" + (System.currentTimeMillis() - currTime)
				+ "豪秒");
		System.out
				.println("----------------------------------------------------------------------------------");
		System.out.println("instance模式：");
		currTime = System.currentTimeMillis();
		for (int i = 0; i < exeCount; i++) {
			servlet = factory.getServletByUrl("/a/b/2342342/a6");
		}
		System.out.println("执行结束，耗时" + (System.currentTimeMillis() - currTime)
				+ "豪秒");
		System.out
				.println("----------------------------------------------------------------------------------");
	}
}
