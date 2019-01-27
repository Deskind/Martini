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
	
	private int oneRowWins;
	private int oneRowLooses;
	private int allContractsCounter;
	private int wins;
	private int looses;
	
	private List<Character> results = new ArrayList<Character>();
	
	private String currentContractId;
	
	public Ledger(float martiniFactor, float stopLoss) {
		this.martiniFactor = martiniFactor;
		this.stopLoss = stopLoss;
	}

	//INSTANCE METHODS
	
	public void updateCounters(float amount) {
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
	
	public void updateWinProfit(float amount, MainController controller, float balance) {
		int size = results.size();
		
		char last = '\u0000';
		char beforeLast = '\u0000';
		
		if(size >= 2) {
			last = results.get(size - 1);
			beforeLast = results.get(size - 2);
		}
		
		float pureProfit = amount - currentStake;
		
		profit += pureProfit;
		
		if(oneRowWins == 2) {
			loss = 0;
		}else if(oneRowWins == 1 && allContractsCounter == 1){
			loss = 0;
		}else if(beforeLast == '-' && last == '+' && loss != 0){
			loss -= pureProfit;
		}else if(beforeLast == '-' && last == '+' && loss == 0){
			loss = 0;
		}
		
		
		controller.updateProfit(profit);
		controller.updateBalance(balance);
		int pointNumber = allContractsCounter;
		controller.addLossPoint(new XYChart.Data<>(pointNumber, loss));
		controller.writeMessage("Result " + round(pureProfit, 1), false, false);
	}

	public void updateLooseProfit(float amount, MainController controller, float balance) {
		
		loss += currentStake;
		
		profit -= currentStake;
		
		controller.updateProfit(profit);
		controller.updateBalance(balance);
		int pointNumber = allContractsCounter;
		controller.addLossPoint(new XYChart.Data<>(pointNumber, loss));
		controller.writeMessage("Result " + round(currentStake, 1), true, false);
	}
	
	/*
	 * 1. Checking stop loss
	 * 2. Process characters in results
	 * 3. Return stake size
	 */
	public float calculateNextStake(LuckyGuy luckyGuy) {
		
		if(stopLossCheck()) {
			//reset counters
			loss = 0;
			oneRowWins = 0;
			oneRowLooses = 0;
			
			MartiniBootApplication.getMainController().writeMessage("STOP LOSS", true, false);
			
			MartiniBootApplication.getMainController().addLossPoint(new XYChart.Data<Number, Number>(allContractsCounter, 0));
			
			return luckyGuy.getLot();
		}
		
		float stake = 0f;
		
		int size = results.size();
		
		char last = '\u0000';
		char beforeLast = '\u0000';
		
		if(size >= 2) {
			last = results.get(size - 1);
			beforeLast = results.get(size - 2);
		}
		
		if(oneRowWins >= 2 || (oneRowWins == 1 && allContractsCounter == 1)) {
			stake = luckyGuy.getLot();
		}else if(beforeLast == '-' && last == '+') {
			stake = currentStake;
		}else{
			stake = loss / 2 / martiniFactor;
		}
		
		return round(stake, 1);
	}
	
	private boolean stopLossCheck() {
		
		if(loss > stopLoss)
			return true;
		
		return false;
	}

	/**
	 * rounding stake value
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
	public int getAllContractsCounter() {
		return allContractsCounter;
	}
	public void setAllContractsCounter(int allContractsCounter) {
		this.allContractsCounter = allContractsCounter;
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
				+ stopLoss + ", martiniFactor=" + martiniFactor + ", oneRowWins=" + oneRowWins + ", oneRowLooses="
				+ oneRowLooses + ", allContractsCounter=" + allContractsCounter + "]";
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
