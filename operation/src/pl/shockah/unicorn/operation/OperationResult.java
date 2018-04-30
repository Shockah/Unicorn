package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;

public final class OperationResult<Input, Output> {
	@Nonnull public final Operation<Input, Output> operation;
	@Nonnull public final Input input;
	@Nonnull public final Output output;

	public OperationResult(@Nonnull Operation<Input, Output> operation, @Nonnull Input input, @Nonnull Output output) {
		this.operation = operation;
		this.input = input;
		this.output = output;
	}
}