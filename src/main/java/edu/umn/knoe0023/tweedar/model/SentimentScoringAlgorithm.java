package edu.umn.knoe0023.tweedar.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SentimentScoringAlgorithm {
	
	private static final String NEG_WORDS_FILE_PATH = "negative-words.txt";
	private static final String POS_WORDS_FILE_PATH = "positive-words.txt";
	
	private Map<String, Integer> sentimentDictionary;
	
	SentimentScoringAlgorithm() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		sentimentDictionary = new HashMap<String, Integer>();
		BufferedReader in = new BufferedReader(new FileReader(NEG_WORDS_FILE_PATH));
		String line = in.readLine();
		while(line != null){
			sentimentDictionary.put(line, -1);
			line = in.readLine();
		}
		in.close();
		
		in = new BufferedReader(new FileReader(POS_WORDS_FILE_PATH));
		line = in.readLine();
		while(line != null){
			sentimentDictionary.put(line, 1);
			line = in.readLine();
		}
		in.close();
		
	}
	public float score(GeoTweet tweet){
		float score = 0;
		String text = tweet.text;
		text = text.replaceAll(" .?!,", " ");
		String[] words = text.split(" ");
		for(String word : words){
			Integer points = sentimentDictionary.get(word);
			if (points != null){
				score += points;
			}
		}
		return score;
	}
	
}
