package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;

public abstract class WrappedOperation<Input, Output, WrappedInput, WrappedOutput> implements Operation<Input, Output> {
	@Nonnull public final Operation<WrappedInput, WrappedOutput> wrapped;

	public WrappedOperation(@Nonnull Operation<WrappedInput, WrappedOutput> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public float getProgress() {
		return wrapped.getProgress();
	}

	@Override
	@Nonnull public String getDescription() {
		return wrapped.getDescription();
	}

	@Override
	public float getWeight() {
		return wrapped.getWeight();
	}

	@Override
	@Nonnull public <T> ChainOperation<Input, Output, T> chain(@Nonnull Operation<Output, T> operation) {
		return ChainOperation.chain(this, operation);
	}

	@Override
	@Nonnull public <T> ChainOperation<Input, OperationResult<Input, Output>, T> chainResult(@Nonnull Operation<OperationResult<Input, Output>, T> operation) {
		return ChainOperation.chainResult(this, operation);
	}

	@Override
	@Nonnull public AsyncOperation<Input, Output> async(@Nonnull Input input) {
		return new AsyncOperation<>(this, input);
	}
}