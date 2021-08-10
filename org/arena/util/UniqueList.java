package org.arena.util;

import java.util.Collection;

public class UniqueList<E> extends InstantList<E> {

	private static final long serialVersionUID = 2L;
	
	@SafeVarargs
	public UniqueList(E... items) {
		this.addAll(items);
	}
	
	
	public UniqueList(Collection<E> list) {
		addAll(list);
	}
	
	
	@Override
	public boolean add(E e) {
		if (!this.contains(e))
			return super.add(e);
	
		return false;
	}
	
	
	@Override
	public void add(int index, E e) {
		if (!this.contains(e)) {
			super.add(index, e);
		} else {
			int oindex = indexOf(e);
			remove(e);
			int newIndex = oindex < index ? index+1 : index;
			if (newIndex >= size())
				add(e);
			else add(newIndex, e);
		}
	}
	
	
	@Override 
	public boolean addAll(Collection<? extends E> c) {
		boolean someAdded = false;
		for (E e : c) {
			if (add(e)) 
				someAdded = true;
		}
		return someAdded;
	}
	
	
	@Override 
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean someAdded = false;
		int nextIndex = index;
		for (E e : c) {
			add(nextIndex, e);
			nextIndex = indexOf(e) + 1;
		}
		return someAdded;
	}
	
	
	public final String concat() {
		String concat = "";
		
		for (E e : this) 
			concat += e.toString();
		
		return concat;
	}


	public String concat(String string) {
		if (size() == 0) return "";
		String lead = get(0).toString();
		
		for (int i = 1; i < size(); i++) 
			lead += string + get(i);
		
		return lead;
	}
}
