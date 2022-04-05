package org.arena.util;

import java.lang.reflect.Array;
import java.util.Collection;

public class UniqueList<E> extends ArenaList<E> {

	private static final long serialVersionUID = 2L;
	
	
	public UniqueList(E item) {
		super();
		this.add(item);
	}
	
	
	@SafeVarargs
	public UniqueList(E... items) {
		super();
		this.addAll(items);
	}
	
	
	public UniqueList(Collection<E> list) {
		super();
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
		if (!contains(e)) {
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
	
	
	@Override
	public void addAll(@SuppressWarnings("unchecked") E... es) {
		for (E e : es) {
			add(e);
		}
	}
	
	
	@Override
	public boolean contains(Object o) {
		if (o.getClass().isArray()) {
			return containsArray(o);
		} else {
			return super.contains(o);
		}
	}
	
	
	private boolean containsArray(Object o) {
		int olen = Array.getLength(o);
		for (int i = 0; i < size(); i++) {
			Object inList = get(i);
			int len = Array.getLength(inList);
			if (olen == len) {
				int count = 0;
				for (int l = 0; l < len; l++) {
					if (Array.get(o, l).equals(Array.get(inList, l))) {
						count++;
					}
				}
				if (count == len) return true;
			}
		}
		return false;
	}
}