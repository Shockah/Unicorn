package pl.shockah.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import pl.shockah.util.func.Action1;
import pl.shockah.util.func.Action2;
import pl.shockah.util.func.Func1;

public class ReadWriteSet<T> implements Set<T> {
	protected final Set<T> set;
	protected final ReentrantReadWriteLock lock;
	
	public ReadWriteSet(Set<T> underlyingSet) {
		this(underlyingSet, true);
	}
	
	public ReadWriteSet(Set<T> underlyingSet, boolean fair) {
		set = underlyingSet;
		lock = new ReentrantReadWriteLock(fair);
	}
	
	public void readOperation(Action1<Set<T>> f) {
		lock.readLock().lock();
		try {
			f.call(Collections.unmodifiableSet(set));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public <R> R readOperation(Func1<Set<T>, R> f) {
		lock.readLock().lock();
		try {
			return f.call(Collections.unmodifiableSet(set));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public boolean tryReadOperation(long timeout, TimeUnit unit, Action1<Set<T>> f) throws InterruptedException {
		if (lock.readLock().tryLock(timeout, unit)) {
			try {
				f.call(Collections.unmodifiableSet(set));
			} finally {
				lock.readLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void writeOperation(Action1<Set<T>> f) {
		lock.writeLock().lock();
		try {
			f.call(set);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public <R> R writeOperation(Func1<Set<T>, R> f) {
		lock.writeLock().lock();
		try {
			return f.call(set);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean tryWriteOperation(long timeout, TimeUnit unit, Action1<Set<T>> f) throws InterruptedException {
		if (lock.writeLock().tryLock(timeout, unit)) {
			try {
				f.call(set);
			} finally {
				lock.writeLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void iterate(Action1<T> f) {
		lock.readLock().lock();
		try {
			Iterator<T> iterator = set.iterator();
			while (iterator.hasNext()) {
				f.call(iterator.next());
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void iterate(Action2<T, ReadIterator<T>> f) {
		lock.readLock().lock();
		try {
			new ReadIterator<T>(set.iterator()).iterate(f);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public T filterFirst(Func1<T, Boolean> f) {
		lock.readLock().lock();
		try {
			for (T t : set) {
				if (f.call(t))
					return t;
			}
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}
	
	public <R> R firstResult(Func1<T, R> f) {
		lock.readLock().lock();
		try {
			for (T t : set) {
				R result = f.call(t);
				if (result != null)
					return result;
			}
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}
	
	public void iterateAndWrite(Action2<T, WriteIterator<T>> f) {
		lock.writeLock().lock();
		try {
			new WriteIterator<T>(set.iterator()).iterate(f);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int size() {
		lock.readLock().lock();
		int ret = set.size();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		boolean ret = set.isEmpty();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		boolean ret = set.contains(o);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		lock.readLock().lock();
		Object[] ret = set.toArray();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public <R> R[] toArray(R[] a) {
		lock.readLock().lock();
		R[] ret = set.toArray(a);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean add(T e) {
		lock.writeLock().lock();
		boolean ret = set.add(e);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		boolean ret = set.remove(o);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		lock.readLock().lock();
		boolean ret = set.containsAll(c);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		lock.writeLock().lock();
		boolean ret = set.addAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean ret = set.removeAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean ret = set.retainAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		set.clear();
		lock.writeLock().unlock();
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