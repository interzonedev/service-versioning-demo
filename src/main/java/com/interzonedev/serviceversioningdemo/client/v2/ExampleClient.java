package com.interzonedev.serviceversioningdemo.client.v2;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.client.AbstractClient;
import com.interzonedev.serviceversioningdemo.common.Command;
import com.interzonedev.serviceversioningdemo.common.ExampleAMQP;
import com.interzonedev.serviceversioningdemo.common.v2.ExampleAPI;

public class ExampleClient extends AbstractClient implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleClient.class);

	@Override
	public void print(String message, boolean timestamp) {

		log.debug("print: message = " + message + " - timestamp = " + timestamp);

		send(new Command("print", message, timestamp));

	}

	@Override
	public void println(String message, boolean timestamp) {

		log.debug("println: message = " + message + " - timestamp = " + timestamp);

		send(new Command("println", message, timestamp));

	}

	@Override
	protected String getVersion() {
		return ExampleAMQP.VERSION_2;
	}

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

	public static void main(String[] args) throws IOException {

		final ExampleClient client = new ExampleClient();

		log.info("main: Deploying client");
		client.deploy();
		log.info("main: Client completed");

		System.exit(0);

	}
}
