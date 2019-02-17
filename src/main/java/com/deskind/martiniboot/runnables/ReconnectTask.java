package com.deskind.martiniboot.runnables;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.binary.requests.ProfitTableRequest;
import com.deskind.martiniboot.connection.SocketPlug;
import com.google.gson.Gson;

public class ReconnectTask implements Runnable {
	
	private final int PAUSE = 3000;
	
	private SocketPlug plug;

	
	public ReconnectTask(SocketPlug plug) {
		super();
		this.plug = plug;
	}


	@Override
	public void run() {
		
		//asking a socket for reconnect
		while(!plug.connected()) {
			
			
			System.out.println("Attemp to reconnect to server");
			
			
			plug.connect().
				 authorize(MartiniBootApplication.getLuckyGuy().getToken()).
				 subscribe();
			
			
			//Pause between attempts
			try {
				Thread.sleep(PAUSE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		System.out.println("Successfull Reconnect");
		
		//Check missed sell messages
		System.out.println("+++Asking for profit table+++");
		MartiniBootApplication.getSocketPlug().sendMessage(new Gson().toJson(new ProfitTableRequest()));
		
		
	}

}
