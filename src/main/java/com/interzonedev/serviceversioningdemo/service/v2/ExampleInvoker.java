package com.interzonedev.serviceversioningdemo.service.v2;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import com.interzonedev.serviceversioningdemo.common.all.Command;
import com.interzonedev.serviceversioningdemo.service.all.ServiceInvoker;

/**
 * Version 2 specific {@link ServiceInvoker} implementation for calling version service methods.
 * 
 * @author mmarkarian
 */
public class ExampleInvoker implements ServiceInvoker {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ExampleInvoker.class);

	private final ExampleService exampleService = new ExampleService();

	/**
	 * Invokes the appropriate version 2 service method with the arguments contained in the specified {@link Command}.
	 * 
	 * @param command A {@link Command} instance that contains the service method to call and the arguments to pass.
	 */
	@Override
	public void invoke(Command command) {

		log.debug("invoke: command = " + command);

		String method = command.getMethod();
		String message = command.getMessage();
		boolean timestamp = command.isTimestamp();

		if ("print".equals(method)) {
			exampleService.print(message, timestamp);
		} else if ("println".equals(method)) {
			exampleService.println(message, timestamp);
		} else {
			log.error("receive: Unsupported method " + method);
		}

	}

}
