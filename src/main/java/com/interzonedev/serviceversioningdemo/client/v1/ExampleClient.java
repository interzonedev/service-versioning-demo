package com.interzonedev.serviceversioningdemo.client.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.client.all.AbstractClient;
import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.interzonedev.serviceversioningdemo.common.v1.ExampleAPI;

public class ExampleClient extends AbstractClient implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleClient.class);

	@Override
	public void print(String message) {

		log.debug("print: message = " + message);

		send(new Command("print", message));

	}

	@Override
	public void println(String message) {

		log.debug("println: message = " + message);

		send(new Command("println", message));

	}

	@Override
	protected String getVersion() {
		return ExampleAMQP.VERSION_1;
	}

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

	public static void main(String[] args) {

		final ExampleClient client = new ExampleClient();

		log.info("main: Deploying client");
		client.deploy();
		log.info("main: Client completed");

		System.exit(0);

	}

}
