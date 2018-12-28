package com.deskind.martiniboot.binary.entities;

/**
 * Used for determine message type from server
 * @author deski
 *
 */
public class MessageType {
	private String msg_type;

	public MessageType(String message_type) {
		super();
		this.msg_type = message_type;
	}

	public String getMessage_type() {
		return msg_type;
	}

	public void setMessage_type(String message_type) {
		this.msg_type = message_type;
	}

	@Override
	public String toString() {
		return "MessageType [message_type=" + msg_type + "]";
	}
	
	
}
