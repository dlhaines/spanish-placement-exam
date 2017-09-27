package edu.umich.its.spe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SPEUtils {

	// get a string property and default it to empty string.
	static protected String safeGetPropertyValue(HashMap<String, String> testproperties, String propertyKey) {
		if (testproperties == null) {
			return "";
		}
		String propertyValue = (testproperties.get(propertyKey) == null ? "" :  testproperties.get(propertyKey));
		return propertyValue;
	}

	static String getISO8601StringForDate(Date date) {
		// Get the current time (with time zone).
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());
		return dateFormat.format(date);
	}

}
