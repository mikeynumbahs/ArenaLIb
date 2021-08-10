package org.arena.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class InstantList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -150344152203432234L;

	public InstantList() {
		super();
	}
	
	@SafeVarargs
	public InstantList(E... items) {
		this.addAll(items);
	}
	
	
	public InstantList(Collection<E> list) {
		addAll(list);
	}
	
	
	@SafeVarargs
	public final void addAll(E... items) {
		for (E e : items) 
			super.add(e);
	}
	
	
	@SafeVarargs
	public final void addAll(int index, E... items) {
		super.addAll(index, Arrays.asList(items));
	}
}
