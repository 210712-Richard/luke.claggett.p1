package com.revature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.controllers.ReimbursementController;
import com.revature.controllers.ReimbursementControllerImpl;
import com.revature.controllers.UserController;
import com.revature.controllers.UserControllerImpl;
import com.revature.factory.BeanFactory;
import com.revature.services.ReimbursementService;
import com.revature.services.ReimbursementServiceImpl;
import com.revature.util.DatabaseCreator;

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Driver {
	private static Logger log = LogManager.getLogger(Driver.class);
	
	public static void main(String[] args) {
		setupDatabase();
		//startThread();
		//launchJavalin();

	}

	private static void startThread() {
		// Create a runnable that will check the database for expired, active requests
		// every 30 seconds.
		Runnable approvalRunnable = () -> {
			ReimbursementService reqService = (ReimbursementService) BeanFactory.getFactory().getObject(ReimbursementService.class,
					ReimbursementServiceImpl.class);
			while (true) {
				reqService.autoApprove();
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					log.error("startThread threw exception: " + e);
					//Loop through and log the stack trace
					for (StackTraceElement element : e.getStackTrace()) {
						log.warn(element);
					}
					if (e.getCause() != null) {
						Throwable cause = e.getCause();
						log.error("startThread threw wrapped exception: " + cause);
						//Loop through and log the wrapped stack trace
						for (StackTraceElement element : cause.getStackTrace()) {
							log.warn(element);
						}
					}
				}
			}
		};
		// Start the thread with the runnable
		Thread thread = new Thread(approvalRunnable);
		thread.start();
	}

	/**
	 * Used to launch Javalin to start the REST API
	 */
	private static void launchJavalin() {

		// Change mapper settings to make sure LocalDateTime objects show correctly
		ObjectMapper jackson = new ObjectMapper();
		jackson.registerModule(new JavaTimeModule());
		jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		JavalinJackson.configure(jackson);

		// Start Javalin to process HTTP requests
		Javalin app = Javalin.create().start(8080);

		// The controllers for users and requests.
		UserController userControl = (UserController) BeanFactory.getFactory().getObject(UserController.class,
				UserControllerImpl.class);
		ReimbursementController reqControl = (ReimbursementController) BeanFactory.getFactory().getObject(ReimbursementController.class,
				ReimbursementControllerImpl.class);

		// As a user, I can login
		app.post("/users", userControl::login);

		// As a user, I can logout
		app.delete("/users", userControl::logout);

		// Create user, mostly for testing purposes
		app.put("/users/:username", userControl::createUser);

		// Get Request
		app.get("/requests/:requestId", reqControl::getReimbursement);

		// As an employee, I can create a reimbursement request.
		app.post("/requests/", reqControl::createReimbursement);

		// Upload files as part of creating the reimbursement requests.
		app.post("/requests/:requestId/fileURIs", reqControl::uploadExtraFile);
		app.post("/requests/:requestId/approvalMsgsURIs", reqControl::uploadMessageFile);

		// Get the files
		app.get("/requests/:requestId/fileURIs/:index", reqControl::getFile);
		app.get("requests/:requestId/approvalMsgsURIs/:index", reqControl::getMessage);

		// As an employee, I can cancel my reimbursement request.
		app.put("/requests/:requestId/status", reqControl::cancelReimbursement);

		// As an employee, I can upload the final grade/presentation for the
		// reimbursement request.
		app.put("/requests/:requestId/presFileName", reqControl::uploadPresentation);
		app.get("/requests/:requestId/presFileName", reqControl::getPresentation);
		app.put("/requests/:requestId/finalGrade", reqControl::putFinalGrade);
		// As a supervisor, I can accept or decline a reimbursement request.
		// As a department head, I can accept or decline a reimbursement request.
		// As a Benefits Coordinator, I can accept or decline a reimbursement request.
		app.put("/requests/:requestId", reqControl::changeApprovalStatus);

		// As a Benefits Coordinator, I can alter the amount of the reimbursement
		// request.
		app.put("/requests/:requestId/finalReimburseAmount/finalReimburseAmountReason",
				reqControl::changeReimbursementAmount);
		app.put("/requests/:requestId/employeeAgrees", reqControl::finalReimbursementCheck);
		
		//Clear notifications from the user
		app.delete("/users/:username/notifications", userControl::deleteNotifications);

	}

	/**
	 * Used to setup the initial tables and populate with default rows
	 */
	private static void setupDatabase() {
		DatabaseCreator.dropTables();
		try {
			Thread.sleep(40000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseCreator.createTables();
		try {
			Thread.sleep(90000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseCreator.populateUser();
		DatabaseCreator.populateDepartment();
		System.exit(0);
	}
}
