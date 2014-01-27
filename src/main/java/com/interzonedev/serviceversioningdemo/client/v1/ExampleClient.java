package com.interzonedev.serviceversioningdemo.client.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.client.all.AbstractClient;
import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.interzonedev.serviceversioningdemo.common.v1.ExampleAPI;

/**
 * Version 1 client implementation of {@link ExampleAPI}.
 * 
 * @author mmarkarian
 */
public class ExampleClient extends AbstractClient implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleClient.class);

	/**
	 * Sends a message to the service to print a version 1 specific message to {@link System#out}.
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void print(String message) {

		log.debug("print: message = " + message);

		send(new Command("print", message));

	}

	/**
	 * Sends a message to the service to print a version 1 specific message to {@link System#out} with a newline.
	 * 
	 * @param message The message to print.
	 */
	@Override
	public void println(String message) {

		log.debug("println: message = " + message);

		send(new Command("println", message));

	}

	/**
	 * Gets the version 1 constant to communicate to the rest of the system that this is the version 1 client.
	 * 
	 * @return Returns {@link ExampleAMQP#VERSION_1}.
	 */
	@Override
	protected String getVersion() {
		return ExampleAMQP.VERSION_1;
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
			log.error("process: usage <method> <message part 1> [<message part 2>...]");
			return;
		}

		String method = args[0];
		StringBuilder messageBuilder = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			messageBuilder.append(args[i]).append(" ");
		}
		String message = messageBuilder.toString().trim();

		if ("println".equals(method)) {
			println(message);
		} else if ("print".equals(method)) {
			print(message);
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
	public static void main(String[] args) {

		final ExampleClient client = new ExampleClient();

		log.info("main: Deploying client");
		client.deploy();
		log.info("main: Client stopped");

		System.exit(0);

	}

}
