package edu.umn.knoe0023.tweedar.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;


public class JsonToGeoTweetTranslator {
	public void translateTweetToGeoTweet(Exchange exchange) throws Exception {
		Map<String, Object> tweetData = (HashMap<String, Object>) exchange.getIn()
				.getBody();
		GeoTweet geoTweet = GeoTweetFactory.createGeoTweet(tweetData);
		exchange.getIn().setBody(geoTweet);
	}
}
