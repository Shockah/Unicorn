package pl.shockah.unicorn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class EventBus {
	@Nonnull
	private final Map<Class<?>, List<Listener<?>>> mappedEventTypes = new HashMap<>();

	@Nonnull
	private final Map<Object, List<Listener<?>>> mappedListeners = new HashMap<>();

	public void register(@Nonnull final Object listener) {
		List<Listener<?>> listenerMappedListeners = null;
		for (final Method method : listener.getClass().getMethods()) {
			if (method.getParameterTypes().length == 1 && method.getAnnotation(Subscribe.class) != null) {
				Class<?> clazz = method.getParameterTypes()[0];
				Listener<?> mappedListener = new Listener<Object>() {
					@Override
					public void onEvent(Object event) {
						try {
							method.invoke(listener, event);
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new UnexpectedException(e);
						}
					}
				};

				if (listenerMappedListeners == null) {
					if (!mappedListeners.containsKey(listener))
						mappedListeners.put(listener, new ArrayList<Listener<?>>());
					listenerMappedListeners = mappedListeners.get(listener);
				}
				listenerMappedListeners.add(mappedListener);

				if (!mappedEventTypes.containsKey(clazz))
					mappedEventTypes.put(clazz, new ArrayList<Listener<?>>());
				List<Listener<?>> eventTypeListeners = mappedEventTypes.get(clazz);
				eventTypeListeners.add(mappedListener);
			}
		}
	}

	public void unregister(@Nonnull Object listener) {
		List<Listener<?>> listenerMappedListeners = mappedListeners.get(listener);
		if (listenerMappedListeners == null)
			return;

		mappedListeners.remove(listener);

		Iterator<Map.Entry<Class<?>, List<Listener<?>>>> iterator = mappedEventTypes.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Class<?>, List<Listener<?>>> entry = iterator.next();
			entry.getValue().removeAll(listenerMappedListeners);
			if (entry.getValue().isEmpty())
				iterator.remove();
		}
	}

	@SuppressWarnings("unchecked")
	public void post(Object event) {
		for (Map.Entry<Class<?>, List<Listener<?>>> entry : mappedEventTypes.entrySet()) {
			if (entry.getKey().isInstance(event)) {
				for (Listener<?> mappedListener : entry.getValue()) {
					Listener<Object> rawListener = (Listener<Object>)mappedListener;
					rawListener.onEvent(event);
				}
			}
		}
	}

	private interface Listener<T> {
		void onEvent(T event);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Subscribe {
	}
}