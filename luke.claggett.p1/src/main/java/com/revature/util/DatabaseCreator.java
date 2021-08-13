package com.revature.util;

import com.revature.beans.Department;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;

public class DatabaseCreator {

	public static void dropTables() {
		StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS User;");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("DROP TABLE IF EXISTS Request;");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("DROP TABLE IF EXISTS Department;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("DROP TABLE IF EXISTS Notification;");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}

	public static void createTables() {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS User (")
				.append("username text, firstName text, ")
				.append("lastName text, type text, departmentName text, supervisorUsername text, ")
				.append("pendingBalance double, awardedBalance double, requests list<UUID>, ")
				.append("primary key(username));");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Request (").append(
				"id uuid, username text, status text, isUrgent boolean, name text, firstName text, lastName text, ")
				.append("deptName text, startDate date, startTime time, location text, ")
				.append("description text, cost double, gradingFormat tuple<text, text>, ")
				.append("type text, fileURIs List<text>, approvalMsgURI text, workTimeMissed text, ")
				.append("reimburseAmount double, supervisorApproval tuple<text, text>, ")
				.append(", deptHeadApproval tuple<text, text>, ")
				.append(", benCoApproval tuple<text, text>, reason text, deadline timestamp, ")
				.append("finalGrade text, isPassing boolean, presFileName text, finalApproval tuple<text, text>, ")
				.append("finalReimburseAmount double, finalReimburseAmountReason text, needsEmployeeReview boolean, employeeAgrees boolean, ")
				.append("primary key(id, username));");
		CassandraUtil.getInstance().getSession().execute(query.toString());

		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Department (")
				.append("name text PRIMARY KEY, deptHeadUsername text").append(");");
		CassandraUtil.getInstance().getSession().execute(query.toString());
		
		query = new StringBuilder("CREATE TABLE IF NOT EXISTS Notification (")
				.append("username text, requestId uuid, notificationTime timestamp, message text, ")
				.append("primary key(username, requestId, notificationTime));");
		CassandraUtil.getInstance().getSession().execute(query.toString());
	}

	public static void populateDepartment() {
		DepartmentDao dao = new DepartmentDaoImpl();
		Department dept = new Department("Test", "TestHead");

		dao.createDepartment(dept);

		dept = new Department("Mailing", "josh-mcnaughton");
		dao.createDepartment(dept);

		dept = new Department("Customer Service", "rob-storey");
		dao.createDepartment(dept);

		dept = new Department("Sales", "andy-groff");
		dao.createDepartment(dept);

		dept = new Department("Logistics", "vicky-strouth");
		dao.createDepartment(dept);

		dept = new Department("CEO", "luke");
		dao.createDepartment(dept);

		dept = new Department("Benefits", "jason-chance");
		dao.createDepartment(dept);
	}

	public static void populateUser() {
		UserDao dao = new UserDaoImpl();

		// Overall head of the organization
		User user = new User("luke", "Luke", "Claggett", UserType.SUPERVISOR,
				"CEO", null);
		dao.createUser(user);

		// Department heads
		user = new User("josh-mcnaughton", "Josh", "Mcnaughton", UserType.SUPERVISOR, "Mailing",
				"luke");
		dao.createUser(user);

		user = new User("rob-storey", "Rob", "Storey", UserType.SUPERVISOR, "Customer Service",
				"luke");
		dao.createUser(user);

		user = new User("andy-groff", "Andy", "Groff", UserType.SUPERVISOR,
				"Sales", "luke");
		dao.createUser(user);

		// Supervisor
		user = new User("allen-kramer", "Allen", "Kramer", UserType.SUPERVISOR, "Mailing",
				"josh-mcnaughton");
		dao.createUser(user);

		// Employee
		user = new User("mary-khan", "Mary", "Khan", UserType.EMPLOYEE, "Mailing",
				"josh-mcnaughton");
		dao.createUser(user);

		// BenCo
		user = new User("jason-chance", "Jason", "Chance", UserType.BENEFITS_COORDINATOR,
				"Benefits", "luke");
		dao.createUser(user);
	}
}
