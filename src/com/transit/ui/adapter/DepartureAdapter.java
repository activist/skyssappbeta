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

public class DepartureAdapter extends ArrayAdapter<Departure> {

	private List<Departure> departures;
	
	public DepartureAdapter(Context context, int textViewResourceId, List<Departure> objects) {
		super(context, textViewResourceId, objects);
		this.departures = objects;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.departure_entry_layout, null);
        }
        
        Departure departure = departures.get(position);
        if (departure != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                if (tt != null) {
                      tt.setText(departure.getTime() + " - Rute " + departure.getRoute());                            
                }
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (bt != null) {
                	String text = departure.getType() + " til " + departure.getDestination();
                	if (departure.getComment() != null && !departure.getComment().equals("")) {
                		text = text + " - " + departure.getComment();
                	}
					bt.setText(text);                            
                }
        }
		return v;
	}
	
	public void addDepartures(List<Departure> departures) {
		this.departures.addAll(departures);
		notifyDataSetChanged();
	}
	
}
