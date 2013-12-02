package com.transit.dom;

import java.util.Date;

import com.transit.util.DateUtil;

public class Departure {

	private Stop stop;
	
	private String type;
	private String destination;
	private String route;
	private String comment;
	private String tripId;
	private Date dateTime;
	
	
	public Departure() {
		
	}
	
	public Departure(Stop stop) {
		this.stop = stop;
	}
	
	public Stop getStop() {
		return stop;
	}
	public void setStop(Stop stop) {
		this.stop = stop;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	
	public String getDate() {
		return DateUtil.removeTime(dateTime);
	}

	public String getTime() {
		return DateUtil.getTimeFromDate(dateTime);
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public boolean hasLeft() {
		Date now = new Date();
		return dateTime.before(now);
	}
	
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
}
