package com.vaguehope.senkyou.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tweet")
@XmlAccessorType(XmlAccessType.NONE)
public class Tweet {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@XmlElement(name = "username") private String username;
	@XmlElement(name = "body") private String body;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public String getUsername () {
		return this.username;
	}
	
	public String getBody () {
		return this.body;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void setUsername (String username) {
		this.username = username;
	}
	
	public void setBody (String body) {
		this.body = body;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
