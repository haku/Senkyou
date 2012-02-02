package com.vaguehope.senkyou.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public final class Model {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	// List of top-level data objects.
	private static final Class<?>[] MODEL_CLASSES = new Class<?>[] {
			TweetList.class,
			Tweet.class,
			User.class
	};
	
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
				m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				return m;
			}
			catch (JAXBException e) {
				throw new UnsupportedOperationException(e);
			}
		}
	};
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
