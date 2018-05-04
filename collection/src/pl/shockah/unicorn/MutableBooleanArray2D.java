package pl.shockah.unicorn;

public class MutableBooleanArray2D extends BooleanArray2D {
	public MutableBooleanArray2D(int width, int height) {
		super(width, height);
	}

	public void set(int x, int y, boolean value) {
		array[getIndex(x, y)] = value;
	}
}