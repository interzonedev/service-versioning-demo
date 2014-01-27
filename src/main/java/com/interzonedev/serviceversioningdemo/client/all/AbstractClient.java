package com.interzonedev.serviceversioningdemo.client.all;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Top level abstract superclass for all client implenmtations. Handles all the AMQP specific functionality of sending
 * remote requests.
 * 
 * @author mmarkarian
 */
public abstract class AbstractClient {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	/**
	 * Command line input value that terminates the client.
	 */
	private static final String QUIT_INPUT = "quit";

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	/**
	 * Initializes this client without a whole lot of tuning or error handling. Creates the AMQP factory, connection and
	 * channel. Creates the necessary exchange to which messages are posted.
	 * 
	 * @throws IOException Thrown if there was an error setting up the AMQP message components.
	 */
	public void init() throws IOException {

		log.info("init: Initializing client");

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();
		channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");

		log.info("init: Initialized client");

	}

	/**
	 * Closes the AMQP channel and connection.
	 * 
	 * @throws IOException Thrown if there was an error closing the AMQP channel or connection.
	 */
	public void shutdown() throws IOException {

		log.info("shutdown: Shutting down client");

		channel.close();
		connection.close();

		log.info("shutdown: Shut down client");

	}

	/**
	 * Sends a remote AMQP request with a message body that is a serialized representation of the specified
	 * {@link Command}. Calls {@link #getVersion()} on the implementing client and sets the version in the request
	 * headers.
	 * 
	 * @param command A {@link Command} instance that represents the use case of the request.
	 */
	protected void send(Command command) {

		log.debug("send: Sending command " + command);

		try {
			BasicProperties messageProperties = getMessageProperties();
			byte[] messageBody = getMessageBody(command);
			channel.basicPublish(ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.ROUTING_KEY, messageProperties, messageBody);
		} catch (IOException ioe) {
			log.error("send: Error sending message", ioe);
		}

	}

	/**
	 * Runs indefinitely and polls {@link System#in} for input until {@link #QUIT_INPUT} is input. Calls the
	 * {@link #process(String)} method on the implementing client so that the client can send the remote request.
	 * 
	 * @throws IOException Thrown if there was an error reading console input.
	 */
	protected void poll() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = br.readLine();

		while (!QUIT_INPUT.equals(input)) {
			if (!"".equals(input.trim())) {
				process(input);
			}
			input = br.readLine();
		}

	}

	/**
	 * Launches an instance of the implementing client and polls for command line input. Blocks and services requests
	 * indefinitely until the JVM is shut down or a {@link #QUIT_INPUT} is encountered. Adds a JVM shutdown hook to
	 * gracefully shutdown the client.
	 */
	protected void deploy() {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					log.info("deploy: Shutting down client");
					shutdown();
					log.info("deploy: Shut down client");
				} catch (IOException ioe) {
					log.error("deploy: Error shutting down client", ioe);
				}
			}
		});

		try {
			log.info("deploy: Initializing client");
			init();
			log.info("deploy: Starting client");
			poll();
			log.info("deploy: Client stopping");
		} catch (IOException ioe) {
			log.error("deploy: Error running client", ioe);
		}

	}

	/**
	 * Gets a {@link BasicProperties} instance with the {@link ExampleAMQP#VERSION_HEADER_NAME} header set to the value
	 * returned by calling {@link #getVersion()} on the implementing client.
	 * 
	 * @return Returns a {@link BasicProperties} instance with the {@link ExampleAMQP#VERSION_HEADER_NAME} header set to
	 *         the value returned by calling {@link #getVersion()} on the implementing client.
	 */
	private BasicProperties getMessageProperties() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(ExampleAMQP.VERSION_HEADER_NAME, getVersion());
		BasicProperties basicProperties = new BasicProperties.Builder().headers(headers).build();
		return basicProperties;
	}

	/**
	 * Serializes the specified {@link Command} instance into an array of bytes to be set as the message body.
	 * 
	 * @param command A {@link Command} instance that represents the use case of the request.
	 * 
	 * @return Returns an array of bytes serialized from the specified {@link Command} instance.
	 * 
	 * @throws IOException Thrown if there was an error serializing the {@link Command} instance.
	 */
	private byte[] getMessageBody(Command command) throws IOException {

		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
			oos = new ObjectOutputStream(baos);
			oos.writeObject(command);
			return baos.toByteArray();
		} finally {
			try {
				if (null != oos) {
					oos.close();
				}
			} catch (IOException ioe) {
				log.error("getMessageBody: Error closing object output stream", ioe);
			}
		}

	}

	/**
	 * Allows the implementing client to return a value specifying the client version.
	 * 
	 * @return Returns a value specifying the client version.
	 */
	protected abstract String getVersion();

	/**
	 * Client specific method to transform command line input into an AMQP request.
	 * 
	 * @param input Command line input that determines the use case of the remote request to send.
	 */
	protected abstract void process(String input);

}
