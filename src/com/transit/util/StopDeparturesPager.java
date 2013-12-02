package com.transit.util;

import java.util.ArrayList;
import java.util.List;

import com.transit.dom.Departure;
import com.transit.dom.Stop;

public class StopDeparturesPager {

	private int departuresIndex = 0;
	private int noOfPagedDepartures = 1;
	private Stop stop;
	
	public StopDeparturesPager() {
		
	}
	
	public StopDeparturesPager(Stop stop, int noOfPagedDepartures) {
		this.stop = stop;
		this.noOfPagedDepartures = noOfPagedDepartures;
	}
	
	public List<Departure> getNextDepartureInterval() {
		List<Departure> depInterval = new ArrayList<Departure>();
		List<Departure> departures = stop.getDepartures();
		
		if (hasNoMoreDepartures()) {
			return depInterval;
		}
		
		if ( moreDeparturesAreLeft(departures) ) {
			return getNextDepartureInterval(departures);
		} else {
			return getRemainingDepartures(departures);
		}
	}

	private List<Departure> getNextDepartureInterval(List<Departure> departures) {
		List<Departure> depInterval = departures.subList(departuresIndex, departuresIndex + noOfPagedDepartures);
		departuresIndex += noOfPagedDepartures;
		return new ArrayList<Departure>(depInterval);
	}

	private boolean moreDeparturesAreLeft(List<Departure> departures) {
		return (departuresIndex + noOfPagedDepartures) < departures.size();
	}

	private List<Departure> getRemainingDepartures(List<Departure> departures) {
		List<Departure> departureListInterval = departures.subList(departuresIndex, departures.size() - 1);
		departuresIndex  = -1;
		return departureListInterval;
	}
	
	public boolean hasNoMoreDepartures() {
		return departuresIndex < 0;
	}

	public int getNoOfPagedDepartures() {
		return noOfPagedDepartures;
	}

	public void setNoOfPagedDepartures(int noOfPagedDepartures) {
		this.noOfPagedDepartures = noOfPagedDepartures;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}
	
}
