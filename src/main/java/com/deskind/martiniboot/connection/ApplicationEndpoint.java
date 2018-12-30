package com.deskind.martiniboot.connection;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.binary.entities.Authorization;
import com.deskind.martiniboot.binary.entities.Authorize;
import com.deskind.martiniboot.binary.entities.MessageType;
import com.deskind.martiniboot.entities.Account;
import com.deskind.martiniboot.fxcontrollers.MainController;
import com.deskind.martiniboot.trade.flow.RandomFlow;
import com.google.gson.Gson;

@ClientEndpoint
public class ApplicationEndpoint {
	
	private RandomFlow flow;
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		MartiniBootApplication.getMainController().writeMessage("CONNECTED", false, true);
	}
	
	@OnMessage
	public void onMessage(String message) {
		Gson gson = new Gson();
		String type = gson.fromJson(message, MessageType.class).getMessage_type();
		
    	switch (type) {
            case "authorize": {
            	processAuthorize(message, gson);
                return;
            }
            
            case "ping": {
            	System.out.println(message);
            	return;
            }
            
            case "buy": {
            	System.out.println(message);
            }
    	}
	}
	
	private void processAuthorize(String message, Gson gson) {
		flow = MartiniBootApplication.getRandomFlow();
		
		System.out.println("Random flow is " + flow.toString());
		
		Authorization authorization = gson.fromJson(message, Authorization.class);
		Authorize authorize = authorization.getAuthorize();
		
		MainController controller = MartiniBootApplication.getMainController();
		controller.setBalance(authorize);
		controller.writeMessage("AUTHORIZED", false, true);
		MartiniBootApplication.getLuckyGuy().setAccount(new Account(authorize));
	}

	@OnClose
	public void onClose() {
		System.out.println("Web Socket session closed ... ");
	}
}
