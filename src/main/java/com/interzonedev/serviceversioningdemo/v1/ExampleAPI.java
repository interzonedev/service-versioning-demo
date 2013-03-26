package com.interzonedev.serviceversioningdemo.v1;

public interface ExampleAPI extends com.interzonedev.serviceversioningdemo.ExampleAPI {

	public static final String VERSION = "v1";

	public void print(String message);

	public void println(String message);

}
