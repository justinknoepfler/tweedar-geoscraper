package edu.umn.knoe0023.tweedar.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

public class TweetFilter {
	public boolean process(Exchange exchange){
		
		Map<String, Object> json = (HashMap<String, Object>)exchange.getIn().getBody();
		if (json.get("coordinates") == null){
			return false;
		}
		return true;	
		
	}
}
