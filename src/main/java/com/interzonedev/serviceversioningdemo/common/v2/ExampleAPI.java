package com.interzonedev.serviceversioningdemo.common.v2;

public interface ExampleAPI {

	public static final String VERSION = "v2";

	public void print(String message, boolean timestamp);

	public void println(String message, boolean timestamp);

}
