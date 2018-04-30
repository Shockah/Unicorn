package pl.shockah.unicorn;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.func.Action1;

@EqualsAndHashCode
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
	public String toString() {
		return String.format("[Box: %s]", value);
	}
	
	public void on(Action1<E> f) {
		if (value != null)
			f.call(value);
	}
}