package com.revature.beans;

import java.util.Objects;

public class Approval {
	private ApprovalStatus status;
	private String username;

	public Approval() {
		super();
		this.status = ApprovalStatus.UNASSIGNED;
	}

	public Approval(ApprovalStatus status, String username) {
		this();
		this.status = status;
		this.username = username;
	}

	public ApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Approval [status=" + status + ", username=" + username + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Approval other = (Approval) obj;
		return status == other.status && Objects.equals(username, other.username);
	}

}
