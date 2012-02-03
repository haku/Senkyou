package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.error;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.vaguehope.senkyou.twitter.TwitterConfigHelper;

public class AuthServlet extends HttpServlet {

	public static final String CONTEXT = "/auth";
	
	private static final String PARAM_ACTION = "a";
	private static final String ACTION_SIGNIN = "signin";
	private static final String ACTION_CALLBACK = "callback";
	
	private static final String SESSION_TWITTER = "twitter";
	private static final String SESSION_REQUEST_TOKEN = "requestToken";
	
	protected static final Logger LOG = Logger.getLogger(AuthServlet.class.getName());
	private static final long serialVersionUID = 8329372619000823417L;
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String action = req.getParameter(PARAM_ACTION);
			if (ACTION_SIGNIN.equals(action)) {
				signin(req, resp);
			}
			else if (ACTION_CALLBACK.equals(action)) {
				callback(req, resp);
			}
			else {
				error(resp, HttpServletResponse.SC_BAD_REQUEST, "invalid action.");
			}
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
	}
	
	private static void signin (HttpServletRequest req, HttpServletResponse resp) throws TwitterException, IOException {
		ServletHelper.resetSession(req);
		
		Twitter twitter = TwitterConfigHelper.getLocalUser();
		if (twitter == null) {
			twitter = TwitterConfigHelper.getTwitter();
			StringBuffer url = req.getRequestURL();
			url.append("?").append(PARAM_ACTION).append("=").append(ACTION_CALLBACK);
			
			RequestToken token = twitter.getOAuthRequestToken(url.toString());
			req.getSession().setAttribute(SESSION_REQUEST_TOKEN, token);
			resp.sendRedirect(token.getAuthenticationURL());
		}
		else {
			resp.sendRedirect(req.getContextPath() + "/");
		}
		
		req.getSession().setAttribute(SESSION_TWITTER, twitter);
	}
	
	private static void callback (HttpServletRequest req, HttpServletResponse resp) throws IOException, TwitterException {
		Twitter twitter = getTwitterOrSetError(req, resp);
		if (twitter == null) return;
		
		RequestToken requestToken = (RequestToken) req.getSession().getAttribute(SESSION_REQUEST_TOKEN);
		String verifier = req.getParameter("oauth_verifier");
		twitter.getOAuthAccessToken(requestToken, verifier);
		req.getSession().removeAttribute(SESSION_REQUEST_TOKEN);
		
		resp.sendRedirect(req.getContextPath() + "/");
	}

	public static Twitter getTwitterOrSetError (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Object rawTwitter = req.getSession().getAttribute(SESSION_TWITTER);
		
		if (rawTwitter != null) {
			return (Twitter) rawTwitter;
		}
		
		error(resp, HttpServletResponse.SC_UNAUTHORIZED, "Not signed into Twitter.");
		return null;
	}
	
}
