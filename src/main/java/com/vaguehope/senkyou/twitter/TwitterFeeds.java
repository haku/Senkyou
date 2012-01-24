package com.vaguehope.senkyou.twitter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public enum TwitterFeeds implements TwitterFeed {
	HOME_TIMELINE {
		@Override
		public String getName () {
			return "home timeline";
		}
		
		@Override
		public ResponseList<Status> getTweets (Twitter t, Paging paging) throws TwitterException {
			return t.getHomeTimeline(paging);
		}
	},
	MENTIONS {
		@Override
		public String getName () {
			return "mentions";
		}
		
		@Override
		public ResponseList<Status> getTweets (Twitter t, Paging paging) throws TwitterException {
			return t.getMentions(paging);
		}
	};
	
	@Override
	public abstract String getName ();
	
	@Override
	public abstract ResponseList<Status> getTweets (Twitter t, Paging paging) throws TwitterException;
}
