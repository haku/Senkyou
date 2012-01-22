package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;

public class TweetServlet extends HttpServlet {
	
	public static final String CONTEXT = "/feeds/tweets";
	
	private static final long serialVersionUID = 2124094443981251745L;
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			procGet(req, resp);
		}
		catch (JAXBException e) {
			throw new ServletException(e);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
	}

	private static void procGet (HttpServletRequest req, HttpServletResponse resp) throws IOException, TwitterException, JAXBException {
		String user = req.getParameter("user");
		if (user != null && !user.isEmpty()) {
			printHomeTimeline(resp, user);
		}
		else {
			ServletHelper.error(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid 'user' param.");
		}
	}

	private static void printHomeTimeline (HttpServletResponse resp, String username) throws IOException, TwitterException, JAXBException {
		TweetCache tweetCache = TweetCacheFactory.getTweetCache(username);
		TweetList tl = tweetCache.getHomeTimeline(10);
		tl.toXml(resp.getWriter());
	}
	
}
