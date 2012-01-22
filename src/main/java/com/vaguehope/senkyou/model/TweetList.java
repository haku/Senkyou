package com.vaguehope.senkyou.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	@XmlElement(name = "tweet") private volatile List<Tweet> tweets = new ArrayList<Tweet>();
	
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
	
	public List<Tweet> getTweets () {
		return Collections.unmodifiableList(this.tweets);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void setTime (long time) {
		this.time = time;
	}
	
	public void addTweet (Tweet tweet) {
		this.tweets.add(tweet);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void toXml (PrintWriter writer) throws JAXBException {
		Model.getMarshaller().marshal(this, writer);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
