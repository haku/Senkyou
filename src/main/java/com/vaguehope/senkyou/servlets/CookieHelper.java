package com.vaguehope.senkyou.servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHelper {

	private static final String SENKYOU_SESSION = "SenkyouSession";

	public static void addExtraSessionCookie (HttpServletRequest req, HttpServletResponse resp) {
		Cookie cookie = new Cookie(SENKYOU_SESSION, req.getSession().getId());
		cookie.setMaxAge(60 * 60 * 72); // 3 days.  TODO extract var.
		resp.addCookie(cookie);
	}

	public static String getExtraSessionId (HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (cookies == null) return null;
		for (Cookie c : cookies) {
			if (SENKYOU_SESSION.equals(c.getName())) return c.getValue();
		}
		return null;
	}

}
