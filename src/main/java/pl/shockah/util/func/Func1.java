package pl.shockah.util.func;

@FunctionalInterface
public interface Func1<T1, R> {
	R call(T1 t1);
}