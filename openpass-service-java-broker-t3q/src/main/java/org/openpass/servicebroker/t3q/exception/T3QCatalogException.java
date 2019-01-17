package org.openpass.servicebroker.t3q.exception;

import org.openpaas.servicebroker.exception.ServiceBrokerException;


public class T3QCatalogException extends ServiceBrokerException{

	private static final long serialVersionUID = 139013461006947252L;

	public T3QCatalogException(int status, String message) {
		super(message);
				
	}
	
	public T3QCatalogException(String message){
		super(message);
	}

}
