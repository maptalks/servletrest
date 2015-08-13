package org.maptalks.servletrest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.maptalks.servletrest.config.exceptions.ServletFactoryNotReadyException;

@Deprecated
abstract class RestFilter implements Filter {

	private static ServletFactory servletFactory;

	public void destroy() {
		servletFactory = null;
	}

	public void doFilter(final ServletRequest req, final ServletResponse rep,
			final FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) req;

		final String realUrl = request.getRequestURI().replaceFirst(
				request.getContextPath(), "");

		HttpServlet servlet = null;
		try {
			servlet = servletFactory.getServletByUrl(realUrl);
		} catch (final ServletFactoryNotReadyException e) {
			e.printStackTrace();
		}
		if (servlet != null) {
			servlet.service(req, rep);
		} /*
			* else { throw new InvalidServletException("There is no servlet for \""
			* + realUrl + "\""); }
			*/
		chain.doFilter(req, rep);
	}

	// @Override
	// public void init(final FilterConfig config) throws ServletException {
	// final String servletConfig = config.getInitParameter("ServletConfig");
	// if (servletConfig == null || servletConfig.length() == 0) {
	// throw new InvalidConfigPathException();
	// }
	// servletFactory = new ServletFactory();
	// servletFactory.init(servletConfig);
	// }
	/**
	 * 重新加载配置文件
	 * 
	 * @throws ServletException
	 */
	// public static void reloadServletConfig() throws ServletException {
	// servletFactory.reload();
	// }

}
