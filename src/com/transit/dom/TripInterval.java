package com.transit.dom;

import java.util.Date;

public class TripInterval {

	private Trip trip;
	private Stop stop;
	private Date passingTime;
	private String type;
	
	
	public TripInterval() {
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

	public Date getPassingTime() {
		return passingTime;
	}

	public void setPassingTime(Date passingTime) {
		this.passingTime = passingTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
