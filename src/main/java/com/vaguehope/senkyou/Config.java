package com.vaguehope.senkyou;

public interface Config {
	
	int USER_COUNT_MAX = 100;
	int USER_AGE_MAX = 10; // 10 minutes.
	
	int TWEET_FETCH_COUNT = 120;
	int TWEET_FETCH_PAGE_SIZE = 40;
	
	long MAX_HOME_TIMELINE_AGE = 30000; // 30 seconds.
	long MAX_MENTIONS_AGE = 30000; // 30 seconds.
	int USER_TWEET_CACHE_COUNT_MAX = 100;
	int USER_TWEET_CACHE_AGE_MAX = 60; // 60 minutes.
	
}
