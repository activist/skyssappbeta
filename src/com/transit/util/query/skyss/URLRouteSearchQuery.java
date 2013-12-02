package com.transit.util.query.skyss;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Query object used to search for routes. 
 * TODO: use mobile version instead? 
 */
public class URLRouteSearchQuery {
	private static final String URL_SKYSS_ROUTE_SEARCH_MAIN = "http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/svar?context=wap.xhtml" +
			"&search=SØK&stagetypefrom=1&stagetypeto=1&changepenalty=1&changepause=0&direction=1";
	private static final String SEARCH_ALL_TRAFFICTYPES = "&traffictype_all=on";
	private static final String SEARCH_FOR_BUS = "&GetTR0=1";
	private static final String SEARCH_FOR_EXPRESSBUS = "&GetTR1=1";
	private static final String SEARCH_FOR_AIRPORTBUS = "&GetTR2=1";
	private static final String SEARCH_FOR_FERRY = "&GetTR3=1";
	private static final String SEARCH_FOR_BOAT = "&GetTR4=1";
	private static final String SEARCH_FOR_TRAM = "&GetTR5=1"; //bybane
	private static final String SEARCH_FOR_TRAIN = "&GetTR6=1"; 
	private static final String SEARCH_FOR_OTHER = "&GetTR7=1"; 
	private static final String SEARCH_FOR_WALKING = "&GetGT0=1"; 
	private static final String SEARCH_FOR_CAR = "&GetGT1=1"; 
	
	private static final String SEARCH_FROM_STOP = "&from="; 
	private static final String SEARCH_TO_STOP = "&to="; 
	private static final String SEARCH_AT_DATE = "&Date="; 
	private static final String SEARCH_AT_TIME = "&Time="; 
	
	
	private StringBuilder query;
	
	public URLRouteSearchQuery() {
		query = new StringBuilder(URL_SKYSS_ROUTE_SEARCH_MAIN);
	}
	
	public URLRouteSearchQuery withAllTrafficTypesSearch() {
		query.append(SEARCH_ALL_TRAFFICTYPES);
		return this;
	}
	
	public URLRouteSearchQuery withBusSeach() {
		query.append(SEARCH_FOR_BUS);
		return this;
	}
	
	public URLRouteSearchQuery withExpressBusSearch() {
		query.append(SEARCH_FOR_EXPRESSBUS);
		return this;
	}
	
	public URLRouteSearchQuery withAirportBusSearch() {
		query.append(SEARCH_FOR_AIRPORTBUS);
		return this;
	}
	
	public URLRouteSearchQuery withFerrySearch() {
		query.append(SEARCH_FOR_FERRY);
		return this;
	}
	
	public URLRouteSearchQuery withBoatSearch() {
		query.append(SEARCH_FOR_BOAT);
		return this;
	}
	
	public URLRouteSearchQuery withTramSearch() {
		query.append(SEARCH_FOR_TRAM);
		return this;
	}
	
	public URLRouteSearchQuery withTrainSearch() {
		query.append(SEARCH_FOR_TRAIN);
		return this;
	}

	public URLRouteSearchQuery withOtherSearch() {
		query.append(SEARCH_FOR_OTHER);
		return this;
	}
	
	public URLRouteSearchQuery withWalkingSearch() {
		query.append(SEARCH_FOR_WALKING);
		return this;
	}
	
	public URLRouteSearchQuery withCarSearch() {
		query.append(SEARCH_FOR_CAR);
		return this;
	}
	
	public URLRouteSearchQuery fromStop(String fromStopName) {
		if (fromStopName != null) {
			query.append(SEARCH_FROM_STOP + urlEncode(fromStopName));
		}
		return this;
	}
	
	public URLRouteSearchQuery toStop(String toStopName) {
		if (toStopName != null) {
			query.append(SEARCH_TO_STOP + urlEncode(toStopName));
		}
		return this;
	}
	
	public URLRouteSearchQuery atDate(String date) {
		if (date != null) {
			query.append(SEARCH_AT_DATE +  date);
		}
		return this;
	}
	
	public URLRouteSearchQuery atTime(String time) {
		if (time != null) {
			query.append(SEARCH_AT_TIME +  time);
		}
		return this;
	}
	
	private String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO : do something useful
		}
		return input;
	}
	
	public String buildQuery() {
		return query.toString();
	}
	
}
