package pl.shockah.unicorn;

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

	public static double min(double... values) {
		double min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static float min(float... values) {
		float min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static long min(long... values) {
		long min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static int min(int... values) {
		int min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	public static double max(double... values) {
		double max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static float max(float... values) {
		float max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static long max(long... values) {
		long max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static int max(int... values) {
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
}