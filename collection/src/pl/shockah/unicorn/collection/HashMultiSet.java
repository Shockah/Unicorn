package pl.shockah.unicorn.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;

public class HashMultiSet<E> extends AbstractMultiSet<E> {
	private int size = 0;
	@Nonnull private final Map<E, Count> counts = new HashMap<>();

	public HashMultiSet() {
	}

	public HashMultiSet(@Nonnull Collection<? extends E> collection) {
		this();
		for (E element : collection) {
			add(element);
		}
	}

	public HashMultiSet(@Nonnull MultiSet<? extends E> multiSet) {
		this();
		for (Entry<? extends E> entry : multiSet.entries()) {
			add(entry.getElement(), entry.getCount());
		}
	}

	@Override
	public void clear() {
		size = 0;
		counts.clear();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int distinctSize() {
		return counts.size();
	}

	@Override
	public int count(Object element) {
		return (counts.containsKey(element) ? counts.get(element) : new Count()).value;
	}

	@Override
	public int add(E element) {
		size++;
		if (!counts.containsKey(element))
			counts.put(element, new Count());
		Count count = counts.get(element);
		return ++count.value;
	}

	@Override
	public int add(E element, int occurences) {
		size += occurences;
		if (!counts.containsKey(element))
			counts.put(element, new Count());
		Count count = counts.get(element);
		count.value += occurences;
		return count.value;
	}

	@Override
	public int remove(E element) {
		Count count = (counts.containsKey(element) ? counts.get(element) : new Count());
		if (count.value == 0)
			return 0;

		size--;
		if (count.value == 1) {
			counts.remove(element);
			return 0;
		}

		return --count.value;
	}

	@Override
	public int remove(E element, int occurences) {
		Count count = (counts.containsKey(element) ? counts.get(element) : new Count());
		if (count.value == 0)
			return 0;

		if (count.value <= occurences) {
			size -= count.value;
			counts.remove(element);
			return 0;
		} else {
			size -= occurences;
			count.value -= occurences;
			return count.value;
		}
	}

	@Override
	@Nonnull public Set<E> distinct() {
		return new HashSet<>(counts.keySet());
	}

	@Override
	@Nonnull public Iterator<E> distinctIterator() {
		return new Iterator<E>() {
			final Iterator<Map.Entry<E, Count>> backingEntries = counts.entrySet().iterator();
			Map.Entry<E, Count> toRemove;

			@Override
			public boolean hasNext() {
				return backingEntries.hasNext();
			}

			@Override
			public E next() {
				final Map.Entry<E, Count> mapEntry = backingEntries.next();
				toRemove = mapEntry;
				return mapEntry.getKey();
			}

			@Override
			public void remove() {
				if (toRemove == null)
					throw new IllegalStateException("Already removed.");
				size -= toRemove.getValue().value;
				backingEntries.remove();
				toRemove = null;
			}
		};
	}

	@Override
	@Nonnull public Set<Entry<E>> entries() {
		Set<Entry<E>> set = new HashSet<>();
		for (final Map.Entry<E, Count> entry : counts.entrySet()) {
			set.add(new MultiSet.Entry<E>() {
				@Override
				public E getElement() {
					return entry.getKey();
				}

				@Override
				public int getCount() {
					return entry.getValue().value;
				}
			});
		}
		return set;
	}

	@Override
	@Nonnull public Iterator<E> iterator() {
		return new Iterator<E>() {
			final Iterator<Map.Entry<E, Count>> entryIterator = counts.entrySet().iterator();
			Map.Entry<E, Count> currentEntry;
			int occurrencesLeft;
			boolean canRemove;

			@Override
			public boolean hasNext() {
				return occurrencesLeft > 0 || entryIterator.hasNext();
			}

			@Override
			public E next() {
				if (occurrencesLeft == 0) {
					currentEntry = entryIterator.next();
					occurrencesLeft = currentEntry.getValue().value;
				}
				occurrencesLeft--;
				canRemove = true;
				return currentEntry.getKey();
			}

			@Override
			public void remove() {
				if (!canRemove)
					throw new IllegalStateException("Already removed.");
				int frequency = currentEntry.getValue().value;
				if (frequency <= 0)
					throw new ConcurrentModificationException();

				Count count = currentEntry.getValue();
				if (count.value == 1)
					entryIterator.remove();
				else
					count.value--;
				size--;
				canRemove = false;
			}
		};
	}

	@EqualsAndHashCode
	private static class Count {
		private int value = 0;
	}
}