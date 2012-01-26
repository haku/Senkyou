package com.vaguehope.senkyou.model;

import static com.vaguehope.senkyou.util.Dates.fixDate;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.primitives.Longs;

@XmlRootElement(name = "tweet")
@XmlAccessorType(XmlAccessType.NONE)
public class Tweet {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@XmlAttribute(name = "id") private volatile long id;
	@XmlAttribute(name = "rid") private volatile long inReplyId;
	@XmlAttribute(name = "user") private volatile String user;
	@XmlAttribute(name = "name") private volatile String name;
	@XmlAttribute(name = "created") private volatile Date createdAt;
	@XmlElement(name = "body") private volatile String body;
	@XmlElement(name = "tweet") private volatile Set<Tweet> replies;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public long getId () {
		return this.id;
	}
	
	public Date getCreatedAt () {
		return fixDate(this.createdAt);
	}
	
	public long getInReplyId () {
		return this.inReplyId;
	}
	
	public String getUser () {
		return this.user;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String getBody () {
		return this.body;
	}
	
	public boolean hasReplies () {
		return this.replies == null ? false : this.replies.size() > 0;
	}
	
	public Set<Tweet> getReplies () {
		return this.replies;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void setId (long id) {
		this.id = id;
	}
	
	public void setCreatedAt (Date createdAt) {
		this.createdAt = fixDate(createdAt);
	}
	
	public void setInReplyId (long inReplyId) {
		this.inReplyId = inReplyId > 0 ? inReplyId : 0;
	}
	
	public void setUser (String user) {
		this.user = user;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public void setBody (String body) {
		this.body = body;
	}
	
	public boolean addReply (Tweet reply) {
		if (this.replies == null) this.replies = Collections.synchronizedSet(new LinkedHashSet<Tweet>());
		return this.replies.add(reply);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@Override
	public boolean equals (Object o) {
		if (o == null) return false;
		if (!(o instanceof Tweet)) return false;
		Tweet t = (Tweet) o;
		if (t.id == 0) return false; // Remote ID must be set.
		return this.id == t.id;
	}
	
	@Override
	public int hashCode () {
		return Longs.hashCode(this.id);
	}
	
	@Override
	public String toString () {
		StringBuilder s = new StringBuilder();
		s.append("Tweet{id=").append(this.id)
				.append(" body=").append(this.body)
				.append(" replies=").append(this.replies == null ? 0 : this.replies.size())
				.append("}");
		return s.toString();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	protected Date getCreatedAtUnsafe () {
		return this.createdAt;
	}
	
	public enum Comp implements Comparator<Tweet> {
		NEWEST_FIRST {
			@Override
			public int compare (Tweet o1, Tweet o2) {
				return o2.getCreatedAtUnsafe().compareTo(o1.getCreatedAtUnsafe());
			}
		};
		
		@Override
		public abstract int compare (Tweet o1, Tweet o2);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void printTweet (PrintStream ps) {
		PrintWriter w = new PrintWriter(ps);
		printTweet(w);
		w.flush();
	}
	
	public void printTweet (PrintWriter w) {
		printTweet(w, this, "t>");
	}
	
	private static void printTweet (PrintWriter w, Tweet t, String indent) {
		w.println(indent + t.toString());
		if (t.hasReplies()) {
			for (Tweet r : t.getReplies()) {
				printTweet(w, r, indent + ">");
			}
		}
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
