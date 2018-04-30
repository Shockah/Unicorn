package pl.shockah.unicorn.algo.tsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java8.util.Comparators;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.function.ToDoubleFunction;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.Getter;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public class NearestNeighborTravellingSalesmanSolver<T> extends TravellingSalesmanSolver<T> {
	public final boolean skipOnFirst;
	public final float ratio;

	public NearestNeighborTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc) {
		this(toVectorFunc, 0.3f);
	}

	public NearestNeighborTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm) {
		this(toVectorFunc, distanceAlgorithm, 0.3f);
	}

	public NearestNeighborTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc, @Nullable Float ratio) {
		this(toVectorFunc, EuclideanDistanceAlgorithm.instance, ratio);
	}

	public NearestNeighborTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm, @Nullable Float ratio) {
		super(toVectorFunc, distanceAlgorithm);
		this.ratio = ratio != null ? ratio : 0f;
		skipOnFirst = ratio == null;
	}

	@Nonnull protected Route execute(@Nonnull Set<T> nodes) {
		SolveInstance instance = new SolveInstance(nodes);
		for (T node : nodes) {
			instance.queue.add(new Route(null, node, 0f));
		}
		instance.solve();
		if (instance.bestRoute == null)
			throw new NullPointerException();
		return (Route)instance.bestRoute;
	}

	protected class SolveInstance extends TravellingSalesmanSolver<T>.SolveInstance {
		@Nonnull public final LinkedList<Route> queue = new LinkedList<>();

		public SolveInstance(@Nonnull Set<T> nodes) {
			super(nodes);
		}

		public void solve() {
			while (!queue.isEmpty()) {
				queue.removeLast().solve(this);
				if (skipOnFirst && bestRoute != null)
					break;
			}
		}
	}

	public class Route extends TravellingSalesmanSolver<T>.Route {
		@Nullable public final Route parent;
		@Nonnull public final T node;

		@Getter
		public final float length;

		@Getter(lazy = true)
		private final List<T> nodes = calculateNodes();

		public Route(@Nullable Route parent, @Nonnull T node, float length) {
			this.parent = parent;
			this.node = node;
			this.length = length;
		}

		protected boolean contains(@Nonnull T node) {
			Route current = this;
			while (current != null) {
				if (current.node.equals(node))
					return true;
				current = current.parent;
			}
			return false;
		}

		protected boolean solveInternal(@Nonnull final SolveInstance instance) {
			List<T> filteredNodes = StreamSupport.stream(instance.nodes)
					.filter(new Predicate<T>() {
						@Override
						public boolean test(T node) {
							return !Route.this.contains(node);
						}
					})
					.sorted(Comparators.comparingDouble(new ToDoubleFunction<T>() {
						@Override
						public double applyAsDouble(T node) {
							return instance.getDistance(Route.this.node, node);
						}
					}))
					.collect(Collectors.<T>toList());

			if (filteredNodes.isEmpty())
				return false;

			List<Route> collected = StreamSupport.stream(filteredNodes)
					.limit(Math.max((int) Math.ceil(filteredNodes.size() * ratio), 1))
					.map(new Function<T, Route>() {
						@Override
						public Route apply(T node) {
							return new Route(Route.this, node, length + instance.getDistance(Route.this.node, node));
						}
					})
					.sorted(Comparators.comparingDouble(new ToDoubleFunction<Route>() {
						@Override
						public double applyAsDouble(Route value) {
							return value.getLength();
						}
					}))
					.collect(Collectors.<Route>toList());

			if (instance.queue.isEmpty())
				instance.queue.addAll(collected);
			else
				instance.queue.addAll(0, collected);

			return true;
		}

		protected void solve(@Nonnull SolveInstance instance) {
			if (instance.bestRoute != null && instance.bestRoute.getLength() < length)
				return;

			boolean hadNodesLeft = solveInternal(instance);
			if (!hadNodesLeft) {
				if (instance.bestRoute == null || instance.bestRoute.getLength() > length)
					instance.bestRoute = this;
			}
		}

		@Override
		@Nonnull protected List<T> calculateNodes() {
			LinkedList<T> list = new LinkedList<>();
			Route current = this;
			while (current != null) {
				list.addFirst(current.node);
				current = current.parent;
			}
			return new ArrayList<>(list);
		}
	}
}