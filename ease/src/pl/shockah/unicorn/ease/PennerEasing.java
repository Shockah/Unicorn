package pl.shockah.unicorn.ease;

import javax.annotation.Nonnull;

public abstract class PennerEasing extends Easing {
	@Nonnull public static final PennerEasing quadIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return f * f;
		}
	};
	@Nonnull public static final PennerEasing quadOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return -(f * (f - 2));
		}
	};
	@Nonnull public static final PennerEasing quadInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f)
				return 2 * f * f;
			else
				return -2 * f * f + (4 * f) - 1;
		}
	};

	@Nonnull public static final PennerEasing cubicIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return f * f * f;
		}
	};
	@Nonnull public static final PennerEasing cubicOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return cubicIn.ease(f - 1) + 1;
		}
	};
	@Nonnull public static final PennerEasing cubicInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f) {
				return 4 * f * f * f;
			} else {
				f = 2 * f - 2;
				return 0.5f * f * f * f + 1;
			}
		}
	};

	@Nonnull public static final PennerEasing quarticIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return f * f * f * f;
		}
	};
	@Nonnull public static final PennerEasing quarticOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			float f2 = (f - 1);
			return f2 * f2 * f2 * (1 - f) + 1;
		}
	};
	@Nonnull public static final PennerEasing quarticInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f) {
				return 8 * f * f * f * f;
			} else {
				f = f - 1;
				return -8 * f * f * f * f + 1;
			}
		}
	};

	@Nonnull public static final PennerEasing quinticIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return f * f * f * f * f;
		}
	};
	@Nonnull public static final PennerEasing quinticOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return quinticIn.ease(f - 1) + 1;
		}
	};
	@Nonnull public static final PennerEasing quinticInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f) {
				return 16 * f * f * f * f * f;
			} else {
				f = 2 * f - 2;
				return 0.5f * f * f * f * f * f + 1;
			}
		}
	};

	@Nonnull public static final PennerEasing sineIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (float)Math.sin((f - 1) * Math.PI * 0.5f) + 1;
		}
	};
	@Nonnull public static final PennerEasing sineOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (float)Math.sin(f * Math.PI * 0.5f);
		}
	};
	@Nonnull public static final PennerEasing sineInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return 0.5f * (1 - (float)Math.cos(f * Math.PI));
		}
	};

	@Nonnull public static final PennerEasing circularIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return 1 - (float)Math.sqrt(1 - (f * f));
		}
	};
	@Nonnull public static final PennerEasing circularOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (float)Math.sqrt((2 - f) * f);
		}
	};
	@Nonnull public static final PennerEasing circularInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f)
				return 0.5f * (1 - (float)Math.sqrt(1 - 4 * (f * f)));
			else
				return 0.5f * ((float)Math.sqrt(-((2 * f) - 3) * ((2 * f) - 1)) + 1);
		}
	};

	@Nonnull public static final PennerEasing exponentialIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (f == 0f) ? f : (float)Math.pow(2, 10 * (f - 1));
		}
	};
	@Nonnull public static final PennerEasing exponentialOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (f == 1f) ? f : 1 - (float)Math.pow(2, -10 * f);
		}
	};
	@Nonnull public static final PennerEasing exponentialInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f == 0f || f == 1f)
				return f;

			if (f < 0.5f)
				return 0.5f * (float)Math.pow(2, (20 * f) - 10);
			else
				return -0.5f * (float)Math.pow(2, (-20 * f) + 10) + 1;
		}
	};

	@Nonnull public static final PennerEasing elasticIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (float)Math.sin(13 * Math.PI * 0.5f * f) * (float)Math.pow(2, 10 * (f - 1));
		}
	};
	@Nonnull public static final PennerEasing elasticOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return (float)Math.sin(-13 * Math.PI * 0.5f * (f + 1)) * (float)Math.pow(2, -10 * f) + 1;
		}
	};
	@Nonnull public static final PennerEasing elasticInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f)
				return 0.5f * (float)Math.sin(13 * Math.PI * 0.5f * (2 * f)) * (float)Math.pow(2, 10 * ((2 * f) - 1));
			else
				return 0.5f * ((float)Math.sin(-13 * Math.PI * 0.5f * ((2 * f - 1) + 1)) * (float)Math.pow(2, -10 * (2 * f - 1)) + 2);
		}
	};

	@Nonnull public static final PennerEasing backIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return f * f * f - f * (float)Math.sin(f * Math.PI);
		}
	};
	@Nonnull public static final PennerEasing backOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			return 1f - backIn.ease(1 - f);
		}
	};
	@Nonnull public static final PennerEasing backInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f) {
				f = 2 * f;
				return 0.5f * (f * f * f - f * (float)Math.sin(f * Math.PI));
			} else {
				f = (1 - (2 * f - 1));
				return 0.5f * (1 - (f * f * f - f * (float)Math.sin(f * Math.PI))) + 0.5f;
			}
		}
	};

	@Nonnull public static final PennerEasing bounceIn = new PennerEasing() {
		@Override
		public float ease(float f) {
			return 1f - bounceOut.ease(1 - f);
		}
	};
	@Nonnull public static final PennerEasing bounceOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 4f / 11f)
				return (121 * f * f) / 16f;
			else if (f < 8f / 11f)
				return (363 / 40f * f * f) - (99 / 10f * f) + 17 / 5f;
			else if (f < 9f / 10f)
				return (4356 / 361f * f * f) - (35442 / 1805f * f) + 16061 / 1805f;
			else
				return (54 / 5f * f * f) - (513 / 25f * f) + 268 / 25f;
		}
	};
	@Nonnull public static final PennerEasing bounceInOut = new PennerEasing() {
		@Override
		public float ease(float f) {
			if (f < 0.5f)
				return 0.5f * bounceIn.ease(f * 2);
			else
				return 0.5f * bounceOut.ease(f * 2 - 1) + 0.5f;
		}
	};
}