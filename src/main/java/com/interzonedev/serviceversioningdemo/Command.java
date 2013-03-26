package com.interzonedev.serviceversioningdemo;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String method;

	private final String message;

	private final boolean timestamp;

	public Command(String method, String message) {
		this.method = method;
		this.message = message;
		this.timestamp = false;
	}

	public Command(String method, String message, boolean timestamp) {
		this.method = method;
		this.message = message;
		this.timestamp = timestamp;
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

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("[method = ").append(getMethod()).append(" - message = ").append(getMessage())
				.append(" - timestamp = ").append(isTimestamp()).append("]");
		return out.toString();
	}

}
