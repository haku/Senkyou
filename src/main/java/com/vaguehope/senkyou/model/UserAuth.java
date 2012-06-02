package com.vaguehope.senkyou.model;

import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import twitter4j.auth.AccessToken;

@XmlRootElement(name = "userAuth")
@XmlAccessorType(XmlAccessType.NONE)
public class UserAuth {

	@XmlAttribute private long created;
	@XmlAttribute private long userId;
	@XmlAttribute private String screenName;
	@XmlAttribute private String token;
	@XmlAttribute private String tokenSecret;

	public UserAuth () {}

	public UserAuth (AccessToken at) {
		this.created = System.currentTimeMillis();
		this.userId = at.getUserId();
		this.screenName = at.getScreenName();
		this.token = at.getToken();
		this.tokenSecret = at.getTokenSecret();
	}

	public long getCreated () {
		return this.created;
	}

	public long getUserId () {
		return this.userId;
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

	public static UserAuth fromXml (String s) throws JAXBException {
		return fromXml(Model.stringToInputStream(s));
	}

	public static UserAuth fromXml (InputStream is) throws JAXBException {
		return UserAuth.class.cast(Model.getUnmarshaller().unmarshal(is));
	}

}
