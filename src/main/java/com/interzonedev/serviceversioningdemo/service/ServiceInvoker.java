package com.interzonedev.serviceversioningdemo.service;

import com.interzonedev.serviceversioningdemo.common.Command;

public interface ServiceInvoker {

	public void invoke(Command command);

}
