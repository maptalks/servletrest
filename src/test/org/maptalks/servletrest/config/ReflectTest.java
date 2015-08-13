package org.maptalks.servletrest.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.maptalks.servletrest.servlets.UServlet;

public class ReflectTest {

	public void testReflect() throws InstantiationException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		final int exeCount = 10000000;
		final Class c = UServlet.class;
		final Object j = c.newInstance();
		Method m = c.getMethod("setId", String.class);
		System.out.println("测试开始，循环次数：" + exeCount / 10000 + "万");
		System.out
				.println("----------------------------------------------------------------------------------");
		System.out.println("直接调用Method：");
		long currTime = System.currentTimeMillis();
		for (int i = 0; i < exeCount; i++) {
			m.invoke(j, "test");
		}
		System.out.println("执行结束，耗时" + (System.currentTimeMillis() - currTime)
				+ "豪秒");
		System.out
				.println("----------------------------------------------------------------------------------");
		System.out.println("每次构造Method再执行：");
		currTime = System.currentTimeMillis();
		for (int i = 0; i < exeCount; i++) {
			m = c.getMethod("setId", String.class);
			m.invoke(j, "test");
		}
		System.out.println("执行结束，耗时" + (System.currentTimeMillis() - currTime)
				+ "豪秒");
		System.out
				.println("----------------------------------------------------------------------------------");
		final UServlet servlet = new UServlet();
		System.out.println("直接调用：");
		currTime = System.currentTimeMillis();
		for (int i = 0; i < exeCount; i++) {
			servlet.setId("test");
		}
		System.out.println("执行结束，耗时" + (System.currentTimeMillis() - currTime)
				+ "豪秒");
	}
}
