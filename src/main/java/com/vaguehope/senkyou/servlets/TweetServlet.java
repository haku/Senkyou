package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.validatePositiveIntParam;
import static com.vaguehope.senkyou.servlets.ServletHelper.validateStringParam;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;
import com.vaguehope.senkyou.twitter.TweetFeed;

public class TweetServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2124094443981251745L;

	private final TweetFeed feed;
	
	public TweetServlet (TweetFeed feed) {
		this.feed = feed;
	}
	
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
		catch (ExecutionException e) {
			throw new ServletException(e);
		}
	}
	
	private void procGet (HttpServletRequest req, HttpServletResponse resp) throws IOException, TwitterException, JAXBException, ExecutionException {
		String user = validateStringParam(req, resp, "u");
		if (user == null) return;
		
		int count = validatePositiveIntParam(req, resp, "n");
		if (count < 1) return;
		
		TweetCache tweetCache = TweetCacheFactory.getTweetCache(user);
		TweetList tl = this.feed.getTweets(tweetCache, count);
		tl.toXml(resp.getWriter());
	}
	
}
