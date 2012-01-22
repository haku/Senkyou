package com.vaguehope.senkyou.twitter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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
	
	private AtomicReference<TweetList> homeTimeline = new AtomicReference<TweetList>();
	private final ReadWriteLock homeTimelineLock = new ReentrantReadWriteLock();
	
	private final String username;

	public TweetCache (String username) {
		this.username = username;
	}
	
	public TweetList getHomeTimeline (int minCount) throws TwitterException, ExecutionException {
		return getTweetList(this.username, this.homeTimelineLock, this.homeTimeline, minCount, MAX_CACHE_AGE);
	}
	
	public TweetList getLastTweetHomeTimeline (int serachDepth) throws TwitterException, ExecutionException {
		TweetList source = getHomeTimeline(serachDepth);
		TweetList target = new TweetList();
		target.setTime(source.getTime());
		copyFirstTweetOfEachUser(source, target);
		return target;
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	Static implementation.
	
	private static void copyFirstTweetOfEachUser (TweetList source, TweetList target) {
		Set<String> users = new HashSet<String>();
		for (Tweet t : source.getTweets()) {
			String u = t.getUser();
			if (!users.contains(u)) {
				target.addTweet(t);
				users.add(u);
			}
		}
	}
	
	private static TweetList getTweetList (String username, ReadWriteLock lock, AtomicReference<TweetList> tweetList, int minCount, long maxAge) throws TwitterException, ExecutionException {
		lock.readLock().lock();
		try {
			if (expired(tweetList.get(), maxAge)) {
				lock.readLock().unlock();
				lock.writeLock().lock();
				try {
					if (expired(tweetList.get(), maxAge)) {
						TweetList timeline = fetchHomeTimeline(username, minCount);
						tweetList.set(timeline);
					}
				}
				finally {
					lock.readLock().lock();
					lock.writeLock().unlock();
				}
			}
			return tweetList.get();
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	public static TweetList fetchHomeTimeline (String username, int minCount) throws TwitterException, ExecutionException {
		Twitter t = TwitterFactory.getTwitter(username);
		return fetchHomeTimeline(username, t, minCount);
	}
	
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
		ret.setUser(s.getUser().getScreenName());
		ret.setName(s.getUser().getName());
		ret.setBody(s.getText());
		return ret;
	}
	
}
