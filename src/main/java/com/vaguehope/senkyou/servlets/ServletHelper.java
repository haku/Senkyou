package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.util.Numbers.isNumeric;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ServletHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private ServletHelper () {/* Static helper */}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static String validateStringParam (HttpServletRequest req, HttpServletResponse resp, String param) throws IOException {
		String p = req.getParameter(param);
		if (p != null && !p.isEmpty()) {
			return p;
		}
		error(resp, HttpServletResponse.SC_BAD_REQUEST, "Param '" + param + "' not valid.");
		return null;
	}
	
	public static int validatePositiveIntParam (HttpServletRequest req, HttpServletResponse resp, String param) throws IOException {
		String p = req.getParameter(param);
		if (p != null && !p.isEmpty() && isNumeric(p)) {
			int i = Integer.parseInt(p);
			if (i > 0) {
				return i;
			}
			error(resp, HttpServletResponse.SC_BAD_REQUEST, "Param '" + param + "' not positive.");
			return 0;
		}
		error(resp, HttpServletResponse.SC_BAD_REQUEST, "Param '" + param + "' not valid.");
		return 0;
	}
	
	public static void error (HttpServletResponse resp, int status, String msg) throws IOException {
		resp.reset();
		resp.setStatus(status);
		resp.setContentType("text/plain");
		resp.getWriter().println("HTTP Error "+status+": " + msg);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
