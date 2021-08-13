package com.revature.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReimbursementRequest implements Reimbursement {
	private UUID id;
	private String username;
	private ReimbursementStatus status;

	private Boolean isUrgent;

	private String name;
	private String firstName;
	private String lastName;
	private String deptName;
	private LocalDate startDate;
	private LocalTime startTime;
	private String location;
	private String description;
	private Double cost;
	private GradingFormat gradingFormat;
	private EventType type;

	private List<String> fileURIs;
	private String approvalMsgURI;
	private String workTimeMissed;

	private Double reimburseAmount;
	private Approval supervisorApproval;
	private Approval deptHeadApproval;
	private Approval benCoApproval;
	private String reason;
	private LocalDateTime deadline;

	// The final step fields
	private String finalGrade;
	private Boolean isPassing;
	private String presFileName;
	private Approval finalApproval;
	private Double finalReimburseAmount;
	private String finalReimburseAmountReason;
	private Boolean needsEmployeeReview;
	private Boolean employeeAgrees;

	public ReimbursementRequest() {
		super();
		status = ReimbursementStatus.ACTIVE;
		isUrgent = false;

		fileURIs = new ArrayList<>();

		supervisorApproval = new Approval();
		deptHeadApproval = new Approval();
		benCoApproval = new Approval();
		finalApproval = new Approval();
		deadline = PLACEHOLDER;
		
		employeeAgrees = true;
		needsEmployeeReview = false;
	}

	public ReimbursementRequest(String username, String firstName, String lastName, String deptName, String name,
			LocalDate date, LocalTime time, String location, String description, Double cost,
			GradingFormat gradingFormat, EventType type) {
		this();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.deptName = deptName;
		this.name = name;
		this.startDate = date;
		this.startTime = time;
		this.location = location;
		this.description = description;
		this.cost = cost;
		this.gradingFormat = gradingFormat;
		this.type = type;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Boolean getEmployeeAgrees() {
		return employeeAgrees;
	}

	public void setEmployeeAgrees(Boolean employeeAgrees) {
		this.employeeAgrees = employeeAgrees;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public ReimbursementStatus getStatus() {
		return status;
	}

	public void setStatus(ReimbursementStatus status) {
		this.status = status;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate date) {
		this.startDate = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime time) {
		this.startTime = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public GradingFormat getGradingFormat() {
		return gradingFormat;
	}

	public void setGradingFormat(GradingFormat gradingFormat) {
		this.gradingFormat = gradingFormat;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public List<String> getFileURIs() {
		return fileURIs;
	}

	public void setFileURIs(List<String> fileURIs) {
		this.fileURIs = fileURIs;
	}

	public String getApprovalMsgURI() {
		return approvalMsgURI;
	}

	public void setApprovalMsgURI(String approvalMsgsURIs) {
		this.approvalMsgURI = approvalMsgsURIs;
	}

	public String getWorkTimeMissed() {
		return workTimeMissed;
	}

	public void setWorkTimeMissed(String workTimeMissed) {
		this.workTimeMissed = workTimeMissed;
	}

	public Double getReimburseAmount() {
		return reimburseAmount;
	}

	public void setReimburseAmount(Double reimburseAmount) {
		this.reimburseAmount = reimburseAmount;
	}

	public Approval getSupervisorApproval() {
		return supervisorApproval;
	}

	public void setSupervisorApproval(Approval supervisorApproval) {
		this.supervisorApproval = supervisorApproval;
	}

	public Approval getDeptHeadApproval() {
		return deptHeadApproval;
	}

	public void setDeptHeadApproval(Approval deptHeadApproval) {
		this.deptHeadApproval = deptHeadApproval;
	}

	public Approval getBenCoApproval() {
		return benCoApproval;
	}

	public void setBenCoApproval(Approval benCoApproval) {
		this.benCoApproval = benCoApproval;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}
	
	public void startDeadline() {
		this.deadline = LocalDateTime.now().plus(TIME_LIMIT);
	}

	public String getFinalGrade() {
		return finalGrade;
	}

	public void setFinalGrade(String finalGrade) {
		this.finalGrade = finalGrade;
	}

	public Approval getFinalApproval() {
		return finalApproval;
	}

	public void setFinalApproval(Approval finalApproval) {
		this.finalApproval = finalApproval;
	}

	@Override
	public void setFinalApprovalStatus(ApprovalStatus status, String reason) {
		this.finalApproval.setStatus(status);
		this.finalApproval.setUsername(reason);
	}

	public Double getFinalReimburseAmount() {
		return finalReimburseAmount;
	}

	public void setFinalReimburseAmount(Double finalReimburseAmount) {
		this.finalReimburseAmount = finalReimburseAmount;
	}

	public Boolean getNeedsEmployeeReview() {
		return needsEmployeeReview;
	}

	public void setNeedsEmployeeReview(Boolean needsEmployeeReview) {
		this.needsEmployeeReview = needsEmployeeReview;
	}

	public Boolean getIsPassing() {
		return isPassing;
	}

	public void setIsPassing(Boolean isPassing) {
		this.isPassing = isPassing;
	}

	public String getPresFileName() {
		return presFileName;
	}

	public void setPresFileName(String presFileName) {
		this.presFileName = presFileName;
	}

	public String getFinalReimburseAmountReason() {
		return finalReimburseAmountReason;
	}

	public void setFinalReimburseAmountReason(String finalReimburseAmountReason) {
		this.finalReimburseAmountReason = finalReimburseAmountReason;
	}

	public Approval[] getApprovalArray() {
		Approval [] arr = { supervisorApproval, deptHeadApproval,
			benCoApproval, finalApproval };
		return arr;
	}
	
	public Boolean getFinalReimburseChanged() {
		return finalReimburseAmount != reimburseAmount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(approvalMsgURI, benCoApproval, cost, deadline, deptHeadApproval, deptName, description,
				employeeAgrees, fileURIs, finalApproval, finalGrade, finalReimburseAmount, finalReimburseAmountReason,
				firstName, gradingFormat, id, isPassing, isUrgent, lastName, location, name, needsEmployeeReview,
				presFileName, reason, reimburseAmount, startDate, startTime, status, supervisorApproval, type, username,
				workTimeMissed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReimbursementRequest other = (ReimbursementRequest) obj;
		return Objects.equals(approvalMsgURI, other.approvalMsgURI)
				&& Objects.equals(benCoApproval, other.benCoApproval) && Objects.equals(cost, other.cost)
				&& Objects.equals(deadline, other.deadline) && Objects.equals(deptHeadApproval, other.deptHeadApproval)
				&& Objects.equals(deptName, other.deptName) && Objects.equals(description, other.description)
				&& Objects.equals(employeeAgrees, other.employeeAgrees) && Objects.equals(fileURIs, other.fileURIs)
				&& Objects.equals(finalApproval, other.finalApproval) && Objects.equals(finalGrade, other.finalGrade)
				&& Objects.equals(finalReimburseAmount, other.finalReimburseAmount)
				&& Objects.equals(finalReimburseAmountReason, other.finalReimburseAmountReason)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(gradingFormat, other.gradingFormat)
				&& Objects.equals(id, other.id) && Objects.equals(isPassing, other.isPassing)
				&& Objects.equals(isUrgent, other.isUrgent) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(location, other.location) && Objects.equals(name, other.name)
				&& Objects.equals(needsEmployeeReview, other.needsEmployeeReview)
				&& Objects.equals(presFileName, other.presFileName) && Objects.equals(reason, other.reason)
				&& Objects.equals(reimburseAmount, other.reimburseAmount) && Objects.equals(startDate, other.startDate)
				&& Objects.equals(startTime, other.startTime) && status == other.status
				&& Objects.equals(supervisorApproval, other.supervisorApproval) && type == other.type
				&& Objects.equals(username, other.username) && Objects.equals(workTimeMissed, other.workTimeMissed);
	}

	@Override
	public String toString() {
		return "ReimbursementRequest [id=" + id + ", username=" + username + ", status=" + status + ", isUrgent="
				+ isUrgent + ", name=" + name + ", firstName=" + firstName + ", lastName=" + lastName + ", deptName="
				+ deptName + ", startDate=" + startDate + ", startTime=" + startTime + ", location=" + location
				+ ", description=" + description + ", cost=" + cost + ", gradingFormat=" + gradingFormat + ", type="
				+ type + ", fileURIs=" + fileURIs + ", approvalMsgsURIs=" + approvalMsgURI + ", workTimeMissed="
				+ workTimeMissed + ", reimburseAmount=" + reimburseAmount + ", supervisorApproval=" + supervisorApproval
				+ ", deptHeadApproval=" + deptHeadApproval + ", benCoApproval=" + benCoApproval + ", reason=" + reason
				+ ", deadline=" + deadline + ", finalGrade=" + finalGrade + ", isPassing=" + isPassing
				+ ", presFileName=" + presFileName + ", finalApproval=" + finalApproval + ", finalReimburseAmount="
				+ finalReimburseAmount + ", finalReimburseAmountReason=" + finalReimburseAmountReason
				+ ", needsEmployeeReview=" + needsEmployeeReview + ", employeeAgrees=" + employeeAgrees + "]";
	}

}
