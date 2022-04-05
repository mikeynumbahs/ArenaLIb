package org.arena.util;

public class FixedList<E> extends ArenaList<E> {
	
	private static final long serialVersionUID = -3187011674728026690L;
	
	private Long len = null;
	private boolean FIFO = true;
	
	
	public FixedList() {
		super();
	}
	
	
	public FixedList(boolean FIFO) {
		this();
		this.FIFO = FIFO;
	}
	
	
	public FixedList(int length, boolean FIFO) {
		this(FIFO);
		setLength((long)length);
	}
	
	
	public FixedList(Long length, boolean FIFO) {
		this(FIFO);
		this.len = length;
	}
	
	
	public final void setLength(int length) {
		setLength((long)length);
	}
	
	
	public final void setLength(Long length) {
		this.len = length;
		applyLen();
	}

	
	
	@Override 
	public final boolean add(E item) {
		boolean result = false;
		if (FIFO) {
			result = super.add(item);
		} else {
			super.add(0, item);
			result = first().equals(item);
		}
	
		applyLen();
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override 
	public final void addAll(E... items) {
		for (E e : items) {
			if (FIFO) {
				super.add(e);
			} else {
				super.add(0, e);
			}
		}
		
		applyLen();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public final void addAll(int index, E... items) {
		int ptr = index > size() ? size() : index;
		
		for (E e : items) {
			super.add(ptr, e);
			if (FIFO) ptr++;
		}
	
		applyLen();
	}

	
	private void applyLen() {
		if (len == null) 
			return;
		
		while (size() > len) {
			remove(FIFO ? first() : last());
		}
	}
}
