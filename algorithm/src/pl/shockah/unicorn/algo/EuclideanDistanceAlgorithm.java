package pl.shockah.unicorn.algo;

import javax.annotation.Nonnull;

public class EuclideanDistanceAlgorithm implements DistanceAlgorithm {
	@Nonnull public static final DistanceAlgorithm instance = new EuclideanDistanceAlgorithm();

	@Override
	public float getDistance(@Nonnull float[] v1, @Nonnull float[] v2) {
		if (v1.length != v2.length)
			throw new IllegalArgumentException("Vector dimensions don't match.");

		float f = 0f;
		for (int i = 0; i < v1.length; i++) {
			f += Math.pow(v2[i] - v1[i], 2);
		}
		return (float)Math.sqrt(f);
	}
}