package com.vaguehope.senkyou.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import twitter4j.http.AccessToken;

public final class TwitterConfigHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private TwitterConfigHelper () {/* Static helper. */}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String DIR_CONFIG = "/.tweetvault";
	
	public static String getConfigDir () {
		String path = System.getProperty("user.home") + DIR_CONFIG;
		
		File f = new File(path);
		if (!f.exists() && !f.mkdirs()) {
			throw new UnsupportedOperationException("Failed to create direactory '"+f.getAbsolutePath()+"'.");
		}
		
		return path;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "tokenSecret";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static AccessToken readAppAuthData () throws IOException {
		String path = getConfigDir() + "/appauth";
		return readAuthData(path);
	}
	
	public static AccessToken readUserAuthData (String username) throws IOException {
		String path = getConfigDir() + "/" + username + ".properties";
		return readAuthData(path);
	}
	
	private static AccessToken readAuthData (String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) throw new FileNotFoundException(path);
		
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			props.load(fis);
		}
		finally {
			if (fis != null) fis.close();
		}
		
		String token = props.getProperty(KEY_TOKEN);
		String tokenSecret = props.getProperty(KEY_TOKEN_SECRET);
		
		return new AccessToken(token, tokenSecret);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
