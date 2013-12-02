package com.transit.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.transit.R;
import com.transit.route.services.RouteService;
import com.transit.util.htmlparser.HtmlStringPresenter;
import com.transit.util.htmlparser.skyss.RouteSearchWebpagePresenterImpl;

public class RouteSearchActivity extends Activity {
	
	private RouteService routeService;

	private Button buttonPickDate;
	private EditText dateEdit;
	private Button buttonPickTime;
	private EditText timeEdit;
	private Button searchButton;
	
	private AutoCompleteTextView fromStopAutoTextView;
	private AutoCompleteTextView toStopAutoTextView;
	private ArrayAdapter<String> stopAdapter;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minutes;
	
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 11;
	
	

    private DatePickerDialog.OnDateSetListener dateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int mYear, 
                                  int monthOfYear, int dayOfMonth) {
                year = mYear;
                month = monthOfYear;
                day = dayOfMonth;
                dateEdit.setText(getDateAsString());
            }
        };
    
    private TimePickerDialog.OnTimeSetListener timeSetListener = 
    	new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
				hour = hourOfDay;
				minutes = minuteOfHour;
				timeEdit.setText(getTimeAsString());
				
			}
		};
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchlayout);
		
		initTimeAndDateComponents();
		initUiComponents();
		
		routeService = new RouteService(this.getResources(), getApplicationContext());
		
				
		final TextWatcher textWatcher = new TextWatcher() {
			
			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 2 && s != null) {
					List<String> stops = findStops(s.toString());
					stopAdapter.clear();
					
					for (String stop : stops) {
						stopAdapter.add(stop);
					}
				}
			}
		};
			
		fromStopAutoTextView.addTextChangedListener(textWatcher);
		toStopAutoTextView.addTextChangedListener(textWatcher);
		fromStopAutoTextView.setThreshold(3);
		toStopAutoTextView.setThreshold(3);
		
		
		searchButton = (Button) findViewById(R.id.buttonSearch);
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String html = routeService.findRoutes(fromStopAutoTextView.getText().toString(),
													toStopAutoTextView.getText().toString(),
													dateEdit.getText().toString(),
													timeEdit.getText().toString());
				
				HtmlStringPresenter htmlPresenter = new RouteSearchWebpagePresenterImpl();
				String presentableHTML = htmlPresenter.getPresentableHTML(html);

//				Intent intent = new Intent(RouteSearchActivity.this, SearchResultActivity.class);
//				intent.putExtra("html", presentableHTML);
//				
//				RouteSearchActivity.this.startActivity(intent);
					
			}
		});
		
	}

	private void initUiComponents() {
		fromStopAutoTextView = (AutoCompleteTextView) findViewById(R.id.searchFromStop);
		toStopAutoTextView = (AutoCompleteTextView) findViewById(R.id.searchToStop);
		
		stopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		stopAdapter.setNotifyOnChange(true);
		
		fromStopAutoTextView.setAdapter(stopAdapter);
		toStopAutoTextView.setAdapter(stopAdapter);
	}

	private void initTimeAndDateComponents() {
		initDateFields();
		
		buttonPickDate = (Button) findViewById(R.id.buttonPickDate);
		dateEdit = (EditText) findViewById(R.id.dateEditText);
		dateEdit.setText(getDateAsString());
		
		
		buttonPickDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		buttonPickTime = (Button) findViewById(R.id.buttonPickTime);
		timeEdit = (EditText) findViewById(R.id.timeEditText);
		timeEdit.setText(getTimeAsString());
		
		buttonPickTime.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    dateSetListener,
	                    year, month, day);
	        
	    case TIME_DIALOG_ID:
	    	return new TimePickerDialog(this,
	    				timeSetListener,
	    				hour,
	    				minutes,
	    				true);
	    }
	    return null;
	}
	
	private List<String> findStops(String input) {
		List<String> stops = new ArrayList<String>();
		try {
			stops.addAll(routeService.findAddressesByInput(input));
		} catch (Exception e) {
			//TODO: do something more useful
			e.printStackTrace();
		}
		return stops;
	}

	private void initDateFields() {
		final Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minutes = calendar.get(Calendar.MINUTE);
	}
	
	
	private String getDateAsString() {
        return new StringBuilder()
		        	.append(pad(day)).append("-")
		        	.append(pad(month + 1)).append("-")
		        	.append(year).toString();
	}
	
	private String getTimeAsString() {
		 return new StringBuilder()
			     	.append(pad(hour)).append(":")
			     	.append(pad(minutes)).toString();
	}
	
	private static String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}
}
