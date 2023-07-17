package com.bynder.exception;

public class EntityExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public EntityExistsException(String message) {
		super(message);
	}
}
