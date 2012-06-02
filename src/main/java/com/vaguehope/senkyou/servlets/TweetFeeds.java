package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.validatePositiveLongParam;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.Config;
import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;
import com.vaguehope.senkyou.twitter.TweetFeed;

public enum TweetFeeds implements HttpProcessor, TweetFeed {
	HOME_TIMELINE {
		@Override
		public void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			procFeed(this, Config.HOME_TIMELINE_LENGTH, req, resp);
		}
		
		@Override
		public String getContext () {
			return CONTEXT_FEEDS_BASE + "home";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getHomeTimeline(t, (int) n);
		}
	},
	MENTIONS {
		@Override
		public void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			procFeed(this, Config.MENTIONS_LENGTH, req, resp);
		}
		
		@Override
		public String getContext () {
			return CONTEXT_FEEDS_BASE + "mentions";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getMentions(t, (int) n);
		}
	},
	MY_REPLIES {
		@Override
		public void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			procFeed(this, Config.MY_REPLIES_LENGTH, req, resp);
		}

		@Override
		public String getContext () {
			return CONTEXT_FEEDS_BASE + "myreplies";
		}

		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getMyReplies(t, (int) n);
		}
	},
	TWEET {
		@Override
		public void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			long n = validatePositiveLongParam(req, resp, "n");
			if (n < 1) return;
			procFeed(this, n, req, resp);
		}
		
		@Override
		public String getContext () {
			return CONTEXT_FEEDS_BASE + "tweet";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getTweet(t, n);
		}
	},
	;
	
	private static final String CONTEXT_FEEDS_BASE = "/feeds/";
	
	void procFeed (TweetFeed feed, long count, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp);
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
