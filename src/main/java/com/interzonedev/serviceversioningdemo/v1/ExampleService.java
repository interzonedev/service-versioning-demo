package com.interzonedev.serviceversioningdemo.v1;

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
