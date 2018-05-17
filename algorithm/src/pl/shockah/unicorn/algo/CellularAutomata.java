package pl.shockah.unicorn.algo;

import pl.shockah.unicorn.collection.BooleanArray2D;
import pl.shockah.unicorn.collection.MutableBooleanArray2D;
import pl.shockah.unicorn.operation.AbstractOperation;

import javax.annotation.Nonnull;

public class CellularAutomata extends AbstractOperation<BooleanArray2D, BooleanArray2D> {
	@Nonnull public final Rule rule;
	public final int iterations;
	public final boolean outOfBoundsValue;

	public CellularAutomata(@Nonnull final BasicRule2 rule, int iterations, boolean outOfBoundsValue) {
		this(new BasicRule() {
			@Override
			public boolean getNewState(int x, int y, boolean previous, int neighbors) {
				return rule.getNewState(previous, neighbors);
			}
		}, iterations, outOfBoundsValue);
	}

	public CellularAutomata(@Nonnull final BasicRule rule, int iterations, boolean outOfBoundsValue) {
		this(new Rule() {
			@Override
			public boolean getNewState(int x, int y, @Nonnull Grid grid) {
				int neighbors = 0;
				for (int yy = -1; yy <= 1; yy++) {
					for (int xx = -1; xx <= 1; xx++) {
						if (xx == 0 && yy == 0)
							continue;
						if (grid.get(x + xx, y + yy))
							neighbors++;
					}
				}
				return rule.getNewState(x, y, grid.get(x, y), neighbors);
			}
		}, iterations, outOfBoundsValue);
	}

	public CellularAutomata(@Nonnull Rule rule, int iterations, boolean outOfBoundsValue) {
		this.rule = rule;
		this.iterations = iterations;
		this.outOfBoundsValue = outOfBoundsValue;
	}

	@Override
	@Nonnull protected BooleanArray2D execute(@Nonnull BooleanArray2D input) {
		BooleanArray2D previous = input;
		for (int i = 0; i < iterations; i++) {
			setProgress(1f * i / iterations);
			MutableBooleanArray2D current = new MutableBooleanArray2D(previous.width, previous.height);
			Grid grid = new Grid(previous, outOfBoundsValue);
			for (int y = 0; y < current.height; y++) {
				setProgress(1f * (i + 1f * y / current.height) / iterations);
				for (int x = 0; x < current.width; x++) {
					current.set(x, y, rule.getNewState(x, y, grid));
				}
			}
			previous = current;
		}
		return previous;
	}

	public static final class Grid {
		@Nonnull private final BooleanArray2D array;
		public boolean outOfBoundsValue;

		public Grid(@Nonnull BooleanArray2D array, boolean outOfBoundsValue) {
			this.array = array;
			this.outOfBoundsValue = outOfBoundsValue;
		}

		public boolean get(int x, int y) {
			if (x < 0 || x >= array.width)
				return outOfBoundsValue;
			if (y < 0 || y >= array.height)
				return outOfBoundsValue;
			return array.get(x, y);
		}
	}

	public interface Rule {
		boolean getNewState(int x, int y, @Nonnull Grid grid);
	}

	public interface BasicRule {
		boolean getNewState(int x, int y, boolean previous, int neighbors);
	}

	public interface BasicRule2 {
		boolean getNewState(boolean previous, int neighbors);
	}
}