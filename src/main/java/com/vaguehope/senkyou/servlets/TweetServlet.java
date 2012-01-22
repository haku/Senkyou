package com.vaguehope.senkyou.servlets;

import static com.vaguehope.senkyou.servlets.ServletHelper.validatePositiveIntParam;
import static com.vaguehope.senkyou.servlets.ServletHelper.validateStringParam;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.TweetList;
import com.vaguehope.senkyou.twitter.TweetCache;
import com.vaguehope.senkyou.twitter.TweetCacheFactory;

public class TweetServlet extends HttpServlet {
	
	public static final String CONTEXT = "/feeds/tweets";
	
	private static final long serialVersionUID = 2124094443981251745L;
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			procGet(req, resp);
		}
		catch (JAXBException e) {
			throw new ServletException(e);
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
		catch (ExecutionException e) {
			throw new ServletException(e);
		}
	}
	
	private static void procGet (HttpServletRequest req, HttpServletResponse resp) throws IOException, TwitterException, JAXBException, ExecutionException {
		String user = validateStringParam(req, resp, "u");
		if (user == null) return;
		
		int count = validatePositiveIntParam(req, resp, "n");
		if (count < 1) return;
		
		printHomeTimeline(resp, user, count);
	}
	
	private static void printHomeTimeline (HttpServletResponse resp, String username, int count) throws IOException, TwitterException, JAXBException, ExecutionException {
		TweetCache tweetCache = TweetCacheFactory.getTweetCache(username);
		TweetList tl = tweetCache.getLastTweetHomeTimeline(count);
		tl.toXml(resp.getWriter());
	}
	
}
