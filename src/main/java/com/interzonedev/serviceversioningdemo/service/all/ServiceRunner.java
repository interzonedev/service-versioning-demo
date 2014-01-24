package com.interzonedev.serviceversioningdemo.service.all;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Listens for version 1 and 2 AMQP service requests and dispatches the message to the appropriate
 * {@link ServiceInvoker} implementation.
 * 
 * @author mmarkarian
 */
public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	/**
	 * Registry for version specific {@link ServiceInvoker} implementations.
	 */
	private final Map<String, ServiceInvoker> serviceInvokers = new HashMap<String, ServiceInvoker>();

	private ConnectionFactory factory;

	private Connection connection;

	private Channel channel;

	private QueueingConsumer consumer;

	/**
	 * Initializes this service runner without a whole lot of tuning or error handling. Registers version specific
	 * {@link ServiceInvoker} implementations, creates the AMQP factory, connection and channel. Creates and binds the
	 * necessary exchange, queue and consumer.
	 * 
	 * @throws IOException Thrown if there was an error setting up the AMQP message binding and consuming components.
	 */
	public void init() throws IOException {

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

	/**
	 * Closes the AMQP channel and connection.
	 * 
	 * @throws IOException Thrown if there was an error closing the AMQP channel or connection.
	 */
	public void shutdown() throws IOException {

		log.info("shutdown: Shut down service runner");

		channel.close();
		connection.close();

		log.info("shutdown: Shut down service runner");

	}

	/**
	 * Runs indefinitely until the JVM is shut down, blocking until an AMQP request in received. Gets the
	 * {@link Command} from the message body and the service version from the request headers and calls the appropriate
	 * {@link ServiceInvoker} implmentation.
	 * 
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void receive() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException,
			ClassNotFoundException, IOException {

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

	/**
	 * Deserializes the message body contained in the specified {@link QueueingConsumer.Delivery} into a {@link Command}
	 * instance.
	 * 
	 * @param delivery The {@link QueueingConsumer.Delivery} received from the consumer for the request.
	 * 
	 * @return Returns a {@link Command} instance deserialized from the body of the specified
	 *         {@link QueueingConsumer.Delivery}.
	 * 
	 * @throws IOException Thrown if there was an error deserializing the message body.
	 * @throws ClassNotFoundException Thrown if there was an error deserializing the message body.
	 */
	private Command getCommand(QueueingConsumer.Delivery delivery) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(delivery.getBody());
			ois = new ObjectInputStream(bais);
			return (Command) ois.readObject();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException ioe) {
				log.error("getCommand: Error closing object input stream", ioe);
			}
		}
	}

	/**
	 * Gets the service version value from the {@link ExampleAMQP#VERSION_HEADER_NAME} header in the properties in the
	 * specified {@link QueueingConsumer.Delivery}.
	 * 
	 * @param delivery The {@link QueueingConsumer.Delivery} received from the consumer for the request.
	 * 
	 * @return Returns the service version value set in the headers of the specified {@link QueueingConsumer.Delivery}.
	 */
	private String getVersion(QueueingConsumer.Delivery delivery) {
		Map<String, Object> headers = delivery.getProperties().getHeaders();
		String version = headers.get(ExampleAMQP.VERSION_HEADER_NAME).toString();
		return version;
	}

	/**
	 * Launches an instance of this service runner and listents for AMQP requests. Blocks and services requests
	 * indefinitely until the JVM is shut down. Adds a JVM shutdown hook to gracefully shutdown the service runner.
	 * 
	 * @param args The array of arguments passed in from the command line. Currently unused.
	 */
	public static void main(String[] args) {

		final ServiceRunner serviceRunner = new ServiceRunner();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					log.info("main: Shutting down service runner");
					serviceRunner.shutdown();
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
