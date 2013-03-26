package com.interzonedev.serviceversioningdemo.v1;

public interface ExampleAPI {

	public static final String EXCHANGE_NAME = "example_exchange";

	public static final String QUEUE_NAME = "example_exchange";

	public static final String VERSION = "v1";
	
	public void print(String message);

	public void println(String message);

}
