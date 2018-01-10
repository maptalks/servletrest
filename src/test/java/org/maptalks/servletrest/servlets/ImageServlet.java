package org.maptalks.servletrest.servlets;

import javax.servlet.http.HttpServlet;

public class ImageServlet extends HttpServlet {
	String name;
	String width;
	String height;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
