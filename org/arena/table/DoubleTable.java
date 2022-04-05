package org.arena.table;

import java.util.List;

public class DoubleTable extends GenTable<Double, DoubleTable> {

	public DoubleTable() {
		this("Double Table");
	}
	
	
	public DoubleTable(String name) {
		super(new Double[][] {}, name);
	}
	
	
	public DoubleTable(Double[][] table) {
		super(table);
	}

	
	public DoubleTable(Double[][] table, String name) {
		super(table, name);
	}

	
	public DoubleTable(Double[][] table, boolean hasHeader) {
		super(table, hasHeader);
	}

	
	public DoubleTable(Double[][] table, String name, boolean hasHeader) {
		super(table, name, hasHeader);
	}

	
	public DoubleTable(List<Double[]> table) {
		super(table);
	}

	
	public DoubleTable(List<Double[]> table, String name) {
		super(table, name);
	}

	
	public DoubleTable(List<Double[]> table, boolean hasHeader) {
		super(table, hasHeader);
	}

	
	public DoubleTable(List<Double[]> table, String name, boolean hasHeader) {
		super(table, name, hasHeader);
	}

	
	public DoubleTable(DoubleTable genTable) {
		super(genTable);
	}
}
