package com.revature.data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.beans.Notification;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class NotificationDaoImpl implements NotificationDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	public List<Notification> getUserNotificationList(String username) {
		List<Notification> notifications = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT username, requestid, notificationtime, message ")
				.append("FROM notification WHERE username = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(username);
		ResultSet rs = session.execute(bound);

		rs.forEach((row) -> {
			Notification notification = new Notification();
			notification.setUsername(row.getString("username"));
			notification.setRequestId(row.getUuid("requestid"));
			notification
					.setNotificationTime(LocalDateTime.ofInstant(row.getInstant("notificationtime"), ZoneOffset.UTC));
			notification.setMessage(row.getString("message"));
			notifications.add(notification);
		});
		return notifications;
	}

	public void createNotification(Notification notification) {
		StringBuilder query = new StringBuilder(
				"INSERT INTO notification(username, requestid, notificationtime, message) ")
						.append("VALUES (?,?,?,?);");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(notification.getUsername(), notification.getRequestId(),
				notification.getNotificationTime().toInstant(ZoneOffset.UTC), notification.getMessage());
		session.execute(bound);
	}

	public void deleteUserNotifications(String username) {
		StringBuilder query = new StringBuilder("DELETE FROM notification WHERE username = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(username);
		session.execute(bound);
	}
	
	public void deleteNotification(String username, UUID requestId) {
		StringBuilder query = new StringBuilder("DELETE FROM notification WHERE username = ? AND requestid = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(username, requestId);
		session.execute(bound);
	}

}
