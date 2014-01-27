package com.interzonedev.serviceversioningdemo.common.v1;

/**
 * Version 1 API for client and service.
 * 
 * @author mmarkarian
 */
public interface ExampleAPI {

	/**
	 * Print the specified message to {@link System#out}.
	 * 
	 * @param message The message to print.
	 */
	public void print(String message);

	/**
	 * Print the specified message to {@link System#out} with a newline.
	 * 
	 * @param message The message to print.
	 */
	public void println(String message);

}
