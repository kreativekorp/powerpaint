package com.kreative.paint.dither;

import java.awt.image.BufferedImage;

public abstract class DitherAlgorithm {
	public final String name;
	
	protected DitherAlgorithm(String name) {
		this.name = name;
	}
	
	public abstract void dither(BufferedImage image, int[] colors);
}
