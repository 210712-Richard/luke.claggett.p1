package com.revature.util;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Verifier {

	private static final Logger log = LogManager.getLogger(Verifier.class);

	/**
	 * Checks to see if a list of strings is null or blank
	 * 
	 * @param strinsg The strings to check
	 * @return true if all the strings is not null or blank; false otherwise.
	 */
	public Boolean verifyStrings(String... strings) {
		log.trace("Checking to see if any of these strings are invalid: " + Arrays.toString(strings));
		if (strings == null || strings.length == 0) {
			return false;
		}

		// This will loop through and see if any string is invalid
		Boolean isValid = Stream.of(strings).allMatch((string) -> (string != null && !string.isBlank()));

		// Will then return the result
		log.debug("All the strings are valid: " + isValid);
		return isValid;
	}

	/**
	 * Checks to see if the passed in objects are null
	 * @param objects The objects to check
	 * @return true if all objects are not null; false otherwise
	 */
	public Boolean verifyNotNull(Object...objects) {
		//If the objects array is null or the length is 0
		if (objects == null || objects.length == 0) {
			return false;
		}
		
		//Checks to see if all the objects are not null
		Boolean isValid = Stream.of(objects).allMatch((o) -> o != null);
		log.debug("All the objects are not null: " + isValid);
		return isValid;
	}
}
