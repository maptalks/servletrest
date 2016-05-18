package org.maptalks.servletrest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;

import org.maptalks.servletrest.config.ServletPattern;
import org.maptalks.servletrest.config.XmlConfigReader;
import org.maptalks.servletrest.config.exceptions.DuplicateUrlPatternException;
import org.maptalks.servletrest.config.exceptions.InvalidConfigPathException;
import org.maptalks.servletrest.config.exceptions.ServletFactoryNotReadyException;
import org.maptalks.servletrest.config.exceptions.XmlReadFailedException;
import org.xml.sax.SAXException;

public class ServletFactory {

	private List<ServletPattern> servletPatterns;
	private String configFilePath;
	private boolean initing = false;
	private static Map<Class, HttpServlet> singletonServlets = new HashMap<Class, HttpServlet>();
	private static ServletConfig servletConfig;

	/**
	 * 重新加载配置
	 * 
	 * @throws ServletException
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@Deprecated
	public void reload(final ServletConfig config) throws ServletException {
		init(configFilePath, config);
	}

	/**
	 * 初始化Servlet配置
	 * 
	 * @param configFilePath
	 * @param config
	 * @throws ServletException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public void init(final String configFilePath, final ServletConfig config)
			throws ServletException {
		if (configFilePath == null || configFilePath.length() == 0) {
			throw new InvalidConfigPathException();
		}
		initing = true;
		//singletonServlets = new HashMap<Class, HttpServlet>();
		this.configFilePath = configFilePath;
		servletPatterns = new ArrayList<ServletPattern>();
		ServletFactory.servletConfig = config;
		try {
			if (configFilePath.indexOf(',') == -1) {
				final InputStream xmlStream = getXMLInputStream(configFilePath);
				this.read(xmlStream);
			} else {
				final String[] xmls = configFilePath.split(",");
				for (final String xml : xmls) {
					if (xml == null || xml.length() == 0) {
						continue;
					}
					final InputStream xmlStream = getXMLInputStream(xml);
					this.read(xmlStream);
				}
			}
			// 对配置项进行排序，方便后面的查找
			Collections.sort(servletPatterns);
			checkDup();
			for (final HttpServlet servlet : singletonServlets.values()) {
				servlet.init(config);
			}
		} catch (final Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			throw new XmlReadFailedException(e);
		} finally {
			initing = false;
		}

	}

	/**
	 * 注册单例模式的servlet
	 * 
	 * @param servlet
	 */
	public static void registerSingletonServlet(final HttpServlet servlet) {
		singletonServlets.put(servlet.getClass(), servlet);
	}

	/**
	 * 取得单例的servlet
	 * 
	 * @return
	 */
	public static HttpServlet getServlet(final Class servletClass) {
		return singletonServlets.get(servletClass);
	}

	/**
	 * 取得文件流
	 * 
	 * @param configFilePath
	 * @return
	 * @throws FileNotFoundException
	 */
	private InputStream getXMLInputStream(final String configFilePath)
			throws FileNotFoundException {
		try {
			return new FileInputStream(new File(configFilePath));
		} catch (final Throwable e) {
			final InputStream xmlStream = this.getClass().getResourceAsStream(
					configFilePath);
			return xmlStream;
		}
	}

	/**
	 * 检查是否有重复的url定义
	 */
	private void checkDup() {
		ServletPattern prePattern = null;
		for (final Iterator<ServletPattern> iterator = servletPatterns
				.iterator(); iterator.hasNext();) {
			final ServletPattern pattern = iterator.next();
			if (pattern.compareTo(prePattern) <= 0) {
				throw new DuplicateUrlPatternException();
			}
			prePattern = pattern;
		}
	}

	/**
	 * 读取Servlet配置文件
	 * 
	 * @param configFilePath
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private void read(final InputStream xmlStream)
			throws ParserConfigurationException, SAXException, IOException {
		if (xmlStream == null) {
			throw new InvalidConfigPathException();
		}

		final XmlConfigReader xmlReader = new XmlConfigReader();
		xmlReader.read(xmlStream);
		final List<ServletPattern> patterns = xmlReader.getResult();
		servletPatterns.addAll(patterns);
	}

	public List<ServletPattern> getServletPatterns() {
		return servletPatterns;
	}

	/**
	 * 通过url取得相应的servlet
	 * 
	 * @param realUrl
	 * @return
	 * @throws ServletFactoryNotReadyException
	 */
	public HttpServlet getServletByUrl(final String realUrl)
			throws ServletFactoryNotReadyException {
		if (initing) {
			throw new ServletFactoryNotReadyException();
		}
		// 折半查找servlet
		int low = 0;
		int high = servletPatterns.size();
		if (high == 0) {
			return null;
		}
		int index = (low + high) / 2;
		int compareRet = servletPatterns.get((low + high) / 2).hit(realUrl);
		while (compareRet != 0 && ((high - low) != 1)) {
			if (compareRet > 0) {
				high = index;
			} else {
				low = index;
			}
			index = (low + high) / 2;
			compareRet = servletPatterns.get(index).hit(realUrl);
		}
		return servletPatterns.get(index).getServlet(realUrl);
	}

	public static ServletConfig getServletConfig() {
		return servletConfig;
	}
}
