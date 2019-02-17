package com.deskind.martiniboot.runnables;

import com.deskind.martiniboot.binary.entities.ProfitTableEntry;
import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.trade.flow.RandomFlow;

public class MissedSellEventCheck implements Runnable {
	
	private RandomFlow flow;
	private ProfitTableEntry[] entries;
	
	public MissedSellEventCheck(RandomFlow flow, ProfitTableEntry[] entries) {
		this.flow = flow;
		this.entries = entries;
	}

	@Override
	public void run() {
		//contractId in flow
		String currentId = String.valueOf(flow.getCurrentContractId());
		
		for(int i = 0; i < entries.length; i++) {
			//contractId in entry
			String id = String.valueOf(entries[i].getContract_id());
			
			
			if(currentId.equals(id)) {
				System.out.println("+++Missed contract found+++");
				
				Transaction transaction = new Transaction();
				transaction.setAmount(Float.valueOf(entries[i].getSell_price()));
				
				flow.makeLuckyBet(transaction);
			}
		}
	}

}
