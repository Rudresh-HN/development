package com.aws.demo.exception;

public class DateNotProperException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DateNotProperException(String msg) {
		super(msg);
	}

}
