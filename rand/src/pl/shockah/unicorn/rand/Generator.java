package pl.shockah.unicorn.rand;

import javax.annotation.Nonnull;

public interface Generator<T> {
	@Nonnull T generate();
}