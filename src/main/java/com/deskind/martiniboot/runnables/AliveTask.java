package com.deskind.martiniboot.runnables;

import java.util.TimerTask;

import com.deskind.martiniboot.connection.SocketPlug;

public class AliveTask extends TimerTask {
	
	private SocketPlug plug;
	
	public AliveTask(SocketPlug plug) {
		this.plug = plug;
	}

	@Override
	public void run() {
			String message = "{\"ping\": 1}";
			plug.sendMessage(message);
	}

}
