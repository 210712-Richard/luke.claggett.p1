package com.revature.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TraceLogProxy implements InvocationHandler{
	//Used to log
	private Logger log;
	//The object being logged
	private Object object;
	
	public TraceLogProxy(Object object) {
		this.object = object;
		log = LogManager.getLogger(object.getClass());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object retObject = null;
		try {
			//If the method is called with no parameters
			if (args == null) {
				log.trace(method + " has been called. Parameters: None");
				retObject = method.invoke(object);
			} 
			//The method is called with parameters
			else {
				log.trace(method + " has been called. Parameters: " + Arrays.toString(args));
				retObject = method.invoke(object, args);
			}
		} catch(Exception e) {
			log.error(method + " threw exception: " + e);
			//Loop through and log the stack trace
			for (StackTraceElement element : e.getStackTrace()) {
				log.warn(element);
			}
			//If the exception has a wrapped exception
			if (e.getCause() != null) {
				Throwable cause = e.getCause();
				log.error(method + " threw wrapped exception: " + cause);
				//Loop through and log the wrapped stack trace
				for (StackTraceElement element : cause.getStackTrace()) {
					log.warn(element);
				}
				//Throw the exception as is
				throw e;
			}
		}
		log.trace(method + " is returning with Object: " + retObject);
		return retObject;
	}
}
