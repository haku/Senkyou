package com.vaguehope.senkyou.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class JvmReporter {

	private static final long DELAY = 10L * 1000L; // 10 seconds.
	private static final long INTERVAL = 5L * 60L * 1000L; // 5 minutes.
	private static final long BYTES_IN_MB = 1024L * 1024L;

	protected static final Logger LOG = Logger.getLogger(JvmReporter.class.getName());

	private Timer timer = new Timer();

	public void start () {
		this.timer.scheduleAtFixedRate(new Task(), DELAY, INTERVAL);
	}

	public void dispose () {
		this.timer.cancel();
	}

	protected static void logStats () {
		StringBuilder s = new StringBuilder("JVM heap: ");
		long heapFreeSize = Runtime.getRuntime().freeMemory() / BYTES_IN_MB;
		long heapSize = Runtime.getRuntime().totalMemory() / BYTES_IN_MB;
		s.append(heapSize - heapFreeSize);
		s.append(" mb of ");
		s.append(heapSize);
		s.append(" mb used.");
		LOG.info(s.toString());
	}

	private static class Task extends TimerTask {

		public Task () {}

		@Override
		public void run () {
			logStats();
		}

	}

}
