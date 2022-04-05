package org.arena.util;

public class LinkedObjects<X, Y> {
	protected X x;
	protected Y y;
	
	
	public LinkedObjects(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	
	protected void set(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public final String toString() {
		return "{ " + x.toString() + ", " + y.toString() + " }";
	}
}
