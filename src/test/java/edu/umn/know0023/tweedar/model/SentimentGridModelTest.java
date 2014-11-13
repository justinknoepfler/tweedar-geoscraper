package edu.umn.know0023.tweedar.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import edu.umn.knoe0023.tweedar.model.GeoTweet;
import edu.umn.knoe0023.tweedar.model.SentimentDatum;
import edu.umn.knoe0023.tweedar.model.SentimentGridModel;

public class SentimentGridModelTest {
	
	private static Logger LOGGER;
	
	@Test
	public void testGpsConversion() {
		SentimentGridModel model = null;
		try {
			model = new SentimentGridModel();
		}
		catch (Exception e){
			Assert.assertTrue("Model creation threw exception " + e.getClass(), false);
		}
		
		List<Double> testLat = Arrays.asList(-90.0, -45.0, 0.0, 45.5, 90.0);
		List<Double> testLong = Arrays.asList(-180.0, -90.0, 0.0, 90.0, 180.0);
		
		float latBinsize = model.latBinSize;
		float lngBinsize = model.longBinSize;
		System.out.println("latsize: " + latBinsize);
		System.out.println("lngsize: " + lngBinsize);
		System.out.println((-45.0 + 90) / latBinsize);
		
		for (int index = 0; index < testLat.size(); index ++){
			float lat = Float.parseFloat(testLat.get(index).toString());
			float lng = Float.parseFloat(testLong.get(index).toString());
			GeoTweet testTweet = new GeoTweet();
			testTweet.latitude = lat;
			testTweet.longitude = lng;
			model.add(testTweet);
		}
		
		HashMap<Integer, SentimentDatum> testMap = model.getSentimentDataMap();
		for(Entry<Integer, SentimentDatum> entry : testMap.entrySet()){
			SentimentDatum testDatum = entry.getValue();
			Integer index = entry.getKey();
			System.out.println(index + ": (" + testDatum.getXCoordinate() + ", " + testDatum.getYCoordinate() + ")");
		}
		
	}

}
