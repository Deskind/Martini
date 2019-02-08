package com.deskind.martiniboot.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.controllers.MainController;
import com.deskind.martiniboot.entities.LuckyGuy;

import javafx.scene.chart.XYChart;

public class Ledger {
	private float currentStake;
	private float profit;
	private float loss;
	private float stopLoss;
	private float martiniFactor;
	
	private int allTimeWins;
	private int allTimeLooses;
	private int allTimeContracts;
	private int wins;
	private int looses;
	
	private String currentContractId;
	
	public Ledger(float martiniFactor, float stopLoss) {
		this.martiniFactor = martiniFactor;
		this.stopLoss = stopLoss;
	}

	public void updateCounters(float amount) {
		allTimeContracts++;
		
		if(amount > 0) {
			wins++;
		}else {
			looses++;
		}
	}
	
	public void resetCounters() {
		wins = 0;
		looses = 0;
	}
	
	public void updateProfit(float amount) {
		if(amount > 0) {
			profit += amount - currentStake;
		}else {
			profit -= amount;
		}
	}
	
	private boolean stopLossCheck() {
		
		if(loss > stopLoss)
			return true;
		
		return false;
	}

	/**
	 * Round float value to specified amount of decimal places
	 * @param number
	 * @param decimalPlace
	 * @return
	 */
	public static float round(float number, int decimalPlace) {
		BigDecimal bd = new BigDecimal(number);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
	
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
	public int getallTimeWins() {
		return allTimeWins;
	}
	public void setallTimeWins(int allTimeWins) {
		this.allTimeWins = allTimeWins;
	}
	public int getallTimeLooses() {
		return allTimeLooses;
	}
	public void setallTimeLooses(int allTimeLooses) {
		this.allTimeLooses = allTimeLooses;
	}
	public int getAllTimeContracts() {
		return allTimeContracts;
	}
	public void setAllTimeContracts(int allTimeContracts) {
		this.allTimeContracts = allTimeContracts;
	}
	
	public float getCurrentStake() {
		return currentStake;
	}
	public void setCurrentStake(float currentStake) {
		this.currentStake = currentStake;
	}
	
	public float getProfit() {
		return profit;
	}
	public void setProfit(float profit) {
		this.profit = profit;
	}

	@Override
	public String toString() {
		return "Ledger [currentStake=" + currentStake + ", profit=" + profit + ", loss=" + loss + ", stopLoss="
				+ stopLoss + ", martiniFactor=" + martiniFactor + ", allTimeWins=" + allTimeWins + ", allTimeLooses="
				+ allTimeLooses + ", allTimeContracts=" + allTimeContracts + "]";
	}

	public void setCurrentContractId(String id) {
		currentContractId = id;
	}

	public String getCurrentContractId() {
		return currentContractId;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLooses() {
		return looses;
	}

	public void setLooses(int looses) {
		this.looses = looses;
	}

	public void resetLoss() {
		loss = 0;
	}

	

	
 }
