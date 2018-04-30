package pl.shockah.unicorn.algo.cluster;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import java8.util.function.Function;
import java8.util.function.IntFunction;
import java8.util.stream.RefStreams;
import pl.shockah.unicorn.HashMultiSet;
import pl.shockah.unicorn.MultiSet;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public abstract class KMeansClustering<T> extends Clustering<T> {
	public final int clusterCount;

	public KMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount);
	}

	public KMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount) {
		super(toVectorFunc, fromVectorFunc, distanceAlgorithm);
		this.clusterCount = clusterCount;
	}

	@Nonnull protected abstract T[] getInitialSeeds(@Nonnull Collection<T> vectors);

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull protected List<T>[] execute(@Nonnull Collection<T> vectors) {
		MultiSet<T> inputVectors = new HashMultiSet<>(vectors);

		MultiSet<T>[] clusters = (MultiSet<T>[])Array.newInstance(MultiSet.class, clusterCount);
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new HashMultiSet<>();
		}

		T[] seeds = getInitialSeeds(vectors);
		for (int i = 0; i < clusters.length; i++) {
			clusters[i].add(seeds[i]);
		}

		MultiSet<T>[] previous = null;
		float inversePercentage = 1f;
		while (true) {
			inversePercentage *= 0.5f;
			setProgress((1f - inversePercentage) * 0.5f + 0.5f);
			for (int i = 0; i < clusters.length; i++) {
				seeds[i] = newSeed(seeds[i], clusters[i]);
				clusters[i].clear();
			}
			for (MultiSet.Entry<T> entry : inputVectors.entries()) {
				T vector = entry.getElement();
				float smallestDistance = Float.POSITIVE_INFINITY;
				int closestClusterIndex = -1;
				for (int i = 0; i < clusters.length; i++) {
					float distance = distanceAlgorithm.getDistance(toVectorFunc.call(vector), toVectorFunc.call(seeds[i]));
					if (closestClusterIndex == -1 || distance < smallestDistance) {
						smallestDistance = distance;
						closestClusterIndex = i;
					}
				}
				clusters[closestClusterIndex].add(vector, entry.getCount());
			}

			if (previous != null && areEqual(previous, clusters))
				break;
			previous = RefStreams.of(clusters)
					.map(new Function<MultiSet<T>, HashMultiSet<T>>() {
						@Override
						public HashMultiSet<T> apply(MultiSet<T> multiSet) {
							return new HashMultiSet<>(multiSet);
						}
					})
					.toArray(new IntFunction<MultiSet<T>[]>() {
						@Override
						public MultiSet<T>[] apply(int size) {
							return (MultiSet<T>[]) Array.newInstance(MultiSet.class, size);
						}
					});
		}

		return RefStreams.of(clusters)
				.map(new Function<MultiSet<T>, Object>() {
					@Override
					public Object apply(MultiSet<T> cluster) {
						List<T> list = new ArrayList<>();
						for (MultiSet.Entry<T> entry : cluster.entries()) {
							T vector = entry.getElement();
							int count = entry.getCount();
							for (int i = 0; i < count; i++) {
								list.add(vector);
							}
						}
						return list;
					}
				})
				.toArray(new IntFunction<List<T>[]>() {
					@Override
					public List<T>[] apply(int size) {
						return (List<T>[]) Array.newInstance(List.class, size);
					}
				});
	}

	private boolean areEqual(@Nonnull MultiSet<T>[] clusters1, @Nonnull MultiSet<T>[] clusters2) {
		if (clusters1.length != clusters2.length)
			return false;
		for (int i = 0; i < clusters1.length; i++) {
			if (clusters1[i].size() != clusters2[i].size())
				return false;
			if (!clusters1[i].equals(clusters2[i]))
				return false;
		}
		return true;
	}

	@Nonnull private T newSeed(@Nonnull T oldSeed, @Nonnull MultiSet<T> vectors) {
		if (vectors.isEmpty())
			throw new IllegalArgumentException("Cannot generate a seed for an empty set.");
		float[] newSeed = new float[toVectorFunc.call(oldSeed).length];
		for (MultiSet.Entry<T> entry : vectors.entries()) {
			int count = entry.getCount();
			float[] vectorf = toVectorFunc.call(entry.getElement());
			for (int n = 0; n < newSeed.length; n++) {
				newSeed[n] += vectorf[n] * count;
			}
		}
		for (int n = 0; n < newSeed.length; n++) {
			newSeed[n] /= vectors.size();
		}
		return fromVectorFunc.call(newSeed);
	}
}