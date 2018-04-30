package pl.shockah.unicorn;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

public class SortedLinkedList<E> extends LinkedList<E> {
	private static final long serialVersionUID = -4751208078850005267L;
	
	protected final Comparator<? super E> comparator;
	
	public SortedLinkedList() {
		super();
		comparator = null;
	}

	public SortedLinkedList(Comparator<? super E> comparator) {
		super();
		this.comparator = comparator;
	}
	
	@SuppressWarnings("unchecked")
	protected int compareObjects(E e1, E e2) {
		if (comparator != null)
			return comparator.compare(e1, e2);
		return ((Comparable<E>)e1).compareTo(e2);
	}

	@Override
	public boolean add(E e) {
		ListIterator<E> it = listIterator();
		while (it.hasNext()) {
			E next = it.next();
			if (compareObjects(e, next) < 0) {
				it.previous();
				it.add(e);
				it.next();
				return true;
			}
		}
		
		super.add(e);
		return true;
	}

	@Override
	public boolean offer(E e) {
		add(e);
		return true;
	}

	@Override
	public void push(E e) {
		add(e);
	}

	@Override
	public Object clone() {
		SortedLinkedList<E> ret = new SortedLinkedList<>();
		for (E e : this)
			ret.add(e);
		return ret;
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c)  {
		int size = size();
		//TODO: implement quick addAll
		
		for (E e : c)
			add(e);
		return size != size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> c) {
		int size = size();
		
		if (c instanceof SortedLinkedList<?>) {
			SortedLinkedList<? extends E> c2 = (SortedLinkedList<? extends E>)c;
			if (c2.comparator == comparator) {
				ListIterator<E> it = listIterator();
				Iterator<? extends E> it2 = c2.iterator();
				L: while (it2.hasNext()) {
					E toRemove = it2.next();
					while (it.hasNext()) {
						E next = it.next();
						if (Objects.equals(toRemove, next)) {
							it.remove();
							continue L;
						}
					}
				}
				return size != size();
			}
		}
		
		return super.removeAll(c);
	}
}