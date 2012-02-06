package com.vaguehope.senkyou;

public interface Config {
	
	// Users.
	int USER_COUNT_MAX = 100;
	int USER_AGE_MAX = 10; // 10 minutes.
	
	// General feed fetching.
	int TWEET_FETCH_PAGE_SIZE = 40;
	long TWEET_FETCH_RETRY_WAIT = 60000L; // 60 seconds.
	
	// All per user.
	long HOME_TIMELINE_MAX_AGE = 30000L; // 30 seconds.
	long MENTIONS_MAX_AGE = 30000L; // 30 seconds.
	int TWEET_CACHE_MAX_COUNT = 100;
	int TWEET_CACHE_MAX_AGE = 60; // 60 minutes.
	
	// Feed lengths.
	int HOME_TIMELINE_LENGTH = 40;
	int MENTIONS_LENGTH = 15;
	
}
