package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class AuthCallbackServlet extends AuthServlet {

	public static final String CONTEXT = "/callback";

	private static final long serialVersionUID = 6017103945666217051L;

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = getSessionTwitterOrSetError(req, resp);
		if (twitter == null) return;

		try {
			RequestToken requestToken = getSessionRequestToken(req);
			String verifier = req.getParameter("oauth_verifier");
			twitter.getOAuthAccessToken(requestToken, verifier);
			clearSessionRequestToken(req);
			resp.sendRedirect(req.getContextPath() + HOME_PAGE);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
	}

}
