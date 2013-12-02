package com.transit.activities;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.transit.R;
import com.transit.application.SkyssApplication;
import com.transit.route.services.RouteService;
import com.transit.util.FindAddressesAsyncTask;
import com.transit.util.RemoteCallListener;
import com.transit.util.factory.skyss.DepartureFactory;
import com.transit.util.htmlparser.skyss.StopSearchMobilePagePresenter;

public class DepartureSearchActivity extends Activity implements RemoteCallListener<Set<String>>{

	private RouteService routeService;

	private ImageButton searchButton;
	private ImageButton clearButton;
	
	private AutoCompleteTextView autoCompleteTextView;
	private ArrayAdapter<String> stopAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departure_search_layout);
		
		routeService = new RouteService(getResources(), getApplicationContext());
		
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchFromStop);

		stopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		stopAdapter.setNotifyOnChange(true);
		
		autoCompleteTextView.setAdapter(stopAdapter);
		
		SkyssApplication.getAnalyticsTracker().trackPageView("/AddressSearchScreen");
				
		final TextWatcher textWatcher = new TextWatcher() {
			
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() >= 3 && s != null) {
					Collection<String> addresses = routeService.findAddressesByInput(s.toString());
					
					stopAdapter.clear();
					for (String address : addresses) {
						stopAdapter.add(address);
					}
					stopAdapter.notifyDataSetChanged();
					
					if (stopAdapter.getCount() < 10) {
						new FindAddressesAsyncTask(DepartureSearchActivity.this).execute(s.toString());
					}
				}
			}
		};
			
		autoCompleteTextView.addTextChangedListener(textWatcher);
		autoCompleteTextView.setThreshold(3);
		
		clearButton = (ImageButton) findViewById(R.id.deleteTextBtn);
		clearButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				autoCompleteTextView.setText("");
				
			}
		});
		
		searchButton = (ImageButton) findViewById(R.id.buttonSearch);
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SkyssApplication.getAnalyticsTracker().trackEvent("Addresses", "Searched address", autoCompleteTextView.getText().toString(), 1);
				
				String html = routeService.findStopsNearAddress(autoCompleteTextView.getText().toString(),
																	getCurrentDateAsString(),
																	getCurrentTimeAsString());
				String presentableHTML = "";
				StopSearchMobilePagePresenter htmlPresenter = new StopSearchMobilePagePresenter();
				
				if (htmlPresenter.isAvailableForInputHTML(html)) {
					presentableHTML = htmlPresenter.getPresentableHTML(html);
					Intent intent = new Intent(DepartureSearchActivity.this, AvailableStopsActivity.class);
					intent.putExtra("html", presentableHTML);
					intent.putExtra("sokestreng", autoCompleteTextView.getText().toString());
					DepartureSearchActivity.this.startActivity(intent);
					
				} else if (DepartureFactory.isAvailableForInputHTML(html)) {
					//FIXME: This should use XML
					Intent intent = new Intent(DepartureSearchActivity.this, DepartureListActivity.class);
					intent.putExtra("stopId", DepartureFactory.getStopId(html));
					DepartureSearchActivity.this.startActivity(intent);
					
				} else {
					Toast.makeText(getApplicationContext(), "Klarte ikke finne noen holdeplasser i nærheten av \""
									+ autoCompleteTextView.getText().toString() + "\".", Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}
	
	private String getCurrentTimeAsString() {
		final Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		return new StringBuilder()
	     	.append(pad(hour)).append(":")
	     	.append(pad(minutes)).toString();
	}
	
	private String getCurrentDateAsString() {
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		 return new StringBuilder()
	     	.append(pad(day)).append(".")
	     	.append(pad(month + 1)).append(".")
	     	.append(year).toString();
	}
	
	private static String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}

	public void onRemoteCallComplete(Set<String> addresses) {
		for (String address : addresses) {
			if(!containsAddress(address)) {
				this.stopAdapter.add(address);
			}
		}
		this.stopAdapter.notifyDataSetChanged();
	}

	private boolean containsAddress(String address) {
		for (int i = 0; i < this.stopAdapter.getCount(); i++) {
			String existingAddress = this.stopAdapter.getItem(i);
			if(address.equals(existingAddress)) {
				return true;
			}
		}
		return false;
	}
	
	
	
}
