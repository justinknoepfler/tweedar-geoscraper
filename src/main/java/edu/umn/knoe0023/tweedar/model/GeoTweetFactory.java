package edu.umn.knoe0023.tweedar.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeoTweetFactory {

	static String regex;

	static {
		regex = "+ – && || ! ( ) { } [ ] ^ ” ~ * ? : \\ \" \'";
		regex = '(' + regex.replaceAll("([^\\s]{1,2})(?=(?:\\s+|$))",
				"\\\\Q$1\\\\E").replace(' ', '|') + ')';
	}

	public static GeoTweet createGeoTweet(Map<String, Object> tweetData) {
		GeoTweet gt = new GeoTweet();

		// get latitude and longitude
		HashMap<String, Object> coords = (HashMap<String, Object>) tweetData
				.get("coordinates");
		if (coords != null) {
			ArrayList<Object> coordArray = (ArrayList<Object>) coords
					.get("coordinates");
			if (coordArray != null) {
				gt.longitude = new Float((Double)coordArray.get(0));
				gt.latitude =  new Float((Double) coordArray.get(1));
			}
		}
		
		String text = (String)tweetData.get("text");
		if (text != null){
			text = text.replaceAll(regex, "\\\\$1");
		}
		
		gt.text = text;

		return gt;
	}
}
