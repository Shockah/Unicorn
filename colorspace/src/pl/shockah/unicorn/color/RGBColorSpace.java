package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class RGBColorSpace implements ColorSpace<RGBColorSpace> {
	public float r;
	public float g;
	public float b;

	public RGBColorSpace(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public String toString() {
		return String.format("[RGBColorSpace: R:%.3f G:%.3f B:%.3f]", r, g, b);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		return this;
	}

	@Override
	public float getDistance(@Nonnull RGBColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(r - other.r, 2)
						+ Math.pow(g - other.g, 2)
						+ Math.pow(b - other.b, 2)
		);
	}

	@Override
	@Nonnull
	public RGBColorSpace ease(@Nonnull RGBColorSpace other, float f) {
		return new RGBColorSpace(
				Easing.linear.ease(r, other.r, f),
				Easing.linear.ease(g, other.g, f),
				Easing.linear.ease(b, other.b, f)
		);
	}
}