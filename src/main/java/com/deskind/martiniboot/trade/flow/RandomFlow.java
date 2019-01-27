package com.deskind.martiniboot.trade.flow;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.analysys.Analyst;
import com.deskind.martiniboot.binary.entities.BuyParameters;
import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.binary.requests.BuyRequest;
import com.deskind.martiniboot.controllers.MainController;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.Signal;
import com.google.gson.Gson;

import javafx.scene.chart.XYChart;


/**
 * Representing Random trade flow
 * @author deski
 *
 */
public class RandomFlow{
	private LuckyGuy luckyGuy;
	private Ledger ledger;
	private Analyst analyst;
	private MainController controller;
	
	private float nextSignalStake;
	
	private boolean stopRequest;
	private boolean suspended;
	private long  currentContractId;
	
	public RandomFlow(LuckyGuy luckyGuy, Ledger ledger, Analyst strategy, MainController controller) {
		super();
		this.luckyGuy = luckyGuy;
		this.ledger = ledger;
		this.analyst = strategy;
		this.controller = controller;
	}
	
	
	public void makeLuckyBet(Transaction transaction) {
		
		//trading process suspended by user request
		if(isSuspended())
			return;
		
		BuyRequest buyRequest = null;
		BuyParameters buyRequestParameters = null;
		
		float stake = 0;
		
		//first stake
		if(transaction == null) {
			
			buyRequestParameters = new BuyParameters(luckyGuy.getLot(),
											analyst.makeDescision(),
											controller.getDuration(),
											controller.getTimeUnits().toString(),
											controller.getSymbol());
			
			buyRequest = new BuyRequest(buyRequestParameters);
			
		//every next stake
		}else {
				float amount = transaction.getAmount();
				float balance = transaction.getBalance();
				
				//write result message
				if(amount > 0) {
					float f = ledger.round((amount - ledger.getCurrentStake()), 1);
					controller.writeMessage("Result: " + f, false, false);
				}else {
					float f = ledger.round(ledger.getCurrentStake(), 1);
					controller.writeMessage("Result: -" + f, true, false);
				}
				
				//update balance label
				controller.updateBalance(balance);
				
				ledger.updateCounters(amount);
				
				//win case
				if(amount > 0) {
					
					//calculate profit
					float stakeProfit = amount - ledger.getCurrentStake();
					ledger.setProfit(ledger.getProfit() + stakeProfit);
					
					//check stop request
					if(stopRequest && getLedger().getWins() >= 2) {
						suspended = true;
						controller.writeMessage("TRADING PROCESS STOPPED BY REQUEST", false, true);
					}
					
					if(ledger.getLoss() != 0) {
						ledger.setLoss(ledger.getLoss() - stakeProfit);
					}
					
					if(ledger.getWins() >= 2) {
						ledger.resetCounters();
						ledger.resetLoss();
						stake = luckyGuy.getLot();
					}else if(ledger.getWins() == 1 && ledger.getLooses() != 0) {
						stake = ledger.getCurrentStake();
					}else if(ledger.getWins() == 1 && ledger.getLooses() == 0) {
						stake = luckyGuy.getLot();
					}else {
						stake = ledger.getLoss() / 2 / ledger.getMartiniFactor();
					}
					
				//loose case	
				}else {
					//calculate profit 
					ledger.setProfit(ledger.getProfit() - ledger.getCurrentStake());
					
					//reset wins
					ledger.setWins(0);
					
					//update common loss
					ledger.setLoss(ledger.getLoss() + ledger.getCurrentStake());
					
					//calculate new stake
					stake = ledger.getLoss() / 2 / ledger.getMartiniFactor();
				}
				
				buyRequestParameters = new BuyParameters(ledger.round(stake, 1),
												analyst.makeDescision(),
												controller.getDuration(),
												controller.getTimeUnits().toString(),
												controller.getSymbol());

				buyRequest = new BuyRequest(buyRequestParameters);
				
		}
		
		//update profit label
		controller.updateProfit(ledger.getProfit());
		
		//add point to chart
		controller.addLossPoint(new XYChart.Data<Number, Number>(ledger.getAllContractsCounter(), ledger.getLoss()));
		
		if(MartiniBootApplication.isRandomMode() && !isSuspended()) {
			
			MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(buyRequest));
			
			//all stakes counter
			ledger.setAllContractsCounter(ledger.getAllContractsCounter() + 1);
			
			String message = String.format("Contract %.1f %s %s", buyRequestParameters.getAmount(),
																	buyRequestParameters.getSymbol(),
																	buyRequestParameters.getContractType());
			controller.writeMessage(message, false, false);
		}else {
			if(nextSignalStake == 0)
				nextSignalStake = stake;
		}
		
	}
	
	public void makeSignalBet(Signal signal) {
		
		if(isSuspended())
			return;
		
		BuyParameters parameters = null;
		int counter = this.getLedger().getAllContractsCounter();
		
		//first stake
		if(counter == 0) {
			parameters = new BuyParameters(luckyGuy.getLot(),
											signal.getType(),
											signal.getDuration(),
											signal.getDurationUnit(),
											signal.getSymbol());
		}else {
			parameters = new BuyParameters(nextSignalStake,
											signal.getType(),
											signal.getDuration(),
											signal.getDurationUnit(),
											signal.getSymbol());
		}
		
		//if stake in process don't process other signals
		if(nextSignalStake == 0 && counter > 1)
			return;
		
		//reset next signal stake
		nextSignalStake = 0;
		
		BuyRequest request = new BuyRequest(parameters);
		
		MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(request));
		
		String message = String.format("Contract %.1f %s %s", parameters.getAmount(),
																parameters.getSymbol(),
																parameters.getContractType());
		
		MartiniBootApplication.getMainController().writeMessage(message, false, false);
	}
	
	//GETTERS AND SETTERS
	
	public LuckyGuy getLuckyGuy() {
		return luckyGuy;
	}

	public void setLuckyGuy(LuckyGuy luckyGuy) {
		this.luckyGuy = luckyGuy;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	public Analyst getStrategy() {
		return analyst;
	}

	public void setStrategy(Analyst strategy) {
		this.analyst = strategy;
	}

	public void setStopRequest(boolean stopRequest) {
		this.stopRequest = stopRequest;
	}

	public void setTransaction(Transaction transaction) {
		
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public long getCurrentContractId() {
		return currentContractId;
	}

	public void setCurrentContractId(long currentContractId) {
		this.currentContractId = currentContractId;
	}

	
}
