package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaguehope.senkyou.Config;
import com.vaguehope.senkyou.DataStore;

public class SignoutServlet extends HttpServlet {

	public static final String CONTEXT = "/signout";

	private static final long serialVersionUID = -4925000182388313054L;

	private transient final DataStore dataStore;

	public SignoutServlet (DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.dataStore.deleteUser(req);
		CookieHelper.deleteCookies(req, resp);
		resp.sendRedirect(req.getContextPath() + Config.HOME_PAGE);
	}

}
