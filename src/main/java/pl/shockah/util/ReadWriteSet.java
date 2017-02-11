package pl.shockah.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import pl.shockah.util.func.Action1;
import pl.shockah.util.func.Action2;
import pl.shockah.util.func.Func1;

public class ReadWriteSet<T> extends ReadWriteObject<Set<T>> implements Set<T> {
	public ReadWriteSet(Set<T> underlyingSet) {
		super(underlyingSet);
	}
	
	public ReadWriteSet(Set<T> underlyingSet, boolean fair) {
		super(underlyingSet, fair);
	}
	
	@Override
	protected Set<T> prepareForRead() {
		return Collections.unmodifiableSet(super.prepareForRead());
	}
	
	public void iterate(Action1<T> f) {
		readOperation((Set<T> set) -> {
			Iterator<T> iterator = set.iterator();
			while (iterator.hasNext()) {
				f.call(iterator.next());
			}
		});
	}
	
	public void iterate(Action2<T, ReadIterator<T>> f) {
		readOperation(set -> {
			new ReadIterator<T>(set.iterator()).iterate(f);
		});
	}
	
	public T filterFirst(Func1<T, Boolean> f) {
		return readOperation(set -> {
			for (T t : set) {
				if (f.call(t))
					return t;
			}
			return null;
		});
	}
	
	public <R> R firstResult(Func1<T, R> f) {
		return readOperation(set -> {
			for (T t : set) {
				R result = f.call(t);
				if (result != null)
					return result;
			}
			return null;
		});
	}
	
	public void iterateAndWrite(Action2<T, WriteIterator<T>> f) {
		writeOperation(set -> {
			new WriteIterator<T>(set.iterator()).iterate(f);
		});
	}

	@Override
	public int size() {
		return readOperation(set -> {
			return set.size();
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(set -> {
			return set.isEmpty();
		});
	}

	@Override
	public boolean contains(Object o) {
		return readOperation(set -> {
			return set.contains(o);
		});
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return readOperation(set -> {
			return set.toArray();
		});
	}

	@Override
	public <R> R[] toArray(R[] a) {
		return readOperation(set -> {
			return set.toArray(a);
		});
	}

	@Override
	public boolean add(T e) {
		return writeOperation(set -> {
			return set.add(e);
		});
	}

	@Override
	public boolean remove(Object o) {
		return writeOperation(set -> {
			return set.remove(o);
		});
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return readOperation(set -> {
			return set.containsAll(c);
		});
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return writeOperation(set -> {
			return set.addAll(c);
		});
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return writeOperation(set -> {
			return set.removeAll(c);
		});
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return writeOperation(set -> {
			return set.retainAll(c);
		});
	}

	@Override
	public void clear() {
		writeOperation(set -> {
			set.clear();
		});
	}
	
	public static class ReadIterator<T> {
		protected final Iterator<T> iterator;
		protected boolean shouldStop = false;
		
		private ReadIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}
		
		private void iterate(Action2<T, ReadIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
	}
	
	public static class WriteIterator<T> extends ReadIterator<T> {
		private final Iterator<T> iterator;
		
		private WriteIterator(Iterator<T> iterator) {
			super(iterator);
			this.iterator = iterator;
		}
		
		private void iterate(Action2<T, WriteIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void remove() {
			iterator.remove();
		}
	}
}