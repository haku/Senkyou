package com.vaguehope.senkyou.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public final class TwitterConfigHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private TwitterConfigHelper () {/* Static helper. */}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String PARAM_CONSUMER_KEY = "consumerKey";
	private static final String PARAM_CONSUMER_SECRET = "consumerSecret";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static Twitter getTwitter () {
		String consumerKey = System.getenv(PARAM_CONSUMER_KEY);
		String consumerSecret = System.getenv(PARAM_CONSUMER_SECRET);
		
		if (consumerKey == null || consumerKey.isEmpty()) throw new IllegalStateException("consumerKey not configured.");
		if (consumerSecret == null || consumerSecret.isEmpty()) throw new IllegalStateException("consumerSecret not configured.");
		
		Twitter t = new TwitterFactory().getInstance();
		t.setOAuthConsumer(consumerKey, consumerSecret);
		return t;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
