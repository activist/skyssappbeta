package com.transit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.transit.R;
import com.transit.application.SkyssApplication;

public class SkyssActivity extends Activity {

	private SharedPreferences mPreferences;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        WebView wv = (WebView) findViewById(R.id.trafikkWeb);
        wv.loadUrl("http://www.skyss.no/nn-no/Rutetider-og-kart/trafikkmeldingar/");
        
        configureMyStopsButton(this);
        configureMapButton(this);
        configureDeparturesButton(this);
        configureHelpButton();
        
        enableConnectivitySettingsIfDisabled();
        
		trackProductDetails();
    }
	
	private void trackProductDetails() {
		GoogleAnalyticsTracker analyticsTracker = SkyssApplication.getAnalyticsTracker();
		analyticsTracker.start("UA-21785607-1", this.getApplicationContext());
		analyticsTracker.setDispatchPeriod(10);
		analyticsTracker.trackPageView("/MainScreen");
		
		
		if (isFirstTimeUserStartsApp()) {
			analyticsTracker.trackEvent("Version Info", "Android release", Build.VERSION.RELEASE, 1);
			analyticsTracker.trackEvent("Version Info", "Model", Build.MODEL, 1);
			analyticsTracker.trackEvent("Version Info", "CPU", Build.CPU_ABI, 1);
			
			try {
				PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				analyticsTracker.trackEvent("Version Info", "App Version", pinfo.versionName, 1);
			} catch (NameNotFoundException e) {
				Log.d("SkyssApp", "Couldnt get packagename.");
			}
		}
	}

	private boolean isFirstTimeUserStartsApp() {
		mPreferences = getSharedPreferences(SkyssApplication.TAG, MODE_PRIVATE);
		boolean firstTime = mPreferences.getBoolean("isFirstTime", true);
		if (firstTime) { 
		    SharedPreferences.Editor editor = mPreferences.edit();
		    editor.putBoolean("isFirstTime", false);
		    editor.commit();
		}
		return firstTime;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SkyssApplication.getAnalyticsTracker().dispatch();
		SkyssApplication.getAnalyticsTracker().stop();
	}
	
	private void configureHelpButton() {
		Button helpButton = (Button) findViewById(R.id.buttonHelp);
        helpButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent().setClass(SkyssActivity.this, HelpActivity.class);;
        		SkyssActivity.this.startActivity(intent);
        	}
        });
	}

	private void configureMyStopsButton(final Activity thisContext) {
		Button myStopsButton = (Button) findViewById(R.id.buttonGoFav);
        myStopsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (internetIsEnabled()) {
					Intent favouritesIntent = new Intent().setClass(thisContext, FavouritesActivity.class);
					thisContext.startActivity(favouritesIntent);
				} else {
					enableConnectivitySettingsIfDisabled();
				}
			}
		});
	}

	private void enableConnectivitySettingsIfDisabled() {
		if (!internetIsEnabled()) {
        	Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        	startActivity(intent);
        }
	}

	private void configureDeparturesButton(final Activity thisContext) {
		Button departureListButton = (Button) findViewById(R.id.buttonDepartureList);
        departureListButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		enableConnectivitySettingsIfDisabled();
        		Intent depListIntent = new Intent().setClass(thisContext, DepartureSearchActivity.class);
        		thisContext.startActivity(depListIntent);
        	}
        });
	}

	private void configureMapButton(final Activity thisContext) {
		Button searchButton = (Button) findViewById(R.id.buttonGoToMap);
        searchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent searchIntent = new Intent().setClass(thisContext, StopMapActivity.class);
				thisContext.startActivity(searchIntent);
			}
		});
	}

	private boolean internetIsEnabled() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

}