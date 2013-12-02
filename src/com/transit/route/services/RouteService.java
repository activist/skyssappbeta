package com.transit.route.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;

import com.transit.R;
import com.transit.application.SkyssApplication;
import com.transit.dom.Stop;
import com.transit.util.TextResourceReader;
import com.transit.util.factory.skyss.DepartureFactory;
import com.transit.util.factory.skyss.DepartureXMLHandler;
import com.transit.util.factory.skyss.StopsXMLHandler;
import com.transit.util.query.skyss.URLDepartureListByIdQuery;
import com.transit.util.query.skyss.URLDepartureListByNameQuery;
import com.transit.util.query.skyss.URLRouteSearchQuery;
import com.transit.util.query.skyss.URLStopSearchQuery;

public class RouteService {

	private static final Map<Character, Integer> resourceFileMap = new HashMap<Character, Integer>();
	
	private static HashSet<String> cachedStops = new HashSet<String>();
	
	//ugly as hell
	static {
		resourceFileMap.put('a', R.raw.stops_abc);
		resourceFileMap.put('b', R.raw.stops_abc);
		resourceFileMap.put('c', R.raw.stops_abc);
		resourceFileMap.put('d', R.raw.stops_def);
		resourceFileMap.put('e', R.raw.stops_def);
		resourceFileMap.put('f', R.raw.stops_def);
		resourceFileMap.put('g', R.raw.stops_gh);
		resourceFileMap.put('h', R.raw.stops_gh);
		resourceFileMap.put('i', R.raw.stops_ijk);
		resourceFileMap.put('j', R.raw.stops_ijk);
		resourceFileMap.put('k', R.raw.stops_ijk);
		resourceFileMap.put('l', R.raw.stops_lm);
		resourceFileMap.put('m', R.raw.stops_lm);
		resourceFileMap.put('n', R.raw.stops_no);
		resourceFileMap.put('o', R.raw.stops_no);
		resourceFileMap.put('p', R.raw.stops_pqr);
		resourceFileMap.put('r', R.raw.stops_pqr);
		resourceFileMap.put('s', R.raw.stops_s);
		resourceFileMap.put('t', R.raw.stops_tu);
		resourceFileMap.put('u', R.raw.stops_tu);
		resourceFileMap.put('v', R.raw.stops_vlast);
		resourceFileMap.put('w', R.raw.stops_vlast);
		resourceFileMap.put('y', R.raw.stops_vlast);
		resourceFileMap.put('æ', R.raw.stops_vlast);
		resourceFileMap.put('ø', R.raw.stops_vlast);
		resourceFileMap.put('å', R.raw.stops_vlast);
	}
	
	private HttpClient httpClient;
	private TextResourceReader textReader;
	private Resources resources;
	private Context context;
	private SAXParserFactory spf;
	
	public RouteService() {
		
	}
	
	public RouteService(Resources resources, Context context) {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		this.httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);
		this.httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, new Integer(15000));
		this.httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, new Integer(15000));
		this.resources = resources;
		this.context = context;
		this.textReader = new TextResourceReader(getResources());
		this.spf = SAXParserFactory.newInstance();
		}
		
	private boolean internetIsAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public Collection<String> findAddressesByInput(String inputString)  {
		inputString = inputString.substring(0,1).toUpperCase() + inputString.substring(1);
		final Set<String> addresses = new HashSet<String>();
		checkCachedStops(inputString);
		
		addresses.addAll(findAddressesByFileSearch(inputString, 10));
		
		cachedStops.addAll(addresses);
		return addresses;
	}

	public Set<String> findAddressesByHttp(String inputString) {
		Set<String> addresses = new HashSet<String>();
		URLStopSearchQuery query = new URLStopSearchQuery()
										.withMaxResults("10")
										.withStop(inputString);
		
		String responseStr = doHttpRequest(query.build());
		
		if (responseStr != null) {
			addresses.addAll( parseStopsFromJSONResponse(responseStr) );
		}
		cachedStops.addAll(addresses);
		return addresses;
	}
	
	private Set<String> findAddressesByFileSearch(String inputString, int noOfResults) {
		Set<String> result = new HashSet<String>();

		if (textReader == null || resources == null) {
			return result;
		}
		
		Integer resource = getResource(inputString);
		if (resource == null) {
			return result;
		}
		
		return textReader.searchText(resource, inputString, noOfResults);
	}
	
	private void checkCachedStops(String inputString) {
		HashSet<String> cachedStopsToBeRemoved = new HashSet<String>();
		for (String stopName : cachedStops) {
			if (!stopName.startsWith(inputString)) {
				cachedStopsToBeRemoved.add(stopName);
			}
		}
		
		if (!cachedStops.isEmpty() && !cachedStopsToBeRemoved.isEmpty()) {
			cachedStops.removeAll(cachedStopsToBeRemoved);
		}
	}
	
	private static Integer getResource(String inputString) {
		char firstChar = inputString.toLowerCase().charAt(0);
		return resourceFileMap.get(firstChar);
	}
	
	public Stop findCurrentDepartureListForStop(String stopId, int maxNumberOfDepartures) {
		return findDepartureListForStop(stopId, maxNumberOfDepartures, new Date());
	}
	
	public Stop findDepartureListForStop(String stopId, int maxNumberOfDepartures, Date atDate) {
		URLDepartureListByIdQuery query = new URLDepartureListByIdQuery()
		.fromStop(stopId)
		.atDate(DateFormat.format("dd-MM-yyyy", atDate).toString());
		
		try {
			URL url = new URL(query.buildQuery());
			SAXParser parser = spf.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			DepartureXMLHandler xmlHandler = new DepartureXMLHandler();
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.parse(new InputSource(url.openStream()));
			return xmlHandler.getParsedData();
		} catch (IOException e) {
			Log.d(SkyssApplication.TAG, "Failed to retrieve departurelist for stop: " + e.getMessage());
		} catch (SAXException e) {
			Log.d(SkyssApplication.TAG, "Failed to retrieve departurelist for stop: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.d(SkyssApplication.TAG, "Failed to retrieve departurelist for stop: " + e.getMessage());
		}
		
		return new Stop();
	}
	
	public Set<Stop> findStopsByCoordinates(String x1, String x2, String y1, String y2) {
		
		//http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/mapxml?x1=5.305795669555664&x2=5.366735458374023&y1=60.35863026181671&y2=60.38196898834704&loc=1
		StringBuilder sb = new StringBuilder("http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll/mapxml?");
		sb.append("x1="+x1);
		sb.append("&x2="+x2);
		sb.append("&y1="+y1);
		sb.append("&y2="+y2);
		sb.append("&loc=1");
		
		//TODO: refactor
		try {
			URL url = new URL(sb.toString());
			SAXParser parser = spf.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			StopsXMLHandler xmlHandler = new StopsXMLHandler();
			xmlReader.setContentHandler(xmlHandler);
			
			xmlReader.parse(new InputSource(url.openStream()));
			
			return xmlHandler.getParsedData();
		} catch (ParserConfigurationException e) {
			Log.d(SkyssApplication.TAG, "findStopsByCoordinates: " + e.getMessage());
		} catch (SAXException e) {
			Log.d(SkyssApplication.TAG, "findStopsByCoordinates: " + e.getMessage());		
		} catch (MalformedURLException e) {
			Log.d(SkyssApplication.TAG, "findStopsByCoordinates: " + e.getMessage());
		} catch (IOException e) {
			Log.d(SkyssApplication.TAG, "findStopsByCoordinates: " + e.getMessage());
		}
		
		return new HashSet<Stop>();
	}

	private String doHttpRequest(String url) {
		if (!internetIsAvailable()) {
			return null;
		}
		
		HttpGet httpGetRequest = new HttpGet(url);
		addFakeHeaders(httpGetRequest);
		
		HttpResponse response = executeHttpRequest(httpGetRequest);
		
		String responseStr = null;
		
		
		if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        try {
				response.getEntity().writeTo(os);
			} catch (IOException e) {
				Log.d(SkyssApplication.TAG, "Write of http response failed " + e.getMessage());
			}
	        responseStr = os.toString();
		} else if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_REQUEST_TIMEOUT) {

		}
		return responseStr;
	}


	public String findRoutes(String fromStop, String toStop, String date, String time) {
		URLRouteSearchQuery query = new URLRouteSearchQuery()
								.withAllTrafficTypesSearch()
								.withBusSeach()
								.withTramSearch()
								.withTrainSearch()
								.fromStop(fromStop)
								.toStop(toStop)
								.atDate(date)
								.atTime(time);
		
		String repsonseHtml = doHttpRequest(query.buildQuery());

		return repsonseHtml;
	}
	
	public String findStopsNearAddress(String fromStop, String date, String time) {
		URLDepartureListByNameQuery query = new URLDepartureListByNameQuery()
										.fromStop(fromStop)
										.atDate(date)
										.atTime(time) //TODO: time doesnt matter.
										;

		String responseHtml = doHttpRequest(query.buildQuery());

		return responseHtml;
	}
	
	private List<String> parseStopsFromJSONResponse(String responseStr) {
		JSONObject json;
		List<String> stopSuggestions = new ArrayList<String>();
		try {
			json = new JSONObject(responseStr);
			JSONArray suggestions = (JSONArray) json.get("suggestions");
			
			for (int i = 0; i < suggestions.length(); i++) {
				stopSuggestions.add(suggestions.getString(i).trim());
			}
		} catch (JSONException e) {
			Log.d(SkyssApplication.TAG, "Couldnt parse JSON: " + e.getMessage());
		}
		return stopSuggestions;
	}

	private HttpResponse executeHttpRequest(HttpGet httpGetRequest) {
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGetRequest);
		} catch (ClientProtocolException e) {
			Log.d(SkyssApplication.TAG, "Failed to execute getRequest: " + e.getMessage());
		} catch (IOException e) {
			Log.d(SkyssApplication.TAG, "Failed to execute getRequest: " + e.getMessage());
		}
		return response;
	}
	
	private void addFakeHeaders(HttpGet httpGetRequest) {
		httpGetRequest.addHeader("Referer", "http://reiseplanlegger.skyss.no/scripts/TravelMagic/TravelMagicWE.dll");
		httpGetRequest.addHeader("Host", "reiseplanlegger.skyss.no");
		httpGetRequest.addHeader("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
		httpGetRequest.addHeader("X-Requested-With", "XMLHttpRequest");
		httpGetRequest.addHeader("X-Prototype-Version", "1.6.1");
		httpGetRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
	}
	
	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
