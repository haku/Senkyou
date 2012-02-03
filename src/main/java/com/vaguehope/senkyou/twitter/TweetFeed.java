package com.vaguehope.senkyou.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;

public interface TweetFeed {
	
	TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException;
	
}
