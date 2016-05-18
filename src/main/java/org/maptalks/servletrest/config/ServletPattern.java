package org.maptalks.servletrest.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.maptalks.servletrest.ServletFactory;
import org.maptalks.servletrest.config.exceptions.InvalidServletException;
import org.maptalks.servletrest.config.exceptions.InvalidServletMethodException;
import org.maptalks.servletrest.config.exceptions.InvalidServletModeException;
import org.maptalks.servletrest.config.exceptions.InvalidURLPatternException;

public class ServletPattern implements Comparable<ServletPattern> {
	private boolean inited;
	private String pattern;
	private HttpServlet servlet;
	private Class servletClass;
	private Method[] methods;
	private Integer[] varIndexes;
	/**
	 * 模式:单例模式/还是每次请求生成新对象
	 */
	private String mode;

	public ServletPattern() {
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		if (pattern == null || pattern.length() == 0) {
			throw new InvalidURLPatternException();
		}
		if (!pattern.startsWith("/")) {
			pattern = "/" + pattern;
		}
		if (pattern.endsWith("/")) {
			pattern = pattern.substring(0, pattern.length() - 1);
		}
		Util.checkPattern(pattern);
		this.pattern = pattern;
		initMethodArr();
	}

	private void initMethodArr() {
		if (pattern == null || servletClass == null) {
			return;
		}
		final List<Method> methodList = new ArrayList<Method>();
		final List<Integer> varIndexList = new ArrayList<Integer>();
		final String[] patternArr = pattern.split("/");
		for (int i = 0; i < patternArr.length; i++) {
			if (patternArr[i].contains("{")) {
				try {
					final String methodName = "set"
							+ Character.toUpperCase(patternArr[i].charAt(1))
							+ patternArr[i].substring(2,
									patternArr[i].length() - 1);
					final java.lang.reflect.Method m = servletClass.getMethod(
							methodName, String.class);
					methodList.add(m);
					varIndexList.add(i);
				} catch (final Exception e) {
					if (e instanceof RuntimeException) {
						throw (RuntimeException) e;
					}
					throw new InvalidServletMethodException(e);
				}
			}
		}

		methods = new Method[methodList.size()];
		methodList.toArray(methods);
		varIndexes = new Integer[methods.length];
		varIndexList.toArray(varIndexes);
	}

	public Class getServletClass() {
		if (servletClass != null) {
			return servletClass;
		}
		return null;
	}

	public HttpServlet getServlet(final String realUrl) {
		if (hit(realUrl) != 0) {
			return null;
		}
		if (methods.length == 0) {
			return servlet;
		}
		return servlet;
	}

	public void setServlet(final HttpServlet servlet)
			throws InvalidServletException {
		if (servlet == null) {
			throw new InvalidServletException();
		}
		if (Const.SERVLET_SINGLETON_MODE.equals(this.mode)) {
			ServletFactory.registerSingletonServlet(servlet);
		} else {
			this.servlet = servlet;
		}
		servletClass = servlet.getClass();
		initMethodArr();
	}

	public int compareTo(final ServletPattern o) {
		if (o == null) {
			return 1;
		}
		return Util.comparePattern(this.pattern, o.pattern);
	}

	/**
	 * 取得servlet生成模式
	 * 
	 * @return
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * 判断url是否命中pattern
	 * 
	 * @param realUrl
	 * @return
	 */
	public int hit(String realUrl) {
		if (realUrl == null || realUrl.length() == 0) {
			return -1;
		}
		if (realUrl.endsWith("/")) {
			realUrl = realUrl.substring(0, realUrl.length() - 1);
		}
		final String[] urlSegs = realUrl.split("/");
		final int compareRet = Util.comparePatternAndUrl(pattern, realUrl);
		if (compareRet != 0) {
			return compareRet;
		}
		try {
			if (Const.SERVLET_INSTANCE_MODE.equals(mode)) {
				servlet = servlet.getClass().newInstance();
				servlet.init(ServletFactory.getServletConfig());
			} else if (Const.SERVLET_SINGLETON_MODE.equals(mode)) {
				servlet = ServletFactory.getServlet(servletClass);
			}
			for (int i = 0; i < varIndexes.length; i++) {
				methods[i].invoke(servlet, urlSegs[varIndexes[i]]);
			}
		} catch (final Exception ex) {
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			throw new InvalidServletMethodException(ex);
		}
		return 0;
	}

	/**
	 * 默认是instance模式
	 * 
	 * @param mode
	 */
	public void setMode(String mode) {
		if (mode == null || mode.length() == 0) {
			mode = Const.SERVLET_INSTANCE_MODE;
		}
		if (mode != null && mode.length() > 0) {
			mode = mode.toLowerCase();
			for (int i = 0; i < Const.SERVLET_MODES.length; i++) {
				if (Const.SERVLET_MODES[i].equals(mode)) {
					break;
				}
				if (i == Const.SERVLET_MODES.length - 1
						&& !Const.SERVLET_MODES[i].equals(mode)) {
					throw new InvalidServletModeException(mode);
				}
			}
		}
		this.mode = mode;
	}

}
