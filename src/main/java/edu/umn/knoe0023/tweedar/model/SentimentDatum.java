package edu.umn.knoe0023.tweedar.model;

public class SentimentDatum {
	private float scoreTotal;
	private float numRecords;
	
	public SentimentDatum(){
		scoreTotal = 0;
		numRecords = 0;
	}

	public float getAverage() {
		if (numRecords > 0) {
			return scoreTotal / numRecords;
		} else {
			return 0;
		}
	}
	
	public float getTotal(){
		return scoreTotal;
	}
	
	public float getNumRecords(){
		return numRecords;
	}
	
	public synchronized void record(float score){
		scoreTotal += score;
		numRecords++;
	}
}
