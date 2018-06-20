package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class LabColorSpace implements ColorSpace<LabColorSpace> {
	public float l;
	public float a;
	public float b;

	public LabColorSpace(float l, float a, float b) {
		this.l = l;
		this.a = a;
		this.b = b;
	}

	@Nonnull
	public static LabColorSpace from(@Nonnull XYZColorSpace xyz) {
		return from(xyz, XYZColorSpace.Reference.D65_2);
	}

	@Nonnull
	public static LabColorSpace from(@Nonnull XYZColorSpace xyz, @Nonnull XYZColorSpace.Reference reference) {
		float x = xyz.x / reference.x;
		float y = xyz.y / reference.y;
		float z = xyz.z / reference.z;

		x = x > 0.008856f ? (float)Math.pow(x, 1f / 3f) : (7.787f * x) + (16f / 116f);
		y = y > 0.008856f ? (float)Math.pow(y, 1f / 3f) : (7.787f * y) + (16f / 116f);
		z = z > 0.008856f ? (float)Math.pow(z, 1f / 3f) : (7.787f * z) + (16f / 116f);

		return new LabColorSpace(
				116 * y - 16,
				500 * (x - y),
				200 * (y - z)
		);
	}

	@Override
	public String toString() {
		return String.format("[LabColorSpace: L:%.3f a:%.3f b:%.3f]", l, a, b);
	}

	@Nonnull
	public XYZColorSpace toXYZ() {
		return toXYZ(XYZColorSpace.Reference.D65_2);
	}

	@Nonnull
	public XYZColorSpace toXYZ(@Nonnull XYZColorSpace.Reference reference) {
		float y = (l + 16) / 116f;
		float x = a / 500f + y;
		float z = y - b / 200f;

		x = x > 0.008856f ? (float)Math.pow(x, 3f) : (x - 16f / 116f) / 7.787f;
		y = y > 0.008856f ? (float)Math.pow(y, 3f) : (y - 16f / 116f) / 7.787f;
		z = z > 0.008856f ? (float)Math.pow(z, 3f) : (z - 16f / 116f) / 7.787f;

		return new XYZColorSpace(
				x * reference.x,
				y * reference.y,
				z * reference.z
		);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		return toXYZ().toRGB();
	}

	@Override
	public float getDistance(@Nonnull LabColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(l - other.l, 2) * 0.01f
						+ Math.pow(a - other.a, 2) * 0.005f
						+ Math.pow(b - other.b, 2) * 0.005f
		);
	}

	@Nonnull
	public RGBColorSpace toRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toXYZ(reference).toRGB();
	}

	@Nonnull
	public RGBColorSpace toExactRGB() {
		return toXYZ().toExactRGB();
	}

	@Nonnull
	public RGBColorSpace toExactRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toXYZ(reference).toExactRGB();
	}

	@Override
	@Nonnull
	public LabColorSpace ease(@Nonnull LabColorSpace other, float f) {
		return new LabColorSpace(
				Easing.linear.ease(l, other.l, f),
				Easing.linear.ease(a, other.a, f),
				Easing.linear.ease(b, other.b, f)
		);
	}
}