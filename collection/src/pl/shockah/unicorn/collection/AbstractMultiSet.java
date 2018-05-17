package pl.shockah.unicorn.collection;

public abstract class AbstractMultiSet<E> implements MultiSet<E> {
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractMultiSet<?>))
			return false;
		AbstractMultiSet<E> other = (AbstractMultiSet<E>)o;

		if (size() != other.size())
			return false;
		if (distinctSize() != other.distinctSize())
			return false;
		for (Entry<E> entry : entries()) {
			int count = other.count(entry.getElement());
			if (count != entry.getCount())
				return false;
		}

		return true;
	}

	@Override
	public boolean contains(Object element) {
		return count(element) != 0;
	}

	@Override
	public int add(E element, int occurences) {
		int count = 0;
		for (int i = 0; i < occurences; i++) {
			count = add(element);
		}
		return count;
	}

	@Override
	public int remove(E element, int occurences) {
		int count = 0;
		for (int i = 0; i < occurences; i++) {
			count = remove(element);
		}
		return count;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
}