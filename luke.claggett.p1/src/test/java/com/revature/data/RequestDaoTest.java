package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Approval;
import com.revature.beans.EventType;
import com.revature.beans.Format;
import com.revature.beans.GradingFormat;
import com.revature.beans.ReimbursementRequest;
import com.revature.beans.Reimbursement;
import com.revature.beans.ReimbursementStatus;

public class RequestDaoTest {
	ReimbursementDao requestDao = null;
	Reimbursement request = null;

	@BeforeEach
	public void beforeTest() {
		requestDao = new ReimbursementDaoImpl();
		request = new ReimbursementRequest();

		request.setId(UUID.fromString("ddd9e879-52d3-47ad-a1b6-87a94cbb321d"));
		request.setUsername("Tester");
		request.setName("Test Certification");
		request.setFirstName("Test");
		request.setLastName("User");
		request.setDeptName("Test");
		request.setStartDate(LocalDate.now().plus(Period.of(0, 1, 0)));
		request.setStartTime(LocalTime.now());
		request.setLocation("101 Test Dr. Test, VA 99999");
		request.setDescription("A Test course");
		request.setCost(200.00);
		request.setGradingFormat(new GradingFormat(Format.LETTER));
		request.setType(EventType.CERTIFICATION);
		request.setReimburseAmount(Reimbursement.MAX_REIMBURSEMENT * request.getType().getPercent());
		request.setDeptHeadApproval(new Approval());
		request.setSupervisorApproval(new Approval());
		request.setBenCoApproval(new Approval());
		request.setFinalApproval(new Approval());
		request.setStatus(ReimbursementStatus.CANCELLED);

	}

	@Test
	public void testCreateRequest() {
		// Make sure a valid request does not throw an exception
		assertAll("Assert that an exception is not thrown for the creation.", () -> requestDao.createRequest(request));

		// Make sure a null request throws an exception.
		assertThrows(Exception.class, () -> requestDao.createRequest(null),
				"Assert that an exception is thrown for the creation of a null department.");
	}

	@Test
	public void testUpdateRequest() {
		// Make sure a valid request does not throw an exception
		assertAll("Assert that an exception is not thrown for the creation.", () -> requestDao.updateRequest(request));

		// Make sure a null request throws an exception.
		assertThrows(Exception.class, () -> requestDao.updateRequest(null),
				"Assert that an exception is thrown for the creation of a null department.");
	}

	@Test
	public void testGetRequestValid() {
		Reimbursement getRequest = requestDao.getRequest(request.getId());

		// Make sure the user returned is the same.
		assertEquals(getRequest.getId(), request.getId(), "Assert that both request ids are the same.");
		assertEquals(getRequest.getUsername(), request.getUsername(),
				"Assert that both request usernames are the same.");
	}

	@Test
	public void testGetRequestInvalid() {

		// Make sure an invalid id results in a null
		assertNull("Assert that a id not in the database returns a null", requestDao.getRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));

		// Make sure a null id returns a null
		assertNull("Assert that a null request returns a null", requestDao.getRequest(null));
	}

	@Test
	public void testGetRequests() {
		List<Reimbursement> requests = requestDao.getRequests();

		assertTrue(requests != null,
				"Assert that the request list is not null.");
	}
	
	@Test
	public void testGetExpiredRequests() {
		List<Reimbursement> requests = requestDao.getExpiredRequests();

		assertTrue(requests != null,
				"Assert that the request list is not null.");
	}
}
