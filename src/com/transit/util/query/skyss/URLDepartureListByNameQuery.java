package com.transit.util.query.skyss;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Query object used to compose URI for searching for departure list for a given stop.  
 */
public class URLDepartureListByNameQuery {

	private static final String DEPARTURE_LIST_SEARCH_URL = "http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/svar?context=wap.xhtml&dep1=Se+avgangsliste&direction=1" +
			"&GetTR0=1&GetTR1=1&GetTR2=1&GetTR3=1&GetTR4=1&GetTR5=1&GetTR6=1&GetTR7=1&GetTR8=1&GetTR9=1&GetTR10=1&GetTR11=1&GetTR12=1&GetGT0=1&GetGT1=1&GetGT2=1&GetGT3=1";
	//TODO: 
	
	private static final String FROM_STOP="&from=";
	private static final String AT_DATE="&Date=";
	private static final String AT_TIME="&Time=";
	
	
	private StringBuilder query;
	
	public URLDepartureListByNameQuery() {
		query = new StringBuilder(DEPARTURE_LIST_SEARCH_URL);
	}
	
	public URLDepartureListByNameQuery fromStop(String fromStop) {
		if (fromStop != null) {
			fromStop = urlEncode(fromStop);
			query.append(FROM_STOP + fromStop);
		}
		return this;
	}
	
	public URLDepartureListByNameQuery atDate(String date) {
		if (date != null) {
			query.append(AT_DATE + date);
		}
		return this;
	}
	
	public URLDepartureListByNameQuery atTime(String time) {
		if (time != null) {
			query.append(AT_TIME + time);
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
