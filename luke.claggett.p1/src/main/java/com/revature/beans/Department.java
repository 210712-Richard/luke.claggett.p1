package com.revature.beans;

import java.util.Objects;

public class Department {
	/**
	 * The name of the department
	 */
	private String name;
	/**
	 * The Username of the department head
	 */
	private String deptHeadUsername;

	public Department() {
		super();
	}

	public Department(String name, String deptHeadUsername) {
		super();
		this.name = name;
		this.deptHeadUsername = deptHeadUsername;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeptHeadUsername() {
		return deptHeadUsername;
	}

	public void setDeptHeadUsername(String deptHeadUsername) {
		this.deptHeadUsername = deptHeadUsername;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deptHeadUsername, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		return Objects.equals(deptHeadUsername, other.deptHeadUsername) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Department [name=" + name + ", deptHeadName=" + deptHeadUsername + "]";
	}

}
