package com.vaguehope.senkyou.twitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

public class TwitterHelper {
	
	private static final ConcurrentMap<String, Twitter> twitters = new ConcurrentHashMap<String, Twitter>();
	
	public static Twitter getTwitter (String username) throws IOException {
		Twitter t = twitters.get(username);
		if (t == null) {
			AccessToken appToken = TwitterConfigHelper.readAppAuthData();
			AccessToken userToken = TwitterConfigHelper.readUserAuthData(username);
			t = new TwitterFactory().getOAuthAuthorizedInstance(appToken.getToken(), appToken.getTokenSecret(), userToken);
			Twitter oldT = twitters.putIfAbsent(username, t);
			if (oldT != null) t = oldT;
		}
		return t;
	}
	
}
