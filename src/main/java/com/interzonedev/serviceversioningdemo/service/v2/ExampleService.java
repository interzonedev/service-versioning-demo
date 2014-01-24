package com.interzonedev.serviceversioningdemo.service.v2;

import com.interzonedev.serviceversioningdemo.common.v2.ExampleAPI;

/**
 * Version 2 service implementation of {@link ExampleAPI}.
 * 
 * @author mmarkarian
 */
public class ExampleService implements ExampleAPI {

	/**
	 * Prints a version 2 specific message to {@link System#out} with an optional timestamp..
	 * 
	 * @param message The message to print.
	 * @param timestamp Whether or not to print a timestamp.
	 */
	@Override
	public void print(String message, boolean timestamp) {

		System.out.print(buildOutput(message, timestamp));

	}

	/**
	 * Prints a version 2 specific message to {@link System#out} with an optional timestamp..
	 * 
	 * @param message The message to print.
	 * @param timestamp Whether or not to print a timestamp.
	 */
	@Override
	public void println(String message, boolean timestamp) {

		System.out.println(buildOutput(message, timestamp));

	}

	/**
	 * Builds the message to print.
	 * 
	 * @param message The message to print.
	 * @param timestamp Whether or not to print a timestamp.
	 * 
	 * @return Returns the message to print.
	 */
	private String buildOutput(String message, boolean timestamp) {

		StringBuilder out = new StringBuilder("Version 2:");
		if (timestamp) {
			out.append(" [").append(System.currentTimeMillis()).append("]");
		}
		out.append(" message = ").append(message);

		return out.toString();

	}

}
