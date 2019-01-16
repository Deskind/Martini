package com.deskind.martiniboot.connection;

import java.io.IOException;
import java.net.URI;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;

import com.deskind.martiniboot.MartiniBootApplication;

/**
 * The class responsible for web socket connection
 * @author deski
 *
 */
public class SocketPlug {
	
	private final String appId = "15135";
	private Session session;
			
	/**
	 * Connecting to server
	 */
	public SocketPlug connect() {
		try {
			session = ContainerProvider.getWebSocketContainer().
					connectToServer(ApplicationEndpoint.class, 
									URI.create("wss://ws.binaryws.com/websockets/v3?app_id=" + appId));
		} catch (DeploymentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	/**
	 * Authorize with token
	 * @param token
	 */
	public SocketPlug authorize(String token) {
		String authorize = String.format("{\"authorize\": \"%s\"}", token);
		
		sendMessage(authorize);
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("+++ Authorized");
		
		return this;
	}

	public boolean connected() {
		return session.isOpen() ? true : false;
	}

	public void sendMessage(String message) {
		if(this.connected())
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				System.out.println("+++ Failed to send message : " + message);
			}
	}

	public void disconnect(CloseReason reason) {
		if(connected()) {
			try {
				session.close(reason);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public SocketPlug subscribe() {
		
		sendMessage("{\"transaction\": 1, \"subscribe\": 1}");
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("+++ Subscribed");
		
		return this;
	}

}
