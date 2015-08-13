package org.maptalks.servletrest;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.maptalks.servletrest.config.ServletPattern;
import org.maptalks.servletrest.config.exceptions.ServletFactoryNotReadyException;

public class RestServlet extends HttpServlet {
	private static ServletFactory servletFactory;
	private static String encoding;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			System.out.println("[RestServlet] begin to initialize servlets");
			final String servletConfigPath = config
					.getInitParameter("ServletConfig");
			System.out.println("[RestServlet] servlet config:"
					+ servletConfigPath);
			servletFactory = new ServletFactory();
			servletFactory.init(servletConfigPath, config);
			List<ServletPattern> patterns = servletFactory.getServletPatterns();
			if (patterns != null) {
				for (Iterator iterator = patterns.iterator(); iterator
						.hasNext();) {
					ServletPattern servletPattern = (ServletPattern) iterator
							.next();
					System.out.println("[RestServlet] servlet pattern:"
							+ servletPattern.getPattern());
				}
			} else {
				System.out.println("[RestServlet] no servlet pattern found.;");
			}
			encoding = config.getInitParameter("Encoding");
			System.out.println("[RestServlet] initializing ended;");
			super.init(config);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void service(ServletRequest req, ServletResponse rep)
			throws ServletException, IOException {
		final HttpServletRequest request = (HttpServletRequest) req;

		final String realUrl = request.getRequestURI().replaceFirst(
				request.getContextPath(), "");
		//		System.out.println("[RestServlet] hit" + realUrl);
		HttpServlet servlet = null;
		try {
			servlet = servletFactory.getServletByUrl(realUrl);
		} catch (final ServletFactoryNotReadyException e) {
			throw new ServletException(e);
		}
		if (servlet != null) {
			rep.setCharacterEncoding(encoding);
			rep.setLocale(new Locale("zh"));
			try {
				servlet.service(req, rep);
			} catch (Throwable e) {
				//				e.printStackTrace();
				throw new ServletException(e);
			}
		} else {
			//			System.out.println("[RestServlet] no servlet serviced!");
			super.service(req, rep);
		}

	}

}
