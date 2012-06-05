package com.vaguehope.senkyou.servlets;

import java.io.IOException;
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
import com.vaguehope.senkyou.twitter.TweetCacheFactory;

public class HomeTimelineServlet extends HttpServlet {

	public static final String CONTEXT = "/feeds/home";

	private static final long serialVersionUID = -8269125436984112817L;

	private final DataStore dataStore;

	public HomeTimelineServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;
		try {
			resp.setContentType("text/xml;charset=UTF-8");
			TweetCacheFactory.getTweetCache(twitter).getHomeTimeline(twitter, Config.HOME_TIMELINE_LENGTH).toXml(resp.getWriter());
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

}
