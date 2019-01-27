package com.deskind.martiniboot.binary.entities;

/**
 * Wrapper class for Authorization
 * @author deski
 *
 */
public class Authorization {
	private Authorize authorize;
	
	private Error error;

	public Authorization(Authorize authorize) {
		super();
		this.authorize = authorize;
	}

	public Authorize getAuthorize() {
		return authorize;
	}

	public void setAuthorize(Authorize authorize) {
		this.authorize = authorize;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	
}
