package com.vaguehope.senkyou;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.UserData;
import com.vaguehope.senkyou.servlets.CookieHelper;
import com.vaguehope.senkyou.twitter.TwitterConfigHelper;

public class DataStore {

	private static final Logger LOG = Logger.getLogger(DataStore.class.getName());

	private final AtomicReference<JedisPool> pool = new AtomicReference<JedisPool>();

	public void start () {
		try {
			URI redisURI = new URI(System.getenv("REDISTOGO_URL")); // Heroko pattern.
			JedisPool jp = new JedisPool(new JedisPoolConfig(),
					redisURI.getHost(),
					redisURI.getPort(),
					Protocol.DEFAULT_TIMEOUT,
					redisURI.getUserInfo().split(":", 2)[1]);
			this.pool.set(jp);
		}
		catch (URISyntaxException e) {
			throw new IllegalStateException();
		}
	}

	public void report (StringBuilder r) {
		JedisPool jedisPool = this.pool.get();
		Jedis jedis = jedisPool.getResource();
		try {
			r.append(jedis.dbSize()).append(" Redis entries.");
		}
		finally {
			jedisPool.returnResource(jedis);
		}
	}

	public void putUserData (HttpServletRequest req, Twitter t) throws TwitterException {
		JedisPool jedisPool = this.pool.get();
		Jedis jedis = jedisPool.getResource();
		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			PrintWriter dataPrinter = new PrintWriter(data);
			new UserData(t.getOAuthAccessToken()).toXml(dataPrinter);
			String key = req.getSession().getId();
			jedis.set(key, data.toString());
			jedis.expire(key, Config.DATASTORE_SESSION_EXPIRY);
		}
		catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
		finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Twitter getUser (HttpServletRequest req, HttpServletResponse resp) {
		String sessionId = CookieHelper.getExtraSessionId(req, resp);
		if (sessionId == null) return null;
		JedisPool jedisPool = this.pool.get();
		Jedis jedis = jedisPool.getResource();
		try {
			return getUser(req, sessionId, jedis);
		}
		finally {
			jedisPool.returnResource(jedis);
		}
	}

	private Twitter getUser (HttpServletRequest req, String sessionId, Jedis jedis) {
		String data = jedis.get(sessionId);
		if (data == null) return null;
		try {
			UserData user = UserData.fromXml(data);
			return getUser(req, sessionId, jedis, user);
		}
		catch (JAXBException e) {
			LOG.log(Level.WARNING, "Failed to parse data: '" + data + "'.", e);
			jedis.del(sessionId);
			return null;
		}
	}

	private Twitter getUser (HttpServletRequest req, String sessionId, Jedis jedis, UserData user) {
		Twitter twitter = TwitterConfigHelper.getTwitter();
		twitter.setOAuthAccessToken(user.getAccessToken());
		try {
			synchronized (req.getSession(true)) { // FIXME is this valid?
				if (twitter.verifyCredentials() != null) {
					putUserData(req, twitter);
					return twitter;
				}
			}
		}
		catch (TwitterException e) {
			// Do not care.
		}
		jedis.del(sessionId);
		return null;
	}

}
