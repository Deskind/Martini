package com.deskind.martiniboot.binary.entities;

public class BuyParameters {
	private float amount;
	private String basis = "stake";
	private String contract_type;
	private String currency = "USD";
	private long duration;
	private String duration_unit = "s";
	private String symbol;
	
	public BuyParameters(float amount, String contractType, long duration, String symbol) {
		super();
		this.amount = amount;
		this.contract_type = contractType;
		this.duration = duration;
		this.symbol = symbol;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String getContractType() {
		return contract_type;
	}

	public void setContractType(String contractType) {
		this.contract_type = contractType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDuration_unit() {
		return duration_unit;
	}

	public void setDuration_unit(String duration_unit) {
		this.duration_unit = duration_unit;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "BuyParameters [amount=" + amount + ", basis=" + basis + ", contract_type=" + contract_type
				+ ", currency=" + currency + ", duration=" + duration + ", duration_unit=" + duration_unit + ", symbol="
				+ symbol + "]";
	}
	
	
}

