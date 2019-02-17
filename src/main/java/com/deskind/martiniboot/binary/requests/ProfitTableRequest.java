package com.deskind.martiniboot.binary.requests;

/**
 * Profit table request
 * 
 * {
  "profit_table": 1,
  "description": 1,
  "limit": 15,
  "sort": "DESC"
  }

 * @author deski
 *
 */
public class ProfitTableRequest {
	private byte profit_table = 1;
	private byte description = 1;
	private byte limit = 15;
	private String sort = "DESC";
	
	
	
	
	
	
	public byte getProfit_table() {
		return profit_table;
	}
	public void setProfit_table(byte profit_table) {
		this.profit_table = profit_table;
	}
	public byte getDescription() {
		return description;
	}
	public void setDescription(byte description) {
		this.description = description;
	}
	public byte getLimit() {
		return limit;
	}
	public void setLimit(byte limit) {
		this.limit = limit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	
}
