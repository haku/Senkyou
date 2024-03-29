package com.vaguehope.senkyou.servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaguehope.senkyou.Config;

public final class CookieHelper {

	private CookieHelper () {}

	public static void addExtraSessionCookie (HttpServletRequest req, HttpServletResponse resp) {
		Cookie cookie = new Cookie(Config.COOKIE_SENKYOU_SESSION, req.getSession().getId());
		cookie.setMaxAge(Config.COOKIE_EXPIRY);
		resp.addCookie(cookie);
	}

	public static String getExtraSessionId (HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if (cookies == null) return null;
		for (Cookie c : cookies) {
			if (Config.COOKIE_SENKYOU_SESSION.equals(c.getName())) return c.getValue();
		}
		CookieHelper.addExtraSessionCookie(req, resp);
		return null;
	}

	public static void deleteCookies (HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0);
				resp.addCookie(cookies[i]);
			}
		}
		req.getSession().invalidate();
	}

}
