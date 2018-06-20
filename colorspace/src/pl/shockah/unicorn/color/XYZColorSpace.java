package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class XYZColorSpace implements ColorSpace<XYZColorSpace> {
	@EqualsAndHashCode
	public static final class Reference {
		public static final Reference D50_2 = new Reference(96.422f, 100f, 82.521f);
		public static final Reference D50_10 = new Reference(96.720f, 100f, 81.427f);

		public static final Reference D65_2 = new Reference(95.047f, 100f, 108.883f);
		public static final Reference D65_10 = new Reference(94.811f, 100f, 107.304f);

		public final float x;
		public final float y;
		public final float z;

		public Reference(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public float x;
	public float y;
	public float z;

	public XYZColorSpace(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Nonnull
	public static XYZColorSpace from(@Nonnull RGBColorSpace rgb) {
		return from(rgb.r, rgb.g, rgb.b);
	}

	@Nonnull
	public static XYZColorSpace from(float r, float g, float b) {
		r = r > 0.04045f ? (float)Math.pow((r + 0.055f) / 1.055f, 2.4f) : r / 12.92f;
		g = g > 0.04045f ? (float)Math.pow((g + 0.055f) / 1.055f, 2.4f) : g / 12.92f;
		b = b > 0.04045f ? (float)Math.pow((b + 0.055f) / 1.055f, 2.4f) : b / 12.92f;

		r *= 100;
		g *= 100;
		b *= 100;

		return new XYZColorSpace(
				r * 0.4124f + g * 0.3576f + b * 0.1805f,
				r * 0.2126f + g * 0.7152f + b * 0.0722f,
				r * 0.0193f + g * 0.1192f + b * 0.9505f
		);
	}

	@Override
	public String toString() {
		return String.format("[XYZColorSpace: X:%.3f Y:%.3f Z:%.3f]", x, y, z);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		float x = this.x / 100;
		float y = this.y / 100;
		float z = this.z / 100;

		float r = x * 3.2406f - y * 1.5372f - z * 0.4986f;
		float g = -x * 0.9689f + y * 1.8758f + z * 0.0415f;
		float b = x * 0.0557f - y * 0.2040f + z * 1.0570f;

		r = r > 0.0031308f ? 1.055f * (float)Math.pow(r, 1f / 2.4f) - 0.055f : r * 12.92f;
		g = g > 0.0031308f ? 1.055f * (float)Math.pow(g, 1f / 2.4f) - 0.055f : g * 12.92f;
		b = b > 0.0031308f ? 1.055f * (float)Math.pow(b, 1f / 2.4f) - 0.055f : b * 12.92f;

		return new RGBColorSpace(r, g, b);
	}

	@Override
	public float getDistance(@Nonnull XYZColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(x - other.x, 2) * 0.01f
						+ Math.pow(y - other.y, 2) * 0.01f
						+ Math.pow(z - other.z, 2) * 0.01f
		);
	}

	@Nonnull
	public RGBColorSpace toExactRGB() {
		RGBColorSpace rgb = toRGB();
		if (rgb.r < 0 || rgb.r > 1)
			throw new IllegalArgumentException("Cannot convert to RGB - R outside the 0-1 bounds.");
		if (rgb.g < 0 || rgb.g > 1)
			throw new IllegalArgumentException("Cannot convert to RGB - G outside the 0-1 bounds.");
		if (rgb.b < 0 || rgb.b > 1)
			throw new IllegalArgumentException("Cannot convert to RGB - B outside the 0-1 bounds.");
		return rgb;
	}

	@Override
	@Nonnull
	public XYZColorSpace ease(@Nonnull XYZColorSpace other, float f) {
		return new XYZColorSpace(
				Easing.linear.ease(x, other.x, f),
				Easing.linear.ease(y, other.y, f),
				Easing.linear.ease(z, other.z, f)
		);
	}
}