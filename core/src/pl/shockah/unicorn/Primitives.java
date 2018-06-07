package pl.shockah.unicorn;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Primitives {
	@Nonnull
	private static final Map<Class<?>, Class<?>> primitiveToBoxedMap = new HashMap<>();

	@Nonnull
	private static final Map<Class<?>, Class<?>> boxedToPrimitiveMap = new HashMap<>();

	static {
		registerPrimitiveAndBoxedMapping(boolean.class, Boolean.class);
		registerPrimitiveAndBoxedMapping(byte.class, Byte.class);
		registerPrimitiveAndBoxedMapping(char.class, Character.class);
		registerPrimitiveAndBoxedMapping(double.class, Double.class);
		registerPrimitiveAndBoxedMapping(float.class, Float.class);
		registerPrimitiveAndBoxedMapping(int.class, Integer.class);
		registerPrimitiveAndBoxedMapping(long.class, Long.class);
		registerPrimitiveAndBoxedMapping(short.class, Short.class);
	}

	private static void registerPrimitiveAndBoxedMapping(@Nonnull Class<?> primitive, @Nonnull Class<?> boxed) {
		primitiveToBoxedMap.put(primitive, boxed);
		boxedToPrimitiveMap.put(boxed, primitive);
	}

	@Nonnull
	public static Class<?> getBoxedClassForPrimitive(@Nonnull Class<?> primitive) {
		Class<?> result = primitiveToBoxedMap.get(primitive);
		if (result == null)
			throw new IllegalArgumentException(String.format("%s is not a primitive class.", primitive));
		return result;
	}

	@Nonnull
	public static Class<?> getPrimitiveClassForBoxed(@Nonnull Class<?> boxed) {
		Class<?> result = boxedToPrimitiveMap.get(boxed);
		if (result == null)
			throw new IllegalArgumentException(String.format("%s is not a boxed class.", boxed));
		return result;
	}

	@Nonnull
	public static Class<?> getGenericClass(@Nonnull Class<?> clazz) {
		if (primitiveToBoxedMap.containsKey(clazz))
			return primitiveToBoxedMap.get(clazz);
		else
			return clazz;
	}
}