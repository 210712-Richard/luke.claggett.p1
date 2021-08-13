package com.revature.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
		private String username;
		private String firstName;
		private String lastName;
		private UserType type;
	private String departmentName;
	private String supervisorUsername;
	private Double pendingBalance;
	private Double awardedBalance;
	private List<UUID> requests;
	private List<Notification> notifications;

	/**
	 * Generates a random id and sets the balances to 0 and the Request Lists to a
	 * empty Lists
	 */
	
	public User() {
		super();
		this.pendingBalance = 0.00;
		this.awardedBalance = 0.00;
		this.requests = new ArrayList<>();
	}
	

	public User(String username, String firstName, String lastName, UserType type,
			String departmentName, String supervisorUsername) {
		this();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.type = type;
		this.departmentName = departmentName;
		this.supervisorUsername = supervisorUsername;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getSupervisorUsername() {
		return supervisorUsername;
	}

	public void setSupervisorUsername(String supervisorUsername) {
		this.supervisorUsername = supervisorUsername;
	}

	public Double getPendingBalance() {
		return pendingBalance;
	}

	public void setPendingBalance(Double pendingBalance) {
		this.pendingBalance = pendingBalance;
	}

	public Double getAwardedBalance() {
		return awardedBalance;
	}

	public void setAwardedBalance(Double awardedBalance) {
		this.awardedBalance = awardedBalance;
	}

	public Double getTotalBalance() {
		return pendingBalance + awardedBalance;
	}

	public List<UUID> getRequests() {
		return requests;
	}

	public void setRequests(List<UUID> requests) {
		this.requests = requests;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", type=" + type + ", departmentName=" + departmentName
				+ ", supervisorUsername=" + supervisorUsername + ", pendingBalance=" + pendingBalance
				+ ", awardedBalance=" + awardedBalance + ", requests=" + requests + ", notifications=" + notifications
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(awardedBalance, departmentName, firstName, lastName, notifications,
				pendingBalance, requests, supervisorUsername, type, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(awardedBalance, other.awardedBalance)
				&& Objects.equals(departmentName, other.departmentName)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(notifications, other.notifications)
				&& Objects.equals(pendingBalance, other.pendingBalance) && Objects.equals(requests, other.requests)
				&& Objects.equals(supervisorUsername, other.supervisorUsername) && type == other.type
				&& Objects.equals(username, other.username);
	}

	public void alterPendingBalance(Double cost) {
		this.pendingBalance += cost;
	}

	public void alterAwardedBalance(Double cost) {
		this.awardedBalance += cost;
	}

}
