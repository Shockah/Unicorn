package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;

public class ChainOperation<Input, IntermediateOutput, Output> implements Operation<Input, Output> {
	@Nonnull private final Operation<Input, IntermediateOutput> firstOperation;
	@Nonnull private final Operation<IntermediateOutput, Output> secondOperation;

	private boolean executing = false;
	@Nonnull private final Object lock = new Object();

	public ChainOperation(@Nonnull Operation<Input, IntermediateOutput> firstOperation, @Nonnull Operation<IntermediateOutput, Output> secondOperation) {
		this.firstOperation = firstOperation;
		this.secondOperation = secondOperation;
	}

	@Nonnull public static <Input, IntermediateOutput, Output> ChainOperation<Input, IntermediateOutput, Output> chain(@Nonnull Operation<Input, IntermediateOutput> firstOperation, @Nonnull Operation<IntermediateOutput, Output> secondOperation) {
		return new ChainOperation<>(firstOperation, secondOperation);
	}

	@Nonnull public static <Input, IntermediateOutput, Output> ChainOperation<Input, OperationResult<Input, Output>, IntermediateOutput> chainResult(@Nonnull Operation<Input, Output> firstOperation, @Nonnull Operation<OperationResult<Input, Output>, IntermediateOutput> secondOperation) {
		return new ChainOperation<>(new WrappedOperation<Input, OperationResult<Input, Output>, Input, Output>(firstOperation) {
			@Override
			@Nonnull public OperationResult<Input, Output> run(@Nonnull Input input) {
				return new OperationResult<>(wrapped, input, wrapped.run(input));
			}
		}, secondOperation);
	}

	@Override
	public float getWeight() {
		return firstOperation.getWeight() + secondOperation.getWeight();
	}

	@Override
	public float getProgress() {
		float totalWeight = firstOperation.getWeight() + secondOperation.getWeight();
		return (firstOperation.getProgress() * firstOperation.getWeight() + secondOperation.getProgress() * secondOperation.getWeight()) / totalWeight;
	}

	@Override
	@Nonnull public String getDescription() {
		return firstOperation.getProgress() < 1f ? firstOperation.getDescription() : secondOperation.getDescription();
	}

	@Override
	@Nonnull public Output run(@Nonnull Input input) {
		synchronized (lock) {
			if (executing)
				throw new IllegalStateException("Operation is already being executed.");
			executing = true;
		}
		IntermediateOutput intermediateOutput = firstOperation.run(input);
		Output output = secondOperation.run(intermediateOutput);
		synchronized (lock) {
			executing = false;
		}
		return output;
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