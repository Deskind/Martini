package com.deskind.martiniboot.connection;

import java.io.IOException;
import java.net.URI;

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
	public void connect() {
		try {
			session = ContainerProvider.getWebSocketContainer().
					connectToServer(ApplicationEndpoint.class, 
									URI.create("wss://ws.binaryws.com/websockets/v3?app_id=" + appId));
		} catch (DeploymentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Authorizing with token
	 * @param token
	 */
	public void authorize(String token) {
		String authorize = String.format("{\"authorize\": \"%s\"}", token);
		
		try {
			session.getBasicRemote().sendText(authorize);
		} catch (IOException e) {
			System.out.println("+++Cant authorize+++");
			e.printStackTrace();
		}
	}

	public boolean connected() {
		return session.isOpen() ? true : false;
	}

	public void sendMessage(String message) {
		if(this.connected())
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
