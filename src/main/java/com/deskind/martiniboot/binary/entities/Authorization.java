package com.deskind.martiniboot.binary.entities;

/**
 * Wrapper class for Authorization
 * @author deski
 *
 */
public class Authorization {
	private Authorize authorize;

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

	@Override
	public String toString() {
		return "Authorization [authorize=" + authorize + "]";
	}
	
	
}
