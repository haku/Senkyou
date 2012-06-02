package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.twitter.TwitterConfigHelper;

public class AuthSigninServlet extends AuthServlet {

	public static final String CONTEXT = "/signin";

	private static final long serialVersionUID = 3223481188324574439L;

	public AuthSigninServlet (DataStore dataStore) {
		super(dataStore);
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		clearSession(req);

		Twitter twitter = TwitterConfigHelper.getLocalUser();
		if (twitter == null) {
			twitter = this.dataStore.getUser(CookieHelper.getExtraSessionId(req));
			if (twitter == null) {
				twitter = TwitterConfigHelper.getTwitter();
				
				StringBuffer callbackUrl = req.getRequestURL();
				callbackUrl.replace(callbackUrl.lastIndexOf("/"), callbackUrl.length(), "").append(AuthCallbackServlet.CONTEXT);
				
				try {
					RequestToken token = twitter.getOAuthRequestToken(callbackUrl.toString());
					setSessionRequestToken(req, token);
					resp.sendRedirect(token.getAuthenticationURL());
				}
				catch (TwitterException e) {
					throw new ServletException(e);
				}
			}
			else {
				resp.sendRedirect(req.getContextPath() + HOME_PAGE);
			}
		}
		else {
			resp.sendRedirect(req.getContextPath() + HOME_PAGE);
		}

		setSessionTwitter(req, twitter);
	}

}
