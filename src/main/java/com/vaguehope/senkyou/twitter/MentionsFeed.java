package com.vaguehope.senkyou.twitter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.Config;
import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.servlets.HttpProcessor;

public class MentionsFeed extends AbstractTweetFeed implements HttpProcessor, TweetFeed {

	public MentionsFeed (DataStore dataStore) {
		super(dataStore);
	}

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

}
