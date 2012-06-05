package com.vaguehope.senkyou.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;

import com.vaguehope.senkyou.DataStore;

public class TweetServlet extends HttpServlet {

	public static final String CONTEXT = "/tweet";

	private static final long serialVersionUID = -5606626611510620577L;
	private static final Logger LOG = Logger.getLogger(TweetServlet.class.getName());

	private transient final DataStore dataStore;

	public TweetServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;

		String replyId = req.getParameter("replyId");
		String body = req.getParameter("body");

		LOG.info("Tweet in reply to " + replyId + ": " + body);
	}

}
