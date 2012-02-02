package com.vaguehope.senkyou.twitter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaguehope.senkyou.Config;

public final class TweetCacheFactory {
	
	private TweetCacheFactory () {/* Static helper. */}
	
	private static final LoadingCache<Long, TweetCache> CACHE = CacheBuilder.newBuilder()
			.maximumSize(Config.USER_COUNT_MAX)
			.softValues()
			.expireAfterAccess(Config.USER_AGE_MAX, TimeUnit.MINUTES)
			.build(new CacheLoader<Long, TweetCache>() {
				@Override
				public TweetCache load (Long id) throws ExecutionException {
					return new TweetCache(id);
				}
				
			});
	
	public static TweetCache getTweetCache (Twitter t) throws ExecutionException, TwitterException {
		return CACHE.get(Long.valueOf(t.getId()));
	}
	
}
