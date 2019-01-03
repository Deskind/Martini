package com.deskind.martiniboot.fxcontrollers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.analysys.DoubleCallPutAnalyst;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.entities.Account;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.managers.UserInputManager;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.Stash;
import com.deskind.martiniboot.trade.SymbolGenerator;
import com.deskind.martiniboot.trade.flow.Flow;
import com.deskind.martiniboot.trade.flow.RandomFlow;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
	
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
	private CheckBox randomCheck;
	
	@FXML
	private Label virRealLabel, balanceLabel;
	
    @FXML
    public void start(ActionEvent event) throws InterruptedException {
    	
    	Flow flow = null;
    	
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
    		
    		luckyGuy.setAccount(new Account());
    		
    		MartiniBootApplication.setLuckyGuy(luckyGuy);
    		
    		//random mode
    		if(randomCheck.isSelected()) { 
    			flow = new RandomFlow(luckyGuy,
    					new Ledger(),
    					new DoubleCallPutAnalyst(),
    					new ArrayList<Stash>());
    			
    			MartiniBootApplication.setFlow(flow);
    		}
    		
    		String authorize = String.format("{\"authorize\": \"%s\"}", luckyGuy.getToken());
    		
    		Thread.sleep(1000);
    		
    		String subscribe = "{\"transaction\": 1, \"subscribe\": 1}";
    		
    		Thread.sleep(1000);
    		
    		MartiniBootApplication.getSocketPlug().sendMessage(authorize);
    		System.out.println("...Authorized");
    		MartiniBootApplication.getSocketPlug().sendMessage(subscribe);
    		System.out.println("...Subscribed");
    		
    		flow.makeLuckyBet(null);
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
	
	/**
	 * Write message to logs 'TextFlow'
	 * @param message
	 */
	public void writeMessage(String message, boolean makeRed, boolean log){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //create and decorate date element
            	Text date = new Text(dateFormatter.format(new Date()));
                date.getStyleClass().add("date-text");
                
                //create and decorate message itself
                Text messageText = new Text(message);
                
                //red color if error
                if(makeRed){
                    messageText.getStyleClass().add("error-text");
                }else{
                    messageText.getStyleClass().add("regular-text");
                }
                
                //write to log messages or to trader messages
                if(log) {
                	logMessages.getChildren().addAll(date, messageText);
                }else{
                	tradeMessages.getChildren().
                	addAll(date, messageText);
                }
            }
        });
    
    
	}

	public long getDuration() {
		return Integer.parseInt(expirationInput.getText());
	}
	
	public static String getSymbol() {
		return SymbolGenerator.getSymbol(); 
	}
}
