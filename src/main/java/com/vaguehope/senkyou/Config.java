package com.vaguehope.senkyou;

public interface Config {
	
	public final int USER_COUNT_MAX = 100;
	public final int USER_AGE_MAX = 10; // 10 minutes.
	
	public final int TWEET_FETCH_PAGE_SIZE = 40;
	
	public final long MAX_HOME_TIMELINE_AGE = 30000; // 30 seconds.
	public final int USER_TWEET_CACHE_COUNT_MAX = 100;
	public final int USER_TWEET_CACHE_AGE_MAX = 60; // 60 minutes.
	
}
