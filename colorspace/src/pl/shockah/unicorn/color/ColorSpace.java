package pl.shockah.unicorn.color;

import javax.annotation.Nonnull;

import pl.shockah.unicorn.ease.Easable;

public interface ColorSpace<CS extends ColorSpace<CS>> extends Easable<CS> {
	@Nonnull
	RGBColorSpace toRGB();

	float getDistance(@Nonnull CS other);
}