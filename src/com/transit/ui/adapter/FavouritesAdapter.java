package com.transit.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.transit.R;
import com.transit.activities.DepartureListActivity;
import com.transit.application.SkyssApplication;
import com.transit.dom.Departure;
import com.transit.dom.Stop;
import com.transit.util.FavouritesUtil;

public class FavouritesAdapter extends ArrayAdapter<Stop>{

	
	private List<Stop> stops;
	private FavouritesUtil favUtil;
	private Context context;
	
	public FavouritesAdapter(Context context, int textViewResourceId, List<Stop> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		stops = items;
		favUtil = new FavouritesUtil(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.favourites_list_entry_layout, null);
        }
        
        ImageButton viewButton = (ImageButton) v.findViewById(R.id.viewFavBtn);
        ImageButton deleteButton = (ImageButton) v.findViewById(R.id.deleteFavBtn);
        
        final Stop stop = stops.get(position);
        if (stop != null) {
        		viewButton.setTag(stop);
        		viewButton.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Intent intent = new Intent(context, DepartureListActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    	intent.putExtra("stopId", stop.getId());
				    	intent.putExtra("entryFromMyStops", true);
						context.startActivity(intent);
					}
				});
        	
        	
            	deleteButton.setTag(stop);
            	deleteButton.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						removeStopFromAdapterList(stop);
						SkyssApplication.getAnalyticsTracker().trackEvent("Favorites", "Deleted favorite", stop.getName(), 1);
						favUtil.deleteStop(stop.getId());
						notifyDataSetChanged();
					}
				});
            	
                TextView stopName = (TextView) v.findViewById(R.id.myStopName);
                
                if (stopName != null) {
                	stopName.setText(stop.getName());                            
                }
                
                List<TextView> textViews = new ArrayList<TextView>();
                textViews.add((TextView) v.findViewById(R.id.myStopInfo1));
                textViews.add((TextView) v.findViewById(R.id.myStopInfo2));
                textViews.add((TextView) v.findViewById(R.id.myStopInfo3));
                textViews.add((TextView) v.findViewById(R.id.myStopInfo4));
                textViews.add((TextView) v.findViewById(R.id.myStopInfo5));
                
                
                List<Departure> stopDepartures = stop.getDepartures();
                
				if (stopDepartures != null) {
					addTextForDepartures(textViews, stopDepartures);
                } else {
                	TextView myStopInfo = textViews.get(0);
                	myStopInfo.setText("\tIngen flere avganger i dag!");
                }
        }
		return v;
	}

	private void addTextForDepartures(List<TextView> textViews, List<Departure> stopDepartures) {
		List<Departure> shortList = stripToMaxFiveDepartures(stopDepartures);
		
		if (shortList.isEmpty()) {
			for (int i = 0; i < textViews.size(); i++) {
				TextView textView = textViews.get(i);
				if (i == 0) {
					textViews.get(0).setText("\tIngen flere avganger i dag!");
				} else {
					textView.setText("");
				}
			}
		} else {
			int index = 0;
			for (Departure dep : shortList) {
				TextView myStopInfo = textViews.get(index);
				
				if (myStopInfo != null) {
					myStopInfo.setText(createText(dep));
				}
				index++;
			}
		}
		
	}
	
	public void removeStopFromAdapterList(Stop stop) {
		if(stops.contains(stop)) {
			stops.remove(stop);
		}
	}

	private String createText(Departure dep) {
		return "\t" + dep.getTime() + " - Rute " + dep.getRoute() + ". \t" + dep.getType() + " til " + dep.getDestination();
	}

	private List<Departure> stripToMaxFiveDepartures(List<Departure> stopDepartures) {
		List<Departure> shortList = new ArrayList<Departure>(5);
		int size = stopDepartures.size();
		for (int i = 0; i < 5; i++) {
			if (size > i) {
				shortList.add(stopDepartures.get(i));
			}
		}
		return shortList;
	}
}
