package com.vaguehope.senkyou.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

public class Model {
	
	private static Class<?>[] MODEL_CLASSES = new Class<?>[]{
		TweetList.class
	};
	
	private static final JAXBContext JAXB_CONTEXT = createJAXBContext();
	
	private static JAXBContext createJAXBContext () {
		try {
			return JAXBContext.newInstance(MODEL_CLASSES);
		}
		catch (JAXBException e) {
			// TODO log this.
			return null;
		}
	}
	
	public static JAXBContext getJaxbContext () {
		if (JAXB_CONTEXT == null) throw new UnsupportedOperationException("JAXB context no available.");
		return JAXB_CONTEXT;
	}
	
	public static Marshaller getMarshaller () {
		Marshaller m = MARSHALLER_FACTORY.get();
		try {
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		catch (PropertyException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		return m;
	}
	
	private static final ThreadLocal<Marshaller> MARSHALLER_FACTORY = new ThreadLocal<Marshaller>() {
		@Override
		protected Marshaller initialValue () {
			try {
				return getJaxbContext().createMarshaller();
			}
			catch (JAXBException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}
	};
	
}
