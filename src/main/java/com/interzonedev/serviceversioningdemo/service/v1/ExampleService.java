package com.interzonedev.serviceversioningdemo.service.v1;

import com.interzonedev.serviceversioningdemo.common.v1.ExampleAPI;

public class ExampleService implements ExampleAPI {

	@Override
	public void print(String message) {

		System.out.print("Version 1: message = " + message);

	}

	@Override
	public void println(String message) {

		System.out.println("Version 1: message = " + message);

	}

}
