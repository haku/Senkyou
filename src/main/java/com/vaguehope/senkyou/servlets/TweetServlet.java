package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.validatePositiveLongParam;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;
import com.vaguehope.senkyou.twitter.TweetFeed;

public class TweetServlet extends HttpServlet {
	
	private static final String CONTEXT_FEEDS_BASE = "/feeds/";

	private static final long serialVersionUID = 2124094443981251745L;

	private final TweetFeed feed;
	
	public TweetServlet (TweetFeed feed) {
		this.feed = feed;
	}
	
	public String getContext () {
		return CONTEXT_FEEDS_BASE + this.feed.getContext();
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
		Twitter twitter = AuthServlet.getTwitterOrSetError(req, resp);
		if (twitter == null) return;
		
		long count = validatePositiveLongParam(req, resp, "n");
		if (count < 1) return;
		
		TweetCache tweetCache = TweetCacheFactory.getTweetCache(twitter);
		TweetList tl = this.feed.getTweets(twitter, tweetCache, count);
		resp.setContentType("text/xml;charset=UTF-8");
		tl.toXml(resp.getWriter());
	}
	
}
