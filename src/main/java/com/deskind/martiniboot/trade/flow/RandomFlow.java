package com.deskind.martiniboot.trade.flow;

import java.util.List;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.analysys.Analyst;
import com.deskind.martiniboot.binary.entities.BuyParameters;
import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.binary.requests.BuyRequest;
import com.deskind.martiniboot.binary.requests.PassthroughBuyRequest;
import com.deskind.martiniboot.binary.responses.TransactionUpdate;
import com.deskind.martiniboot.controllers.MainController;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.Signal;
import com.deskind.martiniboot.trade.Stash;
import com.google.gson.Gson;


/**
 * Representing Random trade flow
 * @author deski
 *
 */
public class RandomFlow implements Flow{
	private LuckyGuy luckyGuy;
	private Ledger ledger;
	private Analyst analyst;
	private List<Stash> stashes;
	
	private float nextSignalStake;
	
	private boolean stopRequest;
	private boolean suspended;
	private String  buyContractsId = null;
	
	public RandomFlow(LuckyGuy luckyGuy, Ledger ledger, Analyst strategy, List<Stash> stashes) {
		super();
		this.luckyGuy = luckyGuy;
		this.ledger = ledger;
		this.analyst = strategy;
		this.stashes = stashes;
	}
	
	@Override
	public void makeLuckyBet(TransactionUpdate update) {
		
		if(isSuspended())
			return;
		
		MainController controller = MartiniBootApplication.getMainController();
		BuyParameters parameters = null;
		BuyRequest request = null;
		long duration = controller.getDuration();
		String symbol = controller.getSymbol();
		
		float stake = 0;
		
		if(update == null) {
			
			parameters = new BuyParameters(luckyGuy.getLot(),
											analyst.makeDescision(),
											duration,
											MartiniBootApplication.getMainController().getTimeUnits().toString(),
											symbol);
			
			request = new BuyRequest(parameters, new PassthroughBuyRequest(getBuyContractsId()));
			
		}else {
				Transaction transaction = update.getTransaction();
				float amount = transaction.getAmount();
				float balance = transaction.getBalance();
				
				if(amount > 0.1) {
					
					stake = winCase(amount, controller, balance);
					
					//check stop request
					if(stopRequest && getLedger().getOneRowWins() >= 2) {
						setSuspended(true);
						System.out.println("+++ Trading process suspended +++");
						controller.writeMessage("TRADING PROCESS STOPPED BY REQUEST", false, true);
					}
					
				}else {
					stake = looseCase(amount, controller, balance);
				}
				
				String type = analyst.makeDescision();
				
				parameters = new BuyParameters(stake,
												type,
												duration,
												MartiniBootApplication.getMainController().getTimeUnits().toString(),
												symbol);

				request = new BuyRequest(parameters, new PassthroughBuyRequest(getBuyContractsId()));
				
		}
		
		if(MartiniBootApplication.isRandomMode() && !isSuspended()) {
			
			MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(request));
			String message = String.format("Contract %.1f %s %s", parameters.getAmount(),
																	parameters.getSymbol(),
																	parameters.getContractType());
			controller.writeMessage(message, false, false);
		}else {
			if(nextSignalStake == 0)
				nextSignalStake = stake;
		}
		
	}
	
	@Override
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
		
		BuyRequest request = new BuyRequest(parameters, new PassthroughBuyRequest(getBuyContractsId()));
		
		MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(request));
		
		String message = String.format("Contract %.1f %s %s", parameters.getAmount(),
																parameters.getSymbol(),
																parameters.getContractType());
		
		MartiniBootApplication.getMainController().writeMessage(message, false, false);
	}
	
	private float winCase(float amount, MainController controller, float balance) {
		ledger.countersWinUpdate();
		
		ledger.updateWinProfit(amount, controller, balance);
		
		float stake = ledger.calculateNextStake(luckyGuy);
		
		
		return stake;
	}
	
	private float looseCase(float amount, MainController controller, float balance) {
		ledger.countersLooseUpdate();
		
		ledger.updateLooseProfit(amount, controller, balance);
		
		float stake = ledger.calculateNextStake(luckyGuy);
		
		return stake;
	}
	
	
	
	//GETTERS AND SETTERS
	
	public LuckyGuy getLuckyGuy() {
		return luckyGuy;
	}

	public void setLuckyGuy(LuckyGuy luckyGuy) {
		this.luckyGuy = luckyGuy;
	}

	@Override
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

	public List<Stash> getStashes() {
		return stashes;
	}

	public void setStashes(List<Stash> stashes) {
		this.stashes = stashes;
	}

	public boolean isStopRequest() {
		return stopRequest;
	}
	
	@Override
	public void setStopRequest(boolean stopRequest) {
		this.stopRequest = stopRequest;
	}

	@Override
	public void setTransaction(Transaction transaction) {
		
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	
	public void setBuyContractsId(String id) {
		buyContractsId = id;
	}
	
	public String getBuyContractsId() {
		return buyContractsId;
	}

}
