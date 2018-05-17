package pl.shockah.unicorn.algo.tsp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java8.util.Maps;
import java8.util.function.Function;
import lombok.Getter;
import pl.shockah.unicorn.collection.UnorderedPair;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;
import pl.shockah.unicorn.operation.AbstractOperation;

public abstract class TravellingSalesmanSolver<T> extends AbstractOperation<Set<T>, TravellingSalesmanSolver<T>.Route> {
	@Nonnull protected final Func1<T, float[]> toVectorFunc;
	@Nonnull protected final DistanceAlgorithm distanceAlgorithm;

	public TravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc) {
		this(toVectorFunc, EuclideanDistanceAlgorithm.instance);
	}

	public TravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm) {
		this.toVectorFunc = toVectorFunc;
		this.distanceAlgorithm = distanceAlgorithm;
	}

	protected class SolveInstance {
		@Nonnull public final Set<T> nodes;
		@Nonnull public final Map<T, float[]> vectorCache = new HashMap<>();
		@Nonnull public final Map<UnorderedPair<T>, Float> distanceCache = new HashMap<>();
		@Nullable public Route bestRoute;

		public SolveInstance(@Nonnull Set<T> nodes) {
			this.nodes = nodes;
		}

		public float getDistance(@Nonnull final T node1, @Nonnull final T node2) {
			return Maps.computeIfAbsent(distanceCache, new UnorderedPair<>(node1, node2), new Function<UnorderedPair<T>, Float>() {
				@Override
				public Float apply(UnorderedPair<T> key) {
					float[] node1Vector = Maps.computeIfAbsent(vectorCache, node1, new Function<T, float[]>() {
						@Override
						public float[] apply(T t1) {
							return toVectorFunc.call(t1);
						}
					});
					float[] node2Vector = Maps.computeIfAbsent(vectorCache, node2, new Function<T, float[]>() {
						@Override
						public float[] apply(T t1) {
							return toVectorFunc.call(t1);
						}
					});
					return distanceAlgorithm.getDistance(node1Vector, node2Vector);
				}
			});
		}
	}

	public abstract class Route {
		@Getter(lazy = true)
		private final List<T> nodes = calculateNodes();

		@Nonnull protected abstract List<T> calculateNodes();

		public abstract float getLength();
	}
}