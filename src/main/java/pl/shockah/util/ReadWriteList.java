package pl.shockah.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import pl.shockah.util.func.Action1;
import pl.shockah.util.func.Action2;
import pl.shockah.util.func.Func1;

public class ReadWriteList<T> extends ReadWriteObject<List<T>> implements List<T> {
	public ReadWriteList(List<T> underlyingList) {
		super(underlyingList);
	}
	
	public ReadWriteList(List<T> underlyingList, boolean fair) {
		super(underlyingList, fair);
	}
	
	@Override
	protected List<T> prepareForRead() {
		return Collections.unmodifiableList(super.prepareForRead());
	}
	
	public void iterate(Action1<T> f) {
		readOperation((List<T> list) -> {
			Iterator<T> iterator = list.iterator();
			while (iterator.hasNext()) {
				f.call(iterator.next());
			}
		});
	}
	
	public void iterate(Action2<T, ReadIterator<T>> f) {
		readOperation(list -> {
			new ReadIterator<T>(list.iterator()).iterate(f);
		});
	}
	
	public T filterFirst(Func1<T, Boolean> f) {
		return readOperation(list -> {
			for (T t : list) {
				if (f.call(t))
					return t;
			}
			return null;
		});
	}
	
	public <R> R firstResult(Func1<T, R> f) {
		return readOperation(list -> {
			for (T t : list) {
				R result = f.call(t);
				if (result != null)
					return result;
			}
			return null;
		});
	}
	
	public void iterateAndWrite(Action2<T, WriteIterator<T>> f) {
		writeOperation(list -> {
			new WriteIterator<T>(list.listIterator()).iterate(f);
		});
	}

	@Override
	public int size() {
		return readOperation(list -> {
			return list.size();
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(list -> {
			return list.isEmpty();
		});
	}

	@Override
	public boolean contains(Object o) {
		return readOperation(list -> {
			return list.contains(o);
		});
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return readOperation(list -> {
			return list.toArray();
		});
	}

	@Override
	public <R> R[] toArray(R[] a) {
		return readOperation(list -> {
			return list.toArray(a);
		});
	}

	@Override
	public boolean add(T e) {
		return writeOperation(list -> {
			return list.add(e);
		});
	}

	@Override
	public boolean remove(Object o) {
		return writeOperation(list -> {
			return list.remove(o);
		});
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return readOperation(list -> {
			return list.containsAll(c);
		});
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return writeOperation(list -> {
			return list.addAll(c);
		});
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return writeOperation(list -> {
			return list.addAll(index, c);
		});
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return writeOperation(list -> {
			return list.removeAll(c);
		});
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return writeOperation(list -> {
			return list.retainAll(c);
		});
	}

	@Override
	public void clear() {
		writeOperation(list -> {
			list.clear();
		});
	}

	@Override
	public T get(int index) {
		return readOperation(list -> {
			return list.get(index);
		});
	}

	@Override
	public T set(int index, T element) {
		return writeOperation(list -> {
			return list.set(index, element);
		});
	}

	@Override
	public void add(int index, T element) {
		writeOperation(list -> {
			list.add(index, element);
		});
	}

	@Override
	public T remove(int index) {
		return writeOperation(list -> {
			return list.remove(index);
		});
	}

	@Override
	public int indexOf(Object o) {
		return readOperation(list -> {
			return list.indexOf(o);
		});
	}

	@Override
	public int lastIndexOf(Object o) {
		return readOperation(list -> {
			return list.lastIndexOf(o);
		});
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReadWriteList<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	public static class ReadIterator<T> {
		protected final Iterator<T> iterator;
		protected boolean shouldStop = false;
		
		public ReadIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}
		
		public void iterate(Action2<T, ReadIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
	}
	
	public static class WriteIterator<T> {
		private final ListIterator<T> listIterator;
		protected boolean shouldStop = false;
		
		public WriteIterator(ListIterator<T> iterator) {
			listIterator = iterator;
		}
		
		public void iterate(Action2<T, WriteIterator<T>> f) {
			while (!shouldStop && listIterator.hasNext()) {
				f.call(listIterator.next(), this);
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
		
		public void add(T e) {
			listIterator.add(e);
		}
		
		public void remove() {
			listIterator.remove();
		}
		
		public void set(T e) {
			listIterator.set(e);
		}
	}
}