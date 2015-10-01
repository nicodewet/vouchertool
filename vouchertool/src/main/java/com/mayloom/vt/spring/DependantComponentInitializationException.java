package com.mayloom.vt.spring;

public class DependantComponentInitializationException extends RuntimeException {

	private static final long serialVersionUID = 6455932210555466387L;
	
	public DependantComponentInitializationException(String message) {
		super(message);
	}
	
	public DependantComponentInitializationException(String message, Throwable e) {
		super(message, e);
	}

}
