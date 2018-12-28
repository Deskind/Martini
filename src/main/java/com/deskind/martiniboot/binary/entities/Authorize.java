package com.deskind.martiniboot.binary.entities;

public class Authorize {
	private String balance;
	private int is_virtual;
	
	public Authorize(String balance, int is_virtual) {
		super();
		this.balance = balance;
		this.is_virtual = is_virtual;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public int getIs_virtual() {
		return is_virtual;
	}

	public void setIs_virtual(int is_virtual) {
		this.is_virtual = is_virtual;
	}

	@Override
	public String toString() {
		return "Authorize [balance=" + balance + ", is_virtual=" + is_virtual + "]";
	}
	

}
