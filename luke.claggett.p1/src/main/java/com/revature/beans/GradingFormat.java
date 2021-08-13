package com.revature.beans;

import java.util.Objects;

public class GradingFormat {

	private Format format;
	private String passingGrade;
	
	public GradingFormat() {
		this.format = Format.LETTER;
		this.passingGrade = format.getDefaultPassGrade();
	}

	public GradingFormat(Format format) {
		this.format = format;
		this.passingGrade = format.getDefaultPassGrade();
	}

	public GradingFormat(Format format, String passingGrade) {
		this.format = format;
		this.passingGrade = passingGrade;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getPassingGrade() {
		return passingGrade;
	}

	public void setPassingGrade(String passingGrade) {
		this.passingGrade = passingGrade;
	}

	@Override
	public int hashCode() {
		return Objects.hash(format, passingGrade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GradingFormat other = (GradingFormat) obj;
		return format == other.format && Objects.equals(passingGrade, other.passingGrade);
	}

	@Override
	public String toString() {
		return "GradingFormat [format=" + format + ", passingGrade=" + passingGrade + "]";
	}
	
	public Boolean isPassing(String grade) {
		Boolean retGrade = null;
		switch(format) {
		case LETTER:
			
			if (grade.charAt(0) <= passingGrade.charAt(0)) {
				retGrade = true;
			} else {
				retGrade = false;
			}
			break;
		case PASS_FAIL:
			if ("Pass".equals(grade)) {
				retGrade = true;
			} else {
				retGrade = false;
			}
			break;
		case PERCENT:
			if (Double.parseDouble(grade) >= Double.parseDouble(passingGrade)) {
				retGrade = true;
			} else {
				retGrade = false;
			}
			break;
		default: //For presentation
			if (Boolean.parseBoolean(grade)) {
				retGrade = true;
			} else {
				retGrade = false;
			}
			break;
			
		}
		return retGrade;
	}

}
