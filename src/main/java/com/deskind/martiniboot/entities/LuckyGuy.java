package com.deskind.martiniboot.entities;

public class LuckyGuy {
	
	private Account account;
	private String token;
	private float lot;
	
	public LuckyGuy(String token, float lot) {
		this.token = token;
		this.lot = lot;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
