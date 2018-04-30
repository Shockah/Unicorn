package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import pl.shockah.unicorn.Math2;

public abstract class AbstractOperation<Input, Output> implements Operation<Input, Output> {
	@Getter
	public final float weight;

	private float progress = 0f;

	@Getter
	@Nonnull public final String description;

	private boolean executing = false;
	@Nonnull private final Object lock = new Object();

	public AbstractOperation() {
		this(1f, null);
	}

	public AbstractOperation(float weight) {
		this(weight, null);
	}

	public AbstractOperation(@Nullable String description) {
		this(1f, description);
	}

	public AbstractOperation(float weight, @Nullable String description) {
		this.weight = weight;
		this.description = description != null ? description : getClass().getName();
	}

	public float getProgress() {
		synchronized (lock) {
			return progress;
		}
	}

	protected void setProgress(float progress) {
		synchronized (lock) {
			this.progress = Math2.clamp(progress, 0f, 1f);
		}
	}

	@Override
	@Nonnull public final Output run(@Nonnull Input input) {
		synchronized (lock) {
			if (executing)
				throw new IllegalStateException("Operation is already being executed.");
			executing = true;
			progress = 0f;
		}
		Output output = execute(input);
		synchronized (lock) {
			executing = false;
			progress = 1f;
		}
		return output;
	}

	@Nonnull protected abstract Output execute(@Nonnull Input input);

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