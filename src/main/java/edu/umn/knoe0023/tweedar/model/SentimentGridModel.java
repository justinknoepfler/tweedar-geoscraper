package edu.umn.knoe0023.tweedar.model;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import edu.umn.knoe0023.tweedar.stream.Constants;

public class SentimentGridModel {

	ArrayList<SentimentDatum> sentimentDataArray = new ArrayList<SentimentDatum>(
			NUM_CELLS);
	long timeStart;
	long timeEnd;
	
	private SentimentScoringAlgorithm scoringAlgorithm;
	
	private static final String DB_TABLE_NAME = "SentimentGrid";
	private static final int NUM_CELLS = 2500;
	private static final int ROWS = 50;
	private static final int COLUMNS = 50;
	private static final String PUBLICATION_URI = "seda://model-publishSentimentGrid";

	private float longBinSize = 360 / COLUMNS;
	private float latBinSize = 180 / ROWS;

	public SentimentGridModel() throws Exception{
		scoringAlgorithm = new SentimentScoringAlgorithm();
		for (int x = 0; x < NUM_CELLS; x++) {
			sentimentDataArray.add(new SentimentDatum());
		}
		initialize();
	}
	
	public void initialize() {
		timeStart = System.currentTimeMillis();
		for (int x = 0; x < NUM_CELLS; x++) {
			sentimentDataArray.set(x, new SentimentDatum());
		}
	}

	public void add(GeoTweet tweet) {
		float score = scoringAlgorithm.score(tweet);
		int index = convertGPSCoordsToBin(tweet);
		sentimentDataArray.get(index).record(score);
	};

	private int convertGPSCoordsToBin(GeoTweet tweet) {
		float longitude = tweet.longitude + 180;
		float latitude = tweet.latitude + 90;
		int longBin = (int) (longitude / longBinSize);
		int latBin = (int) (latitude / latBinSize);
		int bin = (latBin * COLUMNS) + longBin;
		return bin;
	}

	public void publish(Exchange exchange) {
		timeEnd = System.currentTimeMillis();
		String queryString = "INSERT INTO " + DB_TABLE_NAME
				+ " VALUES(" + timeStart + "," + timeEnd + "," + ROWS + "," + COLUMNS + ",\"";
		for(int x = 0; x < NUM_CELLS; x++){
			queryString = queryString.concat(this.sentimentDataArray.get(x).getTotal() + " ");
		}
		queryString = queryString.concat("\");");
		ProducerTemplate camelProducer = exchange.getContext()
				.createProducerTemplate();
		exchange.getIn().setBody(queryString);
		camelProducer.send(PUBLICATION_URI, exchange);
		initialize();
	};
}
