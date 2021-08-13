package com.revature.data;

import java.util.List;
import java.util.UUID;

import com.revature.beans.Reimbursement;

public interface ReimbursementDao {
	
	/**
	 * Get the Request by its id
	 * @param id The UUID of the Request
	 * @return The Request with the same UUID
	 */
	public Reimbursement getRequest(UUID id);
	
	/**
	 * Get all Requests in the database
	 * @return A List of all Requests
	 */
	public List<Reimbursement> getRequests();
	
	/**
	 * Get the list of requests that are active and deadline have passed.
	 * @return The list of requests
	 */
	public List<Reimbursement> getExpiredRequests();
	
	/**
	 * Update the Request
	 * @param request The request to update
	 */
	public void updateRequest(Reimbursement request);
	
	/**
	 * Create a new Request and put it in the database
	 * @param request The Request to put in the database
	 */
	public void createRequest(Reimbursement request);
	
}
