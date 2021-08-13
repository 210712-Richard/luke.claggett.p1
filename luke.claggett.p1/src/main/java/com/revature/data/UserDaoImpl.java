package com.revature.data;

import java.util.UUID;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;
import com.revature.util.Verifier;

@TraceLog
public class UserDaoImpl implements UserDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();
	private static final Verifier VERIFIER = new Verifier();

	@Override
	public User getUser(String username) {

		if (!VERIFIER.verifyNotNull(username)) {
			return null;
		}
		StringBuilder query = new StringBuilder("SELECT username, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests "
						+ "FROM user WHERE username = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(username);

		ResultSet rs = session.execute(bound);

		Row row = rs.one();

		if (row == null) {
			return null;
		}

		User user = new User();

		user.setUsername(row.getString("username"));
		user.setFirstName(row.getString("firstname"));
		user.setLastName(row.getString("lastname"));
		user.setType(UserType.valueOf(row.getString("type")));
		user.setDepartmentName(row.getString("departmentname"));
		user.setSupervisorUsername(row.getString("supervisorusername"));
		user.setPendingBalance(row.getDouble("pendingbalance"));
		user.setAwardedBalance(row.getDouble("awardedbalance"));
		user.setRequests(row.getList("requests", UUID.class));

		return user;
	}

	@Override
	public User getUser(String username, String password) {

		if (!VERIFIER.verifyNotNull(username, password)) {
			return null;
		}
		StringBuilder query = new StringBuilder("SELECT username, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests "
						+ "FROM user WHERE username = ? AND password = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(username, password);

		ResultSet rs = session.execute(bound);

		Row row = rs.one();

		if (row == null) {
			return null;
		}

		User user = new User();

		user.setUsername(row.getString("username"));
		user.setFirstName(row.getString("firstname"));
		user.setLastName(row.getString("lastname"));
		user.setType(UserType.valueOf(row.getString("type")));
		user.setDepartmentName(row.getString("departmentname"));
		user.setSupervisorUsername(row.getString("supervisorusername"));
		user.setPendingBalance(row.getDouble("pendingbalance"));
		user.setAwardedBalance(row.getDouble("awardedbalance"));
		user.setRequests(row.getList("requests", UUID.class));

		return user;
	}

	@Override
	public void updateUser(User user) {

		StringBuilder query = new StringBuilder("UPDATE user SET firstname=?, ").append(
				"lastname=?, type=?, departmentname=?, supervisorusername=?, pendingbalance=?, awardedbalance=?, requests=? "
						+ "WHERE username = ?");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(user.getFirstName(), user.getLastName(),
				user.getType().toString(), user.getDepartmentName(), user.getSupervisorUsername(),
				user.getPendingBalance(), user.getAwardedBalance(), user.getRequests(), user.getUsername());

		// Execute the query
		session.execute(bound);
	}

	@Override
	public void createUser(User user) {

		StringBuilder query = new StringBuilder("INSERT INTO user (username, firstname, ")
				.append("lastname, type, departmentname, supervisorusername, pendingbalance, awardedbalance, requests")	
				.append(") values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(user.getUsername(),
				user.getFirstName(), user.getLastName(), user.getType().toString(), user.getDepartmentName(),
				user.getSupervisorUsername(), user.getPendingBalance(), user.getAwardedBalance(), user.getRequests());

		session.execute(bound);

	}
}
