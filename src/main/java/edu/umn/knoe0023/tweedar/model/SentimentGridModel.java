package edu.umn.knoe0023.tweedar.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;

public class SentimentGridModel {

	HashMap<Integer, SentimentDatum> sentimentDataMap = new HashMap<Integer, SentimentDatum>();
	long timeStart;
	long timeEnd;

	private SentimentScoringAlgorithm scoringAlgorithm;

	private static final String GRIDMAP_TABLE_NAME = "gridmap";
	private static final String BIN_TABLE_NAME = "bin";
	private static final String PUBLICATION_URI = "seda://model-publishSentimentGrid";

	public static final int ROWS = 128;
	public static final int COLUMNS = 256;

	public float longBinSize = (float) (360.0f / COLUMNS);
	public float latBinSize = (float) (180.0f / ROWS);

	public SentimentGridModel() throws Exception {
		scoringAlgorithm = new SentimentScoringAlgorithm();
		initialize();
	}

	public void initialize() {
		timeStart = System.currentTimeMillis();
	}

	public void add(GeoTweet tweet) {
		float score = scoringAlgorithm.score(tweet);
		int index = convertGPSCoordsToBin(tweet);
		SentimentDatum datum = sentimentDataMap.get(index);
		if (datum == null) {
			datum = new SentimentDatum();
			datum.setXCoordinate((int) ((tweet.longitude + 180) / longBinSize));
			datum.setYCoordinate((int) ((-1 * (tweet.latitude - 90)) / latBinSize));
		}
		datum.record(score);
		sentimentDataMap.put(index, datum);
	};

	public int convertGPSCoordsToBin(GeoTweet tweet) {
		float longitude = tweet.longitude + 180;
		float latitude = tweet.latitude + 90;
		int longBin = (int) (longitude / longBinSize);
		int latBin = (int) (latitude / latBinSize);
		int bin = (latBin * COLUMNS) + longBin;
		return bin;
	}

	public void publish(Exchange exchange) {
		// time to stop and publish
		timeEnd = System.currentTimeMillis();
		ProducerTemplate camelProducer = exchange.getContext()
				.createProducerTemplate();
		publishGridMap(exchange, camelProducer);
		publishDatapoints(exchange, camelProducer);

		// reset model
		initialize();
	}

	private void publishGridMap(Exchange exchange,
			ProducerTemplate camelProducer) {
		String queryString = "INSERT INTO " + GRIDMAP_TABLE_NAME + " VALUES("
				+ timeStart + "," + timeStart + "," + timeEnd + ");";
		exchange.getIn().setBody(queryString);
		camelProducer.send(PUBLICATION_URI, exchange);
	}

	private void publishDatapoints(Exchange exchange,
			ProducerTemplate camelProducer) {
		Queue<SentimentDatum> toPublish = new LinkedList<SentimentDatum>();
		for (Entry<Integer, SentimentDatum> entry : sentimentDataMap.entrySet()) {
			SentimentDatum datum = entry.getValue();
			toPublish.add(datum);
		}
		while (!toPublish.isEmpty()) {
			SentimentDatum datum = toPublish.poll();
			if (datum.getTotal() != 0) {
				Exchange datumExchange = new DefaultExchange(
						exchange.getContext());
				String datumQuery = "INSERT INTO " + BIN_TABLE_NAME
						+ " VALUES(" + timeStart + ", "
						+ datum.getXCoordinate() + ", "
						+ datum.getYCoordinate() + ", " + datum.getTotal()
						+ ")";
				datumExchange.getIn().setBody(datumQuery);
				camelProducer.send(PUBLICATION_URI, datumExchange);
			}
		}
	};
	
	public HashMap<Integer, SentimentDatum> getSentimentDataMap(){
		return this.sentimentDataMap;
	}
}
