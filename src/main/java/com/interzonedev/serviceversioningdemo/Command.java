package com.interzonedev.serviceversioningdemo;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String method;

	private final String message;

	public Command(String method, String message) {
		this.method = method;
		this.message = message;
	}

	public String getMethod() {
		return method;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("[method = ").append(getMethod()).append(" - message = ").append(getMessage()).append("]");
		return out.toString();
	}

}
