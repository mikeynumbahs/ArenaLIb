package org.arena.table;

import java.util.List;

public class StringTable extends GenTable<String, StringTable> {

	public StringTable() {
		super(new String[][] {});
	}
	
	
	public StringTable(String[][] table) {
		super(table);
	}

	
	public StringTable(String[][] table, String name) {
		super(table, name);
	}

	
	public StringTable(String[][] table, boolean hasHeader) {
		super(table, hasHeader);
	}

	
	public StringTable(String[][] table, String name, boolean hasHeader) {
		super(table, name, hasHeader);
	}

	
	public StringTable(List<String[]> table) {
		super(table);
	}

	
	public StringTable(List<String[]> table, String name) {
		super(table, name);
	}

	
	public StringTable(List<String[]> table, boolean hasHeader) {
		super(table, hasHeader);
	}

	
	public StringTable(List<String[]> table, String name, boolean hasHeader) {
		super(table, name, hasHeader);
	}

	
	public StringTable(String name) {
		super(new String[][] {}, name);
	}
	
	
	public StringTable(StringTable genTable) {
		super(genTable);
	}
}
