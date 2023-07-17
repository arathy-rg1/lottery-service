package com.bynder.exception;

public class MissingMandatoryParamException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingMandatoryParamException(String message) {
		super(message);
	}

}
