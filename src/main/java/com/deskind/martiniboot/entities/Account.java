package com.deskind.martiniboot.entities;

import com.deskind.martiniboot.binary.entities.Authorize;

/**
 *Lucky guy account
 * @author deski
 *
 */
public class Account {
	private final String CURRENCY = "USD";
	
	private float balance;
	
	/*
	 * value - '1' means virtual
	 * value - '0' means real
	 */
	private int real;
	
	public Account(Authorize authorize) {
		this.balance = Float.parseFloat(authorize.getBalance());
		this.real = authorize.getIs_virtual();
	}

	public Account() {	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public int getReal() {
		return real;
	}

	public void setReal(int real) {
		this.real = real;
	}

	@Override
	public String toString() {
		return "Account [CURRENCY=" + CURRENCY + ", balance=" + balance + ", real=" + real + "]";
	}
	
	
}
