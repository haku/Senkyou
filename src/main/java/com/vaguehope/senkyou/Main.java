package com.vaguehope.senkyou;

import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.vaguehope.senkyou.servlets.ThreadServlet;
import com.vaguehope.senkyou.servlets.TweetServlet;

public class Main {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final int MAX_IDLE_TIME = 30000; // 30 seconds.
	private static final int ACCEPTORS = 2;
	private static final int LOW_RESOURCES_CONNECTIONS = 100;
	private static final int LOW_RESOURCES_MAX_IDLE_TIME = 5000; // 5 seconds.
	
	protected static final Logger LOG = Logger.getLogger(Main.class.getName());
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private Server server;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public Main () throws Exception { // NOSONAR Exception is throw by Server.start().
		// Servlet container.
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");
		
		// Tweet servlet.
		TweetServlet tweetServlet = new TweetServlet();
		servletHandler.addServlet(new ServletHolder(tweetServlet), TweetServlet.CONTEXT);
		
		// Thread servlet.
		ThreadServlet threadServlet = new ThreadServlet();
		servletHandler.addServlet(new ServletHolder(threadServlet), ThreadServlet.CONTEXT);
		
		// Static files on classpath.
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		URL webroot = getClass().getResource("/webroot");
		resourceHandler.setResourceBase(webroot.toExternalForm());
		
		// Prepare final handler.
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });
		
		// Listening connector.
		String portString = System.getenv("PORT"); // Heroko pattern.
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setMaxIdleTime(MAX_IDLE_TIME);
		connector.setAcceptors(ACCEPTORS);
		connector.setStatsOn(false);
		connector.setLowResourcesConnections(LOW_RESOURCES_CONNECTIONS);
		connector.setLowResourcesMaxIdleTime(LOW_RESOURCES_MAX_IDLE_TIME);
		connector.setPort(Integer.parseInt(portString));
		
		// Start server.
		this.server = new Server();
		this.server.setHandler(handlers);
		this.server.addConnector(connector);
		this.server.start();
		LOG.info("Server ready on port " + portString + ".");
	}
	
	public void join () throws InterruptedException {
		this.server.join();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static void main (String[] args) throws Exception { // NOSONAR Exception is throw by Server.start().
		Main m = new Main();
		m.join();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
