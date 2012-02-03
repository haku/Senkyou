package com.vaguehope.senkyou.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessorServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2124094443981251745L;

	private final HttpProcessor processor;

	public ProcessorServlet (HttpProcessor processor) {
		this.processor = processor;
	}
	
	public String getContext () {
		return this.processor.getContext();
	}
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processor.processRequest(req, resp);
	}
	
}
