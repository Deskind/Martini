package com.deskind.martiniboot.trade.flow;

import java.util.ArrayList;
import java.util.List;

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
		if(update == null) {
			BuyParameters parameters = new BuyParameters(luckyGuy.getLot(),
					analyst.makeDescision(),
					MartiniBootApplication.getMainController().getDuration(),
					MainController.getSymbol());
			BuyRequest buyRequest = new BuyRequest(parameters);
			
			MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(buyRequest));
		}
		
	}
	

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

	public List<Stash> getStashes() {
		return stashes;
	}

	public void setStashes(List<Stash> stashes) {
		this.stashes = stashes;
	}

	@Override
	public void take(TransactionUpdate update) {
		MainController controller = MartiniBootApplication.getMainController();
		Transaction transaction = update.getTransaction();
		String action = transaction.getAction();
		
		
		if(action.equals("sell")) {
			float amount = update.getTransaction().getAmount();
			
			if(amount > 0.1) {
				winCase();
			}else {
				looseCase();
			}
				
		}
		
	}

	private void looseCase() {
		// TODO Auto-generated method stub
		
	}

	private void winCase() {
		// TODO Auto-generated method stub
		
	}

}
