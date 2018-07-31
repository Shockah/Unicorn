package pl.shockah.unicorn.javafx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;

import javafx.scene.Scene;
import javafx.scene.layout.Region;

public abstract class Controller {
	@Nonnull
	public abstract Region getRoot();

	protected void onLoaded() {
	}

	protected void onAddedToScene(@Nonnull Scene scene) {
	}

	protected void onRemovedFromScene(@Nonnull Scene scene) {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface InjectedParent {
		@Nonnull
		Class<? extends Controller> value();

		@Nonnull
		String parentName() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface InjectedChild {
		@Nonnull
		String parentName() default "";
	}
}