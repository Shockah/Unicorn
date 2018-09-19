package pl.shockah.unicorn.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Either3<A, B, C> {
	@Nullable
	public final A first;

	@Nullable
	public final B second;

	@Nullable
	public final C third;

	protected Either3(@Nullable A first, @Nullable B second, @Nullable C third) {
		if ((first != null ? 1 : 0) + (second != null ? 1 : 0) + (third != null ? 1 : 0) != 1)
			throw new IllegalArgumentException("Exactly one parameter has to be non-null.");
		this.first = first;
		this.second = second;
		this.third = third;
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> first(@Nonnull A value) {
		return new Either3<>(value, null, null);
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> second(@Nonnull B value) {
		return new Either3<>(null, value, null);
	}

	@Nonnull
	public static <A, B, C> Either3<A, B, C> third(@Nonnull C value) {
		return new Either3<>(null, null, value);
	}

	public void apply(@Nonnull Consumer<A> first, @Nonnull Consumer<B> second, @Nonnull Consumer<C> third) {
		if (this.first != null)
			first.accept(this.first);
		else if (this.second != null)
			second.accept(this.second);
		else if (this.third != null)
			third.accept(this.third);
		else
			throw new IllegalArgumentException("Exactly one parameter has to be non-null.");
	}

	interface Consumer<T> {
		void accept(@Nonnull T obj);
	}
}