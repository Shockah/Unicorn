package pl.shockah.util;

import java.lang.ref.WeakReference;
import java.util.*;

public class WeakValueMap<K, V> implements Map<K, V> {
	protected final Map<K, WeakReference<V>> map;

	public WeakValueMap(Map<K, WeakReference<V>> underlyingMap) {
		map = underlyingMap;
	}

	protected void cleanup() {
		Iterator<Entry<K, WeakReference<V>>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<K, WeakReference<V>> entry = it.next();
			if (entry.getValue().get() == null)
				it.remove();
		}
	}

	@Override
	public int size() {
		cleanup();
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		cleanup();
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		boolean contains = map.containsKey(key);
		if (contains) {
			if (map.get(key).get() == null) {
				map.remove(key);
				contains = false;
			}
		}
		return contains;
	}

	@Override
	public boolean containsValue(Object value) {
		cleanup();
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key).get();
	}

	@Override
	public V put(K key, V value) {
		if (value == null)
			return null;
		WeakReference<V> ref = map.put(key, new WeakReference<V>(value));
		return ref == null ? null : ref.get();
	}

	@Override
	public V remove(Object key) {
		WeakReference<V> ref = map.remove(key);
		return ref == null ? null : ref.get();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			map.put(entry.getKey(), new WeakReference<V>(entry.getValue()));
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		cleanup();
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		cleanup();
		List<V> values = new ArrayList<>(map.size());
		for (WeakReference<V> ref : map.values()) {
			values.add(ref.get());
		}
		return values;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Entry<K, V>> entrySet() {
		cleanup();
		Set<Entry<K, V>> entries = new LinkedHashSet<>();
		for (Entry<K, WeakReference<V>> entry : map.entrySet()) {
			final Entry<K, WeakReference<V>> f_entry = entry;
			entries.add(new Entry<K, V>() {
				@Override
				public K getKey() {
					return f_entry.getKey();
				}

				@Override
				public V getValue() {
					return f_entry.getValue().get();
				}

				@Override
				public V setValue(V value) {
					return f_entry.setValue(new WeakReference<>(value)).get();
				}

				@Override
				public boolean equals(Object o) {
					if (o == null)
						return false;
					if (getClass() != o.getClass())
						return false;
					Entry<K, V> entry = (Entry<K, V>)o;
					return getKey().equals(entry.getKey()) && getValue().equals(entry.getValue());
				}

				@Override
				public int hashCode() {
					return f_entry.hashCode();
				}
			});
		}
		return entries;
	}
}