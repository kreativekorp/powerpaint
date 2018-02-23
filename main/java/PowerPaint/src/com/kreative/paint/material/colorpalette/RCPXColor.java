package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

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
	
	public static class Gray extends RCPXColor {
		public final float gray;
		public final String name;
		public Gray(float gray, String name) {
			this.gray = gray;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			float w = gray / 100f;
			return new Color(w, w, w);
		}
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class CMYK extends RCPXColor {
		public final float c, m, y, k;
		public final String name;
		public CMYK(float c, float m, float y, float k, String name) {
			this.c = c; this.m = m; this.y = y; this.k = k;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			float c = this.c / 100f;
			float m = this.m / 100f;
			float y = this.y / 100f;
			float k = this.k / 100f;
			if (COLORSPACE == null) {
				float r = 1f - Math.min(1f, c * (1f - k) + k);
				float g = 1f - Math.min(1f, m * (1f - k) + k);
				float b = 1f - Math.min(1f, y * (1f - k) + k);
				return new Color(r, g, b);
			} else {
				float[] cmyk = new float[]{c, m, y, k};
				return new Color(COLORSPACE, cmyk, 1f);
			}
		}
		@Override
		public String name() {
			return name;
		}
		private static final ColorSpace COLORSPACE;
		static {
			ColorSpace colorspace;
			try {
				InputStream in = RCPXColor.class.getResourceAsStream("ISOcoated_v2_300_eci.icc");
				colorspace = new ICC_ColorSpace(ICC_Profile.getInstance(in));
				in.close();
			} catch (IOException e) {
				colorspace = null;
			}
			COLORSPACE = colorspace;
		}
	}
	
	public static class CIELab extends RCPXColor {
		public final float l, a, b;
		public final String name;
		public CIELab(float l, float a, float b, String name) {
			this.l = l; this.a = a; this.b = b;
			this.name = name;
		}
		@Override
		public Color awtColor() {
			// CIELab -> CIEXYZ
			float w = (l + 16f) / 116f;
			float x = X * f1(w + a / 500f);
			float y = Y * f1(w);
			float z = Z * f1(w - b / 200f);
			// CIEXYZ -> linear RGB
			float lr = +3.2406f * x -1.5372f * y -0.4986f * z;
			float lg = -0.9689f * x +1.8758f * y +0.0415f * z;
			float lb = +0.0557f * x -0.2040f * y +1.0570f * z;
			// linear RGB -> sRGB
			float sr = sf1(lr), sg = sf1(lg), sb = sf1(lb);
			float min = Math.min(Math.min(Math.min(sr, sg), sb), 0);
			float max = Math.max(Math.max(Math.max(sr, sg), sb), 1);
			sr = (sr - min) / (max - min);
			sg = (sg - min) / (max - min);
			sb = (sb - min) / (max - min);
			return new Color(sr, sg, sb);
		}
		@Override
		public String name() {
			return name;
		}
		// D65 illuminant
		private static final float X = 0.95047f;
		private static final float Y = 1f;
		private static final float Z = 1.08883f;
		// CIELab -> CIEXYZ
		private static final float T1 = 6f / 29f;
		private static final float M1 = 108f / 841f;
		private static final float B1 = 4f / 29f;
		private static float f1(float t) {
			if (t > T1) return t * t * t;
			else return M1 * (t - B1);
		}
		// linear RGB -> sRGB
		private static final float ST1 = 0.0031308f;
		private static final float SL1 = 12.92f;
		private static final double SM1 = 1.055;
		private static final double SG1 = 1.0 / 2.4;
		private static final double SB1 = 0.055;
		private static float sf1(float t) {
			if (t <= ST1) return SL1 * t;
			else return (float)(SM1 * Math.pow(t, SG1) - SB1);
		}
	}
}
