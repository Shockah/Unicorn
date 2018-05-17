package pl.shockah.unicorn.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import pl.shockah.unicorn.func.Action1;
import pl.shockah.unicorn.func.Action2;
import pl.shockah.unicorn.func.Func1;

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
	
	public void iterate(final Action1<T> f) {
		readOperation(new Action1<Set<T>>() {
			@Override
			public void call(Set<T> set) {
				Iterator<T> iterator = set.iterator();
				while (iterator.hasNext()) {
					f.call(iterator.next());
				}
			}
		});
	}
	
	public void iterate(final Action2<T, ReadIterator<T>> f) {
		readOperation(new Action1<Set<T>>() {
			@Override
			public void call(Set<T> set) {
				new ReadIterator<T>(set.iterator()).iterate(f);
			}
		});
	}
	
	public T filterFirst(final Func1<T, Boolean> f) {
		return readOperation(new Func1<Set<T>, T>() {
			@Override
			public T call(Set<T> set) {
				for (T t : set) {
					if (f.call(t))
						return t;
				}
				return null;
			}
		});
	}
	
	public <R> R firstResult(final Func1<T, R> f) {
		return readOperation(new Func1<Set<T>, R>() {
			@Override
			public R call(Set<T> set) {
				for (T t : set) {
					R result = f.call(t);
					if (result != null)
						return result;
				}
				return null;
			}
		});
	}
	
	public void iterateAndWrite(final Action2<T, WriteIterator<T>> f) {
		writeOperation(new Action1<Set<T>>() {
			@Override
			public void call(Set<T> set) {
				new WriteIterator<T>(set.iterator()).iterate(f);
			}
		});
	}

	@Override
	public int size() {
		return readOperation(new Func1<Set<T>, Integer>() {
			@Override
			public Integer call(Set<T> set) {
				return set.size();
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.isEmpty();
			}
		});
	}

	@Override
	public boolean contains(final Object o) {
		return readOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.contains(o);
			}
		});
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return readOperation(new Func1<Set<T>, Object[]>() {
			@Override
			public Object[] call(Set<T> set) {
				return set.toArray();
			}
		});
	}

	@Override
	public <R> R[] toArray(final R[] a) {
		return readOperation(new Func1<Set<T>, R[]>() {
			@Override
			public R[] call(Set<T> set) {
				return set.toArray(a);
			}
		});
	}

	@Override
	public boolean add(final T e) {
		return writeOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.add(e);
			}
		});
	}

	@Override
	public boolean remove(final Object o) {
		return writeOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.remove(o);
			}
		});
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return readOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.containsAll(c);
			}
		});
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return writeOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.addAll(c);
			}
		});
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return writeOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.removeAll(c);
			}
		});
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return writeOperation(new Func1<Set<T>, Boolean>() {
			@Override
			public Boolean call(Set<T> set) {
				return set.retainAll(c);
			}
		});
	}

	@Override
	public void clear() {
		writeOperation(new Action1<Set<T>>() {
			@Override
			public void call(Set<T> set) {
				set.clear();
			}
		});
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
		private final Iterator<T> iterator;
		protected boolean shouldStop = false;
		
		public WriteIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}
		
		public void iterate(Action2<T, WriteIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
		
		public void remove() {
			iterator.remove();
		}
	}
}