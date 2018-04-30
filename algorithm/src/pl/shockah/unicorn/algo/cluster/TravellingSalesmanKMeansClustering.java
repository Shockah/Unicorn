package pl.shockah.unicorn.algo.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

import java8.util.function.IntFunction;
import java8.util.stream.IntStreams;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.algo.tsp.NearestNeighborTravellingSalesmanSolver;
import pl.shockah.unicorn.algo.tsp.TravellingSalesmanSolver;
import pl.shockah.unicorn.func.Func1;

public class TravellingSalesmanKMeansClustering<T> extends KMeansClustering<T> {
	@Nonnull public final TravellingSalesmanSolver<T> solver;

	public TravellingSalesmanKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount);
	}

	public TravellingSalesmanKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, int clusterCount, @Nonnull TravellingSalesmanSolver<T> solver) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance, clusterCount, solver);
	}

	public TravellingSalesmanKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount) {
		this(toVectorFunc, fromVectorFunc, distanceAlgorithm, clusterCount, new NearestNeighborTravellingSalesmanSolver<>(toVectorFunc, distanceAlgorithm));
	}

	public TravellingSalesmanKMeansClustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, int clusterCount, @Nonnull TravellingSalesmanSolver<T> solver) {
		super(toVectorFunc, fromVectorFunc, distanceAlgorithm, clusterCount);
		this.solver = solver;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull protected T[] getInitialSeeds(@Nonnull Collection<T> vectors) {
		setProgress(0f);
		final List<T> route = solver.run(new HashSet<>(vectors)).getNodes();
		return IntStreams.range(0, clusterCount).mapToObj(new IntFunction<Object>() {
			@Override
			public Object apply(int index) {
				float f = 1f * index / clusterCount;
				return route.get((int) (f * route.size()));
			}
		}).toArray(new IntFunction<T[]>() {
			@Override
			public T[] apply(int size) {
				return (T[]) new Object[size];
			}
		});
	}
}