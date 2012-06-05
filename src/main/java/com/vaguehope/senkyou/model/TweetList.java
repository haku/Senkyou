package com.vaguehope.senkyou.model;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tweets")
@XmlAccessorType(XmlAccessType.NONE)
public class TweetList {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	@XmlAttribute(name = "time") private volatile long time;
	@XmlElement(name = "tweet") private volatile Set<Tweet> tweets = Collections.synchronizedSet(new LinkedHashSet<Tweet>());

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public TweetList () {
		this.time = System.currentTimeMillis();
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public long getTime () {
		return this.time;
	}

	public int tweetCount () {
		return this.tweets.size();
	}

	public Set<Tweet> getTweets () {
		return Collections.unmodifiableSet(this.tweets);
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public void setTime (long time) {
		this.time = time;
	}

	public void addTweet (Tweet t) {
		this.tweets.add(t);
	}

	public void addAdd (Collection<Tweet> t) {
		this.tweets.addAll(t);
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public void toXml (PrintWriter writer) throws JAXBException {
		Model.getMarshaller().marshal(this, writer);
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
