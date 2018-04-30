package pl.shockah.unicorn.operation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AsyncOperation<Input, Output> implements Runnable {
	@Nonnull public final Operation<Input, Output> operation;
	@Nonnull public final Input input;

	@Nullable private Output output;

	@Nonnull private final Object lock = new Object();
	private boolean started = false;
	private boolean finished = false;

	public AsyncOperation(@Nonnull Operation<Input, Output> operation, @Nonnull Input input) {
		this.operation = operation;
		this.input = input;
	}

	public boolean isStarted() {
		synchronized (lock) {
			return started;
		}
	}

	public boolean isFinished() {
		synchronized (lock) {
			return finished;
		}
	}

	public float getProgress() {
		return operation.getProgress();
	}

	@Nonnull public String getDescription() {
		return operation.getDescription();
	}

	@Nonnull public Output waitAndGetOutput() {
		while (true) {
			synchronized (lock) {
				if (finished) {
					if (output == null)
						throw new IllegalStateException("Output should not be null at this point.");
					return output;
				}
			}
			Thread.yield();
		}
	}

	@Nullable public Output getOutput() {
		synchronized (lock) {
			return output;
		}
	}

	@Override
	public void run() {
		synchronized (lock) {
			if (started)
				throw new IllegalStateException("AsyncOperation can only be started once.");
			started = true;
		}
		Output output = operation.run(input);
		synchronized (lock) {
			this.output = output;
			finished = true;
		}
	}
}