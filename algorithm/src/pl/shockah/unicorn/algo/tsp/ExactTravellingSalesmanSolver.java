package pl.shockah.unicorn.algo.tsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;

public class ExactTravellingSalesmanSolver<T> extends TravellingSalesmanSolver<T> {
	public ExactTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc) {
		super(toVectorFunc);
	}

	public ExactTravellingSalesmanSolver(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm) {
		super(toVectorFunc, distanceAlgorithm);
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

		protected boolean solveInternal(@Nonnull SolveInstance instance) {
			boolean hadNodesLeft = false;
			for (T node : instance.nodes) {
				if (contains(node))
					continue;
				instance.queue.add(new Route(this, node, length + instance.getDistance(this.node, node)));
				hadNodesLeft = true;
			}
			return hadNodesLeft;
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