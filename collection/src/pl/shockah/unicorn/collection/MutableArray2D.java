package pl.shockah.unicorn.collection;

import java.util.Arrays;

import javax.annotation.Nonnull;

public class MutableArray2D<T> extends Array2D<T> {
	public MutableArray2D(@Nonnull Class<? extends T> clazz, int width, int height) {
		super(clazz, width, height);
	}

	@SuppressWarnings("unchecked")
	public MutableArray2D(int width, int height, @Nonnull T fillWith) {
		super((Class<? extends T>)fillWith.getClass(), width, height);
		Arrays.fill(array, fillWith);
	}

	public void set(int x, int y, T value) {
		array[getIndex(x, y)] = value;
	}

	public T computeIfAbsent(int x, int y, @Nonnull Generator<T> generator) {
		T value = get(x, y);
		if (value == null) {
			value = generator.compute(x, y);
			set(x, y, value);
		}
		return value;
	}

	public T getOrDefault(int x, int y, T defaultValue) {
		T value = get(x, y);
		return value != null ? value : defaultValue;
	}

	public interface Generator<T> {
		T compute(int x, int y);
	}
}