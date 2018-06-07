package pl.shockah.unicorn.collection;

import java.util.Arrays;

import javax.annotation.Nonnull;

public class MutableArray1D<T> extends Array1D<T> {
	public MutableArray1D(@Nonnull T[] javaArray) {
		super(javaArray);
	}

	public MutableArray1D(@Nonnull Class<? extends T> clazz, int length) {
		super(clazz, length);
	}

	@SuppressWarnings("unchecked")
	public MutableArray1D(int length, @Nonnull T fillWith) {
		super((Class<? extends T>)fillWith.getClass(), length);
		Arrays.fill(array, fillWith);
	}

	public void set(int index, T value) {
		array[index] = value;
	}

	public T computeIfAbsent(int index, @Nonnull Generator<T> generator) {
		T value = get(index);
		if (value == null) {
			value = generator.compute(index);
			set(index, value);
		}
		return value;
	}

	public T getOrDefault(int index, T defaultValue) {
		T value = get(index);
		return value != null ? value : defaultValue;
	}

	public interface Generator<T> {
		T compute(int index);
	}
}