package com.transit.util;

import java.util.HashSet;
import java.util.Set;

import android.os.AsyncTask;
import android.util.Log;

import com.transit.application.SkyssApplication;
import com.transit.dom.Stop;
import com.transit.route.services.RouteService;

public class FindStopCoordinatesTask extends AsyncTask<String, Void, Set<Stop>> {

	private RemoteCallListener<Set<Stop>> listener;
	private Set<Stop> stops;
	
	public FindStopCoordinatesTask(RemoteCallListener<Set<Stop>> listener) {
		this.listener = listener;
	}
	
    protected void onPreExecute() {
    	//show dialog 
    }
	
    // automatically done on worker thread (separate from UI thread)
	@Override
	protected Set<Stop> doInBackground(String... points) {
		RouteService routeService = SkyssApplication.getRouteService();
		stops = new HashSet<Stop>();
		
		if (points.length != 4) {
			Log.d(FindStopCoordinatesTask.class.getName(), "Not four coords passed to method!");
			return stops;
		}
		
		String x1 = points[0];
		String x2 = points[1];
		String y1 = points[2];
		String y2 = points[3];
		
		stops = routeService.findStopsByCoordinates(x1, x2, y1, y2);
		return stops;
	}

	@Override
    protected void onPostExecute(Set<Stop> stopList) {
		this.listener.onRemoteCallComplete(stopList);
    }

}

