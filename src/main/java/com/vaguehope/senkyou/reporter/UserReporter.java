package com.vaguehope.senkyou.reporter;

import com.vaguehope.senkyou.twitter.TweetCacheFactory;

public class UserReporter implements ReportProvider {

	@Override
	public void appendReport (StringBuilder r) {
		r.append(TweetCacheFactory.getCount()).append(" users.");
	}

}
