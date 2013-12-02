package com.transit.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.transit.R;
import com.transit.dom.Departure;
import com.transit.dom.Stop;


public class AvailableStopsAdapter extends ArrayAdapter<Stop> {
	
	private List<Stop> stops;
	
	public AvailableStopsAdapter(Context context, int textViewResourceId, List<Stop> items) {
		super(context, textViewResourceId, items);
		stops = items;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.available_stop_entry_layout, null);
        }
        
        Stop stop = stops.get(position);
        if (stop != null) {
                TextView tt = (TextView) v.findViewById(R.id.availableStopName);
                if (tt != null) {
                      tt.setText(stop.getName());
                      
                      TextView nextDeparture = (TextView) v.findViewById(R.id.nextDepartureFromStop);
                      if (nextDeparture != null) {
                    	  if (!stop.getDepartures().isEmpty()) {
                    		  Departure departure = stop.getDepartures().iterator().next();
                    		  nextDeparture.setText("Neste avgang kl. " + departure.getTime() + " - "+ departure.getType() + " " + departure.getRoute() + " til " + departure.getDestination());
                    	  } else {
                    		  nextDeparture.setText("Ingen avganger funnet.");
                    	  }
                      }
                }
        }
		return v;
	}

}
