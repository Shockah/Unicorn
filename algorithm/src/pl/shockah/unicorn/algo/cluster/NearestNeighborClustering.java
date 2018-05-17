package pl.shockah.unicorn.algo.cluster;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import java8.util.Maps;
import java8.util.function.Function;
import java8.util.function.IntFunction;
import java8.util.stream.StreamSupport;
import pl.shockah.unicorn.collection.HashMultiSet;
import pl.shockah.unicorn.collection.MultiSet;
import pl.shockah.unicorn.collection.UnorderedPair;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public class NearestNeighborClustering<T> extends Clustering<T> {
	public final float threshold;

	public NearestNeighborClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, float threshold) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, threshold);
	}

	public NearestNeighborClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, float threshold) {
		super(toVectorFunc, fromVectorFunc, distanceAlgorithm);
		this.threshold = threshold;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull protected List<T>[] execute(@Nonnull Collection<T> vectors) {
		List<MultiSet<T>> clusters = new ArrayList<>();
		final Map<T, float[]> vectorCache = new HashMap<>();
		Map<UnorderedPair<T>, Float> distanceCache = new HashMap<>();

		int index = 0;
		int updateProgressEvery = Math.max((int)Math.sqrt(vectors.size() / 10f), 1);

		for (final T vector : vectors) {
			if (index++ % updateProgressEvery == 0)
				setProgress(1f * index / vectors.size());

			float smallestDistance = Float.POSITIVE_INFINITY;
			MultiSet<T> closestCluster = null;
			for (MultiSet<T> cluster : clusters) {
				for (final T vector2 : cluster.distinct()) {
					float distance = Maps.computeIfAbsent(distanceCache, new UnorderedPair<>(vector, vector2), new Function<UnorderedPair<T>, Float>() {
						@Override
						public Float apply(UnorderedPair<T> key) {
							float[] node1Vector = Maps.computeIfAbsent(vectorCache, vector, new Function<T, float[]>() {
								@Override
								public float[] apply(T t1) {
									return toVectorFunc.call(t1);
								}
							});
							float[] node2Vector = Maps.computeIfAbsent(vectorCache, vector2, new Function<T, float[]>() {
								@Override
								public float[] apply(T t1) {
									return toVectorFunc.call(t1);
								}
							});
							return distanceAlgorithm.getDistance(node1Vector, node2Vector);
						}
					});
					if (closestCluster == null || distance < smallestDistance) {
						closestCluster = cluster;
						smallestDistance = distance;
					}
				}
			}

			if (closestCluster == null || smallestDistance >= threshold) {
				closestCluster = new HashMultiSet<>();
				clusters.add(closestCluster);
			}
			closestCluster.add(vector);
		}

		return StreamSupport.stream(clusters)
				.map(new Function<MultiSet<T>, Object>() {
					@Override
					public Object apply(MultiSet<T> cluster) {
						List<T> list = new ArrayList<>(cluster.size());
						for (T vector : cluster) {
							list.add(vector);
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
}