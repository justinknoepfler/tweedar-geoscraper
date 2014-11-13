package edu.umn.knoe0023.tweedar.model;

public class SentimentDatum {
	private float scoreTotal;
	private float numRecords;
	private int xCoordinate;
	private int yCoordinate;

	public int getXCoordinate() {
		return xCoordinate;
	}

	public void setXCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getYCoordinate() {
		return yCoordinate;
	}

	public void setYCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}


	
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
