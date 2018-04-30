package pl.shockah.unicorn.algo;

import javax.annotation.Nonnull;

public class ManhattanDistanceAlgorithm implements DistanceAlgorithm {
	@Nonnull public static final DistanceAlgorithm instance = new ManhattanDistanceAlgorithm();

	@Override
	public float getDistance(@Nonnull float[] v1, @Nonnull float[] v2) {
		if (v1.length != v2.length)
			throw new IllegalArgumentException("Vector dimensions don't match.");

		float f = 0f;
		for (int i = 0; i < v1.length; i++) {
			f += Math.abs(v2[i] - v1[i]);
		}
		return f;
	}
}