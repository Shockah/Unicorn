package pl.shockah.unicorn.ease;

import javax.annotation.Nonnull;

public abstract class Easing {
	@Nonnull public static final Easing linear = new Easing() {
		@Override
		public float ease(float f) {
			return f;
		}
	};

	public final float ease(float a, float b, float f) {
		return a + ease(f) * (b - a);
	}

	public final <T extends Easable<T>> T ease(@Nonnull T a, @Nonnull T b, float f) {
		return a.ease(b, ease(f));
	}

	public abstract float ease(float f);
}