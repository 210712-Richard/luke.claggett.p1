package com.revature.controllers;

import java.io.InputStream;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.beans.Approval;
import com.revature.beans.ApprovalStatus;
import com.revature.beans.Format;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;
import com.revature.beans.User;
import com.revature.beans.UserType;
import com.revature.exceptions.IllegalApprovalAttemptException;
import com.revature.factory.BeanFactory;
import com.revature.factory.TraceLog;
import com.revature.services.ReimbursementService;
import com.revature.services.ReimbursementServiceImpl;
import com.revature.util.S3Util;

import io.javalin.http.Context;

@TraceLog
public class ReimbursementControllerImpl implements ReimbursementController {
	ReimbursementService reqService = (ReimbursementService) BeanFactory.getFactory().getObject(ReimbursementService.class,
			ReimbursementServiceImpl.class);
	private static Logger log = LogManager.getLogger(ReimbursementControllerImpl.class);

	private static final String[] FILETYPES = { "pdf", "jpg", "png", "txt", "doc" };
	private static final S3Util s3Instance = S3Util.getInstance();

	@Override
	public void createReimbursement(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from the body: " + request);

		request = reqService.createRequest(request.getUsername(), request.getFirstName(), request.getLastName(),
				request.getDeptName(), request.getName(), request.getStartDate(), request.getStartTime(),
				request.getLocation(), request.getDescription(), request.getCost(), request.getGradingFormat(),
				request.getType());

		if (request == null) {
			ctx.status(400);
			ctx.html("The Reimbursement Request sent was incorrectly entered.");
		} else { 
			ctx.status(201);
			ctx.json(request);
		}
	}

	@Override
	public void changeApprovalStatus(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = null;
		synchronized (ReimbursementService.APPROVAL_LOCK) {
			request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
			log.debug("Request from requestId: " + request);
		}

		Reimbursement approval = ctx.bodyAsClass(ReimbursementRequest.class);
		log.debug("Request from body: " + approval);
		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID found");
			return;
		}

		if (approval == null || approval.getSupervisorApproval() == null
				|| (!approval.getSupervisorApproval().getStatus().equals(ApprovalStatus.APPROVED)
						&& !approval.getSupervisorApproval().getStatus().equals(ApprovalStatus.DENIED))
				|| (!request.getStatus().equals(ReimbursementStatus.ACTIVE)
						&& !request.getStatus().equals(ReimbursementStatus.APPROVED))
				|| request.getNeedsEmployeeReview() == true) {
			ctx.status(400);
			ctx.html("This request cannot be set to the specified status.");
			return;
		}

		Approval[] approvals = request.getApprovalArray();
		for (int i = 0; i < approvals.length; i++) {
			Approval currentApproval = approvals[i];

			if (currentApproval.getStatus().equals(ApprovalStatus.APPROVED)
					|| currentApproval.getStatus().equals(ApprovalStatus.AUTO_APPROVED)
					|| currentApproval.getStatus().equals(ApprovalStatus.BYPASSED)) {
				continue;

			}

			if (currentApproval.getStatus().equals(ApprovalStatus.UNASSIGNED)) {
				ctx.status(403);
				return;
			}
			if ((i == Reimbursement.BENCO_INDEX && loggedUser.getDepartmentName().equals("Benefits"))
					|| currentApproval.getUsername().equals(loggedUser.getUsername())) {
				if (i == Reimbursement.BENCO_INDEX) {
					currentApproval.setUsername(loggedUser.getUsername());

				}
				try {
					request = reqService.changeApprovalStatus(request, approval.getSupervisorApproval().getStatus(),
							approval.getReason());
					if (request == null) {
						ctx.status(400);
						ctx.html("Approval is invalid.");
					} else {
						ctx.json(request);
					}

					return;
				} catch (IllegalApprovalAttemptException e) {
					ctx.status(500);
					ctx.html("Server Error");
					return;
				}
			}
		}

		ctx.status(403);
	}

	@Override
	public void getReimbursement(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null) {
			ctx.status(401);
			return;
		}
		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Reimbursement request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);
		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername())
				&& !loggedUser.getUsername().equals(request.getFinalApproval().getUsername())) {
			request.setFinalGrade(null);
			request.setIsPassing(null);
		}

		ctx.json(request);
	}

	@Override
	public void cancelReimbursement(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));

		if (request == null) {
			ctx.status(404);
			ctx.html("No Request with that ID");
			return;
		}

		if (loggedUser == null || !loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		if (request.getStatus().equals(ReimbursementStatus.ACTIVE)) {
			reqService.cancelRequest(request);
			ctx.status(204);
		} else {
			ctx.status(406);
			ctx.html("The request cannot be cancelled");
		}

	}

	@Override
	public void uploadExtraFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Boolean isValidFiletype = Stream.of(FILETYPES).anyMatch((type) -> type.equals(filetype));
		if (!isValidFiletype) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Reimbursement request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		if (!request.getStatus().equals(ReimbursementStatus.ACTIVE)
				|| !request.getSupervisorApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			ctx.status(403);
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		String key = request.getId() + "/files/" + request.getFileURIs().size() + "." + filetype;
		s3Instance.uploadToBucket(key, ctx.bodyAsBytes());

		request.getFileURIs().add(key);
		reqService.updateRequest(request);
		ctx.json(request);
	}

	@Override
	public void uploadMessageFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		final String MSG = "msg";
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		if (!MSG.equals(filetype)) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Reimbursement request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		if (!request.getStatus().equals(ReimbursementStatus.ACTIVE)
				|| !request.getSupervisorApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			ctx.status(403);
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		String key = request.getId() + "/messages/apprvoaEmail." + filetype;
		s3Instance.uploadToBucket(key, ctx.bodyAsBytes());
		request.setApprovalMsgURI(key);

		reqService.changeApprovalStatus(request, ApprovalStatus.BYPASSED, null);
		
		ctx.json(request);

	}

	@Override
	public void uploadPresentation(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String filetype = ctx.header("filetype");
		final String PPT = "pptx";
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		if (!PPT.equals(filetype)) {
			ctx.status(400);
			ctx.html("Incorret filetype entered");
			return;
		}

		UUID requestId = UUID.fromString(ctx.pathParam("requestId"));
		Reimbursement request = reqService.getRequest(requestId);
		log.debug("Request from the requestId" + request);

		if (request == null) {
			ctx.status(404);
			ctx.html("No request with that ID");
			return;
		}

		if (!request.getStatus().equals(ReimbursementStatus.APPROVED)
				|| !request.getFinalApproval().getStatus().equals(ApprovalStatus.AWAITING)
				|| !request.getGradingFormat().getFormat().equals(Format.PRESENTATION)) {
			ctx.status(403);
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername())) {
			ctx.status(403);
			return;
		}

		String key = request.getId() + "/presentations/presentation." + filetype;
		s3Instance.uploadToBucket(key, ctx.bodyAsBytes());
		request.setPresFileName(key);
		reqService.addFinalGrade(request, "true");;
		ctx.json(request);

	}

	public void getFile(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);
		Integer index = Integer.parseInt(ctx.pathParam("index"));
		log.debug("The index from the path: " + index);

		if (request == null) {
			ctx.status(404);
			ctx.html("The request wasn't found");
			return;
		}
		if (index == null || index < 0 || index >= request.getFileURIs().size()) {
			ctx.status(404);
			ctx.html("The file wasn't found");
			return;
		}
		String key = request.getFileURIs().get(index);

		try {
			InputStream file = s3Instance.getObject(key);
			ctx.result(file);
		} catch (Exception e) {
			ctx.status(500);
		}
	}

	public void getMessage(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);
		if (request == null) {
			ctx.status(404);
			ctx.html("The request wasn't found");
			return;
		}
		if (request.getApprovalMsgURI() == null) {
			ctx.status(404);
			ctx.html("The file wasn't found");
			return;
		}
		String key = request.getApprovalMsgURI();

		try {
			InputStream file = s3Instance.getObject(key);
			ctx.result(file);
		} catch (Exception e) {
			ctx.status(500);
		}
	}

	public void getPresentation(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");
		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId in path: " + request);

		if (request == null || request.getPresFileName() == null) {
			ctx.status(404);
			ctx.html("The presentation wasn't found");
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername())
				&& !loggedUser.getUsername().equals(request.getFinalApproval().getUsername())) {
			ctx.status(403);
			return;
		}
		String key = request.getPresFileName();

		try {
			InputStream file = s3Instance.getObject(key);
			ctx.result(file);
		} catch (Exception e) {
			ctx.status(500);
		}
	}

	@Override
	public void changeReimbursementAmount(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null || !loggedUser.getType().equals(UserType.BENEFITS_COORDINATOR)) {
			ctx.status(403);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		Reimbursement approval = ctx.bodyAsClass(ReimbursementRequest.class);

		if (!request.getBenCoApproval().getStatus().equals(ApprovalStatus.AWAITING)) {
			ctx.status(403);
			return;
		}
		if (request != null && approval.getFinalReimburseAmount() != null && request.getFinalReimburseAmount() != null
				&& request.getFinalReimburseAmount() <= 0.0
				&& approval.getFinalReimburseAmount() != request.getReimburseAmount()) {
			if (approval.getFinalReimburseAmountReason() == null
					|| approval.getFinalReimburseAmountReason().isBlank()) {

				ctx.status(400);
				ctx.html("If changing the reimburse amount, need a reason");
				return;
			}
			request.getBenCoApproval().setUsername(loggedUser.getUsername());
			request = reqService.changeReimburseAmount(request, approval.getFinalReimburseAmount(),
					approval.getFinalReimburseAmountReason());

			if (request != null) {
				ctx.json(request);
			} else {
				ctx.status(500);
			}
			return;
		}
		ctx.status(403);
	}

	@Override
	public void finalReimbursementCheck(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));
		log.debug("Request from the requestId path param: " + request);

		if (request == null) {
			ctx.status(404);
			ctx.html("No Request Found");
			return;
		}

		if (!loggedUser.getUsername().equals(request.getUsername()) || !request.getNeedsEmployeeReview()) {
			ctx.status(403);
			return;
		}

		Reimbursement review = ctx.bodyAsClass(ReimbursementRequest.class);

		if (review == null || review.getEmployeeAgrees() == null) {
			ctx.status(400);
			return;
		}

		reqService.changeEmployeeAgrees(request, review.getEmployeeAgrees());
		ctx.status(204);
	}

	@Override
	public void putFinalGrade(Context ctx) {
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if (loggedUser == null) {
			ctx.status(401);
			return;
		}

		Reimbursement request = reqService.getRequest(UUID.fromString(ctx.pathParam("requestId")));

		if (request == null) {
			ctx.status(404);
			ctx.html("No request found");
			return;
		}

		if (Format.PRESENTATION.equals(request.getGradingFormat().getFormat())
				|| !(loggedUser.getUsername().equals(request.getUsername())
						&& ReimbursementStatus.APPROVED.equals(request.getStatus())
						&& ApprovalStatus.AWAITING.equals(request.getFinalApproval().getStatus()))) {
			ctx.status(403);
			return;
		}

		Reimbursement grade = ctx.bodyAsClass(ReimbursementRequest.class);

		if (grade == null) {
			ctx.status(400);
			ctx.html("The grade was not entered correctly");
			return;
		}

		if (request.getFinalGrade() != null) {
			ctx.status(409);
			ctx.html("The final grade has already been sent");
			return;
		}

		reqService.addFinalGrade(request, grade.getFinalGrade());
		ctx.status(204);
	}
}
