package com.n26.exceptions;

public class OutdatedTransactionException extends Exception {
	private static final long serialVersionUID = 1L;

	public OutdatedTransactionException(final String message) {
		super(message);
	}
}
