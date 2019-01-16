package com.deskind.martiniboot.binary.requests;

import com.deskind.martiniboot.binary.entities.BuyParameters;

public class BuyRequest {
	private int buy = 1;
	private int price = 10000;
	
	private PassthroughBuyRequest passthrough;
	
	private BuyParameters parameters;

	public BuyRequest(BuyParameters parameters) {
		super();
		this.parameters = parameters;
	}

	public BuyRequest(BuyParameters parameters, PassthroughBuyRequest passthrough) {
		this.parameters = parameters;
		this.passthrough = passthrough;
	}

	public int getBuy() {
		return buy;
	}

	public void setBuy(int buy) {
		this.buy = buy;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public BuyParameters getParameters() {
		return parameters;
	}

	public void setParameters(BuyParameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "BuyRequest [buy=" + buy + ", price=" + price + ", parameters=" + parameters + "]";
	}
	
	
}
