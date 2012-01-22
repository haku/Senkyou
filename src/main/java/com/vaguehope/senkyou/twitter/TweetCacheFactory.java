package com.vaguehope.senkyou.twitter;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class TweetCacheFactory {
	
	private TweetCacheFactory () {/* Static helper. */}
	
	private static LoadingCache<String, TweetCache> CACHE = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.softValues()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, TweetCache>() {
				@Override
				public TweetCache load (String username) throws Exception {
					return new TweetCache(username);
				}
				
			});
	
	public static TweetCache getTweetCache (String username) {
		return CACHE.getUnchecked(username);
	}
	
}
