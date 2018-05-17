package pl.shockah.unicorn.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class SafeList<T> {
	@Nonnull private final List<T> wrapped;
	@Nonnull private final List<T> unmodifiableList;
	@Nonnull private final List<T> waitingToAdd = new ArrayList<>();
	@Nonnull private final List<T> waitingToRemove = new ArrayList<>();
	@Nonnull private final List<T> unmodifiableWaitingToAdd = Collections.unmodifiableList(waitingToAdd);
	@Nonnull private final List<T> unmodifiableWaitingToRemove = Collections.unmodifiableList(waitingToRemove);

	public SafeList(@Nonnull List<T> wrapped) {
		this.wrapped = wrapped;
		unmodifiableList = Collections.unmodifiableList(wrapped);
	}

	@Nonnull public List<T> get() {
		return unmodifiableList;
	}

	@Nonnull public List<T> getWaitingToAdd() {
		return unmodifiableWaitingToAdd;
	}

	@Nonnull public List<T> getWaitingToRemove() {
		return unmodifiableWaitingToRemove;
	}

	public void add(T element) {
		waitingToAdd.add(element);
	}

	public void remove(T element) {
		waitingToRemove.add(element);
	}

	public boolean contains(T element) {
		return (wrapped.contains(element) || waitingToAdd.contains(element)) && !waitingToRemove.contains(element);
	}

	public void update() {
		if (!waitingToAdd.isEmpty()) {
			wrapped.addAll(waitingToAdd);
			waitingToAdd.clear();
		}
		if (!waitingToRemove.isEmpty()) {
			wrapped.removeAll(waitingToRemove);
			waitingToRemove.clear();
		}
	}
}