package pl.shockah.unicorn.ease;

import javax.annotation.Nonnull;

public interface Easable<T> {
	@Nonnull T ease(@Nonnull T other, float f);
}