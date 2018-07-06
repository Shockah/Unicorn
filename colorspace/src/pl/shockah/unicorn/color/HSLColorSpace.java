package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class HSLColorSpace implements ColorSpace<HSLColorSpace> {
	public float h;
	public float s;
	public float l;

	public HSLColorSpace(float h, float s, float l) {
		this.h = h;
		this.s = s;
		this.l = l;
	}

	@Nonnull
	public static HSLColorSpace from(@Nonnull RGBColorSpace rgb) {
		return from(rgb.r, rgb.g, rgb.b);
	}

	@Nonnull
	public static HSLColorSpace from(float r, float g, float b) {
		float max = Math2.max(r, g, b);
		float min = Math2.min(r, g, b);
		float range = max - min;

		float h = 0, s;
		float l = (max + min) / 2f;

		if (range == 0) {
			s = 0;
		} else {
			s = l < 0.5f ? range / (max + min) : range / (2 - max - min);

			float rr = ((max - r) / 6f + range / 2f) / range;
			float gg = ((max - g) / 6f + range / 2f) / range;
			float bb = ((max - b) / 6f + range / 2f) / range;

			if (max == r)
				h = bb - gg;
			else if (max == g)
				h = (1f / 3f) + rr - bb;
			else if (max == b)
				h = (2f / 3f) + gg - rr;

			if (h < 0)
				h += 1f;
			if (h > 1)
				h -= 1f;
		}

		return new HSLColorSpace(h, s, l);
	}

	@Override
	public String toString() {
		return String.format("[HSLColorSpace: H:%.3f S:%.3f L:%.3f]", h, s, l);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		if (s == 0)
			return new RGBColorSpace(l, l, l);

		float v2 = l < 0.5f ? l * (1 + s) : (l + s) - (s * l);
		float v1 = 2 * l - v2;

		float r = hue2rgb(v1, v2, h + 1f / 3f);
		float g = hue2rgb(v1, v2, h);
		float b = hue2rgb(v1, v2, h - 1f / 3f);
		return new RGBColorSpace(r, g, b);
	}

	public float getDistance(@Nonnull HSLColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(Math.abs(Math2.deltaAngle(h * 360f, other.h * 360f) / 360f), 2)
						+ Math.pow(s - other.s, 2)
						+ Math.pow(l - other.l, 2)
		);
	}

	private float hue2rgb(float v1, float v2, float vh) {
		if (vh < 0)
			vh += 1f;
		if (vh > 1)
			vh -= 1f;

		if (6 * vh < 1)
			return v1 + (v2 - v1) * 6 * vh;
		else if (2 * vh < 1)
			return v2;
		else if (3 * vh < 2)
			return v1 + (v2 - v1) * (2f / 3f - vh) * 6;
		else
			return v1;
	}

	@Override
	@Nonnull
	public HSLColorSpace ease(@Nonnull HSLColorSpace other, float f) {
		float h2 = Math2.deltaAngle(this.h * 360f, other.h * 360f) >= 0 ? other.h : other.h - 1f;
		float h = Easing.linear.ease(this.h, h2, f);
		if (h < 0)
			h += 1f;
		if (h > 0)
			h -= 1f;

		return new HSLColorSpace(
				h,
				Easing.linear.ease(s, other.s, f),
				Easing.linear.ease(l, other.l, f)
		);
	}
}