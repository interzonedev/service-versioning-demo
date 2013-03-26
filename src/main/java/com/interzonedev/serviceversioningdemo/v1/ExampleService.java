package com.interzonedev.serviceversioningdemo.v1;

import java.io.IOException;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.interzonedev.serviceversioningdemo.Command;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ExampleService implements ExampleAPI {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleService.class);

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	private QueueingConsumer consumer;

	public void init() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		log.info("Initializing service");

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		channel.queueDeclare(QUEUE_NAME, false, true, true, null);
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, VERSION);

		consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		log.info("Initialized service");

		receive();

	}

	public void destroy() throws IOException {
		log.info("Destroying service");
		channel.close();
		connection.close();
		log.info("Destroyed service");
	}

	private void receive() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

			Command command = (Command) SerializationUtils.deserialize(delivery.getBody());

			String routingKey = delivery.getEnvelope().getRoutingKey();

			log.debug("receive: routingKey = " + routingKey + " - command = " + command);

			String method = command.getMethod();
			String message = command.getMessage();

			if ("print".equals(method)) {
				print(message);
			} else if ("println".equals(method)) {
				println(message);
			} else {
				log.error("receive: Unsupported method " + method);
			}
		}
	}

	public void print(String message) {
		System.out.print("Example: message = " + message);

	}

	public void println(String message) {
		System.out.println("Example: message = " + message);
	}

	public static void main(String[] args) throws ShutdownSignalException, ConsumerCancelledException, IOException,
			InterruptedException {

		final ExampleService service = new ExampleService();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					service.destroy();
				} catch (IOException ioe) {
					log.error("main: Error destroying service", ioe);
				}
			}
		});

		service.init();

	}

}
