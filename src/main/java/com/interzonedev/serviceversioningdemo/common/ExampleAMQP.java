package com.interzonedev.serviceversioningdemo.common;

/**
 * Static definitions of properties for doing RPC over AMQP.
 * 
 * @author mmarkarian
 */
public interface ExampleAMQP {

	/**
	 * The name of the exchange to which messages are posted.
	 */
	public static final String EXCHANGE_NAME = "example_exchange";

	/**
	 * The name of the AMQP queue to which the service binds to receive messages.
	 */
	public static final String QUEUE_NAME = "example_queue";

	/**
	 * The name of the routing key with which messages are sent.
	 */
	public static final String ROUTING_KEY = "example_key";

	/**
	 * The key used to indicate version 1 of the service and client API.
	 */
	public static final String VERSION_1_KEY = "v1";

	/**
	 * The key used to indicate version 2 of the service and client API.
	 */
	public static final String VERSION_2_KEY = "v2";

}
