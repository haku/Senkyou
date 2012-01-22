package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public final class ServletHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private ServletHelper () {/* Static helper */}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static void error (HttpServletResponse resp, int status, String msg) throws IOException {
		resp.reset();
		resp.setStatus(status);
		resp.setContentType("text/plain");
		resp.getWriter().println("HTTP Error "+status+": " + msg);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
