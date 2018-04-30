package pl.shockah.unicorn;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import pl.shockah.unicorn.func.Action1;
import pl.shockah.unicorn.func.Action2;
import pl.shockah.unicorn.func.Action3;
import pl.shockah.unicorn.func.Func1;

public class ReadWriteMap<K, V> extends ReadWriteObject<Map<K, V>> implements Map<K, V> {
	public ReadWriteMap(Map<K, V> underlyingMap) {
		super(underlyingMap);
	}
	
	public ReadWriteMap(Map<K, V> underlyingMap, boolean fair) {
		super(underlyingMap, fair);
	}
	
	@Override
	protected Map<K, V> prepareForRead() {
		return Collections.unmodifiableMap(super.prepareForRead());
	}
	
	public void iterate(final Action2<K, V> f) {
		readOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				for (Map.Entry<K, V> entry : map.entrySet()) {
					f.call(entry.getKey(), entry.getValue());
				}
			}
		});
	}
	
	public void iterateKeys(final Action1<K> f) {
		readOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				for (Map.Entry<K, V> entry : map.entrySet()) {
					f.call(entry.getKey());
				}
			}
		});
	}
	
	public void iterateValues(final Action1<V> f) {
		readOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				for (Map.Entry<K, V> entry : map.entrySet()) {
					f.call(entry.getValue());
				}
			}
		});
	}
	
	public void iterate(final Action3<K, V, ReadIterator<K, V>> f) {
		readOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				new ReadIterator<>(map.entrySet()).iterate(f);
			}
		});
	}
	
	public V findOne(final Func1<V, Boolean> f) {
		return readOperation(new Func1<Map<K, V>, V>() {
			@Override
			public V call(Map<K, V> map) {
				for (V value : map.values()) {
					if (f.call(value))
						return value;
				}
				return null;
			}
		});
	}
	
	public void iterateAndWrite(final Action3<K, V, WriteIterator<K, V>> f) {
		writeOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				new WriteIterator<K, V>(map.entrySet()).iterate(f);
			}
		});
	}
	
	@Override
	public int size() {
		return readOperation(new Func1<Map<K, V>, Integer>() {
			@Override
			public Integer call(Map<K, V> map) {
				return map.size();
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(new Func1<Map<K, V>, Boolean>() {
			@Override
			public Boolean call(Map<K, V> map) {
				return map.isEmpty();
			}
		});
	}

	@Override
	public boolean containsKey(final Object key) {
		return readOperation(new Func1<Map<K, V>, Boolean>() {
			@Override
			public Boolean call(Map<K, V> map) {
				return map.containsKey(key);
			}
		});
	}

	@Override
	public boolean containsValue(final Object value) {
		return readOperation(new Func1<Map<K, V>, Boolean>() {
			@Override
			public Boolean call(Map<K, V> map) {
				return map.containsValue(value);
			}
		});
	}

	@Override
	public V get(final Object key) {
		return readOperation(new Func1<Map<K, V>, V>() {
			@Override
			public V call(Map<K, V> map) {
				return map.get(key);
			}
		});
	}

	@Override
	public V put(final K key, final V value) {
		return writeOperation(new Func1<Map<K, V>, V>() {
			@Override
			public V call(Map<K, V> map) {
				return map.put(key, value);
			}
		});
	}

	@Override
	public V remove(final Object key) {
		return writeOperation(new Func1<Map<K, V>, V>() {
			@Override
			public V call(Map<K, V> map) {
				return map.remove(key);
			}
		});
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		writeOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				map.putAll(m);
			}
		});
	}

	@Override
	public void clear() {
		writeOperation(new Action1<Map<K, V>>() {
			@Override
			public void call(Map<K, V> map) {
				map.clear();
			}
		});
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
	public static class ReadIterator<K, V> {
		protected final Set<Map.Entry<K, V>> set;
		protected boolean shouldStop = false;
		protected Map.Entry<K, V> currentEntry;
		
		public ReadIterator(Set<Map.Entry<K, V>> set) {
			this.set = set;
		}
		
		public void iterate(Action3<K, V, ReadIterator<K, V>> f) {
			for (Map.Entry<K, V> entry : set) {
				currentEntry = entry;
				f.call(entry.getKey(), entry.getValue(), this);
				if (shouldStop)
					break;
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
	}
	
	public static class WriteIterator<K, V> {
		protected final Set<Map.Entry<K, V>> set;
		protected boolean shouldStop = false;
		protected Map.Entry<K, V> currentEntry;
		
		public WriteIterator(Set<Map.Entry<K, V>> set) {
			this.set = set;
		}
		
		public void iterate(Action3<K, V, WriteIterator<K, V>> f) {
			for (Map.Entry<K, V> entry : set) {
				currentEntry = entry;
				f.call(entry.getKey(), entry.getValue(), this);
				if (shouldStop)
					break;
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
		
		public void set(V value) {
			currentEntry.setValue(value);
		}
	}
}