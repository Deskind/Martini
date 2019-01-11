package com.deskind.martiniboot.trade.flow;

import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.binary.responses.TransactionUpdate;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.Signal;

public interface Flow {
	void makeLuckyBet(TransactionUpdate update);
	void makeSignalBet(Signal signal);
	Ledger getLedger();
	void setTransaction(Transaction transaction);
	
	void setStopRequest(boolean b);
	boolean isStopRequest();
}
