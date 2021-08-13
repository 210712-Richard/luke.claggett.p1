package com.revature.beans;

public enum Format {
	LETTER("C"), PERCENT("80"), PASS_FAIL("Pass"), PRESENTATION("true");
	
	private String defaultPassGrade;
	
	Format(String defaultPassGrade){
		this.defaultPassGrade = defaultPassGrade;
	}
	
	public String getDefaultPassGrade() {
		return defaultPassGrade;
	}

}
