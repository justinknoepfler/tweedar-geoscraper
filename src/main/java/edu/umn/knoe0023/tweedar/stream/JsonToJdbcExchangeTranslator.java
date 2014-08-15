package edu.umn.knoe0023.tweedar.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

public class JsonToJdbcExchangeTranslator {
	
	String regex;
	
	public JsonToJdbcExchangeTranslator(){
		// your special characters
		regex = "+ – && || ! ( ) { } [ ] ^ ” ~ * ? : \\ \" \'";
		// building a valid regex out of above
		regex = '(' + regex.replaceAll("([^\\s]{1,2})(?=(?:\\s+|$))",
		                               "\\\\Q$1\\\\E").replace(' ', '|') + ')';
	}
	public void translateTweetToJdbcInsertExchange(Exchange exchange){
		Map<String, Object> tweet = (HashMap<String, Object>) exchange.getIn().getBody();
		String query = "INSERT INTO " + Constants.TWEET_TABLE + " VALUES(";
		query = query.concat(tweetToString(tweet));
		query = query.concat(");");
		exchange.getIn().setBody(query);
	}
	
	private String tweetToString(Map<String, Object> tweet){
		String string = "";
		String coordsStr = "";
		HashMap<String, Object> coords = (HashMap<String, Object>)tweet.get("coordinates");
		if (coords != null){
			ArrayList<Object> coordArray = (ArrayList<Object>) coords.get("coordinates");
			if (coordArray != null){
				coordsStr = coordArray.get(0) + " " + coordArray.get(1);
			}
		}
		
		String text = (String)tweet.get("text");
		if (text != null){


			// actual replacement
			text = text.replaceAll(regex, "\\\\$1");
		}
		string = string.concat(tweet.get("id") + ", ");
		string = string.concat("\"" + coordsStr + "\"" + ", ");
		string = string.concat("\"" + text + "\"");
		return string;
	}

}
