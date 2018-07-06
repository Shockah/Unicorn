package pl.shockah.unicorn;

import javax.annotation.Nonnull;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Math2 {
	public static double root(double value, double degree) {
		return Math.pow(value, 1d / degree);
	}

	public static float ldirX(float dist, float angle) {
		return (float)-Math.cos(Math.toRadians(angle + 180f)) * dist;
	}

	public static float ldirY(float dist, float angle) {
		return (float)-Math.sin(Math.toRadians(angle + 180f)) * dist;
	}

	public static float deltaAngle(float angle1, float angle2) {
		while (angle2 <= -180)
			angle2 += 360;
		while (angle2 > 180)
			angle2 -= 360;
		while (angle1 <= -180)
			angle1 += 360;
		while (angle1 > 180)
			angle1 -= 360;

		float r = angle2 - angle1;
		return r + ((r > 180) ? -360 : (r < -180) ? 360 : 0);
	}

	public static double frac(double value) {
		double sign = Math.signum(value);
		value = Math.abs(value);
		return (value - Math.floor(value)) * sign;
	}

	public static double min(@Nonnull double... values) {
		double min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static float min(@Nonnull float... values) {
		float min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static long min(@Nonnull long... values) {
		long min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static int min(@Nonnull int... values) {
		int min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static double max(@Nonnull double... values) {
		double max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static float max(@Nonnull float... values) {
		float max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static long max(@Nonnull long... values) {
		long max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static int max(@Nonnull int... values) {
		int max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static long clamp(long value, long min, long max) {
		return value > max ? max : (value < min ? min : value);
	}

	public static int clamp(int value, int min, int max) {
		return value > max ? max : (value < min ? min : value);
	}

	public static float clamp(float value, float min, float max) {
		return value > max ? max : (value < min ? min : value);
	}

	public static double clamp(double value, double min, double max) {
		return value > max ? max : (value < min ? min : value);
	}

	public static float average(@Nonnull float... values) {
		float sum = 0f;
		for (float value : values) {
			sum += value;
		}
		return sum / values.length;
	}

	public static double average(@Nonnull double... values) {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		return sum / values.length;
	}

	public static float standardDeviation(@Nonnull float... values) {
		float average = average(values);
		float sum = 0f;
		for (float value : values) {
			float v = value - average;
			sum += v * v;
		}
		return (float)Math.sqrt(sum / values.length);
	}

	public static double standardDeviation(@Nonnull double... values) {
		double average = average(values);
		double sum = 0.0;
		for (double value : values) {
			double v = value - average;
			sum += v * v;
		}
		return Math.sqrt(sum / values.length);
	}
}