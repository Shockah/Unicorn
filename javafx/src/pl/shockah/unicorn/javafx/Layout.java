package pl.shockah.unicorn.javafx;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
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
	private FXMLLoader getLoader() {
		FXMLLoader loader = new FXMLLoader(manager.getLayoutUrl(name));
		loader.setControllerFactory(param -> {
			try {
				return param.newInstance();
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		});
		return loader;
	}

	@Nonnull
	public T load() {
		try {
			FXMLLoader loader = getLoader();

			loader.load();
			T controller = loader.getController();
			controller.setRoot(loader.getRoot());
			setupControllerRecursive(controller);

			return controller;
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public void loadIntoController(@Nonnull T controller) {
		try {
			FXMLLoader loader = getLoader();
			loader.setController(controller);

			loader.load();
			controller.setRoot(loader.getRoot());
			setupControllerRecursive(controller);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	private static void setupControllerRecursive(@Nonnull Controller controller) {
		setupControllerRecursive(controller, new LinkedHashSet<>());
	}

	private static void setupControllerRecursive(@Nonnull Controller controller, @Nonnull Set<Controller> handledControllers) {
		if (handledControllers.contains(controller))
			return;

		handledControllers.add(controller);

		controller.getRoot().sceneProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == null && newValue != null)
				controller.onAddedToScene(newValue);
			else if (oldValue != null && newValue == null)
				controller.onRemovedFromScene(oldValue);
		});

		try {
			for (Field field : getInstanceFields(controller.getClass())) {
				if (!Controller.class.isAssignableFrom(field.getType()))
					continue;

				field.setAccessible(true);
				Controller childController = (Controller)field.get(controller);
				if (childController == null)
					continue;

				Field controllerRootField = getMatchingControllerRootField(controller, childController, field);
				if (controllerRootField != null) {
					controllerRootField.setAccessible(true);
					childController.setRoot((Region)controllerRootField.get(controller));
				}

				if (field.getAnnotation(FXML.class) != null) {
					Controller.InjectedChild injectedChild = field.getAnnotation(Controller.InjectedChild.class);
					if (injectedChild != null) {
						Field matchingField = getMatchingChildInjectField(injectedChild, controller, childController);
						if (matchingField != null) {
							matchingField.setAccessible(true);
							matchingField.set(childController, controller);
						}
					}
				}

				setupControllerRecursive(childController, handledControllers);
			}
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}

		controller.onLoaded();
	}

	@Nullable
	private static Field getMatchingControllerRootField(@Nonnull Controller parent, @Nonnull Controller child, @Nonnull Field childControllerField) {
		String rootFieldName = childControllerField.getName();
		rootFieldName = rootFieldName.substring(0, rootFieldName.length() - "Controller".length());

		for (Field field : getInstanceFields(parent.getClass())) {
			if (field == childControllerField)
				continue;
			if (!Region.class.isAssignableFrom(field.getType()))
				continue;
			if (!field.getName().equals(rootFieldName))
				continue;
			return field;
		}

		return null;
	}

	@Nullable
	private static Field getMatchingChildInjectField(@Nonnull Controller.InjectedChild injectedChild, @Nonnull Controller parent, @Nonnull Controller child) {
		for (Field field : getInstanceFields(child.getClass())) {
			Controller.InjectedParent injectedParent = field.getAnnotation(Controller.InjectedParent.class);
			if (injectedParent == null)
				continue;

			if (!field.getType().isInstance(parent))
				continue;

			if (!injectedParent.parentName().equals("")) {
				if (!injectedParent.parentName().equals(injectedChild.parentName()))
					continue;
			}

			return field;
		}

		return null;
	}

	@Nonnull
	private static List<Field> getInstanceFields(@Nonnull Class<?> clazz) {
		return getAllFields(clazz).stream()
				.filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
				.collect(Collectors.toList());
	}

	@Nonnull
	private static List<Field> getAllFields(@Nonnull Class<?> clazz) {
		return getAllFields(clazz, Controller.class);
	}

	@Nonnull
	private static List<Field> getAllFields(@Nonnull Class<?> clazz, @Nonnull Class<?> topClass) {
		List<Field> result = new ArrayList<>();
		Class<?> current = clazz;

		while (current != null) {
			result.addAll(Arrays.asList(current.getDeclaredFields()));
			if (current == topClass)
				break;
			current = current.getSuperclass();
		}

		return result;
	}
}