package pl.shockah.unicorn.ease;

import javax.annotation.Nonnull;

public class SmoothstepEasing extends Easing {
	@Nonnull public static final SmoothstepEasing smoothstep = new SmoothstepEasing();
	@Nonnull public static final SmoothstepEasing smoothstep2 = new SmoothstepEasing() {
		@Override
		public float ease(float f) {
			f = super.ease(f);
			return f * f;
		}
	};
	@Nonnull public static final SmoothstepEasing smoothstep3 = new SmoothstepEasing() {
		@Override
		public float ease(float f) {
			f = super.ease(f);
			return f * f * f;
		}
	};

	@Override
	public float ease(float f) {
		return f * f * (3f - 2f * f);
	}
}