package com.interzonedev.serviceversioningdemo.service.all;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.common.all.ExampleAMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	private final Map<String, ServiceInvoker> serviceInvokers = new HashMap<String, ServiceInvoker>();

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	private QueueingConsumer consumer;

	public void init() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		log.info("init: Initializing service runner");

		// Register invokers.
		serviceInvokers.put(ExampleAMQP.VERSION_1,
				new com.interzonedev.serviceversioningdemo.service.v1.ExampleInvoker());
		serviceInvokers.put(ExampleAMQP.VERSION_2,
				new com.interzonedev.serviceversioningdemo.service.v2.ExampleInvoker());

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();

		channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");
		channel.queueDeclare(ExampleAMQP.QUEUE_NAME, false, false, true, null);
		channel.queueBind(ExampleAMQP.QUEUE_NAME, ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.ROUTING_KEY);

		consumer = new QueueingConsumer(channel);
		channel.basicConsume(ExampleAMQP.QUEUE_NAME, true, consumer);

		log.info("init: Initialized service runner");

	}

	public void destroy() throws IOException {

		log.info("destroy: Destroying service runner");

		channel.close();
		connection.close();

		log.info("destroy: Destroyed service runner");

	}

	public void receive() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

			log.debug("receive: Received request");

			// Get the command and version from the request.
			Command command = getCommand(delivery);
			String version = getVersion(delivery);

			log.debug("receive: version = " + version + " - command = " + command);

			// Get the service invoker based on the version in the request.
			ServiceInvoker serviceInvoker = serviceInvokers.get(version);
			if (null == serviceInvoker) {
				log.error("receive: Unsupported version " + version);
			}

			log.debug("receive: Invoking service");

			serviceInvoker.invoke(command);

			log.debug("receive: Completed request");

		}

	}

	private Command getCommand(QueueingConsumer.Delivery delivery) {
		return (Command) SerializationUtils.deserialize(delivery.getBody());
	}

	private String getVersion(QueueingConsumer.Delivery delivery) {
		Map<String, Object> headers = delivery.getProperties().getHeaders();
		String version = headers.get(ExampleAMQP.VERSION_HEADER_NAME).toString();
		return version;
	}

	public static void main(String[] args) {

		final ServiceRunner serviceRunner = new ServiceRunner();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					log.info("main: Shutting down service runner");
					serviceRunner.destroy();
					log.info("main: Shut down service runner");
				} catch (IOException ioe) {
					log.error("main: Error destroying service runner", ioe);
				}
			}
		});

		try {
			log.info("main: Initializing service runner");
			serviceRunner.init();
			log.info("main: Starting service runner");
			serviceRunner.receive();
			log.info("main: Service runner completed");
		} catch (Exception e) {
			log.error("main: Error running service runner", e);
		}

		System.exit(0);

	}

}
