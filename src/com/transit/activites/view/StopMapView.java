package com.transit.activites.view;

import java.text.DecimalFormat;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.transit.dom.Stop;
import com.transit.util.FindStopCoordinatesTask;
import com.transit.util.MapUtil;
import com.transit.util.RemoteCallListener;

public class StopMapView extends MapView {

	private RemoteCallListener<Set<Stop>> listenerActivity;
	
	public static int MIN_ZOOM_LEVEL_FOR_UPDATE = 16;
	private int oldZoomLevel = 16;
	private MapUtil mapUtil;
	
	public StopMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mapUtil = new MapUtil();
	}

	public StopMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mapUtil = new MapUtil();
	}

	public StopMapView(Context context, String apiKey) {
		super(context, apiKey);
		mapUtil = new MapUtil();
	}

	
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			updateStopOverlaysOnMap();
		}
		return super.onTouchEvent(ev);
	}
	

	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != oldZoomLevel) {
			updateStopOverlaysOnMap();
			oldZoomLevel = getZoomLevel();
		}
	}
	
	private void updateStopOverlaysOnMap() {
		GeoPoint upperLeftCoordinates = getUpperLeftCoordinates();
        GeoPoint lowerRightCoordinates = getLowerRightCoordinates();
        
        String x1s = mapUtil.convertToString(upperLeftCoordinates.getLongitudeE6());
        String x2s = mapUtil.convertToString(lowerRightCoordinates.getLongitudeE6());
        String y1s = mapUtil.convertToString(lowerRightCoordinates.getLatitudeE6());
        String y2s = mapUtil.convertToString(upperLeftCoordinates.getLatitudeE6());
        
        //triggering new task to be handled at listener activity
        if (getZoomLevel() >= MIN_ZOOM_LEVEL_FOR_UPDATE) {
        	new FindStopCoordinatesTask(listenerActivity).execute(x1s, x2s, y1s, y2s);
        }
	}
	private GeoPoint getUpperLeftCoordinates() {
		return this.getProjection().fromPixels(0, 0);
	}
	private GeoPoint getLowerRightCoordinates() {
		return this.getProjection().fromPixels(getWidth(), getHeight());
	}
	
	public void setStopsRemoteCallListenerActivity(RemoteCallListener<Set<Stop>> listenerActivity) {
		this.listenerActivity = listenerActivity;
	}
	
}
