package com.revature.exceptions;

/**
 * Exeception used whenever an approval is attempted on an awaiting approval 
 *
 */
public class IllegalApprovalAttemptException extends RuntimeException{
	/**
	 * Default value that RuntimeException wants there
	 */
	private static final long serialVersionUID = 1L;

	public IllegalApprovalAttemptException(String exception) {
		super(exception);
	}
}
