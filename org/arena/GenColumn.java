package org.arena.table;

import java.util.List;

import org.arena.util.LabeledList;

public class GenColumn<T> extends LabeledList<T>{

	private static final long serialVersionUID = 7415164315062792530L;


	public GenColumn() {
		super();
	}

	
	public GenColumn(Object key) {
		super(key);
	}
	
	
	@SafeVarargs
	public GenColumn(T... column) {
		super(column);
	}
	
	
	public GenColumn(List<T> column) {
		super(column);
	}
	
	
	@SafeVarargs
	public GenColumn(Object key, T... column) {
		super(key, column);
	}
	
	
	public GenColumn(Object key, List<T> column) {
		super(key, column);
	}
}