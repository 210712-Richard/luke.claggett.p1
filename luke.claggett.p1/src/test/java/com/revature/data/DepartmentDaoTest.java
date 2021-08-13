package com.revature.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.revature.beans.Department;

public class DepartmentDaoTest {
	
	private DepartmentDao deptDao = null;
	private Department dept = null;
	
	@BeforeAll
	public static void beforeAll() {
	}
	
	@BeforeEach
	public void beforeEach() {
		deptDao = new DepartmentDaoImpl();
		dept = new Department("Test", "TestHead");
	}
	
	@Test
	public void testCreateDepartment(){
		
		//Make sure a valid department is passed through to the creation.
		assertAll("Assert that an exception is not thrown for the creation.", ()-> deptDao.createDepartment(dept));
		
		//Make sure a null department throws an exception.
		assertThrows(Exception.class, ()-> deptDao.createDepartment(null), "Assert that an exception is thrown for the creation of a null department.");
	}
	
	@Test
	public void testGetDepartment() {
		Department getDept = deptDao.getDepartment(dept.getName());
		
		//Ensure that the object that came back has the same name and deptHeadId
		assertEquals(dept.getName(), getDept.getName(), "Assert that both departments have the same name.");
		assertEquals(dept.getDeptHeadUsername(), getDept.getDeptHeadUsername(), "Assert that both departments have the same deptHeadID");
	
		//Make sure a name not in the database returns null
		Department wrongName = deptDao.getDepartment("not a name");
		assertNull("Assert that a name not in the database returns null", wrongName);
		
		//Make sure a null name returns null
		Department nullName = deptDao.getDepartment(null);
		assertNull("Assert that a null name returns null.", nullName);
	}
}
