package com.transit.util.factory.skyss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.transit.application.SkyssApplication;
import com.transit.dom.Departure;
import com.transit.dom.Stop;
import com.transit.util.DateUtil;

public class DepartureXMLHandler extends DefaultHandler {

	private Stop stop;
	
	public DepartureXMLHandler() {
		super();
	}
	
	public Stop getParsedData() {
		return stop;
	}

	@Override
	public void startDocument() throws SAXException {
		stop = new Stop();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		
//		http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/avgangsinfoXML?hpl=12011096&date=06.03.2011&context=wap.xhtml
//		<departurelist date="06.03.2011" number="12011096" name="Olav Kyrres gate Peppes/Fridays (Bergen)" shortname="" type="Overgang" numdepartures="119">
//		<departure icon="Buss.GIF" type="Buss" departuretime="06.03.2011 00:08:00" routename="41" destination="Vadmyra Hetlevikåsen" merknad="" firm="<a href="http://www.tide.no" class="ASelskap" target="_blank">TIDE</a>" tripid="29"/>

		if (localName.equals("departurelist")) {
			stop.setName(atts.getValue("name"));
			stop.setId(atts.getValue("number"));
		}
		
		if (localName.equals("departure")) {
			Departure departure = new Departure(stop);
			
			departure.setDestination(atts.getValue("destination"));
			departure.setRoute(atts.getValue("routename"));
			departure.setTripId(atts.getValue("tripid"));
			departure.setType(atts.getValue("type"));
			
			String deptime = atts.getValue("departuretime");
			if (deptime.length() < 11) {
				deptime = deptime + " 00:00:00";
			}

			Date departureDateTime = DateUtil.parseDate(deptime);
			departure.setDateTime(departureDateTime);

			if (!departure.hasLeft()) {
				stop.addDeparture(departure);
			}
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		// do nothing
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		// do nothing
	}
}
