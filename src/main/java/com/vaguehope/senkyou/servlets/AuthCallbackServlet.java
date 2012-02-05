package com.vaguehope.senkyou.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

public class AuthCallbackServlet extends AuthServlet {

	public static final String CONTEXT = "/callback";

	private static final Logger LOG = Logger.getLogger(AuthCallbackServlet.class.getName());
	private static final long serialVersionUID = 6017103945666217051L;

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = getTwitterOrSetError(req, resp);
		if (twitter == null) return;

		LOG.info("Callback: " + req.toString());

		try {
			RequestToken requestToken = (RequestToken) req.getSession().getAttribute(SESSION_REQUEST_TOKEN);
			String verifier = req.getParameter("oauth_verifier");
			twitter.getOAuthAccessToken(requestToken, verifier);
			req.getSession().removeAttribute(SESSION_REQUEST_TOKEN);
			resp.sendRedirect(req.getContextPath() + HOME_PAGE);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
	}

}
