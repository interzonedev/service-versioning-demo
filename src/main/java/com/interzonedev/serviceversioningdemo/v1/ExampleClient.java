package com.interzonedev.serviceversioningdemo.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.interzonedev.serviceversioningdemo.Command;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ExampleClient implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleClient.class);

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	public void init() throws IOException {
		log.info("Initializing client");

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");

		log.info("Initialized client");
	}

	public void destroy() throws IOException {
		log.info("Destroying client");
		channel.close();
		connection.close();
		log.info("Destroyed client");
	}

	public void print(String message) {

		log.debug("print: message = " + message);

		send(new Command("print", message));

	}

	public void println(String message) {

		log.debug("println: message = " + message);

		send(new Command("println", message));

	}

	private void send(Command command) {

		log.debug("send: Sending command " + command);

		try {
			channel.basicPublish(EXCHANGE_NAME, VERSION, null, SerializationUtils.serialize(command));
		} catch (IOException ioe) {
			log.error("print: Error sending message", ioe);
		}
	}

	private void poll() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String message = br.readLine();

		while (!"quit".equals(message)) {
			if (StringUtils.isNotBlank(message)) {
				println(message);
			}
			message = br.readLine();
		}

	}

	public static void main(String[] args) throws IOException {
		final ExampleClient client = new ExampleClient();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					client.destroy();
				} catch (IOException ioe) {
					log.error("main: Error destroying service", ioe);
				}
			}
		});

		client.init();
		client.poll();
		//client.destroy();
		
		System.exit(0);
		
	}

}
