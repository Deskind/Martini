package com.deskind.martiniboot.connection;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.binary.entities.Authorization;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.binary.entities.Buy;
import com.deskind.martiniboot.binary.entities.MessageType;
import com.deskind.martiniboot.binary.entities.Transaction;
import com.deskind.martiniboot.binary.responses.BuyResponse;
import com.deskind.martiniboot.binary.responses.TransactionUpdate;
import com.deskind.martiniboot.controllers.MainController;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.flow.Flow;
import com.deskind.martiniboot.trade.flow.RandomFlow;
import com.google.gson.Gson;

@ClientEndpoint
public class ApplicationEndpoint {
	
	private MainController controller;
	
	private RandomFlow flow;
	
	private String transactionStreamId = null;
	
	public ApplicationEndpoint() {
		 controller = MartiniBootApplication.getMainController();
	}

	@OnOpen
	public void onOpen(Session session) throws IOException {
		controller.writeMessage("CONNECTED\n", false, true);
	}
	
	@OnMessage
	public void onMessage(String message) {
		
		//in case of reconnection flow reference will be null
		if(flow == null) {
			setFlow(MartiniBootApplication.getFlow());
		}
		
		//print error messages
		if(message.contains("error"))
			System.out.println("...Error message is :" + message);
		
		Gson gson = new Gson();
		String type = gson.fromJson(message, MessageType.class).getMessage_type();
		
    	switch (type) {
            case "authorize": {
            	flow = MartiniBootApplication.getFlow();
            	
            	processAuthorize(message, gson);
            	
                return;
            }
            
            case "buy": {
            	processBuy(gson, message);
            	return;
            }
            
            case "transaction": {
            	processTransaction(message, gson);
            	
            	return;
            }
    	}
	}
	
	private void processBuy(Gson gson, String message) {
		BuyResponse buyResponse = gson.fromJson(message, BuyResponse.class);
		
		String buyId = buyResponse.getPassthrough().getBuyContractsUniqueId();
		
		if(buyId.equals(transactionStreamId)) {
			Ledger ledger = flow.getLedger();
			Buy buy = buyResponse.getBuy();
		
			ledger.setCurrentContractId(buy.getContract_id());
			ledger.setCurrentStake(buy.getBuy_price());
		}
			
	}

	/**
	 * Responsible for:
	 * 1.transactionStreamId
	 * 2.sell or buy action
	 * @param message
	 * @param gson
	 */
	private void processTransaction(String message, Gson gson) {
		
		TransactionUpdate update = gson.fromJson(message, TransactionUpdate.class);
		Transaction transaction = update.getTransaction();
		
		
		String id = transaction.getId();
		String action = transaction.getAction();
		
		
		//remember stream id
		if(action == null && transactionStreamId == null) { 
			transactionStreamId = id;
			flow.setBuyContractsId(id);
			
    		return;
		}
		
    	
    	if(transactionStreamId.equals(id) && action.equals("sell"))
    			flow.makeLuckyBet(update);
	}

	/**
	 * Responsible for:
	 * 1. Setting balance label
	 * 2. For printing "AUTHORIZED" message to logs
	 * @param message
	 * @param gson
	 */
	private void processAuthorize(String message, Gson gson) {
		
		Authorization authorization = gson.fromJson(message, Authorization.class);
		Authorize authorize = authorization.getAuthorize();
		
		controller.setBalance(authorize);
		controller.writeMessage("AUTHORIZED\n", false, true);
	}
	
	/*
	 * Responsible for:
	 * 1. Reset transactionStreamId to null
	 * 2. Write log message
	 * 3. Read reason phrase and:
	 * 3.1 Close connection
	 * 3.2 Reconnect
	 */
	@OnClose
	public void onClose(CloseReason reason) {
		
		transactionStreamId = null;
		
		controller.writeMessage("DISCONNECTED WITH CLOSE EVENT\n", true, true);
		
		String reasonPhrase = reason.getReasonPhrase();
		
		System.out.println("...Close reason " + reasonPhrase);
		
		if(!reasonPhrase.equals("!!!Bye!!!")) {
			SocketPlug plug = MartiniBootApplication.getSocketPlug();
			plug.connect().
					authorize(MartiniBootApplication.getLuckyGuy().getToken()).
					subscribe();
		}
	}
	
	@OnError
	public void onError(Throwable throwable) {
		transactionStreamId = null;
		
		controller.writeMessage("DISCONNECTED WITH ERROR\n", true, true);
		
		//reconnect process
		SocketPlug plug = MartiniBootApplication.getSocketPlug();
		plug.connect().
				authorize(MartiniBootApplication.getLuckyGuy().getToken()).
				subscribe();
		
	}
	
	//setters
	private void setFlow(RandomFlow flow) {
		this.flow = flow;
	}
}
