package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.revature.beans.ApprovalStatus;
import com.revature.beans.EventType;
import com.revature.beans.GradingFormat;
import com.revature.beans.Reimbursement;

public interface ReimbursementService {
	
	Object APPROVAL_LOCK = new Object();
	
	/**
	 * Creates a request with the parameters
	 * @param username The username of the user
	 * @param firstName The first name of the user
	 * @param lastName The last name of the user
	 * @param deptName The department name of the user
	 * @param name The name of the event
	 * @param startDate The start date of the event
	 * @param startTime The start time of the event
	 * @param location The location of the event
	 * @param description The description of the event
	 * @param cost The cost of the event
	 * @param gradingFormat The grading format of the event
	 * @param type The type of event the event is
	 * @return A new Request
	 */
	public Reimbursement createRequest(String username, String firstName, String lastName, String deptName, String name, LocalDate startDate,
			LocalTime startTime, String location, String description, Double cost, GradingFormat gradingFormat,
			EventType type);
	
	/**
	 * Change the approval status of the request 
	 * @param request The request being changed
	 * @param status The status the request is adding 
	 * @param reason The potential reason (can be null if just being approved)
	 * @param username The username of the user doing the approving
	 * @return The request. Will return null if there was an issue
	 */
	public Reimbursement changeApprovalStatus(Reimbursement request, ApprovalStatus status, String reason);
	
	/**
	 * Get the Request based on the ID of the request
	 * @param id The ID of the request
	 * @return The Request with the same id; null otherwise
	 */
	public Reimbursement getRequest(UUID id);
	
	/**
	 * Send the Request to the DAO to save to the database
	 * @param request The request being saved
	 */
	public void updateRequest(Reimbursement request);
	
	/**
	 * Cancel the Request of the user
	 * @param request The Request being cancelled.
	 */
	public void cancelRequest(Reimbursement request);
	
	/**
	 * Change the final reimburse amount and set other fields
	 * @param request The request that is changing
	 * @param reimburse The amount the reimburse is chaning to
	 * @param reason The reason for the reimburse changed
	 * @return The request back. Null if there was an issue
	 */
	public Reimbursement changeReimburseAmount(Reimbursement request, Double reimburse, String reason);
	
	/**
	 * Change whether the employee agrees with the new reimburse amount
	 * @param request The request being changed
	 * @param employeeAgrees Whether the employee agrees or not
	 */
	public void changeEmployeeAgrees(Reimbursement request, Boolean employeeAgrees);
	
	/**
	 * Add the final grade to the request
	 * @param request The request getting the final grade
	 * @param grade The final grade
	 */
	public void addFinalGrade(Reimbursement request, String grade);
	
	/**
	 * Auto approves the request
	 */
	public void autoApprove();

}
