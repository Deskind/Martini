package com.deskind.martiniboot.binary.entities;

public class Transaction {
	private float balance;
	private String action;
	private float amount;
	private String id;
	private String symbol;
	
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	@Override
	public String toString() {
		return "Transaction [balance=" + balance + ", action=" + action + ", amount=" + amount + ", id=" + id
				+ ", symbol=" + symbol + "]";
	}
	
	
	
	
}
