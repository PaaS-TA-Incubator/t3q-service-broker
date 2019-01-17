package org.openpass.servicebroker.t3q.exception;

import org.openpaas.servicebroker.exception.ServiceBrokerException;

public class T3QServiceInstanceException extends ServiceBrokerException{

	private static final long serialVersionUID = 1L;
	
	private int statusCode;
	private String codeMessage;
	
	public T3QServiceInstanceException(int status, String message) {
		super(message);
		
		this.statusCode = status;
		this.codeMessage = message;
	}
	
	public T3QServiceInstanceException(String message) {
		super(message);
		this.codeMessage = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getCodeMessage() {
		return codeMessage;
	}

	public void setCodeMessage(String codeMessage) {
		this.codeMessage = codeMessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}