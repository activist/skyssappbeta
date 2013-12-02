package com.transit.activities;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.transit.R;
import com.transit.application.SkyssApplication;
import com.transit.dom.Departure;
import com.transit.dom.Stop;
import com.transit.route.services.RouteService;
import com.transit.ui.adapter.FavouritesAdapter;
import com.transit.util.FavouritesUtil;

public class FavouritesActivity extends ListActivity {

	private RouteService routeService;
	private FavouritesUtil favouritesUtil;
	private List<Stop> favoriteStops;
	private FavouritesAdapter favoritesAdapter;
	private ProgressDialog progressDialog;
	private Toast toast;
	
	
	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			setListAdapter(favoritesAdapter);
			
			if (favoritesAdapter.isEmpty()) {
				toast = Toast.makeText(getApplicationContext(), "Du har ikke lagt til noen holdeplasser enda! Legg til nye i under \"avganger\".", Toast.LENGTH_LONG);
				toast.show();
			}
		}
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_layout);
        
        favouritesUtil = new FavouritesUtil(getApplicationContext());
        favoriteStops = favouritesUtil.getMyStops();
        
        routeService = new RouteService(getResources(), getApplicationContext());
        
        initListView();
        SkyssApplication.getAnalyticsTracker().trackPageView("/FavoritesScreen");

	}
	
	private void initListView() {
		progressDialog = ProgressDialog.show(FavouritesActivity.this, "", "Henter neste avganger for dine favoritter..", true);
		
		Thread populateListView = new Thread() {
			public void run() {

				for (Stop favoriteStop : favoriteStops) {
					Stop currentStop = routeService.findCurrentDepartureListForStop(favoriteStop.getId(), 5);
					favoriteStop.setName(currentStop.getName());
					favoriteStop.setDepartures(currentStop.getDepartures());
				}
				favoritesAdapter = new FavouritesAdapter(getApplicationContext(),
						R.layout.favourites_list_entry_layout, favoriteStops);

				handler.sendEmptyMessage(0);
			}
		};
	   populateListView.start();
	}
	
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
	    Stop stop = favoritesAdapter.getItem(position);
	    
    	Intent intent = new Intent(FavouritesActivity.this, DepartureListActivity.class);
    	intent.putExtra("stopId", stop.getId());
    	intent.putExtra("entryFromMyStops", true);
    	
    	this.startActivity(intent);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.favorites_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		progressDialog = ProgressDialog.show(FavouritesActivity.this, "", "Henter neste avganger for dine holdeplasser..", true);
		
	
		Thread populateListView = new Thread() {
			public void run() {

				for (Stop stop : favoriteStops) {
					Stop currentStop = routeService.findCurrentDepartureListForStop(stop.getId(), 5);
					List<Departure> departures = currentStop.getDepartures();
					stop.setName(departures.get(0).getStop().getName());
					stop.setDepartures(departures);
				}
				favoritesAdapter = new FavouritesAdapter(getApplicationContext(),
						R.layout.favourites_list_entry_layout, favoriteStops);
				
				handler.sendEmptyMessage(0);
			}
		};
	   populateListView.start();
	   return true;
	}
		
}
