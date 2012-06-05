package com.vaguehope.senkyou.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.Config;
import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.model.Tweet;
import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;

public class ThreadServlet extends HttpServlet {

	public static final String CONTEXT = "/feeds/threads";

	private static final long serialVersionUID = 7600513438072003737L;

	private final DataStore dataStore;

	public ThreadServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;
		try {
			proc(resp, twitter);
		}
		catch (ExecutionException e) {
			throw new ServletException(e);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
		catch (JAXBException e) {
			throw new ServletException(e);
		}
	}

	public void proc (HttpServletResponse resp, Twitter twitter) throws ExecutionException, TwitterException, JAXBException, IOException {
		TweetCache tweetCache = TweetCacheFactory.getTweetCache(twitter);

		TweetList mentions = tweetCache.getMentions(twitter, Config.MENTIONS_LENGTH);
		TweetList myReplies = tweetCache.getMyReplies(twitter, Config.MY_REPLIES_LENGTH);

		Map<Long, Tweet> tmap = new HashMap<Long, Tweet>();
		putTweetsInMap(mentions, tmap);
		putTweetsInMap(myReplies, tmap);
		putRepliesInMap(tmap, twitter, tweetCache);

		resp.setContentType("text/xml;charset=UTF-8");
		TweetList out = new TweetList();
		out.addAdd(tmap.values());
		out.toXml(resp.getWriter());
	}

	private static void putTweetsInMap (TweetList list, Map<Long, Tweet> map) {
		for (Tweet t : list.getTweets()) {
			map.put(Long.valueOf(t.getId()), t); // TODO make Tweet equals() Long of id?
		}
	}

	public static void putRepliesInMap (Map<Long, Tweet> map, Twitter twitter, TweetCache tweetCache) {
		Queue<Tweet> queue = new LinkedList<Tweet>();
		queue.addAll(map.values());
		Tweet t;
		while ((t = queue.poll()) != null) {
			if (t.getInReplyId() > 0L) {
				Long inReplyId = Long.valueOf(t.getInReplyId());
				if (!map.containsKey(inReplyId)) {
					Tweet replyT = tweetCache.getTweet(twitter, inReplyId);
					map.put(inReplyId, replyT);
					queue.add(replyT);
				}
			}
		}
	}

}
