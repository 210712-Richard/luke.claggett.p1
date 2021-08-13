package com.revature.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeanFactory {
	// Used for logging
	private static Logger log = LogManager.getLogger(BeanFactory.class);
	// The instance of the factory
	private static BeanFactory beanFactory = null;

	// Private Constructor
	private BeanFactory() {
		super();
	}

	public static synchronized BeanFactory getFactory() {
		// If the instance is null, create a new instance
		if (beanFactory == null) {
			beanFactory = new BeanFactory();
		}
		return beanFactory;
	}

	public Object getObject(Class<?> inter, Class<?> clazz) {
		if (!clazz.isAnnotationPresent(TraceLog.class)) {
			log.error(clazz.getName() + " does not have @TraceLog annotation.");
			throw new RuntimeException(clazz.getName() + " does not have @TraceLog annotation.");
		}
		Object object = null;
		Constructor<?> constructor;

		try {
			// This means each class needs a no parameters constructor
			constructor = clazz.getConstructor();

			// Create a proxy of the interface that the class implements
			object = Proxy.newProxyInstance(inter.getClassLoader(), new Class[] { inter },
					new TraceLogProxy(constructor.newInstance()));
			
		} catch (Exception e) {
			log.error("getObject has thrown exception " + e);

			// Loop through and log the stack trace
			for (StackTraceElement element : e.getStackTrace()) {
				log.warn(element);
			}
			// Throw a new RuntimeException with e
			throw new RuntimeException(e);
		}
		return object;
	}
}
