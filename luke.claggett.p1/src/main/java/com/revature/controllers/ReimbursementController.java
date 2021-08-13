package com.revature.controllers;

import io.javalin.http.Context;

public interface ReimbursementController {
	/**
	 * Creates the request
	 * @param ctx The context.<br>
	 * The body should have firstName, lastName, deptName, startDate, startTime, location, description,
	 * cost, gradingFormat, and type
	 */
	public void createReimbursement(Context ctx);
	
	/**
	 * Change the approval status of the request
	 * @param ctx The context.<br>
	 * The body should have the approval and (if the request is declined) a reason
	 */
	public void changeApprovalStatus(Context ctx);
	
	/**
	 * Get the request by the requestId in the path param
	 * @param ctx The context
	 */
	public void getReimbursement(Context ctx);
	
	/**
	 * Allow the user to cancel their request
	 * @param ctx The context
	 */
	public void cancelReimbursement(Context ctx);
	
	/**
	 * Allow the user to upload extra files to their request
	 * @param ctx The context<br>
	 * The body should be the file and should include a filetype header
	 */
	public void uploadExtraFile(Context ctx);
	
	/**
	 * Upload an approval message
	 * @param ctx The context
	 */
	public void uploadMessageFile(Context ctx);
	
	/**
	 * Upload the final presentation
	 * @param ctx The context
	 */
	public void uploadPresentation(Context ctx);
	
	/**
	 * Get the file
	 * @param ctx The context
	 */
	public void getFile(Context ctx);
	
	/**
	 * Get a message file in the request
	 * @param ctx The context
	 */
	public void getMessage(Context ctx);
	/**
	 * Get the presentation
	 * @param ctx The context
	 */
	public void getPresentation(Context ctx);
	
	/**
	 * Change the Reimburse Amount
	 * @param ctx The context
	 */
	public void changeReimbursementAmount(Context ctx);
	
	/**
	 * Set whether the employee agrees with the finalReimburseAmount or not
	 * @param ctx The context
	 */
	public void finalReimbursementCheck(Context ctx);
	
	/**
	 * Set the final grade of the event
	 * @param ctx The context
	 */
	public void putFinalGrade(Context ctx);
}
