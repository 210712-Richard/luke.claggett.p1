package com.revature.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.User;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.UserService;
import com.revature.services.UserServiceImpl;

import io.javalin.http.Context;

@TraceLog
public class UserControllerImpl implements UserController {

	UserService userService = (UserService) BeanFactory.getFactory().getObject(UserService.class,
			UserServiceImpl.class);

	// Log
	private static Logger log = LogManager.getLogger(UserControllerImpl.class);

	@Override
	public void login(Context ctx) {

		User user = ctx.bodyAsClass(User.class);
		log.debug("User object from body: " + user);

		user = userService.login(user.getUsername());
		log.debug("User returned from the service: " + user);

		if (user == null) {
			ctx.status(401);
			return;
		}

		ctx.sessionAttribute("loggedUser", user);
		ctx.json(user);
	}

	@Override
	public void logout(Context ctx) {
		ctx.req.getSession().invalidate();
		ctx.status(204);
	}

	@Override
	public void createUser(Context ctx) {
		User user = ctx.bodyAsClass(User.class);
		log.debug("User from the body: " + user);
		String username = ctx.pathParam("username");
		log.debug("Username from the path: " + username);
			System.out.println(username);
		if (!user.getUsername().equals(username)) {
			ctx.status(400);
			ctx.html("Usernames do not match");
			return;
		}

		if (userService.isUsernameUnique(user.getUsername())) {
			user = userService.createUser(user.getUsername(), user.getFirstName(),
					user.getLastName(), user.getType(), user.getDepartmentName(), user.getSupervisorUsername());
			log.debug("User added to the database: " + user);
			ctx.status(201);
			ctx.json(user);
		} else {
			ctx.status(409);
			ctx.html("Username is already taken");
		}
	}
	
	@Override
	public void deleteNotifications(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		log.debug("Logged in user: " + loggedUser);
		String username = ctx.pathParam("username");
		log.debug("Username from the path: " + username);
		
		if (loggedUser == null || !loggedUser.getUsername().equals(username)) {
			ctx.status(403);
			return;
		}
		
		userService.deleteNotifications(username);
		loggedUser.getNotifications().clear();
		
		ctx.status(204);
	}
}
