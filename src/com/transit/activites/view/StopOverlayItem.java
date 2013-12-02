package com.transit.activites.view;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.transit.dom.Stop;

public class StopOverlayItem extends OverlayItem {

	private Stop stop;
	
	public StopOverlayItem(GeoPoint point, String title, String snippet, Stop stop) {
		super(point, title, snippet);
		this.stop = stop;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}
}
