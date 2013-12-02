package com.transit.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.transit.R;
import com.transit.activites.view.StopItemizedOverlay;
import com.transit.activites.view.StopMapView;
import com.transit.activites.view.StopOverlayItem;
import com.transit.application.SkyssApplication;
import com.transit.dom.Stop;
import com.transit.util.FindStopCoordinatesTask;
import com.transit.util.MapUtil;
import com.transit.util.RemoteCallListener;

public class StopMapActivity extends MapActivity implements RemoteCallListener<Set<Stop>>, LocationListener {

	private StopMapView mapView;
	private MapController mapController;
	private List<Overlay> overLays;
	private LocationManager locManager;
	private MyLocationOverlay myLocLayer;
	
	private StopItemizedOverlay busStopsOverlay;
	private StopItemizedOverlay tramStopsOverlay;
	private StopItemizedOverlay boatStopsOverlay;
	private StopItemizedOverlay trainStopsOverlay;
	private List<StopItemizedOverlay> allOverlays;
	
	private Set<Stop> currentStops;
	
	private GeoPoint bergen;
	private GeoPoint currentPoint;
	private MapUtil mapUtil;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopsmap_layout);
        mapUtil = new MapUtil();
        initMapView();
        
        currentStops = new HashSet<Stop>();
        overLays = mapView.getOverlays();
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocLayer = new MyLocationOverlay(getApplicationContext(), mapView);

        initOverlays(myLocLayer);
        updateStopOverlaysOnMap();
        SkyssApplication.getAnalyticsTracker().trackPageView("/MapsScreen");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		currentPoint = mapView.getMapCenter();
		locManager.removeUpdates(this);
		locManager = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (locManager == null) {
			locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		
		if (myLocLayer == null ) {
	        myLocLayer = new MyLocationOverlay(getApplicationContext(), mapView);
		}
		
		if (currentPoint == null) {
			mapController.animateTo(bergen);
		} 
	}

	private void initOverlays(MyLocationOverlay myLocLayer) {
		Drawable busMarker = this.getResources().getDrawable(R.drawable.busstopp_marker);
        Drawable boatMarker = this.getResources().getDrawable(R.drawable.ferge_marker);
        Drawable tramMarker = this.getResources().getDrawable(R.drawable.trikk_marker);
        Drawable trainMarker = this.getResources().getDrawable(R.drawable.train_marker);
        busStopsOverlay = new StopItemizedOverlay(busMarker, mapView);
        tramStopsOverlay = new StopItemizedOverlay(tramMarker, mapView);
        boatStopsOverlay = new StopItemizedOverlay(boatMarker, mapView);
        trainStopsOverlay = new StopItemizedOverlay(trainMarker, mapView);
        
        allOverlays = new ArrayList<StopItemizedOverlay>();
        allOverlays.add(busStopsOverlay);
        allOverlays.add(tramStopsOverlay);
        allOverlays.add(trainStopsOverlay);
        allOverlays.add(boatStopsOverlay);
        
        overLays.add(myLocLayer);
        overLays.addAll(allOverlays);
	}
	
	private void updateStopOverlaysOnMap() {
		String[] screenCoordinates = getScreenCoordinates();
        new FindStopCoordinatesTask(this).execute(screenCoordinates);
	}

	private String[] getScreenCoordinates() {
		GeoPoint upperLeftCoordinates = getUpperLeftCoordinates();
        GeoPoint lowerRightCoordinates = getLowerRightCoordinates();
        
        String x1s = mapUtil.convertToString(upperLeftCoordinates.getLongitudeE6());
        String x2s = mapUtil.convertToString(lowerRightCoordinates.getLongitudeE6());
        String y1s = mapUtil.convertToString(lowerRightCoordinates.getLatitudeE6());
        String y2s = mapUtil.convertToString(upperLeftCoordinates.getLatitudeE6());
        
        String screenCoordinates[] = new String[]{x1s, x2s, y1s, y2s};
		return screenCoordinates;
	}

	private void updateStopsOverlay(Set<Stop> stops) {
		if (mapView.getZoomLevel() >= StopMapView.MIN_ZOOM_LEVEL_FOR_UPDATE) {
			for (Stop stop : stops) {
				
				GeoPoint g = new GeoPoint(stop.getLat().intValue(), stop.getLon().intValue());
				StopOverlayItem item = new StopOverlayItem(g,  stop.getName(), "Klikk for å se avgangsliste.", stop);
				
				if (stop.isBusStop()) {
					busStopsOverlay.addOverlay(item);
				} else if (stop.isTramStop()) {
					tramStopsOverlay.addOverlay(item);
				} else if (stop.isBoatStop()) {
					boatStopsOverlay.addOverlay(item);
				} else if (stop.isTrainStop()) {
					trainStopsOverlay.addOverlay(item);
				} else {
					busStopsOverlay.addOverlay(item);
				}
			}
			mapView.invalidate();
		}
			
	}
	
	private GeoPoint getUpperLeftCoordinates() {
		return mapView.getProjection().fromPixels(0, 0);
	}
	private GeoPoint getLowerRightCoordinates() {
		return mapView.getProjection().fromPixels(320, 480);
	}

	private void initMapView() {
		mapView = (StopMapView) findViewById(R.id.mapview);
		mapView.setStopsRemoteCallListenerActivity(this);
        mapView.setTraffic(true);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();
        mapController.setZoom(16);
        
        Double lat = 60.39028202275972*1E6;
        Double lng = 5.328197479248047*1E6;
        bergen = new GeoPoint(lat.intValue(), lng.intValue());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onRemoteCallComplete(Set<Stop> stops) {
		stops.removeAll(currentStops);
		currentStops.addAll(stops);
		updateStopsOverlay(stops);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_optionsmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		String locProvider = locManager.getBestProvider(criteria, true);
		if (locProvider != null) {
			Location lastKnownLocation = locManager.getLastKnownLocation(locProvider);
			
			if (lastKnownLocation != null) {
				locManager.requestLocationUpdates(locProvider, 5000l, 10, this);
				MyLocationOverlay myLocOverlay = new MyLocationOverlay(getApplicationContext(), mapView);
				myLocOverlay.enableMyLocation();
				overLays.add(myLocOverlay);
				
				updateWithNewLocation(lastKnownLocation);
				
				if (mapView.getZoomLevel() >= StopMapView.MIN_ZOOM_LEVEL_FOR_UPDATE) {
					String[] screenCoordinates = getScreenCoordinates();
					new FindStopCoordinatesTask(this).execute(screenCoordinates);
					mapView.invalidate();
				}
				return true;
			}
		} 
		return false;
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			updateWithNewLocation(location);
		}
		
	}

	private void updateWithNewLocation(Location location) {
		Double latitude = location.getLatitude()*1E6;
		Double longitude = location.getLongitude()*1E6;
		GeoPoint myLocation = new GeoPoint(latitude.intValue(), longitude.intValue());
		mapController.animateTo(myLocation);
	}

	public void onProviderDisabled(String provider) {
		updateWithNewLocation(null);
	}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
