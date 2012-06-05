package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.DataStore;

public class TweetServlet extends HttpServlet {

	public static final String CONTEXT = "/tweet";

	private static final long serialVersionUID = -5606626611510620577L;

	private transient final DataStore dataStore;

	public TweetServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;

		long replyTo = Long.parseLong(req.getParameter("replyTo"));
		String body = req.getParameter("body");

		try {
			StatusUpdate status = new StatusUpdate(body);
			status.setInReplyToStatusId(replyTo);
			twitter.updateStatus(status);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
	}

}
