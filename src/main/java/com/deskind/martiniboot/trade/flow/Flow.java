package com.deskind.martiniboot.trade.flow;

import com.deskind.martiniboot.binary.responses.TransactionUpdate;
import com.deskind.martiniboot.trade.Ledger;

public interface Flow {
	void makeLuckyBet(TransactionUpdate update);

	Ledger getLedger();
}
