package pl.shockah.unicorn.javafx;

import java.net.URL;

import javax.annotation.Nonnull;

import javafx.scene.layout.Region;

public interface LayoutManager {
	@Nonnull
	URL getLayoutUrl(@Nonnull String layoutName);

	@Nonnull
	default <T extends Controller<? extends Region>> Layout<T> getLayout(@Nonnull String layoutName) {
		return new Layout<>(this, layoutName);
	}

	class Classpath implements LayoutManager {
		@Nonnull
		public final String pathFormat;

		public Classpath(@Nonnull String pathFormat) {
			this.pathFormat = pathFormat;
		}

		@Nonnull
		@Override
		public URL getLayoutUrl(@Nonnull String layoutName) {
			URL url = getClass().getClassLoader().getResource(String.format(pathFormat, layoutName));
			if (url == null)
				throw new IllegalArgumentException(String.format("Could not resolve layout `%s`.", layoutName));
			return url;
		}
	}
}