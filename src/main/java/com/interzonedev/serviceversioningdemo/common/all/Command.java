package com.interzonedev.serviceversioningdemo.common.all;

import java.io.Serializable;

/**
 * Immutable, {@link Serializable} value object that encapsulates the information necessary for sending requests from
 * the client to the service for all versions.
 * 
 * @author mmarkarian
 */
public class Command implements Serializable {

	private static final long serialVersionUID = 4765308290019325755L;

	/**
	 * The method the call on the service.
	 */
	private final String method;

	/**
	 * The message to pass into the service method.
	 */
	private final String message;

	/**
	 * Whether or not the service method should output a timestamp.
	 */
	private final boolean timestamp;

	private final String toStringMessage;

	public Command(String method, String message) {
		this.method = method;
		this.message = message;
		this.timestamp = false;

		toStringMessage = getToStringMessage();
	}

	public Command(String method, String message, boolean timestamp) {
		this.method = method;
		this.message = message;
		this.timestamp = timestamp;

		toStringMessage = getToStringMessage();
	}

	public String getMethod() {
		return method;
	}

	public String getMessage() {
		return message;
	}

	public boolean isTimestamp() {
		return timestamp;
	}

	private String getToStringMessage() {
		StringBuilder out = new StringBuilder();
		out.append("[method = ").append(method).append(" - message = ").append(message).append(" - timestamp = ")
				.append(timestamp).append("]");
		return out.toString();
	}

	@Override
	public String toString() {
		return toStringMessage;
	}

}
