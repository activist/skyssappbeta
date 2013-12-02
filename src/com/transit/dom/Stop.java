package com.transit.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Jan
 *
 */
public class Stop implements Comparable<Stop> {

	private String id;
	private String name;
	private Double lat;
	private Double lon;
	private String type;
	
	
	private List<Departure> departures;
	
	public Stop() {	
		departures = new ArrayList<Departure>();
	}
	
	public Stop(String id, String name) {
		this.id = id;
		this.name = name;
		this.departures = new ArrayList<Departure>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat*1e6;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon*1e6;
	}

	public String toString() {
		return this.id + "," + this.name;
	}

	public String getType() {
		return type;
	}
	
	public boolean isBusStop() {
		return type.equalsIgnoreCase("Buss");
	}
	
	public boolean isTrainStop() {
		return type.equalsIgnoreCase("Tog");
	}
	
	public boolean isTramStop() {
		return type.equalsIgnoreCase("Trikk");
	}
	
	public boolean isBoatStop() {
		return type.equalsIgnoreCase("Båt");
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addDeparture(Departure departure) {
		if (departure != null ) {
			departure.setStop(this);
			departures.add(departure);
		}
	}
	
	public void addDepartures(Collection<Departure> departures) {
		for (Departure dep : departures) {
			dep.setStop(this);
		}
		departures.addAll(departures);
	}
	
	public List<Departure> getDepartures() {
		return departures;
	}

	public void setDepartures(List<Departure> departures) {
		for (Departure dep : departures) {
			dep.setStop(this);
		}
		this.departures = departures;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o instanceof Stop) {
			Stop otherStop = (Stop) o;
			
			return this.getId().equals(otherStop.getId());
		}
		return false;
	}
	
	public int hashCode() {
		if (id != null) {
			return Integer.parseInt(getId());
		}
		return 0;
	}

	public int compareTo(Stop another) {
		if (another == null) {
			return 0;
		}
		return this.getName().compareTo(another.getName());
	}
}
