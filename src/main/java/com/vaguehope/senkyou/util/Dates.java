package com.vaguehope.senkyou.util;

import java.util.Date;

public final class Dates {
	
	private Dates () {/* Static helper */}
	
	public static Date fixDate (Date date) {
		return date == null ? null : new Date(date.getTime());
	}
}
