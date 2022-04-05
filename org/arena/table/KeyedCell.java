package org.arena.table;

public class KeyedCell<T> {
	public final T value;
	public final String colKey;
	public final String rowKey;
	
	
	
	public KeyedCell(T value, Object colKey, Object rowKey) {
		this.value = value;
		this.colKey = colKey == null ? null : colKey.toString();
		this.rowKey = rowKey == null ? null : rowKey.toString();
	}
	
	
	public KeyedCell(T value, String colKey, String rowKey) {
		this.value = value;
		this.colKey = colKey == null ? null : colKey;
		this.rowKey = rowKey == null ? null : rowKey;
	}
}
