package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class HSVColorSpace implements ColorSpace<HSVColorSpace> {
	public float h;
	public float s;
	public float v;

	public HSVColorSpace(float h, float s, float v) {
		this.h = h;
		this.s = s;
		this.v = v;
	}

	@Nonnull public static HSVColorSpace from(@Nonnull RGBColorSpace rgb) {
		return from(rgb.r, rgb.g, rgb.b);
	}

	@Nonnull public static HSVColorSpace from(float r, float g, float b) {
		float max = Math2.max(r, g, b);
		float min = Math2.min(r, g, b);
		float range = max - min;

		float h, s, v;
		if (range == 0)
			h = 0;
		else if (max == r)
			h = (60 * (g - b) / range + 360) % 360;
		else if (max == g)
			h = 60 * (b - r) / range + 120;
		else
			h = 60 * (r - g) / range + 240;

		if (max > 0)
			s = 1 - min / max;
		else
			s = 0;

		v = max;

		return new HSVColorSpace(h / 360f, s, v);
	}

	@Override
	public String toString() {
		return String.format("[HSVColorSpace: H:%.3f S:%.3f V:%.3f]", h, s, v);
	}

	@Override
	@Nonnull public RGBColorSpace toRGB() {
		float h = this.h * 360f;
		float x = (h / 60f + 6) % 6;
		int i = (int)x;
		float f = x - i;
		float p = v * (1 - s);
		float q = v * (1 - s * f);
		float t = v * (1 - s * (1 - f));
		switch (i) {
			case 0:
				return new RGBColorSpace(v, t, p);
			case 1:
				return new RGBColorSpace(q, v, p);
			case 2:
				return new RGBColorSpace(p, v, t);
			case 3:
				return new RGBColorSpace(p, q, v);
			case 4:
				return new RGBColorSpace(t, p, v);
			default:
				return new RGBColorSpace(v, p, q);
		}
	}

	@Override
	public float getDistance(@Nonnull HSVColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(Math.abs(Math2.deltaAngle(h * 360f, other.h * 360f) / 360f), 2)
						+ Math.pow(s - other.s, 2)
						+ Math.pow(v - other.v, 2)
		);
	}

	@Override
	@Nonnull public HSVColorSpace ease(@Nonnull HSVColorSpace other, float f) {
		float h2 = Math2.deltaAngle(this.h, other.h) >= 0 ? other.h : other.h - 1f;
		float h = Easing.linear.ease(this.h, h2, f);
		if (h < 0)
			h += 1f;
		if (h > 0)
			h -= 1f;

		return new HSVColorSpace(
				h,
				Easing.linear.ease(s, other.s, f),
				Easing.linear.ease(v, other.v, f)
		);
	}
}