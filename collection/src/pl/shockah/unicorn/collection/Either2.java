package pl.shockah.unicorn.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Either2<A, B> {
	@Nullable
	public final A first;

	@Nullable
	public final B second;

	protected Either2(@Nullable A first, @Nullable B second) {
		if ((first != null ? 1 : 0) + (second != null ? 1 : 0) != 1)
			throw new IllegalArgumentException("Exactly one parameter has to be non-null.");
		this.first = first;
		this.second = second;
	}

	@Nonnull
	public static <A, B> Either2<A, B> first(@Nonnull A value) {
		return new Either2<>(value, null);
	}

	@Nonnull
	public static <A, B> Either2<A, B> second(@Nonnull B value) {
		return new Either2<>(null, value);
	}

	public void apply(@Nonnull Consumer<A> first, @Nonnull Consumer<B> second) {
		if (this.first != null)
			first.accept(this.first);
		else if (this.second != null)
			second.accept(this.second);
		else
			throw new IllegalArgumentException("Exactly one parameter has to be non-null.");
	}

	public interface Consumer<T> {
		void accept(@Nonnull T value);
	}
}