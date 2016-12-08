package io.shockah.util;

public final class Box<E> {
	public E value;
	
	public Box() {
		this(null);
	}
	
	public Box(E value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Box<?>))
			return false;
		Box<?> box = (Box<?>)obj;
		if (value == null)
			return box.value == null;
		return value.equals(box.value);
	}
	
	@Override
	public String toString() {
		return String.format("[Box: %s]", value);
	}
}