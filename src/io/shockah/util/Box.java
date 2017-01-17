package io.shockah.util;

import io.shockah.util.func.Action1;

public final class Box<E> {
	public E value;
	
	public Box() {
		this(null);
	}
	
	public Box(E value) {
		this.value = value;
	}
	
	public static <E> Box<E> of(E value) {
		return new Box<>(value);
	}
	
	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
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
	
	public void on(Action1<E> f) {
		if (value != null)
			f.call(value);
	}
}