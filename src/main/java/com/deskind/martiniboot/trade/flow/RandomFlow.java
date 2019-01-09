package com.deskind.martiniboot.trade.flow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.analysys.Analyst;
import com.deskind.martiniboot.binary.entities.BuyParameters;
import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.binary.requests.BuyRequest;
import com.deskind.martiniboot.binary.responses.TransactionUpdate;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.fxcontrollers.MainController;
import com.deskind.martiniboot.trade.Ledger;
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
	
	public RandomFlow(LuckyGuy luckyGuy, Ledger ledger, Analyst strategy, List<Stash> stashes) {
		super();
		this.luckyGuy = luckyGuy;
		this.ledger = ledger;
		this.analyst = strategy;
		this.stashes = stashes;
	}
	
	@Override
	public void makeLuckyBet(TransactionUpdate update) {
		
		MainController controller = MartiniBootApplication.getMainController();
		BuyParameters parameters = null;
		BuyRequest request = null;
		long duration = controller.getDuration();
		String symbol = controller.getSymbol();
		
		if(update == null) {
			
			parameters = new BuyParameters(luckyGuy.getLot(),
											analyst.makeDescision(),
											duration,
											symbol);
			
			request = new BuyRequest(parameters);
			
		}else {
				Transaction transaction = update.getTransaction();
				float amount = transaction.getAmount();
				float balance = transaction.getBalance();
				float stake = 0;
				
				if(amount > 0.1) {
					stake = winCase(amount, controller, balance);
				}else {
					stake = looseCase(amount, controller, balance);
				}
				
				String type = analyst.makeDescision();
				
				parameters = new BuyParameters(stake,
												type,
												duration,
												symbol);

				request = new BuyRequest(parameters);
				
		}
		
		MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(request));
		
		String message = String.format("Contract %.1f %s %s\n", parameters.getAmount(),
															parameters.getContractType(),
															parameters.getSymbol());
		
		controller.writeMessage(message, false, false);
		
	}
	
	private float winCase(float amount, MainController controller, float balance) {
		float stake = 0;
		
		ledger.countersWinUpdate();
		
		ledger.updateWinProfit(amount, controller, balance);
		
		stake = ledger.calculateNextStake(luckyGuy);
		
		
		return stake;
	}
	
	private float looseCase(float amount, MainController controller, float balance) {
		float stake = 0;
		
		ledger.countersLooseUpdate();
		
		ledger.updateLooseProfit(amount, controller, balance);
		
		stake = ledger.calculateNextStake(luckyGuy);
		
		return stake;
	}
	
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

}
