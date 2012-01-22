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

@XmlRootElement(name = "threads")
@XmlAccessorType(XmlAccessType.NONE)
public class ThreadList {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@XmlAttribute(name = "time") private volatile long time;
	@XmlElement(name = "thread") private volatile List<TweetList> threads = new ArrayList<TweetList>();
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public ThreadList () {
		this.time = System.currentTimeMillis();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public long getTime () {
		return this.time;
	}
	
	public int threadCount () {
		return this.threads.size();
	}
	
	public List<TweetList> getThreads () {
		return Collections.unmodifiableList(this.threads);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void setTime (long time) {
		this.time = time;
	}
	
	public void addThread (TweetList thread) {
		this.threads.add(thread);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void toXml (PrintWriter writer) throws JAXBException {
		Model.getMarshaller().marshal(this, writer);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
