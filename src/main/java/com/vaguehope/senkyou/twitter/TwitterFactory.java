package com.vaguehope.senkyou.twitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.http.AccessToken;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaguehope.senkyou.Config;

public final class TwitterFactory {
	
	protected static final Logger LOG = Logger.getLogger(TwitterFactory.class.getName());
	
	private TwitterFactory () {/* Static helper. */}
	
	private static final LoadingCache<String, Twitter> CACHE = CacheBuilder.newBuilder()
			.maximumSize(Config.USER_COUNT_MAX)
			.softValues()
			.expireAfterAccess(Config.USER_AGE_MAX, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Twitter>() {
				@Override
				public Twitter load (String username) throws IOException {
					return makeTwitter(username);
				}
			});
	
	protected static Twitter makeTwitter (String username) throws IOException {
		AccessToken appToken = TwitterConfigHelper.readAppAuthData();
		AccessToken userToken = TwitterConfigHelper.readUserAuthData(username);
		Twitter t = new twitter4j.TwitterFactory().getOAuthAuthorizedInstance(appToken.getToken(), appToken.getTokenSecret(), userToken);
		
		t.setRateLimitStatusListener(new RateLimitLogger());
		
		return t;
	}
	
	public static Twitter getTwitter (String username) throws ExecutionException {
		return CACHE.get(username);
	}
	
	private static class RateLimitLogger implements RateLimitStatusListener {
		
		public RateLimitLogger () {}

		@Override
		public void onRateLimitStatus (RateLimitStatusEvent event) {
			logRate(event.getRateLimitStatus());
		}
		
		@Override
		public void onRateLimitReached (RateLimitStatusEvent event) {
			logRate(event.getRateLimitStatus());
		}
		
		private static void logRate (RateLimitStatus r) {
			LOG.info("Rate: " + r.getRemainingHits() + "/" + r.getHourlyLimit());
		}
		
	}
	
}
