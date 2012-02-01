package com.vaguehope.senkyou.twitter;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;

public interface TweetFeed {
	
	String getContext ();
	
	TweetList getTweets (TweetCache tc, long n) throws TwitterException;
	
}
