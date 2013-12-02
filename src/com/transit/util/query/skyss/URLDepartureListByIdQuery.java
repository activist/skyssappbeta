package com.transit.util.query.skyss;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLDepartureListByIdQuery {

	
		private static final String DEPARTURE_LIST_SEARCH_URL = "http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/avgangsinfoXML?context=wap.xhtml";

	private static final String FROM_STOP="&hpl=";
	private static final String AT_DATE="&date=";


	private StringBuilder query;
	
	public URLDepartureListByIdQuery() {
		query = new StringBuilder(DEPARTURE_LIST_SEARCH_URL);
	}
	
	public URLDepartureListByIdQuery(String stopId, String date) {
		query = new StringBuilder(DEPARTURE_LIST_SEARCH_URL);
		this.fromStop(stopId);
		this.atDate(date);
	}
	
	public URLDepartureListByIdQuery fromStop(String fromStop) {
		if (fromStop != null) {
			fromStop = urlEncode(fromStop);
			query.append(FROM_STOP + fromStop);
		}
		return this;
	}
	
	public URLDepartureListByIdQuery atDate(String date) {
		if (date != null) {
			query.append(AT_DATE + date);
		}
		return this;
	}
	
	public String buildQuery() {
		return query.toString();
	}
	
	private String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO : do something useful
		}
		return input;
	}
}
