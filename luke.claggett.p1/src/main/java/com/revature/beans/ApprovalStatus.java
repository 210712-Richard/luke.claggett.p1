package com.revature.beans;

public enum ApprovalStatus {
	
	/**
	 * The Request was approved by the specified user
	 */
	APPROVED,
	/**
	 * The Request is awaiting for the specified user to enter a status
	 */
	AWAITING,
	/**
	 * The Request was denied by the specified user
	 */
	DENIED, 
	/**
	 * The Request went through without needing the approval by the specified user
	 */
	BYPASSED, 
	/**
	 * The specified user did not approve in time so the request went through automatically
	 */
	AUTO_APPROVED,
	/**
	 * This Approval has not been assigned yet.
	 */
	UNASSIGNED
}
