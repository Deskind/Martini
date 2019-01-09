package com.deskind.martiniboot.runnables;

import java.util.TimerTask;

import com.deskind.martiniboot.connection.SocketPlug;

public class AliveTask extends TimerTask {
	
	private final String PING_MESSAGE = "{\"ping\": 1}";
	
	private SocketPlug plug;
	
	public AliveTask(SocketPlug plug) {
		this.plug = plug;
	}

	@Override
	public void run() {
		if(plug.connected()) {
			plug.sendMessage(PING_MESSAGE);
		}else {
			System.out.println("...Enable to send ping message because of closed session ...");
		}
	}

}
