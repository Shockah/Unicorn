package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import pl.shockah.unicorn.func.Func1;
import pl.shockah.unicorn.func.Func2;

public final class FuncOperation<Input, Output> extends AbstractOperation<Input, Output> {
	@Nonnull private final Func2<FuncOperation<Input, Output>, Input, Output> func;

	public FuncOperation(@Nonnull Func1<Input, Output> func) {
		this(null, func);
	}

	public FuncOperation(@Nonnull Func2<FuncOperation<Input, Output>, Input, Output> func) {
		this(null, func);
	}

	public FuncOperation(float weight, @Nonnull Func1<Input, Output> func) {
		this(weight, null, func);
	}

	public FuncOperation(float weight, @Nonnull Func2<FuncOperation<Input, Output>, Input, Output> func) {
		this(weight, null, func);
	}

	public FuncOperation(@Nullable String description, @Nonnull Func1<Input, Output> func) {
		this(1f, description, func);
	}

	public FuncOperation(@Nullable String description, @Nonnull Func2<FuncOperation<Input, Output>, Input, Output> func) {
		this(1f, description, func);
	}

	public FuncOperation(float weight, @Nullable String description, @Nonnull final Func1<Input, Output> func) {
		this(weight, description, new Func2<FuncOperation<Input, Output>, Input, Output>() {
			@Override
			public Output call(FuncOperation<Input, Output> operation, Input input) {
				return func.call(input);
			}
		});
	}

	public FuncOperation(float weight, @Nullable String description, @Nonnull Func2<FuncOperation<Input, Output>, Input, Output> func) {
		super(weight, description);
		this.func = func;
	}

	@Override
	public void setProgress(float progress) {
		super.setProgress(progress);
	}

	@Override
	@Nonnull protected Output execute(@Nonnull Input input) {
		return func.call(this, input);
	}
}