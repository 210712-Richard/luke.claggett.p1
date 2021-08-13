package com.revature.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Notification;

public class NotificationDaoTest {
	NotificationDao notDao = null;
	Notification notification = null;

	@BeforeEach
	public void beforeEach() {
		notDao = new NotificationDaoImpl();
		notification = new Notification("TestUser", UUID.fromString("ddd9e879-52d3-47ad-a1b6-87a94cbb321d"),
				"This is a test");

	}

	@Test
	public void testGetNotificationUserList() {
		List<Notification> notifications = notDao.getUserNotificationList(notification.getUsername());

		assertTrue(notifications != null, "Assert that the returned list is not null.");

		assertThrows(Exception.class, () -> notDao.getUserNotificationList(null),
				"Assert that a null username throws an exception.");
	}

	@Test
	public void testDeleteUserNotifications() {
		assertAll("Assert that an exception is not thrown for the deletion",
				() -> notDao.deleteUserNotifications(notification.getUsername()));
		
		assertThrows(Exception.class, () -> notDao.deleteUserNotifications(null),
				"Assert that a null username throws an exception.");
	}
	
	@Test
	public void testDeleteNotification() {
		assertAll("Assert that an exception is not thrown for the deletion",
				() -> notDao.deleteNotification(notification.getUsername(), notification.getRequestId()));
		
		assertThrows(Exception.class, () -> notDao.deleteNotification(null, notification.getRequestId()),
				"Assert that a null username throws an exception.");
		
		assertThrows(Exception.class, () -> notDao.deleteNotification(notification.getUsername(), null),
				"Assert that a null requestId throws an exception.");
	}
	
	@Test
	public void testCreateNotification() {
		
		assertAll("Assert that an exception is not thrown for the deletion",
				() -> notDao.createNotification(notification));
		
		assertThrows(Exception.class, () -> notDao.createNotification(null),
				"Assert that a null username throws an exception.");
	}
}
