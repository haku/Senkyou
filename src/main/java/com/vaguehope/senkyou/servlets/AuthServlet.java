package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.error;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;

public abstract class AuthServlet extends HttpServlet {

	protected static final String SESSION_TWITTER = "twitter";
	protected static final String SESSION_REQUEST_TOKEN = "requestToken";

	protected static final String HOME_PAGE = "/";

	private static final long serialVersionUID = -3997970760950061976L;

	public static Twitter getTwitterOrSetError (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Object rawTwitter = req.getSession().getAttribute(SESSION_TWITTER);

		if (rawTwitter != null) {
			return (Twitter) rawTwitter;
		}

		error(resp, HttpServletResponse.SC_UNAUTHORIZED, "Not signed into Twitter.");
		return null;
	}

}
