package org.arena.math;

import java.util.Collection;

import org.arena.util.InstantList;

public class NumberList<N extends Number> extends InstantList<N> {
	
	private static final long serialVersionUID = -7693987151231684822L;
	
	@SafeVarargs
	public NumberList(N... items) {
		this.addAll(items);
	}
	
	
	public NumberList(Collection<N> list) {
		addAll(list);
	}
}
