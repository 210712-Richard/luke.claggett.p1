package com.revature.beans;

public enum ReimbursementStatus {
	/**
	 * The Request is still being processed or is awaiting the grade/presentation of
	 * the user
	 */
	ACTIVE,
	/**
	 * The Request has been approved and the user will get the reimbursement
	 */
	APPROVED,
	/**
	 * The Request has been denied by a approver
	 */
	DENIED,
	/**
	 * The Request has been cancelled by the user who created it
	 */
	CANCELLED, 
	/**
	 * The request has been awarded to the user
	 */
	AWARDED
}
