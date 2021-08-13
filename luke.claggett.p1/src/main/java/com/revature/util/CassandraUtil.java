package com.revature.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

public class CassandraUtil {
	private static CassandraUtil instance = null;
	private static final Logger log = LogManager.getLogger(CassandraUtil.class);
	private static final String KEYSPACE = "luke_claggett_p1";
	
	private CqlSession session = null;
	
	private CassandraUtil() {
		log.trace("Connecting to Cassandra...");
		//Get the loader data from the application.conf file in resources
		DriverConfigLoader loader = DriverConfigLoader.fromClasspath("application.conf");
		
		try {
			//Build the CqlSession
			this.session = CqlSession.builder().withConfigLoader(loader).withKeyspace(KEYSPACE).build();
		} catch(Exception e) {
			//Log the error and the stack trace.
			log.error("CassandraUtil constructor threw exception: " + e);
			for (StackTraceElement element : e.getStackTrace()) {
				log.warn(element);
			}
			//Throw the exception
			throw e;
		}
	}
	
	public static synchronized CassandraUtil getInstance() {
		//If the instance is null, get a new instance
		if (instance == null) {
			instance = new CassandraUtil();
		}
		return instance;
	}
	
	public CqlSession getSession() {
		return session;
	}
}
