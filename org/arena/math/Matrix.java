package org.arena.math;

public class Matrix {
	final Long[][] matrix;
	final int size;
	
	public Matrix(int size) {
		this(size, 0L, false);
	}


	public Matrix(int size, Long init) {
		this(size, init, false);
	}

	
	public Matrix(int size, Long init, boolean identity) {
		matrix = new Long[size][size];
		this.size = size;
		if (identity) 
			mkIdentity();
		else init(init);
	}
	
	
	public Matrix(Long[][] matrix) throws Exception {
		if (matrix.length != matrix[0].length)
			throw new Exception("Matrix must be NxN");
		this.matrix = matrix;
		this.size = matrix.length;
	}
	
	
	public void init() {
		init(0L);
	}
	
	
	public void init(Long initial) {
		for (int i = 0; i < size; i++) {
			for (int k = 0; k < size; k++) {
				matrix[i][k] = (Long)initial;
			}
		}
	}
	
	
	public void mkIdentity() {
		for (int i = 0; i < size; i++) {
			for (int k = 0; k < size; k++) {
				matrix[i][k] = i == k ? 1L : 0L;
			}
		}
	}
	
	
	public Long get(int row, int column) {
		return matrix[row][column];
	}
	
	
	public void set(int row, int column, Long val) {
		matrix[row][column] = val;
	}
	
	
	public Long[] getTrace() {
		Long[] trace = new Long[size];
		for (int i = 0; i < size; i++) 
			trace[i] = matrix[i][i];
		
		return trace;
	}
}
