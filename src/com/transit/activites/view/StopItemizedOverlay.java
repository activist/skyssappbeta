package com.transit.activites.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.transit.activities.DepartureListActivity;
import com.transit.dom.Stop;

public class StopItemizedOverlay extends BalloonItemizedOverlay<StopOverlayItem> {

    private ArrayList<StopOverlayItem> mOverlays = new ArrayList<StopOverlayItem>();
    private int size = 0;
    private Context context;
    
    public StopItemizedOverlay(Drawable defaultMarker, MapView mapView) {

		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
        populate();

    }
    
    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    public void addOverlay(StopOverlayItem overlay) {
        mOverlays.add(overlay);
        size = mOverlays.size();
        populate();
    }
    
    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
    	return super.onTap(p, mapView);
    }

    @Override
    public int size() {
        return size;
    }
    
	@Override
	protected boolean onBalloonTap(int index) {
		Stop stop = mOverlays.get(index).getStop();
			
		Intent intent = new Intent(context, DepartureListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.putExtra("stopId", stop.getId());
    	intent.putExtra("entryFromMyStops", false);
		context.startActivity(intent);
		return true;
	}

}

