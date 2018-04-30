package pl.shockah.unicorn;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import pl.shockah.unicorn.func.Action1;
import pl.shockah.unicorn.func.Action2;
import pl.shockah.unicorn.func.Func1;

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
	
	public void iterate(final Action1<T> f) {
		readOperation(new Action1<List<T>>() {
			@Override
			public void call(List<T> list) {
				Iterator<T> iterator = list.iterator();
				while (iterator.hasNext()) {
					f.call(iterator.next());
				}
			}
		});
	}
	
	public void iterate(final Action2<T, ReadIterator<T>> f) {
		readOperation(new Action1<List<T>>() {
			@Override
			public void call(List<T> list) {
				new ReadIterator<T>(list.iterator()).iterate(f);
			}
		});
	}
	
	public T filterFirst(final Func1<T, Boolean> f) {
		return readOperation(new Func1<List<T>, T>() {
			@Override
			public T call(List<T> list) {
				for (T t : list) {
					if (f.call(t))
						return t;
				}
				return null;
			}
		});
	}
	
	public <R> R firstResult(final Func1<T, R> f) {
		return readOperation(new Func1<List<T>, R>() {
			@Override
			public R call(List<T> list) {
				for (T t : list) {
					R result = f.call(t);
					if (result != null)
						return result;
				}
				return null;
			}
		});
	}
	
	public void iterateAndWrite(final Action2<T, WriteIterator<T>> f) {
		writeOperation(new Action1<List<T>>() {
			@Override
			public void call(List<T> list) {
				new WriteIterator<T>(list.listIterator()).iterate(f);
			}
		});
	}

	@Override
	public int size() {
		return readOperation(new Func1<List<T>, Integer>() {
			@Override
			public Integer call(List<T> list) {
				return list.size();
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.isEmpty();
			}
		});
	}

	@Override
	public boolean contains(final Object o) {
		return readOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.contains(o);
			}
		});
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return readOperation(new Func1<List<T>, Object[]>() {
			@Override
			public Object[] call(List<T> list) {
				return list.toArray();
			}
		});
	}

	@Override
	public <R> R[] toArray(final R[] a) {
		return readOperation(new Func1<List<T>, R[]>() {
			@Override
			public R[] call(List<T> list) {
				return list.toArray(a);
			}
		});
	}

	@Override
	public boolean add(final T e) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.add(e);
			}
		});
	}

	@Override
	public boolean remove(final Object o) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.remove(o);
			}
		});
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return readOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.containsAll(c);
			}
		});
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.addAll(c);
			}
		});
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends T> c) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.addAll(index, c);
			}
		});
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.removeAll(c);
			}
		});
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return writeOperation(new Func1<List<T>, Boolean>() {
			@Override
			public Boolean call(List<T> list) {
				return list.retainAll(c);
			}
		});
	}

	@Override
	public void clear() {
		writeOperation(new Action1<List<T>>() {
			@Override
			public void call(List<T> list) {
				list.clear();
			}
		});
	}

	@Override
	public T get(final int index) {
		return readOperation(new Func1<List<T>, T>() {
			@Override
			public T call(List<T> list) {
				return list.get(index);
			}
		});
	}

	@Override
	public T set(final int index, final T element) {
		return writeOperation(new Func1<List<T>, T>() {
			@Override
			public T call(List<T> list) {
				return list.set(index, element);
			}
		});
	}

	@Override
	public void add(final int index, final T element) {
		writeOperation(new Action1<List<T>>() {
			@Override
			public void call(List<T> list) {
				list.add(index, element);
			}
		});
	}

	@Override
	public T remove(final int index) {
		return writeOperation(new Func1<List<T>, T>() {
			@Override
			public T call(List<T> list) {
				return list.remove(index);
			}
		});
	}

	@Override
	public int indexOf(final Object o) {
		return readOperation(new Func1<List<T>, Integer>() {
			@Override
			public Integer call(List<T> list) {
				return list.indexOf(o);
			}
		});
	}

	@Override
	public int lastIndexOf(final Object o) {
		return readOperation(new Func1<List<T>, Integer>() {
			@Override
			public Integer call(List<T> list) {
				return list.lastIndexOf(o);
			}
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