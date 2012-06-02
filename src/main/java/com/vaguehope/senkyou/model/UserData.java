package com.vaguehope.senkyou.model;

import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import twitter4j.auth.AccessToken;

@XmlRootElement(name = "userData")
@XmlAccessorType(XmlAccessType.NONE)
public class UserData {

	@XmlAttribute private long created;
	@XmlAttribute private String screenName;
	@XmlAttribute private String token;
	@XmlAttribute private String tokenSecret;

	public UserData () {}

	public UserData (AccessToken at) {
		this.created = System.currentTimeMillis();
		this.screenName = at.getScreenName();
		this.token = at.getToken();
		this.tokenSecret = at.getTokenSecret();
	}

	public long getCreated () {
		return this.created;
	}

	public String getScreenName () {
		return this.screenName;
	}

	public AccessToken getAccessToken () {
		return new AccessToken(this.token, this.tokenSecret);
	}

	public void toXml (PrintWriter writer) throws JAXBException {
		Model.getMarshaller().marshal(this, writer);
	}

	public static UserData fromXml (String s) throws JAXBException {
		return fromXml(Model.stringToInputStream(s));
	}

	public static UserData fromXml (InputStream is) throws JAXBException {
		return UserData.class.cast(Model.getUnmarshaller().unmarshal(is));
	}

}
