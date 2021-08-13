package com.revature.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.NotificationDao;
import com.revature.data.UserDao;
import com.revature.util.MockitoHelper;

public class UserServiceTest {
	private UserService service = null;
	private User user = null;
	private UserDao dao = null;
	private NotificationDao notDao = null;

	private static MockitoHelper mock = null;

	@BeforeAll
	public static void beforeAll() {
		mock = new MockitoHelper();
	}

	@BeforeEach
	public void beforeTest() {
		service = new UserServiceImpl();

		user = new User("Tester", "Test", "User", UserType.EMPLOYEE, "Test", "TestSuper");

		dao = (UserDao) mock.setPrivateMock(service, "userDao", UserDao.class);
		
		notDao = (NotificationDao) mock.setPrivateMock(service, "notDao", NotificationDao.class);
	}

	@Test
	public void testLoginValid() {
		// Set up mockito so that when dao.getUser is called, it will return the user as
		// it should.
		Mockito.when(dao.getUser(user.getUsername())).thenReturn(user);

		// Use ArgumentCaptor to get arguments
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> notUserCaptor = ArgumentCaptor.forClass(String.class);
		
		// Get the user using the user credentials
		User loginUser = service.login(user.getUsername());

		// Make sure the user is the same.
		assertEquals(user, loginUser, "Assert that the user returned is the same user.");

		// Verify getUser was called and get the arguments used
		Mockito.verify(dao).getUser(usernameCaptor.capture(), passwordCaptor.capture());
		
		Mockito.verify(notDao).getUserNotificationList(notUserCaptor.capture());

		// Make sure the arguments are correct
		assertEquals(user.getUsername(), usernameCaptor.getValue(),
				"Assert that the username used is the user's username.");
		assertEquals(user.getUsername(), notUserCaptor.getValue(),
				"Assert that the username used for getting notifications is the user's username.");
	}

	@Test
	public void testLoginInvalid() {
		// Set up the mock for the wrong combination:
		String wrong = "Wrong";
		Mockito.when(dao.getUser(user.getUsername(), wrong)).thenReturn(null);

		// The username/password combo is wrong
		User nullUser = service.login(user.getUsername());
		assertNull("Assert that an incorrect password returns a null User.", nullUser);

		nullUser = service.login(wrong);
		assertNull("Assert that an incorrect username returns a null User.", nullUser);

		// The username and/or password is blank
		nullUser = service.login(user.getUsername());
		assertNull("Assert that a blank password returns a null User.", nullUser);

		nullUser = service.login("   ");
		assertNull("Assert that a blank username returns a null User.", nullUser);

		// The username and/or password is null
		nullUser = service.login(user.getUsername());
		assertNull("Assert that a null password returns a null User.", nullUser);

		nullUser = service.login(null);
		assertNull("Assert that a null username returns a null User.", nullUser);
	}

	@Test
	public void testCreateUserValid() {
		// Have a captor ready to get arguments from Mockito
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		// Call the method
		User newUser = service.createUser(user.getUsername(), user.getFirstName(),
				user.getLastName(), user.getType(), user.getDepartmentName(), user.getSupervisorUsername());

		// Verify the dao.createUser is called and capture the arguments
		Mockito.verify(dao).createUser(captor.capture());

		// Make sure the argument matches the user
		User capture = captor.getValue();
		assertEquals(user, capture,
				"Assert that the user passed in is the same as the user with the details passed in.");
		assertEquals(user, newUser, "Assert that the new user is the same as the other user.");
	}

	@Test
	public void testIsUsernameUniqueValid() {
		String unique = "Unique_Username";
		// Use captor to get the username
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		// Set up Mockito to return a null user
		Mockito.when(dao.getUser(unique)).thenReturn(null);

		// Call the method and make sure it returns true.
		Boolean isUnique = service.isUsernameUnique(unique);
		assertTrue(isUnique, "Assert that a unique username returns true.");
		
		//Verify and make sure the arguments are correct
		Mockito.verify(dao).getUser(captor.capture());
		assertEquals(unique, captor.getValue(), "Assert that the username passed in is passed into the getUser.");
	}

	@Test
	public void testIsUsernameUniqueInvalid() {
		// Use captor to get the username
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

		// Set up Mockito to return a null user
		Mockito.when(dao.getUser(user.getUsername())).thenReturn(user);

		// Call the method and make sure it returns false.
		Boolean isUnique = service.isUsernameUnique(user.getUsername());
		assertFalse(isUnique, "Assert that a unique username returns true.");

		//Verify and make sure the arguments are correct
		Mockito.verify(dao).getUser(captor.capture());
		assertEquals(user.getUsername(), captor.getValue(), "Assert that the username passed in is passed into the getUser.");
	}
	
	@Test
	public void testUpdateUserValid() {
		//Set the argument captor
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		
		//Change a field and call the method
		User updated = service.updateUser(user);
		
		//Make sure the updated User is the same as the current user
		assertEquals(user, updated, "Assert that the updated user is the same as the user.");
		
		//Verify that the dao.updateUser is called and that it is passing in the user
		Mockito.verify(dao).updateUser(captor.capture());
		assertEquals(user, captor.getValue(), "Assert that the user is passed in.");
		
	}
	
	@Test
	public void testUpdateUserInvalid() {
		
		//Null user should return null
		User updated = service.updateUser(null);
		
		assertNull("Assert that a null user argument returns a null user", updated);
		
	}
	
	@Test
	public void testDeleteNotificationsValid() {
		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		
		service.deleteNotifications(user.getUsername());
		
		Mockito.verify(notDao).deleteUserNotifications(usernameCaptor.capture());
		
		assertEquals(user.getUsername(), usernameCaptor.getValue(), "Assert that the username passed in is the same username.");
		
	}
	
	@Test
	public void testDeleteNotificationsInvalid() {
		service.deleteNotifications(null);
		service.deleteNotifications(" ");
		
		Mockito.verifyZeroInteractions(notDao);
	}

}
