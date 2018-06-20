package pl.shockah.unicorn.color;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.ease.Easing;

@EqualsAndHashCode
public class HSLuvColorSpace implements ColorSpace<HSLuvColorSpace> {
	private static final float kappa = 903.2962962f;
	private static final float epsilon = 0.0088564516f;

	@Nonnull
	private static final float[][] m = new float[][] {
			new float[] {3.240969941904521f, -1.537383177570093f, -0.498610760293f},
			new float[] {-0.96924363628087f, 1.87596750150772f, 0.041555057407175f},
			new float[] {0.055630079696993f, -0.20397695888897f, 1.056971514242878f},
	};

	public float h;
	public float s;
	public float l;

	public HSLuvColorSpace(float h, float s, float l) {
		this.h = h;
		this.s = s;
		this.l = l;
	}

	@Nonnull
	public static HSLuvColorSpace from(@Nonnull LCHColorSpace lch) {
		if (lch.l > 0.999999999f)
			return new HSLuvColorSpace(lch.h, 0f, 1f);
		if (lch.l < 0.0000000001f)
			return new HSLuvColorSpace(lch.h, 0f, 0f);

		float max = maxChromaForLH(lch.l, lch.h);
		float S = lch.c / max;
		return new HSLuvColorSpace(lch.h, S, lch.l);
	}

	private static float maxChromaForLH(float L, float H) {
		float hrad = (float)(H / 360f * Math.PI * 2f);

		List<float[]> bounds = getBounds(L);
		float min = Float.MAX_VALUE;

		for (float[] bound : bounds) {
			float length = lengthOfRayUntilIntersect(hrad, bound);
			if (length >= 0f)
				min = Math.min(min, length);
		}

		return min;
	}

	private static float lengthOfRayUntilIntersect(double theta, float[] line) {
		return (float)(line[1] / (Math.sin(theta) - line[0] * Math.cos(theta)));
	}

	@Nonnull
	private static List<float[]> getBounds(float L) {
		List<float[]> result = new ArrayList<>();

		float sub1 = (float)(Math.pow(L + 16, 3) / 1560896);
		float sub2 = sub1 > epsilon ? sub1 : L / kappa;

		for (int c = 0; c < 3; ++c) {
			float m1 = m[c][0];
			float m2 = m[c][1];
			float m3 = m[c][2];

			for (int t = 0; t < 2; ++t) {
				float top1 = (284517 * m1 - 94839 * m3) * sub2;
				float top2 = (838422 * m3 + 769860 * m2 + 731718 * m1) * L * sub2 - 769860 * t * L;
				float bottom = (632260 * m3 - 126452 * m2) * sub2 + 126452 * t;

				result.add(new float[]{top1 / bottom, top2 / bottom});
			}
		}

		return result;
	}

	@Nonnull
	public LCHColorSpace toLCH() {
		if (l > 0.999999999f)
			return new LCHColorSpace(1f, 0f, h);
		if (l < 0.0000000001f)
			return new LCHColorSpace(0f, 0f, h);

		float max = maxChromaForLH(l, h);
		float C = max * s;
		return new LCHColorSpace(l, C, h);
	}

	@Override
	public String toString() {
		return String.format("[HSLuvColorSpace: H:%.3f S:%.3f L:%.3f]", h, s, l);
	}

	@Override
	@Nonnull
	public RGBColorSpace toRGB() {
		return toLCH().toRGB();
	}

	@Override
	public float getDistance(@Nonnull HSLuvColorSpace other) {
		return (float)Math.sqrt(
				Math.pow(Math.abs(Math2.deltaAngle(h * 360f, other.h * 360f) / 360f), 2)
						+ Math.pow(s - other.s, 2) * 0.01f
						+ Math.pow(l - other.l, 2) * 0.01f
		);
	}

	@Nonnull
	public RGBColorSpace toRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toLCH().toRGB(reference);
	}

	@Nonnull
	public RGBColorSpace toExactRGB() {
		return toLCH().toExactRGB();
	}

	@Nonnull
	public RGBColorSpace toExactRGB(@Nonnull XYZColorSpace.Reference reference) {
		return toLCH().toExactRGB(reference);
	}

	@Override
	@Nonnull
	public HSLuvColorSpace ease(@Nonnull HSLuvColorSpace other, float f) {
		float h2 = Math2.deltaAngle(this.h * 360f, other.h * 360f) >= 0 ? other.h : other.h - 1f;
		float h = Easing.linear.ease(this.h, h2, f);
		if (h < 0)
			h += 1f;
		if (h > 0)
			h -= 1f;

		return new HSLuvColorSpace(
				h,
				Easing.linear.ease(s, other.s, f),
				Easing.linear.ease(l, other.l, f)
		);
	}
}