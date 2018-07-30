package pl.shockah.unicorn.javafx;

import java.io.IOException;

import javax.annotation.Nonnull;

import javafx.fxml.FXMLLoader;
import pl.shockah.unicorn.UnexpectedException;

public final class Layout<T extends Controller> {
	@Nonnull
	public final LayoutManager manager;

	@Nonnull
	public final String name;

	public Layout(@Nonnull LayoutManager manager, @Nonnull String name) {
		this.manager = manager;
		this.name = name;
	}

	@Nonnull
	public T load() {
		try {
			FXMLLoader loader = new FXMLLoader(manager.getLayoutUrl(name));
			loader.setControllerFactory(param -> {
				try {
					return param.newInstance();
				} catch (Exception e) {
					throw new UnexpectedException(e);
				}
			});

			loader.load();
			T controller = loader.getController();
			controller.view = loader.getRoot();
			controller.onLoaded();

			controller.getView().sceneProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue == null && newValue != null)
					controller.onAddedToScene(newValue);
				else if (oldValue != null && newValue == null)
					controller.onRemovedFromScene(oldValue);
			});
			return controller;
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
}