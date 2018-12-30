package com.deskind.martiniboot.trade;


public class Ledger {
	
	private float loss;
	private float stopLoss;
	private float martiniFactor;
	private int oneRowWins;
	private int oneRowLooses;
	
	public float getLoss() {
		return loss;
	}
	public void setLoss(float loss) {
		this.loss = loss;
	}
	public float getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(float stopLoss) {
		this.stopLoss = stopLoss;
	}
	public float getMartiniFactor() {
		return martiniFactor;
	}
	public void setMartiniFactor(float martiniFactor) {
		this.martiniFactor = martiniFactor;
	}
	public int getOneRowWins() {
		return oneRowWins;
	}
	public void setOneRowWins(int oneRowWins) {
		this.oneRowWins = oneRowWins;
	}
	public int getOneRowLooses() {
		return oneRowLooses;
	}
	public void setOneRowLooses(int oneRowLooses) {
		this.oneRowLooses = oneRowLooses;
	}
	
	
}
