package pl.shockah.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import pl.shockah.util.func.Action1;
import pl.shockah.util.func.Action2;
import pl.shockah.util.func.Action3;
import pl.shockah.util.func.Func1;

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
	
	public void iterate(Action2<K, V> f) {
		readOperation(map -> {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getKey(), entry.getValue());
			}
		});
	}
	
	public void iterateKeys(Action1<K> f) {
		readOperation(map -> {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getKey());
			}
		});
	}
	
	public void iterateValues(Action1<V> f) {
		readOperation(map -> {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getValue());
			}
		});
	}
	
	public void iterate(Action3<K, V, ReadIterator<K, V>> f) {
		readOperation(map -> {
			new ReadIterator<K, V>(map.entrySet()).iterate(f);
		});
	}
	
	public V findOne(Func1<V, Boolean> f) {
		return readOperation(map -> {
			for (V value : map.values()) {
				if (f.call(value))
					return value;
			}
			return null;
		});
	}
	
	public void iterateAndWrite(Action3<K, V, WriteIterator<K, V>> f) {
		writeOperation(map -> {
			new WriteIterator<K, V>(map.entrySet()).iterate(f);
		});
	}
	
	@Override
	public int size() {
		return readOperation(map -> {
			return map.size();
		});
	}

	@Override
	public boolean isEmpty() {
		return readOperation(map -> {
			return map.isEmpty();
		});
	}

	@Override
	public boolean containsKey(Object key) {
		return readOperation(map -> {
			return map.containsKey(key);
		});
	}

	@Override
	public boolean containsValue(Object value) {
		return readOperation(map -> {
			return map.containsValue(value);
		});
	}

	@Override
	public V get(Object key) {
		return readOperation(map -> {
			return map.get(key);
		});
	}

	@Override
	public V put(K key, V value) {
		return writeOperation(map -> {
			return map.put(key, value);
		});
	}

	@Override
	public V remove(Object key) {
		return writeOperation(map -> {
			return map.remove(key);
		});
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		writeOperation(map -> {
			map.putAll(m);
		});
	}

	@Override
	public void clear() {
		writeOperation(map -> {
			map.clear();
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
		
		private ReadIterator(Set<Map.Entry<K, V>> set) {
			this.set = set;
		}
		
		private void iterate(Action3<K, V, ReadIterator<K, V>> f) {
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
	
	public static class WriteIterator<K, V> extends ReadIterator<K, V> {
		private WriteIterator(Set<Map.Entry<K, V>> set) {
			super(set);
		}
		
		private void iterate(Action3<K, V, WriteIterator<K, V>> f) {
			for (Map.Entry<K, V> entry : set) {
				currentEntry = entry;
				f.call(entry.getKey(), entry.getValue(), this);
				if (shouldStop)
					break;
			}
		}
		
		public void set(V value) {
			currentEntry.setValue(value);
		}
	}
}