package com.transit.activities;

import com.transit.R;
import com.transit.application.SkyssApplication;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
//        SkyssApplication.getAnalyticsTracker().trackPageView("/HelpScreen");
    }
}
