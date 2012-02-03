package com.vaguehope.senkyou.twitter;

import org.eclipse.jetty.util.log.Log;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public final class TwitterConfigHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private TwitterConfigHelper () {/* Static helper. */}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String PARAM_CONSUMER_KEY = "consumerKey";
	private static final String PARAM_CONSUMER_SECRET = "consumerSecret";
	
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
	
	private static boolean hasLocalUser = true;
	
	private static final String PARAM_ACCESS_TOKEN = "accessToken";
	private static final String PARAM_ACCESS_SETRET = "accessSecret";
	
	public static Twitter getLocalUser () {
		if (!hasLocalUser) return null;
		
		String accessToken = System.getenv(PARAM_ACCESS_TOKEN);
		String accessSecret = System.getenv(PARAM_ACCESS_SETRET);
		
		if (accessToken == null || accessToken.isEmpty() || accessSecret == null || accessSecret.isEmpty()) {
			hasLocalUser = false;
			return null;
		}
		
		Log.info("LOCAL USER: " + accessToken + " " + accessSecret);
		Twitter t = getTwitter();
		t.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));
		return t;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
