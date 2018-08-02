package pl.shockah.unicorn.func;

import javax.annotation.Nonnull;

public class Lazy<T> implements Func0<T> {
	private volatile Func0<T> generator;

	private volatile T value;

	public Lazy(@Nonnull Func0<T> generator) {
		this.generator = generator;
	}

	public T get() {
		return call();
	}

	@Override
	public T call() {
		if (generator != null) {
			synchronized (this) {
				if (generator != null) {
					value = generator.call();
					generator = null;
				}
			}
		}
		return value;
	}

	@Nonnull
	public <R> Lazy<R> map(@Nonnull final Func1<T, R> mapper) {
		return new Lazy<>(new Func0<R>() {
			@Override
			public R call() {
				return mapper.call(Lazy.this.call());
			}
		});
	}

	@Nonnull
	public <R> Lazy<R> flatMap(@Nonnull final Func1<T, Lazy<R>> mapper) {
		return new Lazy<>(new Func0<R>() {
			@Override
			public R call() {
				return mapper.call(Lazy.this.call()).call();
			}
		});
	}
}