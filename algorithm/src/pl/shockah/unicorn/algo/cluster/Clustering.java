package pl.shockah.unicorn.algo.cluster;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import pl.shockah.unicorn.algo.DistanceAlgorithm;
import pl.shockah.unicorn.algo.EuclideanDistanceAlgorithm;
import pl.shockah.unicorn.func.Func1;
import pl.shockah.unicorn.operation.AbstractOperation;

public abstract class Clustering<T> extends AbstractOperation<Collection<T>, List<T>[]> {
	@Nonnull protected final Func1<T, float[]> toVectorFunc;
	@Nonnull protected final Func1<float[], T> fromVectorFunc;
	@Nonnull protected final DistanceAlgorithm distanceAlgorithm;

	public Clustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc) {
		this(toVectorFunc, fromVectorFunc, EuclideanDistanceAlgorithm.instance);
	}

	public Clustering(@Nonnull Func1<T, float[]> toVectorFunc, @Nonnull Func1<float[], T> fromVectorFunc, @Nonnull DistanceAlgorithm distanceAlgorithm) {
		this.toVectorFunc = toVectorFunc;
		this.fromVectorFunc = fromVectorFunc;
		this.distanceAlgorithm = distanceAlgorithm;
	}
}