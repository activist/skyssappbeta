package com.transit.util;

import java.util.HashSet;
import java.util.Set;

import android.os.AsyncTask;

import com.transit.application.SkyssApplication;
import com.transit.route.services.RouteService;

public class FindAddressesAsyncTask extends AsyncTask<String, Void, Set<String>> {

	private RemoteCallListener<Set<String>> listener;
	private RouteService routeService;
	
	public FindAddressesAsyncTask(RemoteCallListener<Set<String>> listener) {
		this.listener = listener;
	}
	
    protected void onPreExecute() {
    	//show dialog?
    }
    
	@Override
	protected Set<String> doInBackground(String... params) {
		routeService = SkyssApplication.getRouteService();
		
		Set<String> addresses = new HashSet<String>();
		
		if(params[0] != null) {
			addresses.addAll(routeService.findAddressesByHttp(params[0]));
		}
		
		return addresses;
	}

	@Override
    protected void onPostExecute(Set<String> addressList) {
		if(addressList != null) {
			this.listener.onRemoteCallComplete(addressList);
		}
    }
}
