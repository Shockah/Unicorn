package pl.shockah.unicorn.collection;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

public interface MultiSet<E> extends Iterable<E> {
	void clear();

	int size();

	int distinctSize();

	int count(Object element);

	int add(E element);

	int remove(E element);

	@Nonnull Set<E> distinct();

	@Nonnull Iterator<E> distinctIterator();

	@Nonnull Set<Entry<E>> entries();

	boolean contains(Object element);

	int add(E element, int occurences);

	int remove(E element, int occurences);

	boolean isEmpty();

	interface Entry<E> {
		E getElement();
		int getCount();
	}
}