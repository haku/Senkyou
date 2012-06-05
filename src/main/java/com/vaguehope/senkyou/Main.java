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

import com.vaguehope.senkyou.reporter.DataStoreReporter;
import com.vaguehope.senkyou.reporter.JvmReporter;
import com.vaguehope.senkyou.reporter.Reporter;
import com.vaguehope.senkyou.reporter.SessionReporter;
import com.vaguehope.senkyou.reporter.UserReporter;
import com.vaguehope.senkyou.servlets.AuthCallbackServlet;
import com.vaguehope.senkyou.servlets.AuthSigninServlet;
import com.vaguehope.senkyou.servlets.HomeTimelineServlet;
import com.vaguehope.senkyou.servlets.ThreadServlet;
import com.vaguehope.senkyou.servlets.UserServlet;

public class Main {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static final Logger LOG = Logger.getLogger(Main.class.getName());

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private final Server server;

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Main () throws Exception { // NOSONAR Exception is throw by Server.start().
		// Data store.
		DataStore ds = new DataStore();
		ds.start();

		// Reporting.
		SessionReporter sessionReporter = new SessionReporter();
		Reporter reporter = new Reporter(new JvmReporter(), sessionReporter, new UserReporter(), new DataStoreReporter(ds));
		reporter.start();

		// Servlet container.
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");

		// Session management.
		SessionManager sessionManager = servletHandler.getSessionHandler().getSessionManager();
		sessionManager.setMaxInactiveInterval(Config.SERVER_SESSION_INACTIVE_TIMEOUT_SECONDS);
		sessionManager.setMaxCookieAge(Config.SERVER_SESSION_INACTIVE_TIMEOUT_SECONDS);
		sessionManager.setSessionIdPathParameterName(null);
		sessionManager.addEventListener(sessionReporter);

		// Servlets.
		servletHandler.addServlet(new ServletHolder(new AuthSigninServlet(ds)), AuthSigninServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new AuthCallbackServlet(ds)), AuthCallbackServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new UserServlet(ds)), UserServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new ThreadServlet(ds)), ThreadServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new HomeTimelineServlet(ds)), HomeTimelineServlet.CONTEXT);

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
		connector.setMaxIdleTime(Config.SERVER_MAX_IDLE_TIME_MS);
		connector.setAcceptors(Config.SERVER_ACCEPTORS);
		connector.setStatsOn(false);
		connector.setLowResourcesConnections(Config.SERVER_LOW_RESOURCES_CONNECTIONS);
		connector.setLowResourcesMaxIdleTime(Config.SERVER_LOW_RESOURCES_MAX_IDLE_TIME_MS);
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

	public static void main (String[] args) throws Exception { // NOSONAR Exception is throw by
// Server.start().
		System.setProperty("twitter4j.http.useSSL", "true");

		Main m = new Main();
		m.join();
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
