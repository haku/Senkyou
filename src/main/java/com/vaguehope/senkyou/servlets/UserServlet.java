package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.DataStore;
import com.vaguehope.senkyou.model.User;

public class UserServlet extends HttpServlet {

	public static final String CONTEXT = "/user";

	private static final long serialVersionUID = 2523156667833296569L;

	private transient final DataStore dataStore;

	public UserServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Twitter twitter = AuthServlet.getSessionTwitterOrSetError(req, resp, this.dataStore);
		if (twitter == null) return;

		try {
			User u = new User();
			u.setScreenname(twitter.getScreenName());

			resp.setContentType("text/xml;charset=UTF-8");
			u.toXml(resp.getWriter());
		}
		catch (TwitterException e) {
			throw new ServletException(e);
		}
		catch (JAXBException e) {
			throw new ServletException(e);
		}
	}

}
