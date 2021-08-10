package org.arena.util;

public class Point<X, Y> {
	protected X x;
	protected Y y;
	
	
	public Point(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	
	public void set(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public final String toString() {
		return "{ " + x.toString() + ", " + y.toString() + " }";
	}
}
