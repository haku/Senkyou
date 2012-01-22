package com.vaguehope.senkyou.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new RuntimeException("Failed to create direactory '"+f.getAbsolutePath()+"'.");
			}
		}
		
		return path;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String KEY_token = "token";
	private static final String KEY_tokenSecret = "tokenSecret";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	static public File writeAuthData (String username, AccessToken accessToken) throws IOException {
		String fpath = getConfigDir() + "/" + username + ".properties";
		File f = new File(fpath);
		
		Properties props = new Properties();
		props.setProperty(KEY_token, accessToken.getToken());
		props.setProperty(KEY_tokenSecret, accessToken.getTokenSecret());
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			props.store(fos, null);
		}
		finally {
			if (fos != null) fos.close();
		}
		
		return f;
	}
	
	static public AccessToken readAppAuthData () throws IOException {
		String path = getConfigDir() + "/appauth";
		return readAuthData(path);
	}
	
	static public AccessToken readUserAuthData (String username) throws IOException {
		String path = getConfigDir() + "/" + username + ".properties";
		return readAuthData(path);
	}
	
	static private AccessToken readAuthData (String path) throws IOException {
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
		
		String token = props.getProperty(KEY_token);
		String tokenSecret = props.getProperty(KEY_tokenSecret);
		
		AccessToken accessToken = new AccessToken(token, tokenSecret);
		return accessToken;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
