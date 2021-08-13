package com.revature.services;

import com.revature.beans.User;
import com.revature.beans.UserType;

public interface UserService {

	/**
	 * Allows the user to login using their username and password
	 * 
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The user with the same username and password; null otherwise
	 */
	public User login(String username);

	/**
	 * Create the user with the arguments
	 * @param username The username of the user
	 * @param password The password of the user
	 * @param firstName The first name of the user
	 * @param lastName The last name of the user
	 * @param email The email of the user
	 * @param type The UserType of the user
	 * @param supervisorUsername The username of the supervisor of the user
	 * @return The new User
	 */
	public User createUser(String username, String firstName, String lastName, 
			UserType type, String deptName,  String supervisorUsername);
	
	/**
	 * Checks to see if a username is already in the database
	 * @param username The username to check and see if it is in the database
	 * @return true if the user is not in the database; false otherwise
	 */
	public Boolean isUsernameUnique(String username);
	
	/**
	 * Update the user and put them in the database
	 * @param user The user being updated
	 * @return The updated user
	 */
	public User updateUser(User user);
	
	/**
	 * Delete the user's notifications
	 * @param username The username of the user
	 */
	public void deleteNotifications(String username);
}
