package pl.shockah.unicorn.collection;

import java.util.Iterator;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BooleanArray2D implements Iterable<Array2D.Entry<Boolean>> {
	public final int width;
	public final int height;
	public final int length;

	@Nonnull protected final boolean[] array;

	public BooleanArray2D(int width, int height) {
		this.width = width;
		this.height = height;
		length = width * height;
		array = new boolean[length];
	}

	@Override
	public String toString() {
		return String.format("[BooleanArray2D: %dx%d]", width, height);
	}

	protected final int getIndex(int x, int y) {
		if (x < 0 || x >= width)
			throw new ArrayIndexOutOfBoundsException(String.format("X argument out of the 0-%d bounds.", width - 1));
		if (y < 0 || y >= height)
			throw new ArrayIndexOutOfBoundsException(String.format("Y argument out of the 0-%d bounds.", height - 1));
		return y * width + x;
	}

	public boolean get(int x, int y) {
		return array[getIndex(x, y)];
	}

	@Override
	@Nonnull
	public Iterator<Array2D.Entry<Boolean>> iterator() {
		return new Iterator<Array2D.Entry<Boolean>>() {
			private int position = 0;

			@Override
			public boolean hasNext() {
				return position < array.length;
			}

			@Override
			public Array2D.Entry<Boolean> next() {
				int x = position % width;
				int y = position / width;
				boolean value = array[position++];
				return new Array2D.Entry<>(x, y, value);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
		};
	}
}