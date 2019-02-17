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
import com.deskind.martiniboot.analysys.RandomAnalyst;
import com.deskind.martiniboot.analysys.RotateAnalyst;
import com.deskind.martiniboot.analysys.TripleCallPutAnalyst;
import com.deskind.martiniboot.analysys.UpDownAnalyst;
import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.managers.UserInputManager;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.SymbolGenerator;
import com.deskind.martiniboot.trade.TimeUnits;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController implements Initializable{
	
	/*
	 * For log messages
	 */
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
	
	/*
	 * Available strategies
	 */
	private final String ROTATE_STRATEGY = "Rotate";
	private final String RANDOM_STRATEGY = "Random";
	private final String SINGLE_CALL_PUT_STRATEGY = "Single call/put";
	private final String DOUBLE_CALL_PUT_STRATEGY = "Double call/put";
	private final String TRIPLE_CALL_PUT_STRATEGY = "Triple call/put";
	
	/*
	 * Time unit for making stake ('t', 's', 'm')
	 */
	private TimeUnits timeUnits;
	
	
	/*
	 * Loss chart data
	 */
	private XYChart.Series<Number, Number> lossSeries;
	
	
	public MainController() {
		MartiniBootApplication.setMainController(this);
	}
	
	@FXML
	private TextField tokenInput, lotInput, expirationInput, stopLossInput, martiniFactorInput;
	
	@FXML
	private Label wins, looses, winRate, moneyAmount;
	
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
	
	@FXML
	private ChoiceBox<String> strategyChoice, timeUnitChoice;
	
	@FXML
	public void stop(ActionEvent event) {
		MartiniBootApplication.getSocketPlug().
					disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "!!!Bye!!!"));
	}
	
	@FXML
	public void randomCheckBoxClicked(ActionEvent event) {
		
		if(randomCheck.isSelected()) {
			expirationInput.setDisable(false);
			timeUnitChoice.setDisable(false);
			strategyChoice.setDisable(false);
		}else {
			expirationInput.setDisable(true);
			timeUnitChoice.setDisable(true);
			strategyChoice.setDisable(true);
		}
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
    	
    	//input validation
    	UserInputManager userInputManager = new UserInputManager(tokenInput,
											    			lotInput,
											    			expirationInput,
											    			stopLossInput,
											    			martiniFactorInput);
    	
    	//always returns true (need implementation)
    	boolean valid = userInputManager.validate();
        
    	//after validation passed
    	if(valid) {
    		Ledger ledger = new Ledger(getFactor(),
    									getStopLoss());
    		
    		Analyst analyst = getAnalyst(strategyChoice.getValue());
    		
    		LuckyGuy luckyGuy = new LuckyGuy(tokenInput.getText(),
											Float.parseFloat(lotInput.getText()));
    		
    		RandomFlow flow = new RandomFlow(luckyGuy, ledger, analyst, this);
    		
    		MartiniBootApplication.setLuckyGuy(luckyGuy);
    		MartiniBootApplication.setFlow(flow);
    		
    		//set time units
    		setTimeUnits(timeUnitChoice.getValue());
    		
    		//get socket and authorize
    		SocketPlug plug = MartiniBootApplication.getSocketPlug();
    		plug.authorize(luckyGuy.getToken()).subscribe();
    		
    		//random mode
    		if(randomCheck.isSelected()) { 
    			
    			if(analyst != null)
    				writeMessage("STRATEGY = >" + analyst.getName(), false, true);
    			else
    				writeMessage("Impossible to set strategy", false, true);
    			
    			MartiniBootApplication.setRandomMode(true);
    			
    			//null argument because of first stake
    			flow.makeLuckyBet(null);
    		}
    		
    	}
    }
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//set up choices for strategies
		strategyChoice.getItems().addAll(ROTATE_STRATEGY,
											DOUBLE_CALL_PUT_STRATEGY,
											TRIPLE_CALL_PUT_STRATEGY,
											SINGLE_CALL_PUT_STRATEGY,
											RANDOM_STRATEGY);
		
		//first value is default
		strategyChoice.getSelectionModel().selectFirst();
		
		//set up time units
		for(TimeUnits unit : TimeUnits.values()) {
			timeUnitChoice.getItems().add(unit.toString());
		}
		
		//first value
		timeUnitChoice.getSelectionModel().selectFirst();
		
		
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
    
	private Analyst getAnalyst(String name) {
    	
    	if(name.equals(RANDOM_STRATEGY))
    		return new RandomAnalyst();
    	else if(name.equals(SINGLE_CALL_PUT_STRATEGY))
    		return new UpDownAnalyst();
    	else if(name.equals(DOUBLE_CALL_PUT_STRATEGY))
    		return new DoubleCallPutAnalyst();
		else if(name.equals(ROTATE_STRATEGY))
			return new RotateAnalyst(new RandomAnalyst(), new UpDownAnalyst(), new DoubleCallPutAnalyst());
    	else if(name.equals(TRIPLE_CALL_PUT_STRATEGY))
    		return new TripleCallPutAnalyst();
    	else 
    		return null;
    	
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
                Text messageText = new Text(message + "\n");
                
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
	
	public void updateView(XYChart.Data<Number, Number> point, Float balance, Float profit) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(point != null) {
					lossSeries.getData().add(point);
				}
				
				if(balance != null) {
					balanceLabel.setText(String.valueOf(balance));
				}
				
				if(profit != null) {
					float f = Ledger.round(profit, 1);
					profitLabel.setText(String.valueOf(f));
				}
			}
		});
	}
	
	public void updateStatisticData(String wins, String looses, String winRate, String moneyAmount) {
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				//wins
				MainController.this.wins.setText("W: " + wins);
				
				//looses
				MainController.this.looses.setText("L: " + looses);
				
				//winRate
				MainController.this.winRate.setText("WR: " + winRate + "%");
				
				//moneyAmount
				MainController.this.moneyAmount.setText("M: " + moneyAmount + "$");
				
			}
		});
		
	}

	//SETTERS
	public void setTimeUnits(String value) {
		for (TimeUnits unit : TimeUnits.values()) {
			if(unit.toString().equals(value))
				timeUnits = unit;
		}
	}
	
	//GETTERS
	public long getDuration() {
		return Integer.parseInt(expirationInput.getText());
	}
	
	public String getSymbol() {
		return SymbolGenerator.getSymbol(); 
	}
	
	public TimeUnits getTimeUnits() {
		return timeUnits;
	}
	
	private float getStopLoss() {
		return Float.parseFloat(stopLossInput.getText());
	}

	public float getFactor() {
		return Float.parseFloat(martiniFactorInput.getText());
	}
}
