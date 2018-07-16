package pl.shockah.unicorn.collection;

import java.lang.reflect.Array;
import java.util.Iterator;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Array2D<T> implements Iterable<Array2D.Entry<T>> {
	public final int width;
	public final int height;
	public final int length;

	@Nonnull protected final T[] array;

	@SuppressWarnings("unchecked")
	public Array2D(@Nonnull Class<? extends T> clazz, int width, int height) {
		this.width = width;
		this.height = height;
		length = width * height;
		array = (T[])Array.newInstance(clazz, length);
	}

	@Override
	public String toString() {
		return String.format("[Array2D: %dx%d]", width, height);
	}

	protected final int getIndex(int x, int y) {
		if (x < 0 || x >= width)
			throw new ArrayIndexOutOfBoundsException(String.format("X argument out of the 0-%d bounds.", width - 1));
		if (y < 0 || y >= height)
			throw new ArrayIndexOutOfBoundsException(String.format("Y argument out of the 0-%d bounds.", height - 1));
		return y * width + x;
	}

	public T get(int x, int y) {
		return array[getIndex(x, y)];
	}

	@Override
	@Nonnull
	public Iterator<Entry<T>> iterator() {
		return new Iterator<Entry<T>>() {
			private int position = 0;

			@Override
			public boolean hasNext() {
				return position < array.length;
			}

			@Override
			public Entry<T> next() {
				int x = position % width;
				int y = position / width;
				T value = array[position++];
				return new Entry<>(x, y, value);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}

	@EqualsAndHashCode
	public static class Entry<T> {
		public final int x;

		public final int y;

		public final T value;

		public Entry(int x, int y, T value) {
			this.x = x;
			this.y = y;
			this.value = value;
		}
	}
}