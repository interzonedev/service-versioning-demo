package com.interzonedev.serviceversioningdemo.client.v2;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.client.all.AbstractClient;
import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.interzonedev.serviceversioningdemo.common.v2.ExampleAPI;

/**
 * Version 2 client implementation of {@link ExampleAPI}.
 * 
 * @author mmarkarian
 */
public class ExampleClient extends AbstractClient implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleClient.class);

	/**
	 * Sends a message to the service to print a version 2 specific message to {@link System#out} with an optional
	 * timestamp.
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void print(String message, boolean timestamp) {

		log.debug("print: message = " + message + " - timestamp = " + timestamp);

		send(new Command("print", message, timestamp));

	}

	/**
	 * Sends a message to the service to print a version 2 specific message to {@link System#out} with an optional
	 * timestamp and a newline.
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void println(String message, boolean timestamp) {

		log.debug("println: message = " + message + " - timestamp = " + timestamp);

		send(new Command("println", message, timestamp));

	}

	/**
	 * Gets the version 2 constant to communicate to the rest of the system that this is the version 2 client.
	 * 
	 * @return Returns {@link ExampleAMQP#VERSION_2}.
	 */
	@Override
	protected String getVersion() {
		return ExampleAMQP.VERSION_2;
	}

	/**
	 * Determines the use case represented by the specified input and calls the appropriate method on this instance with
	 * the arguments parsed from the specified input.
	 * 
	 * @param input Command line input that determines the use case of the remote request to send.
	 */
	@Override
	protected void process(String input) {

		log.debug("process: input = " + input);

		String[] args = input.trim().split("\\s+");

		if (args.length < 2) {
			log.error("process: usage <method> [<timestamp>] <message part 1> [<message part 2>...]");
			return;
		}

		String method = args[0];

		int messageIndex = 1;
		boolean timestamp = false;
		String possibleTimestamp = args[1];
		if ("true".equalsIgnoreCase(possibleTimestamp.trim()) || "false".equalsIgnoreCase(possibleTimestamp.trim())) {
			timestamp = Boolean.parseBoolean(possibleTimestamp);
			messageIndex = 2;
		}

		StringBuilder messageBuilder = new StringBuilder();
		for (int i = messageIndex; i < args.length; i++) {
			messageBuilder.append(args[i]).append(" ");
		}
		String message = messageBuilder.toString().trim();

		if ("println".equals(method)) {
			println(message, timestamp);
		} else if ("print".equals(method)) {
			print(message, timestamp);
		} else {
			log.error("process: Unsuppored method " + method);
		}

		log.debug("process: End");

	}

	/**
	 * Launches an instance of this client and polls for command line input. Blocks and gathers input and sends requests
	 * indefinitely until the JVM is shut down.
	 * 
	 * @param args The array of arguments passed in from the command line. Currently unused.
	 */
	public static void main(String[] args) throws IOException {

		final ExampleClient client = new ExampleClient();

		log.info("main: Deploying client");
		client.deploy();
		log.info("main: Client stopped");

		System.exit(0);

	}
}
