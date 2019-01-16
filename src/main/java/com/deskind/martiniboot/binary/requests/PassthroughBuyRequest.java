package com.deskind.martiniboot.binary.requests;

public class PassthroughBuyRequest {
	private String buyId;

	public PassthroughBuyRequest(String buyId) {
		super();
		this.buyId = buyId;
	}

	public String getBuyContractsUniqueId() {
		return buyId;
	}

	public void setBuyContractsUniqueId(String buyContractsUniqueId) {
		this.buyId = buyContractsUniqueId;
	}
	
	
}
