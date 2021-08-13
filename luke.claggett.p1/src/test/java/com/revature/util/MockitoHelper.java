package com.revature.util;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

public class MockitoHelper {

	// Function to set the UserDAO dao to a Mockito mock to verify the functions are
	// being called.
	public Object setPrivateMock(Object set, String field, Class clazz) {
		// Using reflection to get the private dao.
		Object mock = Mockito.mock(clazz);
		try {
			FieldSetter.setField(set, set.getClass().getDeclaredField(field), mock);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return mock;
	}
}
