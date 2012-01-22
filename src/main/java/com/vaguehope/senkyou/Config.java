package com.vaguehope.senkyou;

public interface Config {
	
	final int USER_COUNT_MAX = 100;
	final int USER_AGE_MAX = 10; // 10 minutes.
	
	final int TWEET_FETCH_PAGE_SIZE = 40;
	
	final long MAX_HOME_TIMELINE_AGE = 30000; // 30 seconds.
	final int USER_TWEET_CACHE_COUNT_MAX = 100;
	final int USER_TWEET_CACHE_AGE_MAX = 60; // 60 minutes.
	
}
