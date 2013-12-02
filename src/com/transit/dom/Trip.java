package com.transit.dom;

import java.util.ArrayList;
import java.util.List;

public class Trip {
	
	private String tripId;
	private List<TripInterval> tripIntervals;
	private int duration = 0;
	private int numberOfChanges = 0;
	
	public Trip() {
		tripIntervals = new ArrayList<TripInterval>();
	}
	
	public Trip(String tripId) {
		tripIntervals = new ArrayList<TripInterval>();
	}
	
	
	public void addTripInterval(TripInterval tripInterval) {
		tripIntervals.add(tripInterval);
	}
	
	public String getTripId() {
		return tripId;
	}
	
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	
	public List<TripInterval> getTripIntervals() {
		return tripIntervals;
	}
	
	public void setTripIntervals(List<TripInterval> tripIntervals) {
		this.tripIntervals = tripIntervals;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(int numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}
	
}
