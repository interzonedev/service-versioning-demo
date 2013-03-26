package com.interzonedev.serviceversioningdemo;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	public static void main(String[] args) throws ShutdownSignalException, ConsumerCancelledException, IOException,
			InterruptedException {

		final com.interzonedev.serviceversioningdemo.v1.ExampleService serviceV1 = new com.interzonedev.serviceversioningdemo.v1.ExampleService();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					serviceV1.destroy();
				} catch (IOException ioe) {
					log.error("main: Error destroying service", ioe);
				}
			}
		});

		serviceV1.init();

	}

}
