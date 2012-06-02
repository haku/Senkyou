package com.vaguehope.senkyou.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class Model {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	// List of top-level data objects.
	private static final Class<?>[] MODEL_CLASSES = new Class<?>[] {
			TweetList.class,
			Tweet.class,
			User.class,
			UserData.class
	};

	static final String ENCODING = "UTF-8";

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static final JAXBContext JAXB_CONTEXT = createJAXBContext();

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private Model () {/* Static helper. */}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public static JAXBContext getJaxbContext () {
		if (JAXB_CONTEXT == null) throw new UnsupportedOperationException("JAXB context no available.");
		return JAXB_CONTEXT;
	}

	public static Marshaller getMarshaller () {
		return MARSHALLER_FACTORY.get();
	}

	public static Unmarshaller getUnmarshaller () {
		return UNMARSHALLER_FACTORY.get();
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static JAXBContext createJAXBContext () {
		try {
			return JAXBContext.newInstance(MODEL_CLASSES);
		}
		catch (JAXBException e) {
			// TODO log this.
			return null;
		}
	}

	private static final ThreadLocal<Marshaller> MARSHALLER_FACTORY = new ThreadLocal<Marshaller>() {
		@Override
		protected Marshaller initialValue () {
			try {
				Marshaller m = getJaxbContext().createMarshaller();
				m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				return m;
			}
			catch (JAXBException e) {
				throw new UnsupportedOperationException(e);
			}
		}
	};

	private static final ThreadLocal<Unmarshaller> UNMARSHALLER_FACTORY = new ThreadLocal<Unmarshaller>() {
		@Override
		protected Unmarshaller initialValue () {
			try {
				Unmarshaller u = getJaxbContext().createUnmarshaller();
				u.setSchema(null); // Not validating.
				return u;
			}
			catch (JAXBException e) {
				throw new UnsupportedOperationException(e);
			}
		}
	};

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Helpers.

	public static InputStream stringToInputStream (String s) {
		try {
			return new ByteArrayInputStream(s.getBytes(ENCODING));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("JVM is lacking encoding: " + ENCODING);
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
