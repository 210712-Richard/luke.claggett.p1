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
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.TupleType;
import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.factory.TraceLog;
import com.revature.util.CassandraUtil;

@TraceLog
public class ReimbursementDaoImpl implements ReimbursementDao {
	private CqlSession session = CassandraUtil.getInstance().getSession();

	private static final TupleType TUPLE_TEMPLATE = DataTypes.tupleOf(DataTypes.TEXT, DataTypes.TEXT);

	public Reimbursement getRequest(UUID id) {
		
		if (id == null) {
			return null;
		}

		StringBuilder query = new StringBuilder("SELECT ")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsguri, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, deadline, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees ")
				.append("FROM request WHERE id = ?;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(id);

		ResultSet rs = session.execute(bound);
		Row row = rs.one();

		if (row == null) {
			return null;
		}

		Reimbursement request = new ReimbursementRequest();
		request.setId(row.getUuid("id"));
		request.setUsername(row.getString("username"));
		request.setStatus(ReimbursementStatus.valueOf(row.getString("status")));
		request.setIsUrgent(row.getBoolean("isurgent"));
		request.setName(row.getString("name"));
		request.setFirstName(row.getString("firstname"));
		request.setLastName(row.getString("lastname"));
		request.setDeptName(row.getString("deptname"));
		request.setStartDate(row.getLocalDate("startdate"));
		request.setStartTime(row.getLocalTime("starttime"));
		request.setLocation(row.getString("location"));
		request.setDescription(row.getString("description"));
		request.setCost(row.getDouble("cost"));
		request.setGradingFormat(
				new GradingFormat(Format.valueOf(row.getTupleValue("gradingformat").get(0, String.class)),
						row.getTupleValue("gradingformat").get(1, String.class)));
		request.setType(EventType.valueOf(row.getString("type")));
		request.setFileURIs(row.getList("fileuris", String.class));
		request.setApprovalMsgURI(row.getString("approvalmsguri"));
		request.setWorkTimeMissed(row.getString("worktimemissed"));
		request.setReimburseAmount(row.getDouble("reimburseamount"));
		request.setSupervisorApproval(
				new Approval(ApprovalStatus.valueOf(row.getTupleValue("supervisorapproval").get(0, String.class)),
						row.getTupleValue("supervisorapproval").get(1, String.class)));
		request.setDeptHeadApproval(
				new Approval(ApprovalStatus.valueOf(row.getTupleValue("deptheadapproval").get(0, String.class)),
						row.getTupleValue("deptheadapproval").get(1, String.class)));
		request.setBenCoApproval(
				new Approval(ApprovalStatus.valueOf(row.getTupleValue("bencoapproval").get(0, String.class)),
						row.getTupleValue("bencoapproval").get(1, String.class)));

		request.setReason(row.getString("reason"));
		request.setDeadline(LocalDateTime.ofInstant(row.getInstant("deadline"), ZoneOffset.UTC));
		request.setFinalGrade(row.getString("finalgrade"));
		request.setIsPassing(row.getBoolean("ispassing"));
		request.setPresFileName(row.getString("presfilename"));
		request.setFinalApproval(
				new Approval(ApprovalStatus.valueOf(row.getTupleValue("finalapproval").get(0, String.class)),
						row.getTupleValue("finalapproval").get(1, String.class)));
		request.setFinalReimburseAmount(row.getDouble("finalreimburseamount"));
		request.setFinalReimburseAmountReason(row.getString("finalreimburseamountreason"));
		request.setNeedsEmployeeReview(row.getBoolean("needsemployeereview"));
		request.setEmployeeAgrees(row.getBoolean("employeeagrees"));
		return request;
	}

	public List<Reimbursement> getRequests() {
		List<Reimbursement> requests = new ArrayList<Reimbursement>();

		StringBuilder query = new StringBuilder("SELECT ")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsguri, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, deadline, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees ").append("FROM request;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		ResultSet rs = session.execute(s);

		rs.forEach((row) -> {
			Reimbursement request = new ReimbursementRequest();
			request.setId(row.getUuid("id"));
			request.setUsername(row.getString("username"));
			request.setStatus(ReimbursementStatus.valueOf(row.getString("status")));
			request.setIsUrgent(row.getBoolean("isurgent"));
			request.setName(row.getString("name"));
			request.setFirstName(row.getString("firstname"));
			request.setLastName(row.getString("lastname"));
			request.setDeptName(row.getString("deptname"));
			request.setStartDate(row.getLocalDate("startdate"));
			request.setStartTime(row.getLocalTime("starttime"));
			request.setLocation(row.getString("location"));
			request.setDescription(row.getString("description"));
			request.setCost(row.getDouble("cost"));
			request.setGradingFormat(
					new GradingFormat(Format.valueOf(row.getTupleValue("gradingformat").get(0, String.class)),
							row.getTupleValue("gradingformat").get(1, String.class)));
			request.setType(EventType.valueOf(row.getString("type")));
			request.setFileURIs(row.getList("fileuris", String.class));
			request.setApprovalMsgURI(row.getString("approvalmsguri"));
			request.setWorkTimeMissed(row.getString("worktimemissed"));
			request.setReimburseAmount(row.getDouble("reimburseamount"));
			request.setSupervisorApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("supervisorapproval").get(0, String.class)),
							row.getTupleValue("supervisorapproval").get(1, String.class)));
			request.setDeptHeadApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("deptheadapproval").get(0, String.class)),
							row.getTupleValue("deptheadapproval").get(1, String.class)));
			request.setBenCoApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("bencoapproval").get(0, String.class)),
							row.getTupleValue("bencoapproval").get(1, String.class)));

			request.setReason(row.getString("reason"));
			request.setDeadline(LocalDateTime.ofInstant(row.getInstant("deadline"), ZoneOffset.UTC));
			request.setFinalGrade(row.getString("finalgrade"));
			request.setIsPassing(row.getBoolean("ispassing"));
			request.setPresFileName(row.getString("presfilename"));
			request.setFinalApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("finalapproval").get(0, String.class)),
							row.getTupleValue("finalapproval").get(1, String.class)));
			request.setFinalReimburseAmount(row.getDouble("finalreimburseamount"));
			request.setFinalReimburseAmountReason(row.getString("finalreimburseamountreason"));
			request.setNeedsEmployeeReview(row.getBoolean("needsemployeereview"));
			request.setEmployeeAgrees(row.getBoolean("employeeagrees"));
			requests.add(request);
		});

		return requests;
	}
	
	public List<Reimbursement> getExpiredRequests() {
		List<Reimbursement> requests = new ArrayList<Reimbursement>();
		
		StringBuilder query = new StringBuilder("SELECT ")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsguri, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, deadline, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees ").append("FROM request ")
				.append("WHERE status = 'ACTIVE' AND deadline < ? AND employeeAgrees = true LIMIT 1 ALLOW FILTERING;");
		SimpleStatement s = new SimpleStatementBuilder(query.toString()).build();
		BoundStatement bound = session.prepare(s).bind(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		ResultSet rs = session.execute(bound);

		rs.forEach((row) -> {
			Reimbursement request = new ReimbursementRequest();
			request.setId(row.getUuid("id"));
			request.setUsername(row.getString("username"));
			request.setStatus(ReimbursementStatus.valueOf(row.getString("status")));
			request.setIsUrgent(row.getBoolean("isurgent"));
			request.setName(row.getString("name"));
			request.setFirstName(row.getString("firstname"));
			request.setLastName(row.getString("lastname"));
			request.setDeptName(row.getString("deptname"));
			request.setStartDate(row.getLocalDate("startdate"));
			request.setStartTime(row.getLocalTime("starttime"));
			request.setLocation(row.getString("location"));
			request.setDescription(row.getString("description"));
			request.setCost(row.getDouble("cost"));
			request.setGradingFormat(
					new GradingFormat(Format.valueOf(row.getTupleValue("gradingformat").get(0, String.class)),
							row.getTupleValue("gradingformat").get(1, String.class)));
			request.setType(EventType.valueOf(row.getString("type")));
			request.setFileURIs(row.getList("fileuris", String.class));
			request.setApprovalMsgURI(row.getString("approvalmsguri"));
			request.setWorkTimeMissed(row.getString("worktimemissed"));
			request.setReimburseAmount(row.getDouble("reimburseamount"));
			request.setSupervisorApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("supervisorapproval").get(0, String.class)),
							row.getTupleValue("supervisorapproval").get(1, String.class)));
			request.setDeptHeadApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("deptheadapproval").get(0, String.class)),
							row.getTupleValue("deptheadapproval").get(1, String.class)));
			request.setBenCoApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("bencoapproval").get(0, String.class)),
							row.getTupleValue("bencoapproval").get(1, String.class)));

			request.setReason(row.getString("reason"));
			request.setDeadline(LocalDateTime.ofInstant(row.getInstant("deadline"), ZoneOffset.UTC));
			request.setFinalGrade(row.getString("finalgrade"));
			request.setIsPassing(row.getBoolean("ispassing"));
			request.setPresFileName(row.getString("presfilename"));
			request.setFinalApproval(
					new Approval(ApprovalStatus.valueOf(row.getTupleValue("finalapproval").get(0, String.class)),
							row.getTupleValue("finalapproval").get(1, String.class)));
			request.setFinalReimburseAmount(row.getDouble("finalreimburseamount"));
			request.setFinalReimburseAmountReason(row.getString("finalreimburseamountreason"));
			request.setNeedsEmployeeReview(row.getBoolean("needsemployeereview"));
			request.setEmployeeAgrees(row.getBoolean("employeeagrees"));
			requests.add(request);
		});

		return requests;
	}

	public void updateRequest(Reimbursement request) {

		// Create the query
		StringBuilder query = new StringBuilder("UPDATE request SET ")
				.append("status = ?, isurgent = ?, name = ?, firstname = ?, lastname = ?, ")
				.append("deptname = ?, startdate = ?, starttime = ?, location = ?, description = ?, cost = ?, gradingFormat = ?, ")
				.append("type = ?, fileuris = ?, approvalmsguri = ?, worktimemissed = ?, reimburseamount = ?, supervisorapproval = ?, ")
				.append("deptheadapproval = ?, bencoapproval = ?, reason = ?, deadline = ?, ")
				.append("finalgrade = ?, ispassing = ?, presfilename = ?, finalapproval = ?, finalreimburseamount = ?, ")
				.append("finalreimburseamountreason = ?, needsemployeereview = ?, employeeagrees = ?")
				.append(" WHERE id = ? AND username = ?;");

		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		TupleValue grade = TUPLE_TEMPLATE.newValue(request.getGradingFormat().getFormat().toString(),
				request.getGradingFormat().getPassingGrade());

		TupleValue supervisorApproval = TUPLE_TEMPLATE.newValue(request.getSupervisorApproval().getStatus().toString(),
				request.getSupervisorApproval().getUsername());

		TupleValue deptHeadApproval = TUPLE_TEMPLATE.newValue(request.getDeptHeadApproval().getStatus().toString(),
				request.getDeptHeadApproval().getUsername());

		TupleValue benCoApproval = TUPLE_TEMPLATE.newValue(request.getBenCoApproval().getStatus().toString(),
				request.getBenCoApproval().getUsername());

		TupleValue finalApproval = TUPLE_TEMPLATE.newValue(request.getFinalApproval().getStatus().toString(),
				request.getFinalApproval().getUsername());

		BoundStatement bound = session.prepare(s).bind(request.getStatus().toString(), request.getIsUrgent(),
				request.getName(), request.getFirstName(), request.getLastName(), request.getDeptName(),
				request.getStartDate(), request.getStartTime(), request.getLocation(), request.getDescription(),
				request.getCost(), grade, request.getType().toString(), request.getFileURIs(),
				request.getApprovalMsgURI(), request.getWorkTimeMissed(), request.getReimburseAmount(),
				supervisorApproval, deptHeadApproval, benCoApproval, request.getReason(),
				request.getDeadline().toInstant(ZoneOffset.UTC), request.getFinalGrade(), request.getIsPassing(),
				request.getPresFileName(), finalApproval, request.getFinalReimburseAmount(),
				request.getFinalReimburseAmountReason(), request.getNeedsEmployeeReview(), request.getEmployeeAgrees(),
				request.getId(), request.getUsername());

		session.execute(bound);
	}

	public void createRequest(Reimbursement request) {

		// Create the query
		StringBuilder query = new StringBuilder("INSERT INTO request (")
				.append("id, username, status, isurgent, name, firstname, lastname, ")
				.append("deptname, startdate, starttime, location, description, cost, gradingFormat, ")
				.append("type, fileuris, approvalmsguri, worktimemissed, reimburseamount, supervisorapproval, ")
				.append("deptheadapproval, bencoapproval, reason, deadline, ")
				.append("finalgrade, ispassing, presfilename, finalapproval, finalreimburseamount, ")
				.append("finalreimburseamountreason, needsemployeereview, employeeagrees")
				.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

		SimpleStatement s = new SimpleStatementBuilder(query.toString())
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		TupleValue grade = TUPLE_TEMPLATE.newValue(request.getGradingFormat().getFormat().toString(),
				request.getGradingFormat().getPassingGrade());

		TupleValue supervisorApproval = TUPLE_TEMPLATE.newValue(request.getSupervisorApproval().getStatus().toString(),
				request.getSupervisorApproval().getUsername());

		TupleValue deptHeadApproval = TUPLE_TEMPLATE.newValue(request.getDeptHeadApproval().getStatus().toString(),
				request.getDeptHeadApproval().getUsername());

		TupleValue benCoApproval = TUPLE_TEMPLATE.newValue(request.getBenCoApproval().getStatus().toString(),
				request.getBenCoApproval().getUsername());

		TupleValue finalApproval = TUPLE_TEMPLATE.newValue(request.getFinalApproval().getStatus().toString(),
				request.getFinalApproval().getUsername());

		BoundStatement bound = session.prepare(s).bind(request.getId(), request.getUsername(),
				request.getStatus().toString(), request.getIsUrgent(), request.getName(), request.getFirstName(),
				request.getLastName(), request.getDeptName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), grade, request.getType().toString(),
				request.getFileURIs(), request.getApprovalMsgURI(), request.getWorkTimeMissed(),
				request.getReimburseAmount(), supervisorApproval, deptHeadApproval, benCoApproval, request.getReason(),
				request.getDeadline().toInstant(ZoneOffset.UTC), request.getFinalGrade(), request.getIsPassing(),
				request.getPresFileName(), finalApproval, request.getFinalReimburseAmount(),
				request.getFinalReimburseAmountReason(), request.getNeedsEmployeeReview(), request.getEmployeeAgrees());

		session.execute(bound);
	}

}
