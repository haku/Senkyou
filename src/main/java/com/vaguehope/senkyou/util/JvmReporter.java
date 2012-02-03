package com.vaguehope.senkyou.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class JvmReporter {

	private static final long DELAY = 10L * 1000L; // 10 seconds.
	private static final long INTERVAL = 5L * 60L * 1000L; // 5 minutes.

	protected static final Logger LOG = Logger.getLogger(JvmReporter.class.getName());

	private Timer timer = new Timer();

	public void start () {
		this.timer.scheduleAtFixedRate(new Task(), DELAY, INTERVAL);
	}

	public void dispose () {
		this.timer.cancel();
	}

	private static class Task extends TimerTask {

		public Task () {}

		@Override
		public void run () {
			StringBuilder s = new StringBuilder("JVM heap: ");
			long heapFreeSize = Runtime.getRuntime().freeMemory() / 1024 / 1024;
			long heapSize = Runtime.getRuntime().totalMemory() / 1024 / 1024;
			s.append(heapSize - heapFreeSize);
			s.append(" mb of ");
			s.append(heapSize);
			s.append(" mb used.");
			LOG.info(s.toString());
		}

	}

}
