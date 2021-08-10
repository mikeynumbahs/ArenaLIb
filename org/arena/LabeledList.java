package org.arena.util;

import java.util.List;

public class LabeledList<T> extends InstantList<T> {
	
	private static final long serialVersionUID = 6977475071832591333L;
	
	Object labelObj;
	String label;

	
	public LabeledList() {
		super();
	}

	
	@SafeVarargs
	public LabeledList(T... list) {
		super(list);
	}
	
	
	public LabeledList(List<T> list) {
		super(list);
	}
	
	
	public LabeledList(Object label) {
		super();
		setLabel(label);
	}
	
	
	@SafeVarargs
	public LabeledList(Object label, T... list) {
		this(list);
		setLabel(label);
	}
	
	
	public LabeledList(Object label, List<T> list) {
		this(list);
		setLabel(label);
	}
	
	
	public void setLabel(Object label) {
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
	public String toString() {
		String label = this.label == null ? "" : this.label;
		return label + super.toString();
	}
}
