package com.vaguehope.senkyou.twitter;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;

public enum TweetFeeds implements TweetFeed {
	HOME_TIMELINE {
		@Override
		public String getContext () {
			return "home";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, int n) throws TwitterException {
			return tc.getHomeTimeline(n);
		}
	},
	MENTIONS {
		@Override
		public String getContext () {
			return "mentions";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, int n) throws TwitterException {
			return tc.getMentions(n);
		}
	},
	THREADS {
		@Override
		public String getContext () {
			return "threads";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, int n) throws TwitterException {
			return tc.getThreads(n);
		}
	};
	
	@Override
	public abstract String getContext ();
	
	@Override
	public abstract TweetList getTweets (TweetCache tc, int n) throws TwitterException;
	
}
