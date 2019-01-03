package com.deskind.martiniboot.trade.flow;

import com.deskind.martiniboot.binary.responses.TransactionUpdate;

public interface Flow {
	void makeLuckyBet(TransactionUpdate update);

	void take(TransactionUpdate update); 
}
