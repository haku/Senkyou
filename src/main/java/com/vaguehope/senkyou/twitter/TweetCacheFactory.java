package com.vaguehope.senkyou.twitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TweetCacheFactory {
	
	private static final ConcurrentMap<String, TweetCache> CACHE = new ConcurrentHashMap<String, TweetCache>();
	
	public static TweetCache getTweetCache (String username) {
		TweetCache tc = CACHE.get(username);
		if (tc == null) {
			tc = new TweetCache(username);
			TweetCache prev = CACHE.putIfAbsent(username, tc);
			if (prev != null) tc = prev;
		}
		return tc;
	}
	
}
