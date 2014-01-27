package com.interzonedev.serviceversioningdemo.common.all;

/**
 * Static definitions of properties for doing RPC over AMQP for all versions of the client and service.
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
	 * The value used to indicate version 1 of the service and client API.
	 */
	public static final String VERSION_1 = "v1";

	/**
	 * The value used to indicate version 2 of the service and client API.
	 */
	public static final String VERSION_2 = "v2";

	/**
	 * The name of the header that holds the version value on each request.
	 */
	public static final String VERSION_HEADER_NAME = "version";

}
