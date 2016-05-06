package org.maptalks.servletrest.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 可以作为所有servlet的基类，提供了write方法、显示对象转换类和显示对象的获取方法<br/>
 * 若urlpattern中存在{return}变量定义了返回类型（json或xml）,
 * 则子servlet能够通过getConvertor和getReturnBuilder获取到相应的返回类型转换对象<br/>
 * 若urlpattern中没有定义{return}变量，则默认为json模式 /u/{id}/ /u/10000/
 *
 * @author fuzhen
 *
 */
public class BaseServlet extends HttpServlet {

	private static String ATTRIBUTE_GZIP = "MAPTALKS-ALLOW-GZIP";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String encoding = req.getHeader("Accept-Encoding");
		if (encoding != null && encoding.indexOf("gzip") != -1) {
			req.setAttribute(ATTRIBUTE_GZIP, true);
		}
		super.service(req, resp);
	}

	/**
	 * 从httpservletrequest中取得post请求的data
	 *
	 * @param req
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public Map<String, String> getPostParameters(final HttpServletRequest req)
			throws IOException {
		Map<String, String> postData = new HashMap<String, String>();
        String contentType = req.getHeader("Content-Encoding");
        boolean gzipReader = false;
        if (contentType != null && contentType.indexOf("gzip") > -1) {
            gzipReader = true;
        }
		StringBuilder rawPostDataBuilder = new StringBuilder();
        BufferedReader br;
        if (gzipReader) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                    req.getInputStream()) , "UTF-8"));
        } else {
            br = new BufferedReader(new InputStreamReader(
                    req.getInputStream(), "UTF-8"));
        }

		String encoding = "UTF-8";
		int temp;
		while ((temp = br.read()) != -1) {
			rawPostDataBuilder.append((char)temp);
		}
		br.close();
		String rawPostData = rawPostDataBuilder.toString();
		String[] params = rawPostData.split("&");
		for (int i = 0; i < params.length; i++) {
			String parameter = params[i];
			String[] paraValue = parseParameter(parameter, encoding);
			if (paraValue == null)
				continue;
			postData.put(paraValue[0], paraValue[1]);
		}
		//解析url中的参数
		String queryString = req.getQueryString();
		if (queryString != null) {
			String[] queryParams = queryString.split("&");
			for (int i = 0; i < queryParams.length; i++) {
				String parameter = queryParams[i];
				String[] paraValue = parseParameter(parameter, encoding);
				if (paraValue == null)
					continue;
				postData.put(paraValue[0], paraValue[1]);
			}
		}
		return postData;
	}

	private String[] parseParameter(String parameter, String encoding)
			throws UnsupportedEncodingException {
		final String[] dataArr = parameter.split("=", 2);
		if (dataArr == null || dataArr.length != 2)
			return null;
		dataArr[1] = URLDecoder.decode(dataArr[1], encoding);
		return dataArr;
	}

	/**
	 * 向输出流中写入toWrite
	 *
	 * @param toWrite
	 * @param resp
	 * @throws IOException
	 */
	protected void writeResp(String toWrite, final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
		final Writer writer = getWriter(req, resp);
		writer.write(toWrite);
		writer.flush();
		writer.close();
	}

	protected Writer getWriter(final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
		if (req.getAttribute(ATTRIBUTE_GZIP) != null) {
			resp.setHeader("Content-Encoding", "gzip");
			Writer writer = new OutputStreamWriter(new GZIPOutputStream(
					resp.getOutputStream()), "UTF-8");
			return writer;
		} else {
			return resp.getWriter();
		}

	}

	/**
	 * 读取cookie
	 *
	 * @param req
	 * @param key
	 * @return
	 */
	protected String readCookie(HttpServletRequest req, String key) {
		final Cookie[] cookies = req.getCookies();
		if (cookies == null || cookies.length == 0) {

			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(key)) {
				return cookies[i].getValue();
			}
		}
		return null;
	}

	/**
	 * 保存cookie
	 *
	 * @param resp
	 * @param key
	 * @param value
	 * @param _savecookie
	 *            是否在关闭浏览器后保存该cookie，如果设为true，则expiry会设到cookie中，浏览器关闭后cookie仍会存在
	 * @param expiry
	 */
	protected void saveCookie(HttpServletResponse resp, String key,
			String value, boolean _savecookie, int expiry) {
		final Cookie cookie = new Cookie(key, value);
		if (_savecookie) {
			cookie.setMaxAge(expiry);
		} else {
			// cookie.setMaxAge(300);
		}
		cookie.setPath("/");
		resp.addCookie(cookie);
		// return cookie;
	}

	/**
	 * 保存cookie
	 *
	 * @param resp
	 * @param key
	 * @param value
	 * @param _savecookie
	 *            是否在关闭浏览器后保存该cookie，如果设为true，则expiry会设到cookie中，浏览器关闭后cookie仍会存在
	 * @param expiry
	 */
	protected void saveCookie(HttpServletResponse resp, String key,
			String value, boolean _savecookie, int expiry, String path,
			String domain) {
		final Cookie cookie = new Cookie(key, value);
		if (_savecookie) {
			cookie.setMaxAge(expiry);
		} else {
			// cookie.setMaxAge(300);
		}
		cookie.setPath(path);
		cookie.setDomain(domain);
		resp.addCookie(cookie);
		cookie.setSecure(false);
		// return cookie;
	}

	/**
	 * 删除所有的cookie
	 *
	 * @param req
	 * @param resp
	 */
	protected void removeCookie(HttpServletRequest req, HttpServletResponse resp) {
		final Cookie[] cookies = req.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			final Cookie cookie = cookies[i];
			cookie.setMaxAge(0);
			cookie.setPath("/");
			resp.addCookie(cookie);
		}
	}
}
