package org.arena.table;

public class KeyedCell<T> {
	public final T value;
	public final String colKey;
	
	
	public KeyedCell(T value, String colKey) {
		this.value = value;
		this.colKey = colKey;
	}
}
