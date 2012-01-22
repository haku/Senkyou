package com.vaguehope.senkyou.twitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.Tweet;
import com.vaguehope.senkyou.model.TweetList;

public class TweetCache {
	
	private static final int PAGESIZE = 40;
	private static final long MAX_CACHE_AGE = 30000; // 30 seconds.
	
	private static final Logger LOG = Logger.getLogger(TweetCache.class.getName());
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private AtomicReference<TweetList> homeTimeline = new AtomicReference<TweetList>();
	
	private final String username;

	public TweetCache (String username) {
		this.username = username;
	}
	
	public TweetList getHomeTimeline (int minCount) throws IOException, TwitterException {
		this.lock.readLock().lock();
		try {
			if (expired(this.homeTimeline.get(), MAX_CACHE_AGE)) {
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				try {
					if (expired(this.homeTimeline.get(), MAX_CACHE_AGE)) {
						TweetList timeline = fetchHomeTimeline(minCount);
						this.homeTimeline.set(timeline);
					}
				}
				finally {
					this.lock.readLock().lock();
					this.lock.writeLock().unlock();
				}
			}
			return this.homeTimeline.get();
		}
		finally {
			this.lock.readLock().unlock();
		}
	}
	
	public TweetList fetchHomeTimeline (int minCount) throws IOException, TwitterException {
		Twitter t = TwitterHelper.getTwitter(this.username);
		return fetchHomeTimeline(this.username, t, minCount);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static boolean expired (TweetList list, long maxAge) {
		return list == null || list.getTime() + maxAge < System.currentTimeMillis();
	}
	
	private static TweetList fetchHomeTimeline (String u, Twitter t, int minCount) throws TwitterException {
		LOG.info("Fetching home timeline for: " + u);
		TweetList ret = new TweetList();
		int page = 1; // First page is 1.
		while (ret.tweetCount() < minCount) {
			Paging paging = new Paging(page, PAGESIZE);
			ResponseList<Status> timelinePage = t.getHomeTimeline(paging);
			if (timelinePage.size() < 1) break;
			addTweetsToList(ret, timelinePage);
			page++;
		}
		return ret;
	}

	private static void addTweetsToList (TweetList list, ResponseList<Status> tweets) {
		for (Status status : tweets) {
			Tweet tweet = convertTweet(status);
			list.addTweet(tweet);
		}
	}

	private static Tweet convertTweet (Status s) {
		Tweet ret = new Tweet();
		ret.setUsername(s.getUser().getName());
		ret.setBody(s.getText());
		return ret;
	}
	
}
