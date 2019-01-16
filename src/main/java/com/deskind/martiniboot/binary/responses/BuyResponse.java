package com.deskind.martiniboot.binary.responses;

import com.deskind.martiniboot.binary.entities.Buy;
import com.deskind.martiniboot.binary.requests.BuyRequest;
import com.deskind.martiniboot.binary.requests.PassthroughBuyRequest;

public class BuyResponse {
	
	private Buy buy;
	private  BuyRequest echo_req;
	private String msg_type;
	private Error error;
	
	private PassthroughBuyRequest passthrough;
	
	public BuyResponse(Buy buy, BuyRequest echo_req, String msg_type, Error error, PassthroughBuyRequest passthrough) {
		super();
		this.buy = buy;
		this.echo_req = echo_req;
		this.msg_type = msg_type;
		this.error = error;
		this.passthrough = passthrough;
	}

	public Buy getBuy() {
		return buy;
	}

	public void setBuy(Buy buy) {
		this.buy = buy;
	}

	public BuyRequest getEcho_req() {
		return echo_req;
	}

	public void setEcho_req(BuyRequest echo_req) {
		this.echo_req = echo_req;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public PassthroughBuyRequest getPassthrough() {
		return passthrough;
	}

	public void setPassthrough(PassthroughBuyRequest passthrough) {
		this.passthrough = passthrough;
	}
	
}
