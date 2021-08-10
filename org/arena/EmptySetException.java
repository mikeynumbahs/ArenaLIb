package org.arena.math;

import java.util.List;

public class EmptySetException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmptySetException() {
		super("One or more sets were empty.");
	}

	
	public EmptySetException(List<String> emptySets) {
		super("Empty sets for: " + emptySets);
	}
}
