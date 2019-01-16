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
import com.deskind.martiniboot.analysys.UpDownAnalyst;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.connection.SocketPlug;
import com.deskind.martiniboot.entities.LuckyGuy;
import com.deskind.martiniboot.managers.UserInputManager;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.SymbolGenerator;
import com.deskind.martiniboot.trade.TimeUnits;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController implements Initializable{
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
	private final String RANDOM_STRATEGY = "Random";
	private final String SINGLE_CALL_PUT_STRATEGY = "Single call/put";
	private final String DOUBLE_CALL_PUT_STRATEGY = "Double call/put";
	
	private TimeUnits timeUnits;
	
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
	
	@FXML
	private ChoiceBox<String> strategyChoice, timeUnitChoice;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//set up choices for strategies
		strategyChoice.getItems().addAll(DOUBLE_CALL_PUT_STRATEGY,
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
    		
    		float factor = Float.parseFloat(martiniFactorInput.getText());
    		float stopLoss = Float.parseFloat(stopLossInput.getText());
    		
    		Ledger ledger = new Ledger(factor, stopLoss);
    		
    		Analyst analyst = getAnalyst(strategyChoice.getValue());
    		
    		LuckyGuy luckyGuy = new LuckyGuy(tokenInput.getText(),
											Float.parseFloat(lotInput.getText()));
    		
    		RandomFlow flow = new RandomFlow(luckyGuy, ledger, analyst, null);
    		
    		//set time units
    		setTimeUnits(timeUnitChoice.getValue());
    		
    		MartiniBootApplication.setLuckyGuy(luckyGuy);
    		MartiniBootApplication.setFlow(flow);
    		
    		plug.authorize(luckyGuy.getToken()).subscribe();
    		
    		//random mode
    		if(randomCheck.isSelected()) { 
    			
    			if(analyst != null)
    				writeMessage("You choose = >" + analyst.getName(), false, true);
    			else
    				writeMessage("Impossible to set strategy", false, true);
    			
    			MartiniBootApplication.setRandomMode(true);
    			flow.makeLuckyBet(null);
    		}
    		
    	}
    }
    
    private Analyst getAnalyst(String name) {
    	
    	if(name.equals(RANDOM_STRATEGY))
    		return new RandomAnalyst();
    	else if(name.equals(SINGLE_CALL_PUT_STRATEGY))
    		return new UpDownAnalyst();
    	else if(name.equals(DOUBLE_CALL_PUT_STRATEGY))
    		return new DoubleCallPutAnalyst();
    	else 
    		return null;
    	
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
	
	public TimeUnits getTimeUnits() {
		return timeUnits;
	}

	public void setTimeUnits(String value) {
		for (TimeUnits unit : TimeUnits.values()) {
			if(unit.toString().equals(value))
				timeUnits = unit;
		}
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
