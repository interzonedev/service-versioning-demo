package com.interzonedev.serviceversioningdemo.client.all;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class AbstractClient {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	public void init() throws IOException {

		log.info("init: Initializing client");

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();
		channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");

		log.info("init: Initialized client");

	}

	public void shutdown() throws IOException {

		log.info("shutdown: Shutting down client");

		channel.close();
		connection.close();

		log.info("shutdown: Shut down client");

	}

	protected void send(Command command) {

		log.debug("send: Sending command " + command);

		try {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put(ExampleAMQP.VERSION_HEADER_NAME, getVersion());
			BasicProperties basicProperties = new BasicProperties.Builder().headers(headers).build();
			channel.basicPublish(ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.ROUTING_KEY, basicProperties,
					SerializationUtils.serialize(command));
		} catch (IOException ioe) {
			log.error("send: Error sending message", ioe);
		}

	}

	protected void poll() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = br.readLine();

		while (!"quit".equals(input)) {
			if (StringUtils.isNotBlank(input)) {
				process(input);
			}
			input = br.readLine();
		}

	}

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
			log.info("deploy: Client completed");
		} catch (IOException ioe) {
			log.error("deploy: Error running client", ioe);
		}

	}

	protected abstract String getVersion();

	protected abstract void process(String input);

}
