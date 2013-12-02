package com.transit.util.factory.skyss;

import java.util.HashSet;
import java.util.Set;

import com.transit.dom.Stop;

public class StopsFactory {

	private static final String END_ROW_TAG = "</tr>";
	private static final String END_TABLE_TAG = "</table>";

	public static Set<Stop> createStopsFromHTML(String html) {
		Set<Stop> stops = new HashSet<Stop>();
		
		if (html != null) {
			int endTableIndex = html.indexOf(END_TABLE_TAG);
			
			int startRowIndex = html.indexOf(END_ROW_TAG) + END_ROW_TAG.length();
			int endRowIndex = html.indexOf(END_ROW_TAG, startRowIndex);
			String id = "";
			String name = "";
			
			while (endRowIndex != -1 && endRowIndex <= endTableIndex) {
				String htmlRow = html.substring(startRowIndex, endRowIndex);
				
				int idIndex = htmlRow.indexOf("hpl=") + 4;
				int endIdIndex = htmlRow.indexOf("&amp;date", idIndex);
				id = htmlRow.substring(idIndex, endIdIndex);
				
				int startNameIndex = htmlRow.indexOf("xhtml\">", idIndex);
				int endNameIndex = htmlRow.indexOf("</a>", startNameIndex);
				name = htmlRow.substring(startNameIndex + 7, endNameIndex);
				
				Stop stop = new Stop(id, name);
				stops.add(stop);
				
				startRowIndex = endRowIndex + END_ROW_TAG.length();
				endRowIndex = html.indexOf(END_ROW_TAG, startRowIndex);
			}
		}
		return stops;
	}
	
}
