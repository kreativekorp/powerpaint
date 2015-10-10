package com.kreative.paint.rcp;

import java.awt.Color;

public abstract class RCPXColor {
	public abstract Color awtColor();
	public abstract String name();
	
	public static class RGB extends RCPXColor {
		public final int r, g, b;
		public final String name;
		public RGB(int r, int g, int b, String name) {
			this.r = r; this.g = g; this.b = b;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class RGB16 extends RCPXColor {
		public final int r, g, b;
		public final String name;
		public RGB16(int r, int g, int b, String name) {
			this.r = r; this.g = g; this.b = b;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r / 65535.0f, g / 65535.0f, b / 65535.0f);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class RGBD extends RCPXColor {
		public final float r, g, b;
		public final String name;
		public RGBD(float r, float g, float b, String name) {
			this.r = r; this.g = g; this.b = b;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class RGBA extends RCPXColor {
		public final int r, g, b, a;
		public final String name;
		public RGBA(int r, int g, int b, int a, String name) {
			this.r = r; this.g = g; this.b = b; this.a = a;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b, a);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class RGBA16 extends RCPXColor {
		public final int r, g, b, a;
		public final String name;
		public RGBA16(int r, int g, int b, int a, String name) {
			this.r = r; this.g = g; this.b = b; this.a = a;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r / 65535.0f, g / 65535.0f, b / 65535.0f, a / 65535.0f);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class RGBAD extends RCPXColor {
		public final float r, g, b, a;
		public final String name;
		public RGBAD(float r, float g, float b, float a, String name) {
			this.r = r; this.g = g; this.b = b; this.a = a;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return new Color(r, g, b, a);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class HSV extends RCPXColor {
		public final float h, s, v;
		public final String name;
		public HSV(float h, float s, float v, String name) {
			this.h = h; this.s = s; this.v = v;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			return Color.getHSBColor(h / 360.0f, s / 100.0f, v / 100.0f);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class HSVA extends RCPXColor {
		public final float h, s, v, a;
		public final String name;
		public HSVA(float h, float s, float v, float a, String name) {
			this.h = h; this.s = s; this.v = v; this.a = a;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			int rgb = ((int)Math.round((a / 100.0f) * 255.0f) << 24) |
			          (Color.HSBtoRGB(h / 360.0f, s / 100.0f, v / 100.0f) & 0xFFFFFF);
			return new Color(rgb, true);
		}
		@Override
		public String name() {
			return name;
		}
	}
}
