package com.interzonedev.serviceversioningdemo.v1;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.interzonedev.serviceversioningdemo.AbstractClient;
import com.interzonedev.serviceversioningdemo.Command;

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
		return VERSION;
	}

	@Override
	protected void process(String input) {

		println(input);

	}

	public static void main(String[] args) throws IOException {

		final ExampleClient client = new ExampleClient();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					client.destroy();
				} catch (IOException ioe) {
					log.error("main: Error destroying client", ioe);
				}
			}
		});

		client.init();
		client.poll();

		System.exit(0);

	}

}
