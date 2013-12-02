package com.transit.util.factory.skyss;

import java.util.Collection;
import java.util.Collections;

import com.transit.dom.Departure;
import com.transit.dom.Stop;

public class DepartureFactory {

	private static final String TABLE_END_TAG = "</table>";
	private static final String ANCHOR_TAG = "<a";
	private static final String ANCHOR_END_TAG = "</a>";
	private static final String TD_END_TAG = "</td>";
	private static final String TD_TAG = "<td>";

	public static Stop createDeparuresFromHtml(String html, String stopId, int maxNumberOfDepartures) {

		if (html != null && isAvailableForInputHTML(html)) {
			
			int nameIndex = html.indexOf("<h1>Avganger fra ") + 17;
			int endNameIndex = html.indexOf("</h1>");
			
			String name = html.substring(nameIndex, endNameIndex).trim();
			Stop stop = new Stop(stopId, name);
			
			int rowIndex = html.indexOf(TD_TAG);
			
			int endTableIndex = html.indexOf(TABLE_END_TAG);
			
			String type = "";
			String time = "";
			String destination = "";
			String comment = "";
			
			int departuresCounter = 0;
			while (departuresCounter < maxNumberOfDepartures && rowIndex > 0 && rowIndex < endTableIndex) {
				int endRow = html.indexOf(TD_END_TAG, rowIndex) + 5;
				
				time = html.substring(rowIndex + 4, endRow - 5).trim();
				
				int routeNoIndex = html.indexOf(ANCHOR_END_TAG, endRow);
				String routeNo = html.substring(routeNoIndex - 4, routeNoIndex).trim();
				
				int typeIndex = html.indexOf("title=\"", routeNoIndex) + 7;
				int typeIndexEnd = html.indexOf("\"/>", typeIndex);
				type = html.substring(typeIndex, typeIndexEnd).trim();
				
				int destinationRow = html.indexOf(TD_TAG, typeIndexEnd) + 4;
				int destinationEndRow = html.indexOf(TD_END_TAG, destinationRow);
				
				int commentIndex = html.indexOf("note\" title=\"", destinationRow) + 13;
				destination = "";
				comment = "";
				if (commentIndex > 0 && commentIndex < destinationEndRow) {
					int startCommentIndex = html.indexOf(ANCHOR_TAG, destinationRow);
					
					if (startCommentIndex > 0 && startCommentIndex < destinationEndRow) {
						destination = html.substring(destinationRow, startCommentIndex).trim();
						
						int endCommentIndex = html.indexOf(ANCHOR_END_TAG, startCommentIndex) - 3;
						comment = html.substring(commentIndex, endCommentIndex);
					} else {
						destination = html.substring(destinationRow, destinationEndRow).trim();
					}
				} else {
					destination = html.substring(destinationRow, destinationEndRow).trim();
				}
				
				
				Departure departure = createDeparture(stop, name, time, routeNo, type, destination, comment);
				stop.addDeparture(departure);
				
				rowIndex = html.indexOf(TD_TAG, destinationEndRow);
				departuresCounter++;
			}
			return stop;
		}
		return new Stop();
	}
	
	public static String getStopId(String html) {
		if (html != null && isAvailableForInputHTML(html)) {
			
			int idIndex = html.indexOf("from=") + 5;
			int endIdIndex = html.indexOf("&amp", idIndex);
			
			return html.substring(idIndex, endIdIndex).trim();
		}
		return null;
	}

	private static Departure createDeparture(Stop stop, String name, String time,
			String routeNo, String type, String destination, String comment) {
		Departure departure = new Departure(stop);
//		departure.setTime(time);
		departure.setRoute(routeNo);
		departure.setType(type);
		departure.setDestination(destination);
		departure.setComment(comment.trim());
		return departure;
	}

	public static boolean isAvailableForInputHTML(String html) {
		return html != null && html.contains("<h1>Avganger fra ");
	}
}
