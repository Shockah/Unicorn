package pl.shockah.unicorn.algo.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public class RandomKMeansClustering<T> extends KMeansClustering<T> {
	public RandomKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount);
	}

	public RandomKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount) {
		super(toVectorFunc, fromVectorFunc, distanceAlgorithm, clusterCount);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull protected T[] getInitialSeeds(@Nonnull Collection<T> vectors) {
		setProgress(0f);
		List<T> shuffled = new ArrayList<>(new HashSet<>(vectors));
		Collections.shuffle(shuffled);
		return shuffled.subList(0, clusterCount).toArray((T[])new Object[clusterCount]);
	}
}