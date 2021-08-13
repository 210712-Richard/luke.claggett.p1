package com.revature.beans;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Notification {
	private String username;
	private UUID requestId;
	private LocalDateTime notificationTime;
	private String message;

	public Notification() {
		super();
	}
	public Notification(String username, UUID requestId, String message) {
		this.username = username;
		this.message = message;
		this.requestId = requestId;
		this.notificationTime = LocalDateTime.now();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getNotificationTime() {
		return notificationTime;
	}

	public void setNotificationTime(LocalDateTime notificationTime) {
		this.notificationTime = notificationTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getRequestId() {
		return requestId;
	}

	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, notificationTime, requestId, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		return Objects.equals(message, other.message) && Objects.equals(notificationTime, other.notificationTime)
				&& Objects.equals(requestId, other.requestId) && Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "Notification [username=" + username + ", requestId=" + requestId + ", notificationTime="
				+ notificationTime + ", message=" + message + "]";
	}

}
