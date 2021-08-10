package org.arena.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.arena.io.Console;

public class GenTable<T> {
	protected List<List<T>> table = new ArrayList<>();
	protected List<GenTable<T>> innerTables = new ArrayList<>();
	
	private List<Integer> defColMap = new ArrayList<>();
	private List<Integer> defRowMap = new ArrayList<>();
	
	private HashMap<Object, String> colKeys = new HashMap<>();
	private HashMap<Object, String> rowKeys = new HashMap<>();
	
	private int colId = 0;
	private int rowId = 0;
	private int width = 0;
	
	private String name = "";
	private String type = "";
	
	private String lastRowKey = null;
	private int lastRowIndex = 0;
	
	private String lastColKey = null;
	private int lastColIndex = 0;
	
	public GenTable<T> parent = null;
	
	
	public GenTable(T[][] table) {
		this(table, "", false);
	}
	
	
	public GenTable(T[][] table, String name) {
		this(table, name, false);
	}
	
	
	public GenTable(T[][] table, boolean hasHeader) {
		this(table, "", false);
	}
	
	
	public GenTable(T[][] table, String name, boolean hasHeader) {
		setTable(table);
		setName(name);

		if (hasHeader) 
			setColKeys(this.table.remove(0));
	}
	
	
	public GenTable(List<T[]> table) {
		this(table, "", false);
	}
	
	
	public GenTable(List<T[]> table, String name) {
		this(table, name, false);
	}
	
	
	public GenTable(List<T[]> table, boolean hasHeader) {
		this(table, "", hasHeader);
	}
	
	
	public GenTable(List<T[]> table, String name, boolean hasHeader) {
		setTable(table, hasHeader);
		setName(name);
	}

	
	public final GenTable<T> setTable(List<T[]> table) {
		return setTable(table, false);
	}
	
	
	public final GenTable<T> setTable(List<T[]> table, boolean hasHeader) {
		this.table.clear();
		if (table.size() == 0) return this;
		
		this.table = makeList(table);
		initKeys();
		
		if (hasHeader) {
			setColKeys(this.table.remove(0));
		}
		return this;
	}
	
	
	public final GenTable<T> setTable(T[][] table) {
		return setTable(table, false);
	}
	
	
	public final GenTable<T> setTable(T[][] table, boolean hasHeader) {
		this.table.clear();
		
		this.table = makeList(table);
		initKeys();
		
		if (hasHeader) 
			setColKeys(this.table.remove(0));
		
		return this;
	}
	
	
	public final List<GenTable<T>> getAllTables() {
		List<GenTable<T>> all = new ArrayList<>();
		if (this.table.size() > 0)
			all.add(this);
		for (GenTable<T> child : innerTables) 
			all.addAll(child.getAllTables());
		return all;
	}
	
	
	public final String getType() {
		return type;
	}
	
	
	public final GenTable<T> importTable(Object[][] table, boolean hasHeader) {
		this.table.clear();
		this.table = makeList(convert(table));
		
		initKeys();
		if (hasHeader) 
			setColKeys(this.table.remove(0));
		return this;
	}
	
	
	public final GenTable<T> setName(String name) {
		this.name = name == null ? "" : name;
		return this;
	}
	
	
	public final String getName() {
		return name.isBlank() ? "<Unnamed>" : ""+name;
	}
	
	
	private void initKeys() {
		defRowMap.clear();
		defColMap.clear();
		rowKeys.clear();
		colKeys.clear();
		colId = 0;
		rowId = 0;
		
		for (int i = 0; i < table.size(); i++)
			defRowMap.add(rowId++);
		
		for (int i = 0; i < table.size(); i++) {
			for (int j = 0; j < table.get(i).size(); j++) {
				if (!defColMap.contains(j)) {
					defColMap.add(j);
					colId++;
				}
			}
		}
		
		width = colId;
	}
	
	
	public final GenRow<T> getRow(int index) {
		String key = rowKeys.get(defRowMap.get(index));
		GenRow<T> row = new GenRow<>(key, table.get(index));
		return row;
	}
	
	
	public final GenRow<T> remRow(int index) {
		String key = rowKeys.remove(defRowMap.get(index));
		GenRow<T> removed = new GenRow<>(key, table.remove(index));
		defRowMap.remove(index);
		
		if (index == lastRowIndex) {
			lastRowKey = null;
		} else {
			if (index < lastRowIndex)
				lastRowIndex--;
		}
		
		return removed;
	}
	
	
	public final GenColumn<T> getCol(int index) {
		String key = colKeys.get(defColMap.get(index));
		
		List<T> list = new ArrayList<>();
		for (int i = 0; i < table.size(); i++) {
			try {
				list.add(table.get(i).get(index));
			} catch (Exception e) {
				list.add(null);
			}
		}
		
		GenColumn<T> column = new GenColumn<>(key, list);
		return column;
	}
	
	
	public final GenColumn<T> remCol(int index) {
		List<T> removed = new ArrayList<>();
	
		for (int i = 0; i < table.size(); i++) {
			try {
				removed.add(table.get(i).remove(index));
			} catch (Exception nully) { removed.add(null); }
		}
		
		String key = colKeys.remove(defColMap.remove(index));
		
		if (index == lastColIndex) {
			lastRowKey = null;
		} else {
			if (index < lastColIndex)
				lastColIndex--;
		}
		width--;
		
		GenColumn<T> column = new GenColumn<>(key, removed);
		return column;
	}
	
	
	public final Boolean hasColKeys() {
		return colKeys.size() != 0;
	}
	
	
	public final GenTable<T> remColKeys() {
		colKeys.clear();
		lastColKey = null;
		return this;
	}
	
	
	public final List<String> getColKeys() {
		List<String> keys = new ArrayList<>();
		for (Integer defKey : defColMap) {
			String key = colKeys.get(defKey);
			keys.add(key == null ? "" : key);
		}
		return keys;
	}
	
	
	public final boolean setColKeys(Object[] mapTo) {
		colKeys.clear();
		for (int i = 0; i < mapTo.length; i++) {
			try {
				setColKey( mapTo[i].toString(), defColMap.get(i));
			} catch (Exception OOB) {}
		}
		
		return true;
	}
	
	
	public final boolean setColKeys(String[] mapTo) {
		colKeys.clear();
		for (int i = 0; i < mapTo.length; i++) {
			try {
				setColKey( mapTo[i], defColMap.get(i));
			} catch (Exception OOB) {
			}
		}
		
		return true;
	}
	
	
	public final boolean setColKeys(List<T> mapTo) {
		for (int i = 0; i < mapTo.size(); i++) {
			try {
				setColKey(mapTo.get(i).toString(), defColMap.get(i));
			} catch (Exception OOB) {}
		}
		
		return true;
	}
	
	
	public final boolean setColKey(Object mapTo, int col) {
		colKeys.put(defColMap.get(col), mapTo.toString());
		if (col == lastColIndex) lastColKey = null;
		return true;
	}
	
	
	public final boolean setRowKeys(Object[] mapTo) {
		rowKeys.clear();
		
		for (int i = 0; i < mapTo.length; i++) {
			try {
				setRowKey(mapTo[i].toString(), defRowMap.get(i));
			} catch (Exception OOB) {}
		}
		
		return true;
	}

	
	public final boolean setRowKeys(List<T> mapTo) {
		if (mapTo.size() != table.size()) 
			return false;
		
		for (int i = 0; i < mapTo.size(); i++) {
			setRowKey(mapTo.get(i).toString(), defRowMap.get(i));
		}
		
		return true;
	}

	
	public final boolean setRowKey(Object mapTo, int row) {
		rowKeys.put(defRowMap.get(row), mapTo.toString());
		if (lastRowIndex == row) lastRowKey = null;
		return true;
	}
	
	
	public final Boolean hasRowKeys() {
		return rowKeys.size() != 0;
	}
	
	
	public final GenTable<T> remRowKeys() {
		rowKeys.clear();
		lastRowKey = null; 
		return this;
	}
	
	
	public final List<String> getRowKeys() {
		List<String> keys = new ArrayList<>();
		for (Integer defKey : defRowMap) {
			String key = rowKeys.get(defKey);
			keys.add(key == null ? "" : key);
		}
		return keys;
	}
	
	
	public final Integer getLength() {
		return table.size();
	}
	
	
	public final Integer getWidth() {
		return width;
	}
	
	
	public final boolean insertCol(T[] column) {
		return insertCol(column, getWidth());
	}
	
	
	public final boolean insertCol(T[] column, int index) {
		return insertCol(column, null, index);
	}
	
	
	public final boolean insertCol(T[] column, String header) {
		return insertCol(column, header, getWidth());
	}
	
	
	public final boolean insertCol(T[] column, String header, int index) {
		GenColumn<T> col = new GenColumn<>(header, column);
		return insertCol(col, index);
	}
	
	
	public final boolean insertCol(GenColumn<T> col) {
		return insertCol(col, getWidth());
	}
	
	
	public final boolean insertCol(GenColumn<T> col, int index) {
		if (col.size() != table.size())
			return false;
	
		for (int i = 0; i < table.size(); i++) {
			try {
				table.get(i).add(index, col.get(i));
			} catch (Exception oob) {}
		}
		
		defColMap.add(index, colId++);
		if (col.hasLabel()) 
			colKeys.put(colId-1, col.getLabel());
		
		if (lastColKey != null)
			lastColIndex += index <= lastColIndex ? 1 : 0;
		width++;
		
		return true;
	}
	
	
	public final boolean insertCols(T[][] columns) {
		return insertCols(columns, this.getWidth());
	}
	
	
	public final boolean insertCols(T[][] columns, int index) {
		boolean success = true;
		
		int currentIndex = index + 0;
		for (T[] col : columns) {
			if (!insertCol(col, currentIndex++)) 
				success = false;
		}
		
		return success;
	}
	
	
	public final boolean insertCols(List<List<T>> columns) {
		return insertCols(columns, this.getWidth());
	}
	
	
	public final boolean insertCols(List<List<T>> columns, int index) {
		boolean success = true;
		
		int currentIndex = index + 0;
		for (List<T> col : columns) {
			GenColumn<T> genCol = new GenColumn<>(col);
			if (!insertCol(genCol, currentIndex++)) 
				success = false;
		}
		return success;
	}
	
	
	public final boolean insertRow(T[] row) {
		return insertRow(row, false);
	}

	
	public final boolean insertRow(T[] row, boolean force) {
		return insertRow(row, table.size(), force);
	}
	
	
	public final boolean insertRow(T[] row, int index) {
		return insertRow(new GenRow<T>(row), index);
	}
	
	
	public final boolean insertRow(T[] row, int index, boolean force) {
		return insertRow(new GenRow<T>(row), index, force);
	}
	
	
	public final boolean insertRow(GenRow<T> row) {
		return insertRow(row, table.size());
	}
	
	
	public final boolean insertRow(GenRow<T> row, boolean force) {
		return insertRow(row, table.size(), force);
	}
	
	
	public final boolean insertRow(GenRow<T> row, int index) {
			return insertRow(row, index, false);
	}
	
	
	public final boolean insertRow(GenRow<T> row, int index, boolean force) {
		if (width != 0 && !force) {
			if (index > table.size() || row.size() > width)
					return false;
		}
		
		table.add(index, row);
		
		if (force && row.size() > width) {
			while (defColMap.size() < row.size()) {
				defColMap.add(colId++);
			}
			width = defColMap.size();
		}
		
		//If table size equals 1 then this needs to be initialized
		if (table.size() == 1)
			initKeys();
		
		defRowMap.add(index, rowId++);
		if (row.hasLabel()) 
			rowKeys.put(rowId-1, row.getLabel());
		
		if (lastRowKey != null)
			lastRowIndex += index <= lastRowIndex ? 1 : 0;
		
		return true;
	}
	
	
	public final boolean insertRows(List<List<T>> rows) {
		return insertRows(rows, getLength());
	}
	
	
	public final boolean insertRows(List<List<T>> rows, int index) {
		if (index > table.size()) return false;
		
		boolean success = true;
		
		int currentIndex = index + 0;
		for (List<T> row : rows) {
			GenRow<T> genRow = new GenRow<T>(row);
			if (!insertRow(genRow, currentIndex++)) 
				success = false;
		}
		return success;
	}
	
	
	public final boolean insertRows(T[][] rows) {
		return insertRows(rows, getLength());
	}
	
	
	public final boolean insertRows(T[][] rows, int index) {
		if (index > table.size()) return false;
		
		boolean success = true;
		
		int currentIndex = index + 0;
		for (T[] row : rows) {
			if (!insertRow(row, currentIndex++)) 
				success = false;
		}
		return success;
	}
	
	
	public final T getCell(int row, int col) {
		return table.get(row).get(col);
	}
	
	
	public final T getCell(int row, String colKey) {
		return table.get(row).get(getColIndex(colKey));
	}
	
	
	public final T getCell(String rowKey, int col) {
		return table.get(getRowIndex(rowKey)).get(col);
	}
	
	
	public final T getCell(String rowKey, String colKey) {
		return table.get(getRowIndex(rowKey)).get(getColIndex(colKey));
	}


	public final void setCell(int row, int col, T value) {
		table.get(row).set(col, value);
	}
	
	
	public final void setCell(int row, String colKey, T value) {
		table.get(row).set(getColIndex(colKey), value);
	}
	
	
	public final void setCell(String rowKey, int col, T value) {
		table.get(getRowIndex(rowKey)).set(col, value);
	}
	
	
	public final void setCell(String rowKey, String colKey, T value) {
		table.get(getRowIndex(rowKey)).set(getColIndex(colKey), value);
	}
	
	
	@SafeVarargs
	public final T getCell(String colKey, KeyedCell<T>... where) {
		for (int i = 0; i < getLength(); i++) {
			boolean allMatch = true;
			for (int w = 0; w < where.length && allMatch; w++) {
				try {
					if (!getCell(i, where[w].colKey).equals(where[w].value))
						allMatch = false;
				} catch (Exception nully) {
					allMatch = false;
				}
			}
			
			if (allMatch)
				return table.get(i).get(getColIndex(colKey));
		}

		return null;
	}
	
	
	public final T getCell(String colKey, List<KeyedCell<T>> where) {
		for (int i = 0; i < getLength(); i++) {
			boolean allMatch = true;
			for (int w = 0; w < where.size() && allMatch; w++) {
				KeyedCell<T> kc = where.get(w);
				try {
					if (!getCell(i, kc.colKey).equals(kc.value)) 
						allMatch = false;
				} catch (Exception nully) {
					allMatch = false;
				}
			}
			
			if (allMatch)
				return table.get(i).get(getColIndex(colKey));
		}

		return null;
	}
	
	public final Integer getRowIndex(String rowKey) {
		if (rowKeys.size() == 0) return null;
		
		if (lastRowKey != null && lastRowKey.equals(rowKey)) 
			return lastRowIndex;
		
		Object[] keys = rowKeys.keySet().toArray();
		
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			if (rowKeys.get(key).equals(rowKey)) {
				lastRowKey = rowKey;
				lastRowIndex = defRowMap.indexOf(key);
				return lastRowIndex + 0;
			}
		}
		return null;
	}
	
	
	public final Integer getColIndex(String colKey) {
		if (colKeys.size() == 0) return null;
		
		if (lastColKey != null && lastColKey.equals(colKey)) 
			return lastColIndex;
		
		Object[] keys = colKeys.keySet().toArray();
	
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			if (colKeys.get(key).equals(colKey)) {
				lastColKey = colKey;
				lastColIndex = defColMap.indexOf(key);
				return lastColIndex + 0;
			}
		}
	
		return null;
	}
	
	
	public final KeyedCell<T> getKeyedCell(int index, String key) {
		return new KeyedCell<T>(getCell(index, key), key);
	}
	
	
	public final List<KeyedCell<T>> getKeyedCells(int index, String... keys) {
		List<KeyedCell<T>> list = new ArrayList<>();
		for (String key : keys) {
			list.add(getKeyedCell(index, key));
		}
		
		return list;
	}
	
	
	public final GenTable<T> addTable(GenTable<T> child) {
		return addTable(innerTables.size(), child);
	}
	
	
	public final GenTable<T> addTable(int index, GenTable<T> child) {
		innerTables.add(index, child);
		child.parent = this;
		return this;
	}
	
	
	public final Boolean hasChild() {
		return innerTables.size() > 0;
	}
	
	
	public final GenTable<T> clearChildren() {
		innerTables.clear();
		return this;
	}
	
	
	public final Integer getChildCount() {
		return innerTables.size();
	}
	
	
	public final List<GenTable<T>> getChildren() {
		return innerTables;
	}
	
	
	public final GenTable<T> getChildByName(String name) {
		for (GenTable<T> t : innerTables) {
			try {
				if (t.getName().equals(name)) return t;
				else {
					GenTable<T> table = t.getChildByName(name);
					if (table !=  null) return table;
				}
			} catch (Exception e) {}
		}
		return null;
	}
	
	
	public final Integer getTotalTableCount() {
		return getTotalChildCount()+1;
	}
	
	
	public final Integer getTotalChildCount() {
		int count = 0;
		for (GenTable<?> t : innerTables) {
			count += t.getTotalChildCount();
			count++;
		}
		return count;
	}
	
	
	public final GenTable<T> removeChildByIndex(int x) {
		if (x >= 0 && x < innerTables.size()) {
			GenTable<T> child = innerTables.remove(x);
			child.parent = null;
			return child;
		}
		return null;
	}
	
	
	public final GenTable<T> removeChildByName(String name) {
		for (int i = 0; i < innerTables.size(); i++) {
			GenTable<T> table = innerTables.get(i);
			if (table.getName().equals(name)) {
				return removeChildByIndex(i);
			} else if (table.hasChild()) {
				return table.removeChildByName(name);
			}
		}
		return null;
	}
	
	
	public final GenTable<T> removeChild(GenTable<T> child) {
		if (this.getChildren().remove(child))
			return child;
		return null;
	}
	
	
	public final String getTableType() {
		return ""+type;
	}
	
	
	public final GenTable<T> sortAsString() {
		return sortAsString(0);
	}
	
	
	public final GenTable<T> sortAsString(int colIndex) {
		return sort((a, b) -> a.toString().compareTo(b.toString()), colIndex);
	}
	
	
	public final GenTable<T> sort() {
		return sort(0);
	}
	
	
	public final GenTable<T> sort(int colIndex) {
		return sort(false, colIndex);
	}
	
	
	@SuppressWarnings("unchecked")
	public final GenTable<T> sort(boolean ascending, int colIndex) {
		return sort((a, b) -> ascending ? ((Comparable<T>)a).compareTo(b) 
										: ((Comparable<T>)b).compareTo(a), colIndex);
	}
	
	
	public final GenTable<T> sortAsString(String colKey) {
		return sortAsString(getColIndex(colKey));
	}
	
	
	public final GenTable<T> sort(String colKey) {
		return sort(false, getColIndex(colKey));
	}
	
	
	public final GenTable<T> sort(boolean ascending, String colKey) {
		return sort(ascending, getColIndex(colKey));
	}
	
	
	public final GenTable<T> sort(Comparator<T> comp, String colKey) {
		return sort(comp, getColIndex(colKey));
	}
	
	
	public final GenTable<T> sort(Comparator<T> comp, int colIndex) {
		HashMap<String, Integer> rowState = new HashMap<>();
		for (int i = 0; i < table.size(); i++) {
			rowState.put(table.get(i).toString(), defRowMap.get(i));
		}
		
		table.sort((a, b) -> comp.compare(a.get(colIndex), b.get(colIndex)));
		
		List<Integer> newRowHeader = new ArrayList<>();
		for (int i = 0; i < table.size(); i++) {
			newRowHeader.add(rowState.get(table.get(i).toString()));
		}
		
		defRowMap = newRowHeader;
		return this;
	}
	
	
	public final GenTable<T> reverse() {
		Collections.reverse(table);
		Collections.reverse(defRowMap);
		return this;
	}
	
	
	public final GenTable<T> wrapLeft(int steps) {
		if (steps % width == 0) return this;
		if (steps < 0) return wrapRight(-1*steps);
		
		int fixed = steps;
		if (fixed > width)
			fixed -= (steps/width)*width;
		
		for (int i = 0; i < fixed; i++) {
			GenColumn<T> col = remCol(0);
			insertCol(col);
		}
		return this;
	}
	
	
	public final GenTable<T> wrapRight(int steps) {
		if (steps % width == 0) return this;
		if (steps < 0) return wrapLeft(-1*steps);
		
		int fixed = steps;
		if (fixed > width)
			fixed -= (steps/width)*width;
		
		for (int i = 0; i < fixed; i++) {
			GenColumn<T> col = remCol(width - 1);
			insertCol(col, 0);
		}
		return this;
	}
	
	
	public final GenTable<T> wrapUp(int steps) {
		if (steps % table.size() == 0) return this;
		if (steps < 0) return wrapDown(-1*steps);
		
		int fixed = steps;
		if (fixed > table.size())
			fixed -= (steps/table.size())*table.size();
		
		for (int i = 0; i < fixed; i++) {
			GenRow<T> row = remRow(0);
			insertRow(row);
		}
		return this;
	}
	
	
	public final GenTable<T> wrapDown(int steps) {
		if (steps % table.size() == 0) return this;
		if (steps < 0) return wrapUp(-1*steps);
		
		int fixed = steps;
		if (fixed > table.size())
			fixed -= (steps/table.size())*table.size();
		
		for (int i = 0; i < fixed; i++) {
			GenRow<T> row = remRow(table.size()-1);
			insertRow(row, 0);
		}
		return this;
	}
	
	
	public final List<List<String>> toList() {
		return toList(0, table.size());
	}
	
	
	public final List<List<String>> toList(int start, int end) {
		List<List<String>> strTable = new ArrayList<>();
		
		if (hasColKeys()) {
			List<String> strHeader = new ArrayList<>();
			for (int i = 0; i < defColMap.size(); i++) {
				String headerItem = colKeys.get(defColMap.get(i));
				strHeader.add(headerItem);
			}
		}
		for (int i = start; i < end; i++) {
			List<String> row = new ArrayList<>();
			List<T> table_row = table.get(i);
			for (int j = 0; j < table_row.size(); j++) {
				row.add(table_row.get(j).toString());
			}
			strTable.add(row);
		}
		
		return strTable; 
	}
	
	
	public final String[][] toArray() {
		return toArray(0, table.size());
	}
	
	
	public final String[][] toArray(int start, int end) {
		String[][] temp = new String[end-start][];
		for (int i = start; i < end; i++) {
			temp[i-start] = getRow(i).toArray(new String[] {});
		}
		return temp;
	}
	
	
	@SuppressWarnings("unchecked")
	public final GenTable<T> extract(List<String> keys) {
		List<T[]> extract = new ArrayList<>();
		
		for (int i = 0; i < getLength(); i++) {
			List<T> cells = new ArrayList<>();
			for (String key : keys) {
				try {
					cells.add(getCell(i, key));
				} catch (Exception nully) {
					cells.add(null);
				}
			}

			extract.add((T[])cells.toArray());
		}
		
		GenTable<T> genTable = new GenTable<T>(extract, false);
		genTable.setColKeys(keys.toArray());
		return genTable;
	}
	
	
	public final void printTable() {
		if (table != null && table.size() > 0) {
			Console.println(getParentTreeLabel() + "::" + getName());
			
			List<Integer> widths = getColWidths(5);
			List<String> header = new ArrayList<>();
			//Console.println(widths);
			header.add("");
			for (int i = 0; i < defColMap.size(); i++) {
				String headerItem = colKeys.get(defColMap.get(i));
				if (headerItem == null) headerItem = ""+i;
				header.add(headerItem);
			}
			
			if (this.hasColKeys())
				Console.printf(getLineFormat(widths, header), header.toArray());
			
			for (int row = 0; row < table.size(); row++) {
				List<T> line = table.get(row);
				List<String> printLine = new ArrayList<>();
				String rowLabel = rowKeys.get(defRowMap.get(row));
				printLine.add(rowLabel == null ? ""+row : rowLabel);
				for (int i = 0; i < line.size(); i++) {
					printLine.add(line.get(i) == null ? "null" : line.get(i).toString());
				}
				Console.printf(getLineFormat(widths, printLine), printLine.toArray());
			}
		}
	
		if (getChildCount() > 0) 
			for (int i = 0; i < getChildCount(); i++) 
				innerTables.get(i).printTable();
	}
	
	
	public final List<String> getParentTree() {
		GenTable<?> temp = this.parent;
		List<String> parents = new ArrayList<>();
		
		while (temp != null) {
			parents.add(0, temp.getName());
			temp = temp.parent;
		}
		return parents;
	}
	
	
	public final String getParentTreeLabel() {
		List<String> parentTree = getParentTree();
		if (parentTree.size() == 0) return "";
		String label = "";
		for (String parentName : parentTree) {
			label += parentName + ".";
		}
		
		return label.substring(0, label.length()-1);
	}
	
	
	public final List<Integer> getColWidths() {
		return getColWidths(0);
	}
	
	
	public final List<Integer> getColWidths(int padding) {
		List<Integer> widths = new ArrayList<>();
		
		for (int i = 0; i < defColMap.size(); i++) {
			String headerItem = colKeys.get(defColMap.get(i));
			if (headerItem == null) headerItem = ""+i;
			widths.add(headerItem.length() + padding);
		}
		
		//DETERMINE WIDTHS FOR TABLE 
		for (int i = 0; i < table.size(); i++) {
			for (int w = 0; w < table.get(i).size(); w++) {
				if (widths.size() == w) 
					widths.add(0);
				
				T cell = table.get(i).get(w);
				String item = cell == null ? "" : cell.toString();
				int strWidth = item.length() + padding;
				if (widths.get(w) < strWidth)
					widths.set(w, strWidth);
			}
		}
		
		//LASTLY, GET WIDTHS FOR ROW HEADERS
		int vHeaderLen = 0;
		for (int i = 0; i < table.size(); i++) {
			String label = rowKeys.size() == 0 ? ""+i : rowKeys.get(defRowMap.get(i));
			int len = label == null ? padding : label.length() + padding;
			if (vHeaderLen < len)
				vHeaderLen = len;
		}
		
		//ADD ROW HEADER LENGTH TO TOP OF LIST
		widths.add(0, vHeaderLen);
		
		return widths;
	}
	
	
	private String getLineFormat(List<Integer> widths, List<String> line) {
		String format = "";
		for (int width = 0; width < line.size(); width++) 
			format += "%-"+widths.get(width)+"s ";
		format += "%n";
		
		return format;
	}
	
	
	public final List<List<T>> makeList(List<T[]> table) {
		List<List<T>> converted = new ArrayList<>();
		
		if (type.isEmpty()) 
			type = table.get(0).getClass().getSimpleName().replaceAll("\\[|\\]", "");
		
		for (T[] row : table) {
			List<T> newRow = new ArrayList<>();
			for (int i = 0; i < row.length; i++) 
				newRow.add(row[i]);
			converted.add(newRow);
		}
		return converted;
	}
	
	
	public final List<List<T>> makeList(T[][] table) {
		List<List<T>> converted = new ArrayList<>();
		
		if (type.isEmpty()) 
			type = table.getClass().getSimpleName().replaceAll("\\[|\\]", "");
		
		for (T[] row : table) {
			List<T> newRow = new ArrayList<>();
			for (int i = 0; i < row.length; i++) 
				newRow.add(row[i]);
			converted.add(newRow);
		}
		return converted;
	}
	
	
	@SuppressWarnings("unchecked")
	public final T[][] convert(Object[][] toConvert) {
		switch (type) {
			case "String" : return (T[][]) convToString(toConvert);
			case "Float" : return (T[][]) convToFloat(toConvert);
			case "Double" : return  (T[][]) convToDouble(toConvert);
			case "Integer" : return  (T[][]) convToInteger(toConvert);
		}
		return null;
	}
	
	
	public final String[][] convToString(Object[][] toConvert) {
		String[][] conv = new String[toConvert.length][toConvert[0].length];
		
		for (int i = 0; i < toConvert.length; i++) {
			for (int r = 0; r < toConvert[i].length; r++) {
				conv[i][r] = toConvert[i][r].toString();
			}
		}
		return conv;
	}
	
	
	public final Double[][] convToDouble(Object[][] toConvert) {
		Double[][] conv = new Double[toConvert.length][toConvert[0].length];
		
		for (int i = 0; i < toConvert.length; i++) {
			for (int r = 0; r < toConvert[i].length; r++) {
				conv[i][r] = Double.valueOf(toConvert[i][r].toString());
			}
		}
		return conv;
	}
	
	
	public final Float[][] convToFloat(Object[][] toConvert) {
		Float[][] conv = new Float[toConvert.length][toConvert[0].length];
		
		for (int i = 0; i < toConvert.length; i++) {
			for (int r = 0; r < toConvert[i].length; r++) {
				conv[i][r] = Float.valueOf(toConvert[i][r].toString());
			}
		}
		return conv;
	}
	
	
	public final Integer[][] convToInteger(Object[][] toConvert) {;
		Integer[][] conv = new Integer[toConvert.length][toConvert[0].length];
		
		for (int i = 0; i < toConvert.length; i++) {
			for (int r = 0; r < toConvert[i].length; r++) {
				conv[i][r] = Integer.valueOf(toConvert[i][r].toString());
			}
		}
		return conv;
	}
	
	
	@SuppressWarnings("unchecked")
	public T cast(Object obj) {
		if (obj == null) return null;
		
		String conv = obj.toString();
		switch (type) {
			case "Double" :
				return (T)Double.valueOf(conv);
			case "Integer" :
				return (T)Integer.valueOf(conv);
			case "Float" :
				return (T)Float.valueOf(conv);
			case "Number" :
				return (T)Double.valueOf(conv);
			default:
				return (T)conv;
		}
	}
}
