package com.deskind.martiniboot.binary.entities;

public class ProfitTableEntry {
	
	private long contract_id;
	private float sell_price;
	
	public ProfitTableEntry(long contract_id, float sell_price) {
		super();
		this.contract_id = contract_id;
		this.sell_price = sell_price;
	}
	
	public long getContract_id() {
		return contract_id;
	}
	public void setContract_id(long contract_id) {
		this.contract_id = contract_id;
	}
	public float getSell_price() {
		return sell_price;
	}
	public void setSell_price(float sell_price) {
		this.sell_price = sell_price;
	}
	
	
	
	
}
