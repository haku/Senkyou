package com.vaguehope.senkyou.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;

public enum TweetFeeds implements TweetFeed {
	TWEET {
		@Override
		public String getContext () {
			return "tweet";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getTweet(t, n);
		}
	},
	HOME_TIMELINE {
		@Override
		public String getContext () {
			return "home";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getHomeTimeline(t, (int) n);
		}
	},
	MENTIONS {
		@Override
		public String getContext () {
			return "mentions";
		}
		
		@Override
		public TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException {
			return tc.getMentions(t, (int) n);
		}
	},
	;
	
	@Override
	public abstract String getContext ();
	
	@Override
	public abstract TweetList getTweets (Twitter t, TweetCache tc, long n) throws TwitterException;
	
}
