package com.deskind.martiniboot.managers;

import javafx.scene.control.TextField;

/**
 * Manager for validation application input fields
 * @author deski
 *
 */
public class UserInputManager {
	private TextField token, lot, expiration, stopLoss, waitTime, martiniFactor;

	public UserInputManager(TextField token, TextField lot, TextField expiration, TextField stopLoss,
			TextField waitTime, TextField martiniFactor) {
		super();
		this.token = token;
		this.lot = lot;
		this.expiration = expiration;
		this.stopLoss = stopLoss;
		this.waitTime = waitTime;
		this.martiniFactor = martiniFactor;
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	
}
