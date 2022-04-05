package org.arena.util;

import java.util.List;

public class LabeledList<L,T> extends ArenaList<T> {
	
	private static final long serialVersionUID = 6977475071832591333L;
	
	L labelObj;
	String label;

	
	public LabeledList() {
		super();
	}

	
	public LabeledList(L label, T item) {
		super(item);
		setLabel(label);
	}
	
	
	@SafeVarargs
	public LabeledList(T... list) {
		super(list);
	}
	
	
	public LabeledList(List<T> list) {
		super(list);
	}
	
	
	public LabeledList(L label) {
		super();
		setLabel(label);
	}
	
	
	@SafeVarargs
	public LabeledList(L label, T... list) {
		this(list);
		setLabel(label);
	}
	
	
	public LabeledList(L label, List<T> list) {
		this(list);
		setLabel(label);
	}
	
	
	public void setLabel(L label) {
		labelObj = label;
		if (labelObj != null)
			this.label = label.toString();
	}
	
	
	public boolean hasLabel() {
		return label != null;
	}
	
	
	public String getLabel() {
		return label;
	}
	
	
	public Object getObject() {
		return labelObj;
	}
	
	
	@Override
	public LabeledList<L,T> clone() {
		LabeledList<L,T> clone = new LabeledList<>(labelObj, this);
		return clone;
	}
	
	
	@Override
	public String toString() {
		String label = this.label == null ? "" : this.label;
		return label + super.toString();
	}
}
