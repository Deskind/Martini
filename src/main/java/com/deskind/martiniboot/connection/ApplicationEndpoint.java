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
	
	public ApplicationEndpoint() {
		 controller = MartiniBootApplication.getMainController();
	}

	@OnOpen
	public void onOpen(Session session) throws IOException {
		controller.writeMessage("CONNECTED", false, true);
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
		
		//every next contract
		flow.setCurrentContractId(buyResponse.getBuy().getContract_id());
		flow.getLedger().setCurrentStake(buyResponse.getBuy().getBuy_price());
			
	}

	/**
	 * Responsible for:
	 * 1.transactionStreamId
	 * 2.sell or buy action
	 * @param message
	 * @param gson
	 */
	private void processTransaction(String message, Gson gson) {
		
		//transaction from transaction update
		Transaction transaction = gson.fromJson(message, TransactionUpdate.class).getTransaction();
		
		if(transaction.getAction() == null)
			return;
		
    	if(flow.getCurrentContractId() == transaction.getContract_id() && transaction.getAction().equals("sell")) {
    		flow.makeLuckyBet(transaction);
    	}
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
		
		if(authorization.getError() != null) {
			controller.writeMessage(authorization.getError().toString(), true, true);
			return;
		}
		
		controller.setBalance(authorize);
		controller.writeMessage("AUTHORIZED", false, true);
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
		
		String message = String.format("DISCONNECTED. Reason: %s. Phrase: %s", reason.getCloseCode().toString(), reason.getReasonPhrase());
		
		if(reason.getReasonPhrase().equals("!!!Bye!!!")) {
			controller.writeMessage(message, true, true);
		}else {
			controller.writeMessage(message, true, true);
			SocketPlug plug = MartiniBootApplication.getSocketPlug();
			plug.connect().
					authorize(MartiniBootApplication.getLuckyGuy().getToken()).
					subscribe();
		}
	}
	
	@OnError
	public void onError(Throwable throwable) {
		
		System.out.println(throwable.getMessage());
		
		controller.writeMessage("CONNECTION ERROR", true, true);
		
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
