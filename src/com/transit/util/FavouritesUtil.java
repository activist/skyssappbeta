package com.transit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.transit.application.SkyssApplication;
import com.transit.dom.Stop;

public class FavouritesUtil {
	
	private static final String INTERNAL_FILENAME = "mystops.dat";
	
	private static final String EXTERNAL_PATH = "/Android/data/skyssappbeta/files";
	private static final String EXTERNAL_FILENAME = "mystops.dat";

	private Context context;
	
	private FavouritesUtil() {
	}
	
	
	public FavouritesUtil(Context context) {
		this.context = context;
	}

	
	public List<Stop> getMyStops() {
		
		return getMyStopsFromFile();
	}
	
	
	private boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}
	
	private boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
	
	private List<Stop> getMyStopsFromFile() {
		List<Stop> stops = new ArrayList<Stop>();
		String[] commaSeparatedStops = readCommaSeparatedTextFile().split(",");
		
		
		for (String stopId : commaSeparatedStops) {
			if (stopId != null && !stopId.equals("")) {
				Stop stop = new Stop();
				stop.setId(stopId);
				stops.add(stop);
			}
		}
		
		return stops;
	}


	private String readCommaSeparatedTextFile() {
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream input = null;
			if (isExternalStorageReadable()) {
				File root = Environment.getExternalStorageDirectory();
				File file = new File(root.getAbsolutePath() + EXTERNAL_PATH, EXTERNAL_FILENAME);
				input = new FileInputStream(file);
			} else {
				input = context.openFileInput(INTERNAL_FILENAME);
			}
			
			int ch = 0;
			while( (ch = input.read()) != -1) {
				sb.append((char)ch);
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			Log.d(SkyssApplication.TAG, "Fant ikke filen for å lese mine stopp!");
		} catch (IOException e) {
			
		}
		return sb.toString();
	}
	
	public void saveStopToMyStops(String stopId) {
		if (stopId == null) {
			return;
		}
		
		stopId = stopId.trim();
		
		if(hasFavoriteStopWithId(stopId) || stopId.equals("")) {
			return;
		}
		
		stopId = stopId +",";
		writeStopToFile(stopId, Context.MODE_APPEND);
	}


	public void writeStopToFile(String stopId, int mode) {
		try {
			FileOutputStream output = null;
			if (isExternalStorageWriteable()) {
				File root = Environment.getExternalStorageDirectory();
				File dir = new File(root.getAbsolutePath() + EXTERNAL_PATH);
				File file = new File(root.getAbsolutePath() + EXTERNAL_PATH, EXTERNAL_FILENAME);
				
				if(!dir.exists()) {
					dir.mkdirs();
					if(!file.exists()) {
						file.createNewFile();
					}
				}
				
				FileWriter writer = null;
				
				if (mode == Context.MODE_APPEND) {
					writer = new FileWriter(file, true);
					
				} else {
					writer = new FileWriter(file, false);
				}
				writer.append(stopId);
				writer.flush();
				writer.close();
			} else {
				output = context.openFileOutput(INTERNAL_FILENAME, mode);
				output.write(stopId.getBytes());
				output.flush();
				output.close();
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			Log.d(SkyssApplication.TAG, "Fant ikke filen for å skrive til mine stopp: " + e.getMessage());
		} catch (IOException e) {
			Log.d(SkyssApplication.TAG, "Klarte ikke skrive til mine stopp: " + e.getMessage());
		}
	}
	
	public boolean hasFavoriteStopWithId(String id) {
		List<Stop> myStops = getMyStops();
		
		return containsStopWithId(id, myStops);
	}
	
	public void deleteStop(String stopId) {
		
		Stop stopToBeRemoved = findStopToBeRemoved(stopId);
		if (stopToBeRemoved != null) {
			String[] commaSeparatedStopIds = readCommaSeparatedTextFile().split(",");
			StringBuilder remainingStops = new StringBuilder();
			for (String string : commaSeparatedStopIds) {
				if (!string.equals(stopId)) {
					remainingStops.append(string +",");
				}
			}
			
			writeStopToFile(remainingStops.toString(), Context.MODE_PRIVATE);
		}
	}
	
	private Stop findStopToBeRemoved(String stopId) {
		List<Stop> existingMyStops = getMyStops();
		for (Stop stop : existingMyStops) {
			if (stop.getId().equals(stopId)) {
				return stop;
			}
		}
		return null;
	}


	private boolean containsStopWithId(String id, List<Stop> myStops) {
		for (Stop stop : myStops) {
			if (stop.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
}
