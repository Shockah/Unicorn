package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class LCHColorSpace implements ColorSpace<LCHColorSpace> {
	public float l;
	public float c;
	public float h;

	public LCHColorSpace(float l, float c, float h) {
		this.l = l;
		this.c = c;
		this.h = h;
	}

	@Nonnull
	public static LCHColorSpace from(@Nonnull LabColorSpace lab) {
		float h = (float)Math.atan2(lab.b, lab.a);
		h = h > 0 ? h / (float)Math.PI * 180 : 360 + h / (float)Math.PI * 180;

		return new LCHColorSpace(
				lab.l,
				(float)Math.sqrt(lab.a * lab.a + lab.b * lab.b),
				h / 360f
		);
	}

	@Nonnull
	public LabColorSpace toLab() {
		float h = this.h * 360f;
		return new LabColorSpace(
				l,
				(float)Math.cos(Math.toRadians(h)) * c,
				(float)Math.sin(Math.toRadians(h)) * c
		);
	}

	@Override
	public String toString() {
		return String.format("[LCHColorSpace: L:%.3f C:%.3f H:%.3f]", l, c, h);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		return toLab().toRGB();
	}

	@Override
	public float getDistance(@Nonnull LCHColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(Math.abs(Math2.deltaAngle(h * 360f, other.h * 360f) / 360f), 2)
						+ Math.pow(c - other.c, 2) * 0.01f
						+ Math.pow(l - other.l, 2) * 0.01f
		);
	}

	@Nonnull
	public RGBColorSpace toRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toLab().toRGB(reference);
	}

	@Nonnull
	public RGBColorSpace toExactRGB() {
		return toLab().toExactRGB();
	}

	@Nonnull
	public RGBColorSpace toExactRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toLab().toExactRGB(reference);
	}

	@Override
	@Nonnull
	public LCHColorSpace ease(@Nonnull LCHColorSpace other, float f) {
		float h2 = Math2.deltaAngle(this.h, other.h) >= 0 ? other.h : other.h - 1f;
		float h = Easing.linear.ease(this.h, h2, f);
		if (h < 0)
			h += 1f;
		if (h > 0)
			h -= 1f;

		return new LCHColorSpace(
				Easing.linear.ease(l, other.l, f),
				Easing.linear.ease(c, other.c, f),
				h
		);
	}
}