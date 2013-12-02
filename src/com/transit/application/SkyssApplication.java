package com.transit.application;

import android.app.Application;
import android.content.res.Resources;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.transit.route.services.RouteService;

public class SkyssApplication extends Application {
	
	public static final String TAG = "SkyssApp";

	private static SkyssApplication singleton;
	private static GoogleAnalyticsTracker analyticsTracker;
	
	private static RouteService routeService;
	
	
	public static SkyssApplication getInstance() {
		return singleton;
	}
	
	public final void onCreate() {
		super.onCreate();
		singleton = this;
		routeService = new RouteService(getResources(), getApplicationContext());
	}
	
	public static RouteService getRouteService() {
		return routeService;
	}
	
	public static GoogleAnalyticsTracker getAnalyticsTracker() {
		if (analyticsTracker == null) {
			analyticsTracker = GoogleAnalyticsTracker.getInstance();
		}
		return analyticsTracker;
	}
	
	public static Resources getAppResources() {
		return singleton.getResources();
	}
}
