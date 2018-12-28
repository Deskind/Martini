package com.deskind.martiniboot.fxcontrollers;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.entities.Account;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.managers.UserInputManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

public class MainController {
	
	public MainController() {
		MartiniBootApplication.setMainController(this);
	}
	
	private UserInputManager userInputManager;
	
	@FXML
	private TextField tokenInput, lotInput, expirationInput, stopLossInput, waitTimeInput, martiniFactorInput;
	
	@FXML 
	private TextFlow tradeMessages, logMessages;
	
	@FXML
	private LineChart lossChart;
	
	@FXML
	private Label virRealLabel, balanceLabel;
	
    @FXML
    public void start(ActionEvent event) {
    	
    	userInputManager = new UserInputManager(tokenInput,
											    			lotInput,
											    			expirationInput,
											    			stopLossInput,
											    			waitTimeInput,
											    			martiniFactorInput);
    	
    	//always returns true (need implementation)
    	boolean valid = userInputManager.validate();
        
    	if(valid) {
    		LuckyGuy luckyGuy = new LuckyGuy(tokenInput.getText(),
											Float.parseFloat(lotInput.getText()));
    		
    		MartiniBootApplication.setLuckyGuy(luckyGuy);
    		
    		MartiniBootApplication.getSocketPlug().authorize(luckyGuy.getToken());
    		
    		
    		
    		
    	}
    }
    
    /**
     * Account real or virtual
     * @param label
     */
	public void setBalance(Authorize authorize) {
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
            	String label = "REAL";
            	
            	if(authorize.getIs_virtual() == 1)
            		label = "VIRTUAL";
            	
            	virRealLabel.setText(label);
            	
            	//setting balance
            	balanceLabel.setText(authorize.getBalance());
            }
        });
	}
    
    
    
}
