package com.vaguehope.senkyou.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tweet")
@XmlAccessorType(XmlAccessType.NONE)
public class Tweet {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@XmlElement(name = "user") private volatile String user;
	@XmlElement(name = "name") private volatile String name;
	@XmlElement(name = "body") private volatile String body;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public String getUser () {
		return this.user;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String getBody () {
		return this.body;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void setUser (String user) {
		this.user = user;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public void setBody (String body) {
		this.body = body;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
