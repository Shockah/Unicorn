package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;

public interface Operation<Input, Output> {
	float getProgress();
	@Nonnull String getDescription();
	float getWeight();
	@Nonnull Output run(Input input);

	@Nonnull <T> ChainOperation<Input, Output, T> chain(@Nonnull Operation<Output, T> operation);

	@Nonnull <T> ChainOperation<Input, OperationResult<Input, Output>, T> chainResult(@Nonnull Operation<OperationResult<Input, Output>, T> operation);

	@Nonnull AsyncOperation<Input, Output> async(@Nonnull Input input);
}