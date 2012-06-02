package com.vaguehope.senkyou;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.vaguehope.senkyou.model.UserAuth;
import com.vaguehope.senkyou.twitter.TwitterConfigHelper;

public class DataStore {

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

	public void putUserAuth (HttpSession httpSession, Twitter t) throws TwitterException {
		JedisPool jedisPool = this.pool.get();
		Jedis jedis = jedisPool.getResource();
		try {
			PrintWriter data = new PrintWriter(new ByteArrayOutputStream());
			new UserAuth(t.getOAuthAccessToken()).toXml(data);
			jedis.set(httpSession.getId(), data.toString());
		}
		catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
		finally {
			jedisPool.returnResource(jedis);
		}
	}

	public Twitter getUserAuth (HttpSession httpSession) {
		JedisPool jedisPool = this.pool.get();
		Jedis jedis = jedisPool.getResource();
		try {
			String data = jedis.get(httpSession.getId());
			if (data == null) return null;
			UserAuth userAuth = UserAuth.fromXml(data);
			Twitter twitter = TwitterConfigHelper.getTwitter();
			twitter.setOAuthAccessToken(userAuth.getAccessToken());
			return twitter;
		}
		catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
		finally {
			jedisPool.returnResource(jedis);
		}
	}

}
