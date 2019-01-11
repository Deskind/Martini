package com.deskind.martiniboot.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.analysys.Analyst;
import com.deskind.martiniboot.analysys.DoubleCallPutAnalyst;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.managers.UserInputManager;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.SymbolGenerator;
import com.deskind.martiniboot.trade.flow.Flow;
import com.deskind.martiniboot.trade.flow.RandomFlow;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController implements Initializable{
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
	
	private XYChart.Series<Number, Number> lossSeries;
	
	public MainController() {
		MartiniBootApplication.setMainController(this);
	}
	
	@FXML
	private TextField tokenInput, lotInput, expirationInput, stopLossInput, waitTimeInput, martiniFactorInput;
	
	@FXML 
	private TextFlow tradeMessages, logMessages;
	
	@FXML
	private LineChart<Number, Number> lossChart;
	
	@FXML
	private CheckBox randomCheck;
	
	@FXML
	private Label virRealLabel, balanceLabel, profitLabel;
	
	@FXML
	private ScrollPane textMessagesScroll;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lossSeries = new XYChart.Series<Number, Number>();
		lossChart.getData().add(lossSeries);
		
		//this block needed to prevent text bluring in scroll layout
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tradeMessages.setCache(false);
                ObservableList<Node> elements = textMessagesScroll.getChildrenUnmodifiable();
                for(Node n : elements){
                    n.setCache(false);
                }
                
            }
        });
	}
	
	@FXML
	public void stop(ActionEvent event) {
		MartiniBootApplication.getSocketPlug().
					disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "!!!Bye!!!"));
	}
	
	@FXML
	public void closeSocket(ActionEvent event) {
		MartiniBootApplication.getSocketPlug().
		disconnect(new CloseReason(CloseCodes.CLOSED_ABNORMALLY, "!!!Closed Abnormally!!!"));
	}
	
	@FXML
	public void stopRequest(ActionEvent event) {
		MartiniBootApplication.getFlow().setStopRequest(true);
	}
	
    @FXML
    public void start(ActionEvent event) throws InterruptedException {
    	
    	UserInputManager userInputManager = new UserInputManager(tokenInput,
											    			lotInput,
											    			expirationInput,
											    			stopLossInput,
											    			waitTimeInput,
											    			martiniFactorInput);
    	
    	//always returns true (need implementation)
    	boolean valid = userInputManager.validate();
        
    	if(valid) {
    		SocketPlug plug = MartiniBootApplication.getSocketPlug();
    		
    		Ledger ledger = new Ledger(Float.parseFloat(martiniFactorInput.getText()));
    		
    		Analyst analyst = new DoubleCallPutAnalyst();
    		
    		LuckyGuy luckyGuy = new LuckyGuy(tokenInput.getText(),
											Float.parseFloat(lotInput.getText()));
    		
    		Flow flow = new RandomFlow(luckyGuy, ledger, analyst, null);
    		
    		MartiniBootApplication.setLuckyGuy(luckyGuy);
    		MartiniBootApplication.setFlow(flow);
    		
    		plug.authorize(luckyGuy.getToken()).subscribe();
    		
    		//random mode
    		if(randomCheck.isSelected()) { 
    			MartiniBootApplication.setRandomMode(true);
    			flow.makeLuckyBet(null);
    		}
    		
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
	
	public void addLossPoint(XYChart.Data<Number, Number> point){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lossSeries.getData().add(point);
            }
        });  
        
    }

	public long getDuration() {
		return Integer.parseInt(expirationInput.getText());
	}
	
	public String getSymbol() {
		return SymbolGenerator.getSymbol(); 
	}

	public void updateProfit(float newValue) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				float f = Ledger.round(newValue, 1);
				
				profitLabel.setText(String.valueOf(f));
			}
		});
	}

	public void updateBalance(float balance) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				balanceLabel.setText(String.valueOf(balance));
			}
			
		});
	}

}