package com.kreative.paint.gradient;

import java.awt.Color;

public abstract class GradientColor {
	public static final GradientColor BLACK   = new GradientColor.RGB(  0,   0,   0);
	public static final GradientColor RED     = new GradientColor.RGB(255,   0,   0);
	public static final GradientColor YELLOW  = new GradientColor.RGB(255, 255,   0);
	public static final GradientColor GREEN   = new GradientColor.RGB(  0, 255,   0);
	public static final GradientColor CYAN    = new GradientColor.RGB(  0, 255, 255);
	public static final GradientColor BLUE    = new GradientColor.RGB(  0,   0, 255);
	public static final GradientColor MAGENTA = new GradientColor.RGB(255,   0, 255);
	public static final GradientColor WHITE   = new GradientColor.RGB(255, 255, 255);
	
	public abstract Color awtColor();
	
	@Override
	public final boolean equals(Object that) {
		if (that instanceof GradientColor) {
			Color thisColor = this.awtColor();
			Color thatColor = ((GradientColor)that).awtColor();
			int thisRGB = (thisColor != null) ? thisColor.getRGB() : 0;
			int thatRGB = (thatColor != null) ? thatColor.getRGB() : 0;
			return thisRGB == thatRGB;
		} else {
			return false;
		}
	}
	
	@Override
	public final int hashCode() {
		Color thisColor = this.awtColor();
		int thisRGB = (thisColor != null) ? thisColor.getRGB() : 0;
		return thisRGB;
	}
	
	public static class RGB extends GradientColor {
		public final int r, g, b;
		public RGB(int r, int g, int b) {
			this.r = r8(r);
			this.g = r8(g);
			this.b = r8(b);
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b);
		}
	}
	
	public static class RGB16 extends GradientColor {
		public final int r, g, b;
		public RGB16(int r, int g, int b) {
			this.r = r16(r);
			this.g = r16(g);
			this.b = r16(b);
		}
		@Override
		public Color awtColor() {
			return new Color(r / 65535.0f, g / 65535.0f, b / 65535.0f);
		}
	}
	
	public static class RGBA extends GradientColor {
		public final int r, g, b, a;
		public RGBA(int r, int g, int b, int a) {
			this.r = r8(r);
			this.g = r8(g);
			this.b = r8(b);
			this.a = r8(a);
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b, a);
		}
	}
	
	public static class RGBA16 extends GradientColor {
		public final int r, g, b, a;
		public RGBA16(int r, int g, int b, int a) {
			this.r = r16(r);
			this.g = r16(g);
			this.b = r16(b);
			this.a = r16(a);
		}
		@Override
		public Color awtColor() {
			return new Color(r / 65535.0f, g / 65535.0f, b / 65535.0f, a / 65535.0f);
		}
	}
	
	public static class HSV extends GradientColor {
		public final float h, s, v;
		public HSV(float h, float s, float v) {
			this.h = r360(h);
			this.s = r100(s);
			this.v = r100(v);
		}
		@Override
		public Color awtColor() {
			return Color.getHSBColor(h / 360.0f, s / 100.0f, v / 100.0f);
		}
	}
	
	public static class HSVA extends GradientColor {
		public final float h, s, v, a;
		public HSVA(float h, float s, float v, float a) {
			this.h = r360(h);
			this.s = r100(s);
			this.v = r100(v);
			this.a = r100(a);
		}
		@Override
		public Color awtColor() {
			int rgb = Color.HSBtoRGB(h / 360.0f, s / 100.0f, v / 100.0f);
			int alpha = (int)Math.round(a * 255.0f / 100.0f);
			return new Color((rgb & 0xFFFFFF) | (alpha << 24), true);
		}
	}
	
	private static int r8(int value) {
		if (value <= 0) return 0;
		if (value >= 255) return 255;
		return value;
	}
	
	private static int r16(int value) {
		if (value <= 0) return 0;
		if (value >= 65535) return 65535;
		return value;
	}
	
	private static float r100(float value) {
		if (value <= 0.0f) return 0.0f;
		if (value >= 100.0f) return 100.0f;
		return value;
	}
	
	private static float r360(float value) {
		if (value <= 0.0f) return 0.0f;
		if (value >= 360.0f) return 360.0f;
		return value;
	}
}
