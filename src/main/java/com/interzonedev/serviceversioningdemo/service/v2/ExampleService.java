package com.interzonedev.serviceversioningdemo.service.v2;

import com.interzonedev.serviceversioningdemo.common.v2.ExampleAPI;

public class ExampleService implements ExampleAPI {

	@Override
	public void print(String message, boolean timestamp) {

		System.out.print(buildOutput(message, timestamp));

	}

	@Override
	public void println(String message, boolean timestamp) {

		System.out.println(buildOutput(message, timestamp));

	}

	private String buildOutput(String message, boolean timestamp) {

		StringBuilder out = new StringBuilder("Version 2:");
		if (timestamp) {
			out.append(" [").append(System.currentTimeMillis()).append("]");
		}
		out.append(" message = ").append(message);

		return out.toString();

	}

}
