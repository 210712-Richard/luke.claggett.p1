package com.revature.beans;

public enum EventType {
	UNIVERSITY_COURSE(0.8f), 
	SEMINAR(0.6f), 
	CERTIFICATION_PREPERATION_CLASS(0.75f),
	CERTIFICATION(1f), 
	TECHNICAL_TRAINING(0.9f), 
	OTHER(0.3f);
	
	private Float percent;
	
	EventType(float percent){
		this.percent = percent;
	}
	
	public Float getPercent() {
		return percent;
	}
}
