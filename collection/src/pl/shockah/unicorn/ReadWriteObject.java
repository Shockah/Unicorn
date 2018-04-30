package pl.shockah.unicorn;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pl.shockah.unicorn.func.Action1;
import pl.shockah.unicorn.func.Func1;

public class ReadWriteObject<T> {
	protected final T obj;
	protected final ReentrantReadWriteLock lock;
	
	public ReadWriteObject(T obj) {
		this(obj, true);
	}
	
	public ReadWriteObject(T obj, boolean fair) {
		this.obj = obj;
		lock = new ReentrantReadWriteLock(fair);
	}
	
	public void readOperation(Action1<T> f) {
		lock.readLock().lock();
		try {
			f.call(prepareForRead());
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public <R> R readOperation(Func1<T, R> f) {
		lock.readLock().lock();
		try {
			return f.call(prepareForRead());
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public boolean tryReadOperation(long timeout, TimeUnit unit, Action1<T> f) throws InterruptedException {
		if (lock.readLock().tryLock(timeout, unit)) {
			try {
				f.call(prepareForRead());
			} finally {
				lock.readLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void writeOperation(Action1<T> f) {
		lock.writeLock().lock();
		try {
			f.call(prepareForWrite());
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public <R> R writeOperation(Func1<T, R> f) {
		lock.writeLock().lock();
		try {
			return f.call(prepareForWrite());
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean tryWriteOperation(long timeout, TimeUnit unit, Action1<T> f) throws InterruptedException {
		if (lock.writeLock().tryLock(timeout, unit)) {
			try {
				f.call(prepareForWrite());
			} finally {
				lock.writeLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	protected T prepareForRead() {
		return obj;
	}
	
	protected T prepareForWrite() {
		return obj;
	}
}