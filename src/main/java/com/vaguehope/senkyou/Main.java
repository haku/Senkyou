package com.vaguehope.senkyou;

import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.vaguehope.senkyou.reporter.JvmReporter;
import com.vaguehope.senkyou.reporter.Reporter;
import com.vaguehope.senkyou.reporter.SessionReporter;
import com.vaguehope.senkyou.servlets.AuthServlet;
import com.vaguehope.senkyou.servlets.HttpProcessor;
import com.vaguehope.senkyou.servlets.ProcessorServlet;
import com.vaguehope.senkyou.servlets.TweetFeeds;
import com.vaguehope.senkyou.servlets.UserServlet;

public class Main {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static final int ACCEPTORS = 2;
	private static final int MAX_IDLE_TIME = 25000; // 25 seconds.
	private static final int SESSION_INACTIVE_TIMEOUT = 15 * 60; // 15 minutes.
	private static final int LOW_RESOURCES_CONNECTIONS = 100;
	private static final int LOW_RESOURCES_MAX_IDLE_TIME = 5000; // 5 seconds.

	private static final Logger LOG = Logger.getLogger(Main.class.getName());

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private final Server server;

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Main () throws Exception { // NOSONAR Exception is throw by Server.start().
		// Reporting.
		SessionReporter sessionReporter = new SessionReporter();
		Reporter reporter = new Reporter(new JvmReporter(), sessionReporter);
		reporter.start();
		
		// Servlet container.
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");
		
		// Session management.
		SessionManager sessionManager = servletHandler.getSessionHandler().getSessionManager();
		sessionManager.setMaxInactiveInterval(SESSION_INACTIVE_TIMEOUT);
		sessionManager.addEventListener(sessionReporter);

		// Servlets.
		servletHandler.addServlet(new ServletHolder(new AuthServlet()), AuthServlet.CONTEXT_SPEC);
		servletHandler.addServlet(new ServletHolder(new UserServlet()), UserServlet.CONTEXT);
		for (HttpProcessor proc : TweetFeeds.values()) {
			ProcessorServlet tweetServlet = new ProcessorServlet(proc);
			servletHandler.addServlet(new ServletHolder(tweetServlet), tweetServlet.getContext());
		}

		// Static files on classpath.
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		URL webroot = Main.class.getResource("/webroot");
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
