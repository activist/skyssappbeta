package com.transit.util.query.skyss;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Query object used to search after names of stops (or addresses - since there is no apparent difference in how it is used on skyss.no). 
 */
public class URLStopSearchQuery {

//	private static final String URL_SKYSS_STOPS_SEARCH = "http://82.134.94.138:80/scripts/TravelMagic/TravelMagicWE.dll/StageJSON?";
	private static final String URL_SKYSS_STOPS_SEARCH = "http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/StageJSON?";
	
	private static final String WITH_MAX_RES = "cnt=";
	private static final String WITH_STOP = "query=";
	
	private StringBuilder query;
	
	
	public URLStopSearchQuery() {
		query = new StringBuilder(URL_SKYSS_STOPS_SEARCH);
	}
	
	public URLStopSearchQuery withMaxResults(String noOfResults) {
		if (noOfResults != null) {
			
			if (!isFirstParameter()) {
				query.append("&");
			}
			query.append(WITH_MAX_RES + noOfResults);
		}
		return this;
	}
	
	public URLStopSearchQuery withStop(String stop) {
		if (stop != null) {
			stop = urlEncode(stop);
			
			if (!isFirstParameter()) {
				query.append("&");
			}
			query.append(WITH_STOP + stop);
		}
		return this;
	}
	
	private boolean isFirstParameter() {
		char lastChar = query.charAt(query.length()-1);
		
		return lastChar == '?';
	}
	
	private String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO : do something useful
		}
		return input;
	}

	public String build() {
		return query.toString();
	}
	
}
