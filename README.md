servletrest
===========

[![Circle CI](https://circleci.com/gh/MapTalks/servletrest.svg?style=svg)](https://circleci.com/gh/MapTalks/servletrest)

A simple, light-weighted, easy-to-use restful servlet framework for JAVA.

it is very simple to configure restservlet, including 2 steps:

step 1: configure restservlet in web.xml to handle all the requests with a common prefix of the url, for example: http://foo.com/foo/rest/*
###
		<servlet>
			<servlet-name>RestServlet</servlet-name>
			<servlet-class>cn.com.seegoo.servletrest.RestServlet</servlet-class>
			<init-param>
				<param-name>ServletConfig</param-name>  
				<param-value>/servletconfig.xml</param-value>  
			</init-param> 
			<init-param>
				<param-name>Encoding</param-name>  
				<param-value>UTF-8</param-value>  
			</init-param>
			<load-on-startup>1</load-on-startup>     
		</servlet>
		<servlet-mapping>   
			<servlet-name>RestServlet</servlet-name>
			<url-pattern>rest/</url-pattern>
		</servlet-mapping>
step2: create some url patterns in a file named servletconfig.xml to match request url with a certain servlet class.
save servletconfig.xml in the root source folder which means servletconfig.xml will be copied in WEB-INF/classes.
### 
		<?xml version="1.0" encoding="UTF-8"?>
		<servlets>
		<!--sample start -->
		<!-- this means HelloServlet will handle the url http://foo.com/foo/rest/hello -->
		<servlet class="com.foo.HelloServlet">
			<urlpattern value="/rest/hello"></urlpattern>
		</servlet>
		<!-- someone is a variable in url pattern whose value will be set to servlet instance's field with the same name "someone".-->
		<!-- for expample,http://foo.com/foo/rest/hello/john, you can get "john" in HelloToSomeoneServlet's field someone-->
		<servlet class="com.foo.HelloToSomeoneServlet">
			<urlpattern value="/rest/hello/{someone}"></urlpattern>
		</servlet>
		<!--sample end -->
		</servlets>

