package com.transit.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.transit.R;
import com.transit.application.SkyssApplication;
import com.transit.dom.Departure;
import com.transit.dom.Stop;
import com.transit.route.services.RouteService;
import com.transit.ui.adapter.DepartureAdapter;
import com.transit.util.FavouritesUtil;
import com.transit.util.StopDeparturesPager;

public class DepartureListActivity extends ListActivity {

	private DepartureAdapter departureAdapter;
	private RouteService routeService;
	private FavouritesUtil favoritesUtil;
	private StopDeparturesPager pager;
	private ProgressDialog progressDialog;
	private Toast toast;
	private Boolean hasStopAsFavorite = Boolean.FALSE;
	private Stop currentStop;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.departure_list_layout);
        
        favoritesUtil = new FavouritesUtil(getApplicationContext());
        
        routeService = new RouteService(getResources(), getApplicationContext());
        initListView();

        Button myStopsBtn = (Button)findViewById(R.id.buttonAddToMyStops);
        if (!hasStopAsFavorite) {
        	configureMyStopsButton(myStopsBtn);
        } else {
        	myStopsBtn.setVisibility(View.GONE);
        }
        
        Button moreDeparturesBtn = (Button)findViewById(R.id.buttonSeeMoreDepartures);
        
        moreDeparturesBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (!departureAdapter.isEmpty()) {
					final Handler handler = new Handler () {
						
						public void handleMessage(Message message) {
							List<Departure> nextDepartures = pager.getNextDepartureInterval();
							departureAdapter.addDepartures(nextDepartures);
						}
					};
					
					Thread thread = new Thread() {
						
						public void run() {
							handler.sendEmptyMessage(0);
						}
					};
					thread.start();
				}
			}
		});
        
        SkyssApplication.getAnalyticsTracker().trackPageView("/DeparturesScreen");
	}


	private void configureMyStopsButton(final Button myStopsBtn) {
		myStopsBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (!departureAdapter.isEmpty()) {
					Stop stop = departureAdapter.getItem(0).getStop();
					SkyssApplication.getAnalyticsTracker().trackEvent("Favorites", "Added favorite", stop.getName(), 1);
					favoritesUtil.saveStopToMyStops(stop.getId());
					toast = Toast.makeText(getApplicationContext(), stop.getName() + " lagt til i favoritter", Toast.LENGTH_LONG);
					toast.show();
				} else {
					Bundle extras = getIntent().getExtras();
					String stopId = extras.getString("stopId");
					SkyssApplication.getAnalyticsTracker().trackEvent("Favorites", "Added favorite", stopId, 1);
					favoritesUtil.saveStopToMyStops(stopId);
					
					toast = Toast.makeText(getApplicationContext(), "Stoppet er lagt til i favoritter", Toast.LENGTH_LONG);
					toast.show();
				}
				myStopsBtn.setVisibility(View.GONE);
			}
		});
	}

	
	private void initListView() {
        
		Bundle extras = getIntent().getExtras();
		String stopId = extras.getString("stopId");
		hasStopAsFavorite = favoritesUtil.hasFavoriteStopWithId(stopId);
		
		getDepartures(stopId);
        
        setListAdapter(departureAdapter);
	}


	private void getDepartures(final String stopId) {
		progressDialog = ProgressDialog.show(DepartureListActivity.this, "", "Henter neste avganger..", true);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				setListAdapter(departureAdapter);
				
				if (currentStop != null) {
					SkyssApplication.getAnalyticsTracker().trackEvent("Departure", "Show departure", currentStop.getName(), 1);
				}
				
				TextView stopNameHeader = (TextView) findViewById(R.id.stopNameHeader);
				if (stopNameHeader != null) {
					stopNameHeader.setText(currentStop.getName());
				}
				
				if (departureAdapter.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Holdeplassen har ingen avganger i nærmeste fremtid.", Toast.LENGTH_LONG).show();
				}
			}
		};
		
		Thread populateListView = new Thread() {
			public void run() {
				
				Stop stop = routeService.findDepartureListForStop(stopId, 100, new Date());
				currentStop = stop;
				List<Departure> departures = stop.getDepartures();
				if (!departures.isEmpty()) {
					pager = new StopDeparturesPager(departures.get(0).getStop(), 10);
					departureAdapter = new DepartureAdapter(getApplicationContext(), R.layout.departure_list_layout, pager.getNextDepartureInterval());
				} else {
					departureAdapter = new DepartureAdapter(getApplicationContext(), R.layout.departure_list_layout, new ArrayList<Departure>());
				}
				handler.sendEmptyMessage(0);
			}
		};
		
		populateListView.start();
		
	}

}
