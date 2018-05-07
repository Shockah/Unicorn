package pl.shockah.unicorn.rand;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import lombok.experimental.Delegate;

public class Randomizer {
	@Delegate
	@Nonnull public final Random source;

	public Randomizer() {
		this(new Random());
	}

	public Randomizer(@Nonnull Random source) {
		this.source = source;
	}

	@Nonnull public IntGenerator getIntGenerator() {
		return new IntGenerator() {
			@Override
			public int generate() {
				return Randomizer.this.nextInt();
			}
		};
	}

	@Nonnull public IntGenerator getIntRangeGenerator(final int minimumInclusive, final int maximumInclusive) {
		return new IntGenerator() {
			@Override
			public int generate() {
				return Randomizer.this.nextInt(maximumInclusive - minimumInclusive + 1) + minimumInclusive;
			}
		};
	}

	@Nonnull public FloatGenerator getFloatGenerator() {
		return new FloatGenerator() {
			@Override
			public float generate() {
				return Randomizer.this.nextFloat();
			}
		};
	}

	@Nonnull public FloatGenerator getFloatRangeGenerator(final float minimum, final float maximum) {
		return new FloatGenerator() {
			@Override
			public float generate() {
				return Randomizer.this.nextFloat() * (maximum - minimum) + minimum;
			}
		};
	}

	@Nonnull public FloatGenerator getSquareFloatGenerator() {
		return new FloatGenerator() {
			@Override
			public float generate() {
				float baseF = Randomizer.this.nextFloat() * 2f - 1f;
				float sign = Math.signum(baseF);
				return baseF * baseF * sign;
			}
		};
	}

	@Nonnull public FloatGenerator getSquareFloatRangeGenerator(final float minimum, final float maximum) {
		final FloatGenerator generator = getSquareFloatGenerator();
		return new FloatGenerator() {
			@Override
			public float generate() {
				return (generator.generate() + 1f) * 0.5f * (maximum - minimum) + minimum;
			}
		};
	}

	@Nonnull public FloatGenerator getGaussianFloatGenerator() {
		return new FloatGenerator() {
			@Override
			public float generate() {
				return (float) Randomizer.this.nextGaussian();
			}
		};
	}

	@Nonnull public FloatGenerator getGaussianFloatRangeGenerator(final float minimum, final float maximum) {
		final FloatGenerator generator = getGaussianFloatGenerator();
		return new FloatGenerator() {
			@Override
			public float generate() {
				return (generator.generate() + 1f) * 0.5f * (maximum - minimum) + minimum;
			}
		};
	}

	@Nonnull public <T> Generator<T> getGenerator(@Nonnull final List<T> list) {
		return new Generator<T>() {
			@Nonnull
			@Override
			public T generate() {
				return Randomizer.this.getRandom(list);
			}
		};
	}

	@SafeVarargs
	@Nonnull public final <T> Generator<T> getGenerator(@Nonnull final T... array) {
		return new Generator<T>() {
			@Nonnull
			@Override
			public T generate() {
				return Randomizer.this.getRandom(array);
			}
		};
	}

	@Nonnull public final <T> T getRandom(@Nonnull List<T> list) {
		return list.get(source.nextInt(list.size()));
	}

	@SafeVarargs
	@Nonnull public final <T> T getRandom(@Nonnull T... array) {
		return array[source.nextInt(array.length)];
	}
}