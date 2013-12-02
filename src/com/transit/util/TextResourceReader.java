package com.transit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.res.Resources;


public class TextResourceReader {

	private Resources resources;
	
	public TextResourceReader(Resources resources) {
		this.resources = resources;
	}
	
	/**
	 * Reads a text file and stores each line that matches the prefix in a {@link List}.
	 * 
	 * @param resourceId
	 * @param prefix
	 * @return
	 */
	public Set<String> searchText(int resourceId, String prefix, int noResults) {
		if (resources == null) {
			return new HashSet<String>();
		}
		
		Set<String> matches = new HashSet<String>();
		InputStream is = null;
		BufferedReader bf = null; 
		
		try {
			is = resources.openRawResource(resourceId);
			bf = new BufferedReader(new InputStreamReader(is, "cp1252"));

			String line = null;
			while ((line = bf.readLine()) != null ) {
				
				
				if( matches.size() > noResults) {
					break;
				}
				
				if (line.toLowerCase().startsWith(prefix.toLowerCase())) {
					matches.add(line);
				}
			}
		} catch (IOException e) {
			//swallow.. If this occurs it will get all stops through http as the user types --> slower
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// swallow
				}
			}
		}
		return matches;
	}
}
