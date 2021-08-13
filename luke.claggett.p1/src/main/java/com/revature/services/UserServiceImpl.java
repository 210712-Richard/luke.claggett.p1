package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.NotificationDao;
import com.revature.data.NotificationDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.util.Verifier;

@TraceLog
public class UserServiceImpl implements UserService {
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
	NotificationDao notDao = (NotificationDao) BeanFactory.getFactory().getObject(NotificationDao.class, NotificationDaoImpl.class);
	private static Logger log = LogManager.getLogger(UserServiceImpl.class);
	
	private static final Verifier VERIFIER = new Verifier();

	@Override
	public User login(String username) {
		if (!VERIFIER.verifyStrings(username)) {
			return null;
		}
		User user = userDao.getUser(username);
		log.debug("User returned: " + user);
		
		if (user != null) {
			user.setNotifications(notDao.getUserNotificationList(username));
		}
		
		return user;
	}

	@Override
	public User createUser(String username, String firstName, String lastName,
			UserType type, String deptName, String supervisorUsername) {
		User newUser = new User(username, firstName, lastName, type, deptName, supervisorUsername);
		log.debug("User being created: " + newUser);
		
		userDao.createUser(newUser);
		
		return newUser;
	}

	@Override
	public Boolean isUsernameUnique(String username) {
		User user = userDao.getUser(username);
		log.debug("User returned: " + user);
		
		if (user != null) {
			return false;
		}
		return true;
	}

	@Override
	public User updateUser(User user) {
		
		if (user == null) {
			return null;
		}
		
		userDao.updateUser(user);
		return user;
	}
	
	@Override
	public void deleteNotifications(String username) {
		if (VERIFIER.verifyStrings(username)) {
			notDao.deleteUserNotifications(username);
		}
	}

}
