package com.revature.controllers;

import io.javalin.http.Context;

public interface UserController {
	
	/**
	 * Allows the user to login
	 * @param ctx The Context.
	 * Should have username and password fields
	 */
	public void login(Context ctx);
	
	/**
	 * Allows the user to logout
	 * @param ctx The Context
	 * Should have nothing in the body.
	 */
	public void logout(Context ctx);
	
	/**
	 * Create a user
	 * @param ctx The Context.<br>
	 * Should have a username, password, firstname, lastname, email, type, 
	 * deptname, and supervisorusername in the body.
	 */
	public void createUser(Context ctx);
	
	/**
	 * Deletes the logged in user's notifications
	 * @param ctx The context
	 */
	public void deleteNotifications(Context ctx);
}
