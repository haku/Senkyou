package com.vaguehope.senkyou.twitter;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;

public enum TweetFeeds implements TweetFeed {
	TWEET {
		@Override
		public String getContext () {
			return "tweet";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, long n) throws TwitterException {
			return tc.getTweet(n);
		}
	},
	HOME_TIMELINE {
		@Override
		public String getContext () {
			return "home";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, long n) throws TwitterException {
			return tc.getHomeTimeline((int) n);
		}
	},
	HOME_TIMELINE_LAST_ONLY {
		@Override
		public String getContext () {
			return "homelast";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, long n) throws TwitterException {
			return tc.getLastTweetHomeTimeline((int) n);
		}
	},
	MENTIONS {
		@Override
		public String getContext () {
			return "mentions";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, long n) throws TwitterException {
			return tc.getMentions((int) n);
		}
	},
	THREADS {
		@Override
		public String getContext () {
			return "threads";
		}
		
		@Override
		public TweetList getTweets (TweetCache tc, long n) throws TwitterException {
			return tc.getThreads((int) n);
		}
	};
	
	@Override
	public abstract String getContext ();
	
	@Override
	public abstract TweetList getTweets (TweetCache tc, long n) throws TwitterException;
	
}
