package com.revature.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.Department;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.Notification;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.beans.User;
import com.revature.data.DepartmentDao;
import com.revature.data.DepartmentDaoImpl;
import com.revature.data.NotificationDao;
import com.revature.data.NotificationDaoImpl;
import com.revature.data.ReimbursementDao;
import com.revature.data.ReimbursementDaoImpl;
import com.revature.data.UserDao;
import com.revature.data.UserDaoImpl;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.util.Verifier;

@TraceLog
public class ReimbursementServiceImpl implements ReimbursementService {

	ReimbursementDao reqDao = (ReimbursementDao) BeanFactory.getFactory().getObject(ReimbursementDao.class, ReimbursementDaoImpl.class);
	UserDao userDao = (UserDao) BeanFactory.getFactory().getObject(UserDao.class, UserDaoImpl.class);
	DepartmentDao deptDao = (DepartmentDao) BeanFactory.getFactory().getObject(DepartmentDao.class,
			DepartmentDaoImpl.class);
	NotificationDao notDao = (NotificationDao) BeanFactory.getFactory().getObject(NotificationDao.class,
			NotificationDaoImpl.class);

	private static final Logger log = LogManager.getLogger(ReimbursementServiceImpl.class);

	private static final Verifier VERIFIER = new Verifier();

	@Override
	public Reimbursement createRequest(String username, String firstName, String lastName, String deptName, String name,
			LocalDate startDate, LocalTime startTime, String location, String description, Double cost,
			GradingFormat gradingFormat, EventType type) {

		Reimbursement request = null;

		if (VERIFIER.verifyStrings(username, firstName, lastName, deptName, name, location, description)) {

			if (VERIFIER.verifyNotNull(startDate, startTime, cost, gradingFormat, type)
					&& startDate.isAfter(LocalDate.now()) && cost > 0.00) {
				User user = userDao.getUser(username);

				Double reimburseAmount = cost * type.getPercent();
				Double reimburseMax = Reimbursement.MAX_REIMBURSEMENT - user.getAwardedBalance() - user.getPendingBalance();
				reimburseMax = (reimburseAmount > reimburseMax) ? reimburseMax : reimburseAmount;

				if (reimburseMax <= 0) {
					reimburseMax = reimburseAmount;
				}
				log.debug("Maximum amount that can be reimbursed: " + reimburseMax);

				request = new ReimbursementRequest(username, firstName, lastName, deptName, name, startDate, startTime,
						location, description, cost, gradingFormat, type);
				request.setId(UUID.randomUUID());
				request.setReimburseAmount(reimburseMax);

				LocalDate twoWeeks = LocalDate.now().plus(Period.of(0, 0, 14));
				request.setIsUrgent(startDate.isBefore(twoWeeks));

				request.getSupervisorApproval().setUsername(user.getSupervisorUsername());
				request.startDeadline();
				request.getSupervisorApproval().setStatus(ApprovalStatus.AWAITING);
				log.debug("Deadline set to " + request.getDeadline());
				reqDao.createRequest(request);
				notDao.createNotification(new Notification(user.getSupervisorUsername(), request.getId(),
						"An employee has requested reimbursement!"));
				user.getRequests().add(request.getId());
				user.alterPendingBalance(reimburseMax);
				log.debug("User's new pending balance: " + user.getPendingBalance());

				userDao.updateUser(user);

			}

		}
		log.debug("Returning request: " + request);
		return request;
	}

	@Override
	public Reimbursement changeApprovalStatus(Reimbursement request, ApprovalStatus status, String reason) {
		Reimbursement retRequest = null;
		if (VERIFIER.verifyNotNull(request, status) && (request.getStatus().equals(ReimbursementStatus.ACTIVE)
				|| request.getStatus().equals(ReimbursementStatus.APPROVED))) {
			Approval[] approvals = request.getApprovalArray();

			for (int i = 0; i < approvals.length; i++) {
				Approval currentApproval = approvals[i];
				log.debug("Current Approval being evaluated: " + currentApproval);

				if (currentApproval.getStatus().equals(ApprovalStatus.APPROVED)
						|| currentApproval.getStatus().equals(ApprovalStatus.AUTO_APPROVED)
						|| currentApproval.getStatus().equals(ApprovalStatus.BYPASSED)) {
					continue;

				}

				else if (currentApproval.getStatus().equals(ApprovalStatus.DENIED)
						|| currentApproval.getStatus().equals(ApprovalStatus.UNASSIGNED)) {
					break;
				}
				Approval nextApproval = (i + 1 < approvals.length) ? approvals[i + 1] : null;
				log.debug("The next Approval to be evaluated: " + nextApproval);
				currentApproval.setStatus(status);
				log.debug("Current Approval status changed to " + currentApproval.getStatus());
				if (status.equals(ApprovalStatus.DENIED)) {

					if (!VERIFIER.verifyStrings(reason)) {
						break;
					}
					request.setStatus(ReimbursementStatus.DENIED);
					request.setReason(reason);
					User user = userDao.getUser(request.getUsername());
					user.alterPendingBalance(request.getReimburseAmount() * -1.0);
					reqDao.updateRequest(request);
					userDao.updateUser(user);
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been denied. Reason: " + reason));
					notDao.deleteNotification(currentApproval.getUsername(), request.getId());
					retRequest = request;
					break;
				}

				if (i == Reimbursement.SUPERVISOR_INDEX) {
					Department dept = deptDao.getDepartment(request.getDeptName());

					nextApproval.setUsername(dept.getDeptHeadUsername());
					nextApproval.setStatus(ApprovalStatus.AWAITING);

					if (dept.getDeptHeadUsername().equals(request.getSupervisorApproval().getUsername())) {
						status = ApprovalStatus.BYPASSED;
						log.debug("Status changed to " + status);
						continue;
					}

				}
				else if (i == Reimbursement.BENCO_INDEX) {
					if (request.getGradingFormat().getFormat().equals(Format.PRESENTATION)) {
						nextApproval.setUsername(request.getSupervisorApproval().getUsername());
					} else {
						nextApproval.setUsername(request.getBenCoApproval().getUsername());
					}
					request.setStatus(ReimbursementStatus.APPROVED);
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been approved. Please enter your final submission when ready."));
				}
				else if (i == Reimbursement.FINAL_INDEX) {
					request.setStatus(ReimbursementStatus.AWARDED);
					User user = userDao.getUser(request.getUsername());

					if (request.getFinalReimburseAmount() == null || request.getFinalReimburseAmount() == 0.0) {
						request.setFinalReimburseAmount(request.getReimburseAmount());
					}
					user.alterPendingBalance(request.getFinalReimburseAmount() * -1.0);
					user.alterAwardedBalance(request.getFinalReimburseAmount());
					notDao.createNotification(new Notification(request.getUsername(), request.getId(),
							"Your request has been finalized and the amount will be awarded."));
					userDao.updateUser(user);
				}

				if (nextApproval != null) {
					nextApproval.setStatus(ApprovalStatus.AWAITING);
					request.startDeadline();
					log.debug("New request deadline: " + request.getDeadline());
					if (nextApproval.getUsername() != null && i != Reimbursement.BENCO_INDEX) {
						notDao.createNotification(new Notification(nextApproval.getUsername(), request.getId(),
								"A new request needs your approval"));
					}
				}
				notDao.deleteNotification(currentApproval.getUsername(), request.getId());
				reqDao.updateRequest(request);
				retRequest = request;
				break;

			}

		}
		return retRequest;
	}

	@Override
	public Reimbursement getRequest(UUID id) {

		Reimbursement retRequest = null;

		if (id != null) {
			retRequest = reqDao.getRequest(id);
		}

		log.debug("The request returned: " + retRequest);
		return retRequest;
	}

	@Override
	public void updateRequest(Reimbursement request) {
		if (request != null) {
			reqDao.updateRequest(request);
		}
	}

	@Override
	public void cancelRequest(Reimbursement request) {
		if (request == null) {
			return;
		}
		User user = userDao.getUser(request.getUsername());

		request.setStatus(ReimbursementStatus.CANCELLED);

		Double reimburse = (request.getFinalReimburseAmount() != null && request.getFinalReimburseAmount() > 0.0)
				? request.getFinalReimburseAmount()
				: request.getReimburseAmount();
		log.debug("Amount the user is losing from pendingBalance: " + reimburse);
		user.alterPendingBalance(reimburse * -1.0);
		log.debug("User's new pending balance: " + user.getPendingBalance());
		userDao.updateUser(user);
		reqDao.updateRequest(request);
	}

	@Override
	public Reimbursement changeReimburseAmount(Reimbursement request, Double reimburse, String reason) {
		Reimbursement retRequest = null;
		if (VERIFIER.verifyNotNull(request, reimburse) && reimburse > 0.0 && VERIFIER.verifyStrings(reason)) {

			request.setFinalReimburseAmount(reimburse);
			request.setFinalReimburseAmountReason(reason);
			request.setNeedsEmployeeReview(true);
			request.setEmployeeAgrees(false);

			User user = userDao.getUser(request.getUsername());
			user.alterPendingBalance(reimburse - request.getReimburseAmount());
			log.debug("User's new pending balance: " + user.getPendingBalance());
			userDao.updateUser(user);
			notDao.createNotification(new Notification(request.getUsername(), request.getId(),
					"Your request reimburse amount has changed and needs your approval."));

			reqDao.updateRequest(request);
			retRequest = request;
		}
		return retRequest;
	}

	@Override
	public void changeEmployeeAgrees(Reimbursement request, Boolean employeeAgrees) {
		if (VERIFIER.verifyNotNull(request, employeeAgrees)) {
			request.setEmployeeAgrees(employeeAgrees);
			request.setNeedsEmployeeReview(false);
			log.debug("Employee review set to: " + request.getEmployeeAgrees());

			if (!employeeAgrees) {
				request.getBenCoApproval().setStatus(ApprovalStatus.UNASSIGNED);
				cancelRequest(request);
				notDao.deleteNotification(request.getBenCoApproval().getUsername(), request.getId());
			} else { 
				reqDao.updateRequest(request);
				notDao.createNotification(new Notification(request.getBenCoApproval().getUsername(), request.getId(),
						"The employee agrees with the reimbursement change."));
			}

		}
	}

	@Override
	public void addFinalGrade(Reimbursement request, String grade) {
		if (VERIFIER.verifyNotNull(request) && VERIFIER.verifyStrings(grade)) {

			request.setFinalGrade(grade);
			request.setIsPassing(request.getGradingFormat().isPassing(grade));
			log.debug("Final grade: " + request.getFinalGrade() + ". Is passing: " + request.getIsPassing());
			reqDao.updateRequest(request);
			notDao.createNotification(new Notification(request.getFinalApproval().getUsername(), request.getId(),
					"Final approval is ready on request"));
		}
	}

	@Override
	public void autoApprove() {
		List<Reimbursement> requests = reqDao.getExpiredRequests();

		if (requests != null && !requests.isEmpty()) {

			synchronized (ReimbursementService.APPROVAL_LOCK) {

				for (Reimbursement request : requests) {
					log.debug("Request being auto approved: " + request);

						if (ApprovalStatus.AWAITING.equals(request.getSupervisorApproval().getStatus())
							|| ApprovalStatus.AWAITING.equals(request.getDeptHeadApproval().getStatus())) {
						changeApprovalStatus(request, ApprovalStatus.AUTO_APPROVED, null);
					}

					else if (ApprovalStatus.AWAITING.equals(request.getBenCoApproval().getStatus())) {
						request.startDeadline();
						reqDao.updateRequest(request);
						String benCoSupervisorUsername = deptDao.getDepartment("Benefits").getDeptHeadUsername();
						notDao.createNotification(new Notification(benCoSupervisorUsername, request.getId(),
								"This request needs further approval."));
					}
					else {
						throw new IllegalApprovalAttemptException("Auto-approval attempt on Request.");
					}
				}
			}

		}
	}
}
