package com.interzonedev.serviceversioningdemo.common.v2;

/**
 * Version 2 API for client and service.
 * 
 * @author mmarkarian
 */
public interface ExampleAPI {

	/**
	 * Print the specified message to {@link System#out} with an optional timestamp.
	 * 
	 * @param message The message to print.
	 * @param timestamp Whether or not to print a timestamp.
	 */
	public void print(String message, boolean timestamp);

	/**
	 * Print the specified message to {@link System#out} with an optional timestamp and a newline.
	 * 
	 * @param message The message to print.
	 * @param timestamp Whether or not to print a timestamp.
	 */
	public void println(String message, boolean timestamp);

}
