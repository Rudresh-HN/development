package com.s3.exception;

public class S3RuntimeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public S3RuntimeException(Exception exception) {
		super(exception);
	}
	
	public S3RuntimeException(String message) {
		super(message);
	}

}
