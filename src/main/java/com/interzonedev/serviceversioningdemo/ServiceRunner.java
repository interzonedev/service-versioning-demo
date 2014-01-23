package com.interzonedev.serviceversioningdemo;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	public static void main(String[] args) {

		final ServiceInvoker invoker = new ServiceInvoker();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					log.info("main: Shutting down");
					invoker.destroy();
					log.info("main: Shut down");
				} catch (IOException ioe) {
					log.error("main: Error destroying invoker", ioe);
				}
			}
		});

		try {
			log.info("main: Initializing invoker");
			invoker.init();
			log.info("main: Starting invoker");
			invoker.receive();
			log.info("main: Invoker completed");
		} catch (Exception e) {
			log.error("main: Error running invoker", e);
		}

		System.exit(0);

	}

}
