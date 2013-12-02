package com.transit.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.transit.R;
import com.transit.application.SkyssApplication;
import com.transit.dom.Stop;
import com.transit.route.services.RouteService;
import com.transit.ui.adapter.AvailableStopsAdapter;
import com.transit.util.factory.skyss.StopsFactory;

public class AvailableStopsActivity extends ListActivity {

	private AvailableStopsAdapter stopsAdapter;
	private ProgressDialog progressDialog;
	private RouteService routeService;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_stops_list);
        routeService = new RouteService(getResources(), getApplicationContext());
        initListView();

        Bundle extras = getIntent().getExtras();
		String searchedString = extras.getString("sokestreng");
		TextView header = (TextView) findViewById(R.id.availableStopHeader);
		header.setText("Holdeplasser v/" + searchedString + ":");
		
		SkyssApplication.getAnalyticsTracker().trackPageView("/AvailableStopsScreen");
	}
	
	private void initListView() {
		progressDialog = ProgressDialog.show(AvailableStopsActivity.this, "", "Søker etter holdeplasser..", true);
		
		final Handler handler = new Handler() {
			
			public void handleMessage(Message message) {
				progressDialog.dismiss();
				setListAdapter(stopsAdapter);
				if (stopsAdapter.isEmpty()) {
					final Toast toast = Toast.makeText(getApplicationContext(), "Ingen resultater funnet for angitt navn!", Toast.LENGTH_LONG);
					toast.show();
				}
			}
			
		};
		
		Thread thread = new Thread() {
			
			public void run() {
				Bundle extras = getIntent().getExtras();
				String html = extras.getString("html");
				
				final List<Stop> possibleStops = new ArrayList<Stop>();
				possibleStops.addAll(StopsFactory.createStopsFromHTML(html));
				for (Stop stop : possibleStops) {
					Stop currentStop = routeService.findCurrentDepartureListForStop(stop.getId(), 1);
					stop.setDepartures(currentStop.getDepartures());
				}
				Collections.sort(possibleStops);
				
				stopsAdapter = new AvailableStopsAdapter(getApplicationContext(), R.layout.simple_list_layout, possibleStops);
				handler.sendEmptyMessage(0);
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		
		Stop item = stopsAdapter.getItem(position);
		String stopId = item.getId();
		
		Intent intent = new Intent(AvailableStopsActivity.this, DepartureListActivity.class);
		intent.putExtra("stopId", stopId);
		AvailableStopsActivity.this.startActivity(intent);
	}
}
