package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.error;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

import com.vaguehope.senkyou.DataStore;

public abstract class AuthServlet extends HttpServlet {

	private static final String SESSION_TWITTER = "twitter";
	private static final String SESSION_REQUEST_TOKEN = "requestToken";

	private static final long serialVersionUID = -3997970760950061976L;

	private final DataStore dataStore;

	public AuthServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	protected DataStore getDataStore () {
		return this.dataStore;
	}

	protected void clearSession (HttpServletRequest req) {
		req.getSession().removeAttribute(SESSION_TWITTER);
		req.getSession().removeAttribute(SESSION_REQUEST_TOKEN);
	}

	protected void setSessionRequestToken (HttpServletRequest req, RequestToken token) {
		req.getSession().setAttribute(SESSION_REQUEST_TOKEN, token);
	}

	protected RequestToken getSessionRequestToken (HttpServletRequest req) {
		return (RequestToken) req.getSession().getAttribute(SESSION_REQUEST_TOKEN);
	}

	protected void clearSessionRequestToken (HttpServletRequest req) {
		req.getSession().removeAttribute(SESSION_REQUEST_TOKEN);
	}

	protected static void setSessionTwitter (HttpServletRequest req, Twitter twitter) {
		req.getSession().setAttribute(SESSION_TWITTER, twitter);
	}

	public static Twitter getSessionTwitterOrSetError (HttpServletRequest req, HttpServletResponse resp, DataStore ds) throws IOException {
		Object rawTwitter = req.getSession().getAttribute(SESSION_TWITTER);
		if (rawTwitter != null) return (Twitter) rawTwitter;

		Twitter twitter = ds.getUser(req, resp);
		if (twitter != null) {
			setSessionTwitter(req, twitter);
			return twitter;
		}

		error(resp, HttpServletResponse.SC_UNAUTHORIZED, "Not signed into Twitter.");
		return null;
	}

}
