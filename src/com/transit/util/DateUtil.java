package com.transit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.transit.application.SkyssApplication;

import android.util.Log;

public class DateUtil {
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	public final static SimpleDateFormat SKYSS_DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public static String getTimeFromDate(Date date) {
		return timeFormat.format(date);
	}
	
	public static String removeTime(Date date) {
		return dateFormat.format(date);
	}
	
	public static Date parseDate(String datetime) {
		if (datetime.length() < 11) {
			datetime = datetime + " 00:00:00";
		}
		Date date = null;
		try {
			date = SKYSS_DATEFORMAT.parse(datetime);
		} catch (ParseException e) {
			Log.d(SkyssApplication.TAG, "Couldnt parse date : " + e.getMessage());
		}
		return date;
	}

}
