package com.kreative.paint.gradient;

public class GradientPreset {
	public static final GradientPreset BLACK_TO_WHITE = new GradientPreset(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.BLACK_TO_WHITE, "Black to White"
	);
	public static final GradientPreset WHITE_TO_BLACK = new GradientPreset(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.WHITE_TO_BLACK, "White to Black"
	);
	public static final GradientPreset RGB_SPECTRUM = new GradientPreset(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.RGB_SPECTRUM, "RGB Spectrum"
	);
	public static final GradientPreset RGB_WHEEL = new GradientPreset(
		GradientShape.REVERSE_ANGULAR, GradientColorMap.RGB_SPECTRUM, "RGB Wheel"
	);
	
	public final GradientShape shape;
	public final GradientColorMap colorMap;
	public final String name;
	
	public GradientPreset(GradientShape shape, GradientColorMap colorMap, String name) {
		this.shape = shape;
		this.colorMap = colorMap;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof GradientPreset) {
			return this.equals((GradientPreset)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(GradientPreset that, boolean withName) {
		if (!shapeEquals(this.shape, that.shape)) return false;
		if (!colorMapEquals(this.colorMap, that.colorMap)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		if (this.shape != null) hashCode ^= this.shape.hashCode();
		if (this.colorMap != null) hashCode ^= this.colorMap.hashCode();
		return hashCode;
	}
	
	private static boolean shapeEquals(GradientShape a, GradientShape b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b, false);
	}
	
	private static boolean colorMapEquals(GradientColorMap a, GradientColorMap b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b, false);
	}
}
