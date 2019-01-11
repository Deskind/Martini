package com.deskind.martiniboot.trade;

public class Signal {
	
	private String symbol;
	private String type;
	private int duration;
	private String durationUnit;
	
	public Signal(String symbol, String type, int duration, String durationUnit) {
		super();
		this.symbol = symbol;
		this.type = type;
		this.duration = duration;
		this.durationUnit = durationUnit;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	@Override
	public String toString() {
		return "Signal [symbol=" + symbol + ", type=" + type + ", duration=" + duration + ", durationUnit="
				+ durationUnit + "]";
	}

	
}
