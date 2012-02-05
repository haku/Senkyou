package com.vaguehope.senkyou.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.vaguehope.senkyou.twitter.TwitterConfigHelper;

public class AuthSigninServlet extends AuthServlet {

	public static final String CONTEXT = "/signin";

	private static final Logger LOG = Logger.getLogger(AuthSigninServlet.class.getName());
	private static final long serialVersionUID = 3223481188324574439L;

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resetSession(req);
		req.getSession().setAttribute(SESSION_TWITTER, null);
		req.getSession().setAttribute(SESSION_REQUEST_TOKEN, null);

		Twitter twitter = TwitterConfigHelper.getLocalUser();
		if (twitter == null) {
			twitter = TwitterConfigHelper.getTwitter();

			StringBuffer callbackUrl = req.getRequestURL();
			callbackUrl.replace(callbackUrl.lastIndexOf("/"), callbackUrl.length(), "").append(AuthCallbackServlet.CONTEXT);
			LOG.info("Callback URL: " + callbackUrl.toString());

			try {
				RequestToken token = twitter.getOAuthRequestToken(callbackUrl.toString());
				req.getSession().setAttribute(SESSION_REQUEST_TOKEN, token);
				
				String authenticationURL = token.getAuthenticationURL();
				if (authenticationURL.toLowerCase().startsWith("http://")) {
					authenticationURL = authenticationURL.replaceFirst("http", "https");
				}
				LOG.info("Redirecting to: " + authenticationURL);
				resp.sendRedirect(authenticationURL);
			}
			catch (TwitterException e) {
				throw new ServletException(e);
			}
		}
		else {
			resp.sendRedirect(req.getContextPath() + HOME_PAGE);
		}

		req.getSession().setAttribute(SESSION_TWITTER, twitter);
	}

}
