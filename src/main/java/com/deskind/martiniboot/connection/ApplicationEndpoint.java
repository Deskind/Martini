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
import com.google.gson.Gson;

@ClientEndpoint
public class ApplicationEndpoint {
	@OnOpen
	public void onOpen(Session session) throws IOException {
		
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
    	}
	}
	
	private void processAuthorize(String message, Gson gson) {
		Authorization authorization = gson.fromJson(message, Authorization.class);
		Authorize authorize = authorization.getAuthorize();
		
		MartiniBootApplication.getMainController().setBalance(authorize);
		MartiniBootApplication.getLuckyGuy().setAccount(new Account(authorize));
		
		System.out.println("Authorize is = > " + authorize.toString());
	}

	@OnClose
	public void onClose() {
		System.out.println("Web Socket session closed ... ");
	}
}
