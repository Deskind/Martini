package com.deskind.martiniboot.binary.responses;

import com.deskind.martiniboot.binary.entities.Transaction;

public class TransactionUpdate {
	private Transaction transaction;

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public String toString() {
		return "TransactionUpdate [transaction=" + transaction + "]";
	}
	
}
