package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpProcessor {
	
	String getContext ();
	
	void processRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
	
}
