package com.deskind.martiniboot.connection;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
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
import com.deskind.martiniboot.fxcontrollers.MainController;
import com.deskind.martiniboot.trade.Ledger;
import com.deskind.martiniboot.trade.flow.Flow;
import com.google.gson.Gson;

@ClientEndpoint
public class ApplicationEndpoint {
	
	private Flow flow;
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		MartiniBootApplication.getMainController().writeMessage("CONNECTED", false, true);
	}
	
	@OnMessage
	public void onMessage(String message) {
//		System.out.println("...Server message : " + message);
		
		//in case of reconnection flow reference will be null
		if(flow == null) {
			setFlow(MartiniBootApplication.getFlow());
		}
		
		Gson gson = new Gson();
		String type = gson.fromJson(message, MessageType.class).getMessage_type();
		
    	switch (type) {
            case "authorize": {
            	flow = MartiniBootApplication.getFlow();
            	
            	processAuthorize(message, gson);
            	
                return;
            }
            
            case "buy": {
            	processBuy(message, gson);
            	return;
            }
            
            case "transaction": {
            	processTransaction(message, gson);
            	
            	return;
            }
    	}
	}
	
	private void processTransaction(String message, Gson gson) {
		System.out.println(message);
		
		TransactionUpdate update = gson.fromJson(message, TransactionUpdate.class);
		Transaction transaction = update.getTransaction();
		Ledger ledger = flow.getLedger();
		
		long id = update.getTransaction().getContract_id();
    	String action = transaction.getAction();
    	
    	if(action == null) 
    		return;
    	
    	if(action.equals("buy")) {
    		ledger.setCurrentContractId(id);
    		
    		float amount = Math.abs(transaction.getAmount());
    		ledger.setCurrentStake(amount);
    		
    		return;
    	}
    	
    	if(ledger.getCurrentContractId() == id && action.equals("sell"))
    		flow.makeLuckyBet(update);
    	
	}

	private void processBuy(String message, Gson gson) {
		System.out.println(message);
	}

	private void processAuthorize(String message, Gson gson) {
		
		Authorization authorization = gson.fromJson(message, Authorization.class);
		Authorize authorize = authorization.getAuthorize();
		
		MainController controller = MartiniBootApplication.getMainController();
		controller.setBalance(authorize);
		controller.writeMessage("AUTHORIZED", false, true);
	}

	@OnClose
	public void onClose(CloseReason reason) {
		MartiniBootApplication.getMainController().writeMessage("DISCONNECTED", true, true);
		
		System.out.println("...Close reason " + reason.getReasonPhrase());
		
		SocketPlug plug = MartiniBootApplication.getSocketPlug();
		plug.connect().
				authorize(MartiniBootApplication.getLuckyGuy().getToken()).
				subscribe();
	}
	
	//setters
	private void setFlow(Flow flow) {
		this.flow = flow;
	}
}
