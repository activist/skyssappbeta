package com.transit.util;

import java.text.DecimalFormat;

public class MapUtil {

    private DecimalFormat df = new DecimalFormat();

    public MapUtil() {
        df.setMinimumFractionDigits(13);
        df.setPositiveSuffix("1");
    }
	
	public String convertToString(int coordinate) {
        Double coord = (Double)  Double.valueOf(coordinate) / (Double)1E6;
        return df.format(coord);
	}
	
	public static Double parse(String coordinate) {
		coordinate = coordinate.replace(",", ".");
		return Double.parseDouble(coordinate);
	}
	
}
