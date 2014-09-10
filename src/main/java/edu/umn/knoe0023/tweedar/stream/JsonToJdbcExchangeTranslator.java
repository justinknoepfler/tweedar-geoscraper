package edu.umn.knoe0023.tweedar.stream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;

public class JsonToJdbcExchangeTranslator {

	String regex;
	private static final String TWITTER_DATE_FORMAT  = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	SimpleDateFormat dateFormat;

	public JsonToJdbcExchangeTranslator() {
		// characters to filter from tweets
		regex = "+ – && || ! ( ) { } [ ] ^ ” ~ * ? : \\ \" \'";
		regex = '(' + regex.replaceAll("([^\\s]{1,2})(?=(?:\\s+|$))",
				"\\\\Q$1\\\\E").replace(' ', '|') + ')';
		dateFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT);
		dateFormat.setLenient(true);
	}

	public void translateTweetToJdbcInsertExchange(Exchange exchange) throws Exception {
		Map<String, Object> tweet = (HashMap<String, Object>) exchange.getIn()
				.getBody();
		String query = "INSERT INTO " + Constants.TWEET_TABLE + " VALUES(";
		query = query.concat(tweetToString(tweet));
		query = query.concat(");");
		exchange.getIn().setBody(query);
	}

	private String tweetToString(Map<String, Object> tweet) throws Exception{
		String string = "";
		long time = System.currentTimeMillis();
		
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
			text = text.replaceAll(regex, "\\\\$1");
		}
		string = string.concat(time + ", ");
		string = string.concat("\"" + coordsStr + "\"" + ", ");
		string = string.concat("\"" + text + "\"");
		return string;
	}
}
