package com.deskind.martiniboot.binary.entities;

public class ProfitTable {
	private ProfitTableEntry [] transactions;
	
	public ProfitTable(int count, ProfitTableEntry[] transactions) {
		super();
		this.transactions = transactions;
	}

	public ProfitTableEntry[] getTransactions() {
		return transactions;
	}

	public void setTransactions(ProfitTableEntry[] transactions) {
		this.transactions = transactions;
	}
	
	
}
