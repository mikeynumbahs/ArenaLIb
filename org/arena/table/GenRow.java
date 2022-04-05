package org.arena.table;

import java.util.List;

import org.arena.util.LabeledList;

public class GenRow<T> extends LabeledList<Object, T> {
	
	private static final long serialVersionUID = 4656659669541878424L;


	public GenRow() {
		super();
	}

	
	@SafeVarargs
	public GenRow(T... row) {
		super(row);
	}
	
	
	@SafeVarargs
	public GenRow(Object key, T... row) {
		super(key, row);
	}
	
	
	public GenRow(List<T> row) {
		super(row);
	}
	
	
	public GenRow(Object key, List<T> row) {
		super(key, row);
	}
}