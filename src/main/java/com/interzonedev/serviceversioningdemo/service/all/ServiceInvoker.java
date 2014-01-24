package com.interzonedev.serviceversioningdemo.service.all;

import com.interzonedev.serviceversioningdemo.common.all.Command;

/**
 * Interface for calling service methods.
 * 
 * @author mmarkarian
 */
public interface ServiceInvoker {

	/**
	 * Invokes the appropriate service method with the arguments contained in the specified {@link Command}.
	 * 
	 * @param command A {@link Command} instance that contains the service method to call and the arguments to pass.
	 */
	public void invoke(Command command);

}
