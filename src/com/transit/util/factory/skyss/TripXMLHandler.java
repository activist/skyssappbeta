package com.transit.util.factory.skyss;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.transit.dom.Departure;
import com.transit.dom.Stop;
import com.transit.dom.Trip;
import com.transit.dom.TripInterval;
import com.transit.util.DateUtil;
import com.transit.util.MapUtil;

public class TripXMLHandler extends DefaultHandler {

	
	private Trip trip;
	
	public TripXMLHandler() {
		super();
	}
	
	public Trip getParsedData() {
		return trip;
	}

	@Override
	public void startDocument() throws SAXException {
		trip = new Trip();
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
//		<result from="" to="Skjenlien 1 [adresse]">
//		<trips>
//		<trip duration="71" start="07.03.2011 05:23:00" stop="07.03.2011 06:34:00" changecount="2">
	//		<i pt="1" n="Nesttunbrekka (Bergen)" n2="Nesttun terminal (Bergen)" nd="Nesttun terminal" v="12011515" t="1" i="0" st="" tt="2" tn="Buss" ti="t_bus.gif" tp="Buss.GIF" td="4" x="5,350684" y="60,310419" l="60" ns="" c="<a href="http://www.skyss.no/nn-no/Rutetider-og-kart/Reiseplanlegger/Gruppereiser-og-hittegods/">Tide Buss</a>" p="0" tid="575" tno="5076" a="07.03.2011 05:27:00" d="07.03.2011 05:23:00" dko="DX67" fc="" tc="" fnt="" tnt="" wn=""/>
	//		<i pt="4" n="Skjoldskiftet, Fanaveien (Bergen)" n2="" nd="" v="12011513" t="1" i="0" st="" tt="2" tn="Buss" ti="t_bus.gif" tp="Buss.GIF" td="4" x="5,355014" y="60,312695" l="60" ns="" c="<a href="http://www.skyss.no/nn-no/Rutetider-og-kart/Reiseplanlegger/Gruppereiser-og-hittegods/">Tide Buss</a>" p="0" tid="575" tno="5076" a="07.03.2011 05:24:00" d="07.03.2011 05:24:00" dko="DX67" fc="" tc="" fnt="" tnt="" wn=""/>
	//		<i pt="2" n="Nesttun Terminal (Bergen)" n2="Nesttun terminal, bybanestopp (Bergen)" nd="" v="12011509" t="1" i="0" st="" tt="-1" tn="Gange" ti="t_walk.gif" tp="Gaa.GIF" td="1" x="5,352346" y="60,320037" l="" ns="" c="" p="0" tid="-1" tno="0" a="07.03.2011 05:28:00" d="07.03.2011 05:27:00" dko="" fc="" tc="" fnt="" tnt="" wn=""/>
	//		<i pt="2" n="Nesttun Terminal, Bybanestopp (Bergen)" n2="Bystasjonen, bybanestopp (Bergen)" nd="Byparken" v="12016528" t="1" i="0" st="" tt="9" tn="Bybane" ti="t_tram.gif" tp="Trikk.GIF" td="20" x="5,352707" y="60,319732" l="1" ns="" c="<a href="http://www.skyss.no/nn-no/Rutetider-og-kart/Reiseplanlegger/Gruppereiser-og-hittegods/">Fjord1 Partner</a>" p="3" tid="625" tno="10581" a="07.03.2011 05:51:00" d="07.03.2011 05:31:00" dko="DX567" fc="" tc="" fnt="" tnt="" wn=""/>
		
		if (localName.equals("trip")) {
			
			
			
//			stop.setName(atts.getValue("name"));
//			stop.setId(atts.getValue("number"));
		}
		
		if (localName.equals("i")) {
			TripInterval tripInterval = new TripInterval();
			tripInterval.setType(atts.getValue("tn"));
			String passingTime = atts.getValue("d");
			Date passTime = DateUtil.parseDate(passingTime);
			tripInterval.setPassingTime(passTime);
			
			
			String stopname = atts.getValue("n");
			String stopId = atts.getValue("v");
			String lng = atts.getValue("x");
			String lat = atts.getValue("y");
			Stop stop = new Stop(stopId, stopname);
			stop.setLon(MapUtil.parse(lng));
			stop.setLat(MapUtil.parse(lat));
			
			tripInterval.setStop(stop);
			
		}
		
			
//			String deptime = atts.getValue("departuretime");
//			if (deptime.length() < 11) {
//				deptime = deptime + " 00:00:00";
//			}

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
