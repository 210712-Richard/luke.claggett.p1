package com.revature.data;

import com.revature.beans.User;

public interface UserDao {
	
	/**
	 * Get the user by their username
	 * @param username The username of the user
	 * @return The user with the username
	 */
	User getUser(String username);
	
	/**
	 * Get the user by their username and password
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return The user with the same username and password
	 */
	User getUser(String username, String password);
	
	/**
	 * Add a user to the database
	 * @param user The user to add to the database
	 */
	void createUser(User user);
	
	/**
	 * Update the user in the database
	 * @param user The user to update in the database
	 */
	void updateUser(User user);
}
