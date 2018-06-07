package pl.shockah.unicorn.collection;

import java.lang.reflect.Array;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.Primitives;

@EqualsAndHashCode
public class Array1D<T> {
	public final int length;

	@Nonnull
	protected final T[] array;

	@SuppressWarnings("unchecked")
	public Array1D(@Nonnull T[] javaArray) {
		this((Class<? extends T>)Primitives.getGenericClass(javaArray.getClass().getComponentType()), javaArray.length);
		System.arraycopy(javaArray, 0, array, 0, javaArray.length);
	}

	@SuppressWarnings("unchecked")
	public Array1D(@Nonnull Class<? extends T> clazz, int length) {
		this.length = length;
		array = (T[])Array.newInstance(clazz, length);
	}

	@Override
	public String toString() {
		return String.format("[Array1D: %d]", length);
	}

	public T get(int index) {
		return array[index];
	}
}