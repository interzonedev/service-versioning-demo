package com.interzonedev.serviceversioningdemo.service.v1;

import com.interzonedev.serviceversioningdemo.common.v1.ExampleAPI;

/**
 * Version 1 service implementation of {@link ExampleAPI}.
 * 
 * @author mmarkarian
 */
public class ExampleService implements ExampleAPI {

	/**
	 * Prints a version 1 specific message to {@link System#out}.
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void print(String message) {

		System.out.print("Version 1: message = " + message);

	}

	/**
	 * Prints a version 1 specific message to {@link System#out} with a newline..
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void println(String message) {

		System.out.println("Version 1: message = " + message);

	}

}
