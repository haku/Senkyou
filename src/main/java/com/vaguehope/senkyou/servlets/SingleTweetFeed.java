package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.validatePositiveLongParam;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetFeed;

public class SingleTweetFeed extends AbstractTweetFeed implements HttpProcessor, TweetFeed {

	public SingleTweetFeed (DataStore dataStore) {
		super(dataStore);
	}

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

}
