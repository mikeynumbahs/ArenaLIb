package org.arena.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -150344152203432234L;
	private Comparator<E> comp = (a,b) -> a.toString().compareTo(b.toString());
	
	public ArenaList() {
		super();
	}
	
	
	public ArenaList(E item) {
		this();
		this.add(item);
	}
	
	
	@SafeVarargs
	public ArenaList(E... items) {
		this();
		this.addAll(items);
	}
	
	
	public ArenaList(Collection<E> list) {
		this();
		addAll(list);
	}
	

	@SuppressWarnings("unchecked")
	public void addAll(E... items) {
		for (E e : items) 
			super.add(e);
	}
	
	
	@SuppressWarnings("unchecked")
	public void addAll(int index, E... items) {
		super.addAll(index, Arrays.asList(items));
	}
	
	
	public final E find(Object obj) {
		for (E e : this) {
			if (e.equals(obj))
				return e;
		}
		return null;
	}
	
	
	public final E first() {
		return get(0);
	}
	
	
	public final E last() {
		return get(this.size()-1);
	}
	
	
	public final void setComparator(Comparator<E> comp) {
		this.comp = comp;
	}
	
	
	public final void sort() {
		this.sort(comp);
	}
	
	
	public final String join() {
		return join("");
	}


	public String join(String string) {
		if (size() == 0) return "";
		String lead = get(0).toString();
		
		for (int i = 1; i < size(); i++) 
			lead += string + get(i);
		
		return lead;
	}
	
	
	public ArenaList<E> removeNull() {
		this.removeIf(e -> e == null);
		return this;
	}
	
	
	public E removeLast() {
		E last = last();
		remove(last);
		return last;
	}
	
	
	@Override
	public ArenaList<E> subList(int inc, int exl) {
		return new ArenaList<E>(super.subList(inc, exl));
	}
	
	
	public ArenaList<E> clone() {
		ArenaList<E> clone = new ArenaList<>(subList(0, size()));
		return clone;
	}
	
	
	public E mode() {
		if (this.size() == 0) return null;
		if (this.size() == 1) return get(0);
		
		List<E> distinct = stream().distinct().collect(Collectors.toList());
		final List<Long> counts = new ArrayList<>();
		distinct.forEach(e -> {
			counts.add(stream().filter(f -> f.equals(e)).count());
		});

		final Long max = counts.stream().max(Long::compare).get();
		int index = counts.indexOf(max);
		
		return counts.stream().filter(e -> e == max).count() > 1 ? null : distinct.get(index);
	}
}
