package pl.shockah.unicorn.javafx;

import javax.annotation.Nonnull;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class Controller {
	@Getter
	@Setter(AccessLevel.PROTECTED)
	Region view;

	protected void onLoaded() {
	}

	protected void onAddedToScene(@Nonnull Scene scene) {
	}

	protected void onRemovedFromScene(@Nonnull Scene scene) {
	}
}