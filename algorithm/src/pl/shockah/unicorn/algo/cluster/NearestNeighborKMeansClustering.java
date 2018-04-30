package pl.shockah.unicorn.algo.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import java8.util.function.Function;
import java8.util.function.IntFunction;
import java8.util.stream.RefStreams;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public class NearestNeighborKMeansClustering<T> extends KMeansClustering<T> {
	public final float initialThreshold;
	public final float thresholdMultiplier;

	public NearestNeighborKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount);
	}

	public NearestNeighborKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount, float initialThreshold, float thresholdMultiplier) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount, initialThreshold, thresholdMultiplier);
	}

	public NearestNeighborKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, distanceAlgorithm, clusterCount, 0.9f, 0.95f);
	}

	public NearestNeighborKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount, float initialThreshold, float thresholdMultiplier) {
		super(toVectorFunc, fromVectorFunc, distanceAlgorithm, clusterCount);
		this.initialThreshold = initialThreshold;
		this.thresholdMultiplier = thresholdMultiplier;
	}

	@Nonnull protected final T getAverageVector(@Nonnull Collection<T> vectors) {
		if (vectors.isEmpty())
			throw new IllegalArgumentException("Cannot get an average for an empty set.");
		float[] average = new float[toVectorFunc.call(vectors.iterator().next()).length];
		for (T vector : vectors) {
			float[] vectorf = toVectorFunc.call(vector);
			for (int n = 0; n < average.length; n++) {
				average[n] += vectorf[n];
			}
		}
		for (int n = 0; n < average.length; n++) {
			average[n] /= vectors.size();
		}
		return fromVectorFunc.call(average);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull protected T[] getInitialSeeds(@Nonnull Collection<T> vectors) {
		Set<T> distinct = new HashSet<>(vectors);

		float threshold = initialThreshold;
		float inversePercentage = 1f;
		while (true) {
			inversePercentage *= 0.5f;
			setProgress((1f - inversePercentage) * 0.5f);
			List<T>[] clusters = new NearestNeighborClustering<>(toVectorFunc, fromVectorFunc, threshold).run(distinct);
			if (clusters.length >= clusterCount) {
				return RefStreams.of(clusters)
						.limit(clusterCount)
						.map(new Function<List<T>, Object>() {
							@Override
							public Object apply(List<T> vectors1) {
								return NearestNeighborKMeansClustering.this.getAverageVector(vectors1);
							}
						})
						.toArray(new IntFunction<T[]>() {
							@Override
							public T[] apply(int size) {
								return (T[]) new Object[size];
							}
						});
			}
			threshold *= thresholdMultiplier;
		}
	}
}