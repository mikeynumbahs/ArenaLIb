package org.arena.table;

public class EmptyListException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EmptyListException() {
		this("Lists cannot be empty, when initiating a GenTable.");
	}

	public EmptyListException(String message) {
		super(message);
	}
}
