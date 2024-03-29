package com.vaguehope.senkyou.twitter;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletResponse;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vaguehope.senkyou.Config;
import com.vaguehope.senkyou.model.Tweet;
import com.vaguehope.senkyou.model.TweetList;

public class TweetCache {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private final Long twitterId;

	private final Cache<Long, Tweet> tweetCache = CacheBuilder.newBuilder()
			.maximumSize(Config.TWEET_CACHE_MAX_COUNT)
			.softValues()
			.expireAfterAccess(Config.TWEET_CACHE_MAX_AGE_MIN, TimeUnit.MINUTES)
			.build();

	private final AtomicReference<TweetList> homeTimeline = new AtomicReference<TweetList>();
	private final ReadWriteLock homeTimelineLock = new ReentrantReadWriteLock();

	private final AtomicReference<TweetList> mentionsTimeline = new AtomicReference<TweetList>();
	private final ReadWriteLock mentionsTimelineLock = new ReentrantReadWriteLock();

	private final AtomicReference<TweetList> meTimeline = new AtomicReference<TweetList>();
	private final ReadWriteLock meTimelineLock = new ReentrantReadWriteLock();

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public TweetCache (Long id) {
		this.twitterId = id;
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Long getTwitterId () {
		return this.twitterId;
	}

	public TweetList getHomeTimeline (Twitter t, int minCount) throws TwitterException {
		return getTweetList(t, TwitterFeeds.HOME_TIMELINE, this.homeTimelineLock, this.homeTimeline, minCount, Config.HOME_TIMELINE_MAX_AGE_MS);
	}

	public TweetList getMentions (Twitter t, int minCount) throws TwitterException {
		return getTweetList(t, TwitterFeeds.MENTIONS, this.mentionsTimelineLock, this.mentionsTimeline, minCount, Config.MENTIONS_MAX_AGE_MS);
	}

	public TweetList getMe (Twitter t, int minCount) throws TwitterException {
		return getTweetList(t, TwitterFeeds.ME, this.meTimelineLock, this.meTimeline, minCount, Config.ME_MAX_AGE_MS);
	}

	public TweetList getTweetWrappedInList (Twitter t, long n) {
		TweetList ret = new TweetList();
		ret.addTweet(getTweet(t, Long.valueOf(n)));
		return ret;
	}

	public Tweet getTweet (Twitter t, Long id) {
		return fetchTweetViaCache(t, id, this.tweetCache);
	}

	public TweetList getMyReplies (Twitter t, int minCount) throws TwitterException {
		TweetList me = getMe(t, minCount);
		// TODO move this logic lower down?
		TweetList myReplies = new TweetList();
		for (Tweet tweet : me.getTweets()) {
			if (tweet.getInReplyId() > 0L) myReplies.addTweet(tweet);
		}
		return myReplies;
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	Static implementation.

	private static TweetList getTweetList (Twitter t, TwitterFeeds feed, ReadWriteLock lock, AtomicReference<TweetList> list, int minCount, long maxAge) throws TwitterException {
		lock.readLock().lock();
		try {
			if (expired(list.get(), maxAge)) {
				lock.readLock().unlock();
				lock.writeLock().lock();
				try {
					if (expired(list.get(), maxAge)) {
						TweetList timeline = fetchTwitterFeed(t, feed, minCount);
						list.set(timeline);
					}
				}
				finally {
					lock.readLock().lock();
					lock.writeLock().unlock();
				}
			}
			return list.get();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	private static boolean expired (TweetList list, long maxAge) {
		return list == null || list.getTime() + maxAge < System.currentTimeMillis();
	}

	/**
	 * TODO Pass in list form last call to reuse where possible.
	 */
	private static TweetList fetchTwitterFeed (Twitter t, TwitterFeed feed, int minCount) throws TwitterException {
		TweetList ret = new TweetList();
		int pageSize = Math.min(minCount, Config.TWEET_FETCH_PAGE_SIZE);
		int page = 1; // First page is 1.
		while (ret.tweetCount() < minCount) {
			Paging paging = new Paging(page, pageSize);
			ResponseList<Status> timelinePage = feed.getTweets(t, paging);
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

	private static Tweet fetchTweetViaCache (final Twitter twitter, final Long lid, Cache<Long, Tweet> cache) {
		// Optimistic locking.
		Tweet tweet = cache.getIfPresent(lid);
		if (tweet == null || tweet.isExpiredPlaceholder()) {
			tweet = fetchTweetOrErrObj(twitter, lid);
			cache.put(lid, tweet);
		}
		return tweet;
	}

	private static Tweet fetchTweetOrErrObj (final Twitter twitter, final Long lid) {
		try {
			return fetchTweet(twitter, lid.longValue());
		}
		catch (TwitterException e) {
			String msg;
			if (e.getStatusCode() == HttpServletResponse.SC_NOT_FOUND) {
				msg = "[deleted]";
			}
			else {
				msg = e.getMessage();
			}
			return deadTweet(lid.longValue(), "[unknown]", msg);
		}
	}

	protected static Tweet fetchTweet (Twitter t, long id) throws TwitterException {
		return convertTweet(t.showStatus(id));
	}

	private static Tweet convertTweet (Status s) {
		Tweet t = new Tweet();
		t.setId(s.getId());
		t.setCreatedAt(s.getCreatedAt());
		t.setInReplyId(s.getInReplyToStatusId());
		t.setUser(s.getUser().getScreenName());
		t.setName(s.getUser().getName());
		t.setBody(s.getText());
		return t;
	}

	protected static Tweet deadTweet (long id, String user, String msg) {
		Tweet t = new Tweet();
		t.markAsPlaceholder();
		t.setId(id);
		t.setCreatedAt(new Date());
		t.setBody(msg);
		t.setUser(user);
		return t;
	}
}
