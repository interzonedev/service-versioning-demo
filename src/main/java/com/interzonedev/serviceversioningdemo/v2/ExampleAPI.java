package com.interzonedev.serviceversioningdemo.v2;

public interface ExampleAPI extends com.interzonedev.serviceversioningdemo.ExampleAPI {

	public static final String VERSION = "v2";

	public void print(String message, boolean timestamp);

	public void println(String message, boolean timestamp);

}
