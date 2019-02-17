package com.deskind.martiniboot.binary.responses;

import com.deskind.martiniboot.binary.entities.ProfitTable;

public class ProfitTableResponse {
	private String msg_type;
	private ProfitTable profit_table;
	
	
	public ProfitTableResponse(String msg_type, ProfitTable profit_table) {
		super();
		this.msg_type = msg_type;
		this.profit_table = profit_table;
	}


	public String getMsg_type() {
		return msg_type;
	}


	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}


	public ProfitTable getProfit_table() {
		return profit_table;
	}


	public void setProfit_table(ProfitTable profit_table) {
		this.profit_table = profit_table;
	}
	
	
	
	
}
