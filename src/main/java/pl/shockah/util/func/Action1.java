package pl.shockah.util.func;

@FunctionalInterface
public interface Action1<T1> {
	void call(T1 t1);
}