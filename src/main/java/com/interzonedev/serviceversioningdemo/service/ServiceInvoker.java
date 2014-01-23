package com.interzonedev.serviceversioningdemo.service;

import java.io.IOException;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.common.Command;
import com.interzonedev.serviceversioningdemo.common.ExampleAMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ServiceInvoker {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	private QueueingConsumer consumer;

	private com.interzonedev.serviceversioningdemo.service.v1.ExampleService serviceV1 = new com.interzonedev.serviceversioningdemo.service.v1.ExampleService();
	private com.interzonedev.serviceversioningdemo.service.v2.ExampleService serviceV2 = new com.interzonedev.serviceversioningdemo.service.v2.ExampleService();

	public void init() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		log.info("init: Initializing service");

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		connection = factory.newConnection();

		channel = connection.createChannel();

		channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");
		channel.queueDeclare(ExampleAMQP.QUEUE_NAME, false, false, true, null);
		channel.queueBind(ExampleAMQP.QUEUE_NAME, ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.VERSION_1_KEY);
		channel.queueBind(ExampleAMQP.QUEUE_NAME, ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.VERSION_2_KEY);

		consumer = new QueueingConsumer(channel);
		channel.basicConsume(ExampleAMQP.QUEUE_NAME, true, consumer);

		log.info("init: Initialized service");

	}

	public void destroy() throws IOException {

		log.info("destroy: Destroying service");

		channel.close();
		connection.close();

		log.info("destroy: Destroyed service");

	}

	public void receive() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

			Command command = (Command) SerializationUtils.deserialize(delivery.getBody());

			String routingKey = delivery.getEnvelope().getRoutingKey();

			log.debug("receive: routingKey = " + routingKey + " - command = " + command);

			String method = command.getMethod();
			String message = command.getMessage();
			boolean timestamp = command.isTimestamp();

			if (ExampleAMQP.VERSION_1_KEY.equals(routingKey)) {
				if ("print".equals(method)) {
					serviceV1.print(message);
				} else if ("println".equals(method)) {
					serviceV1.println(message);
				} else {
					log.error("receive: Unsupported method " + method);
				}
			} else if (ExampleAMQP.VERSION_2_KEY.equals(routingKey)) {
				if ("print".equals(method)) {
					serviceV2.print(message, timestamp);
				} else if ("println".equals(method)) {
					serviceV2.println(message, timestamp);
				} else {
					log.error("receive: Unsupported method " + method);
				}
			} else {
				log.error("receive: Unsupported version " + routingKey);
			}

		}

	}

}
