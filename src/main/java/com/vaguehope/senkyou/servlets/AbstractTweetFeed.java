package com.vaguehope.senkyou.servlets;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;
import com.vaguehope.senkyou.twitter.TweetFeed;

public abstract class AbstractTweetFeed implements HttpProcessor, TweetFeed {

	protected static final String CONTEXT_FEEDS_BASE = "/feeds/";

	private final DataStore dataStore;

	public AbstractTweetFeed (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	protected void procFeed (TweetFeed feed, long count, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;

		try {
			TweetCache tweetCache = TweetCacheFactory.getTweetCache(twitter);
			resp.setContentType("text/xml;charset=UTF-8");
			feed.getTweets(twitter, tweetCache, count).toXml(resp.getWriter());
		}
		catch (ExecutionException e) {
			throw new ServletException(e);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
		catch (JAXBException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public abstract void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

}
