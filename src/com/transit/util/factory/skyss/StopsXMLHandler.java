package com.transit.util.factory.skyss;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.transit.dom.Stop;

public class StopsXMLHandler extends DefaultHandler {
	       
          private Set<Stop> stops = new HashSet<Stop>();
   
          public Set<Stop> getParsedData() {
                  return this.stops;
          }
          
          @Override
          public void startDocument() throws SAXException {
                  this.stops = new HashSet<Stop>();
          }
          
          @Override
          public void endDocument() throws SAXException {
                  // Nothing to do
          }
          
          /** Gets be called on opening tags like:
           * <tag>
           * Can provide attribute(s), when xml was like:
           * <tag attribute="attributeValue">*/
          @Override
          public void startElement(String namespaceURI, String localName,
                          String qName, Attributes atts) throws SAXException {
                  if (localName.equals("i")) {
                          String name = atts.getValue("n");
                          String id = atts.getValue("v");
                          String type = atts.getValue("st");
                          
                          Double lon = Double.valueOf(atts.getValue("x").replace(",", "."));
                          Double lat = Double.valueOf(atts.getValue("y").replace(",", "."));
                          
                          if (id != null && id != "0") {
                        	  Stop stop = createStop(name, id, type, lon, lat);
                        	  stops.add(stop);
                          }
                  }
          }

		private Stop createStop(String name, String id, String type,
				Double lon, Double lat) {
			Stop stop = new Stop(id, name);
			  stop.setLat(lat);
			  stop.setLon(lon);
			  stop.setType( getTypeOfStop(type) );
			return stop;
		}

	private String getTypeOfStop(String type) {
		if (type != null) {
			if (type.contains("XBaat")) {
				return "Båt";
			}
			if (type.contains("Tog")) {
				return "Tog";
			}
			if (type.contains("Buss")) {
				return "Buss";
			}
			if (type.contains("Trikk")) {
				return "Trikk";
			}
		}
		return "Buss";
	}
          
          
          /** Gets be called on closing tags like:
           * </tag> */
          @Override
          public void endElement(String namespaceURI, String localName, String qName)
                          throws SAXException {
        	  //do nothing
          }
          /** Gets be called on the following structure:
           * <tag>characters</tag> */
          @Override
      public void characters(char ch[], int start, int length) {
        	  //do nothing
      }
}
