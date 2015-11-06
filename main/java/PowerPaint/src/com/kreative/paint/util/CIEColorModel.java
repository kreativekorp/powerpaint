package com.kreative.paint.util;

import java.awt.Color;

public abstract class CIEColorModel extends ColorModel {
	public abstract float[] toXYZ(float[] channels, float[] xyza);
	public abstract float[] fromXYZ(float x, float y, float z, float a, float[] channels);
	
	@Override
	public final Color makeColor(float[] channels) {
		// Convert from channels to XYZ
		float[] xyza = toXYZ(channels, new float[4]);
		// Convert from XYZ to linear RGB
		float r = +3.2406f * xyza[0] -1.5372f * xyza[1] -0.4986f * xyza[2];
		float g = -0.9689f * xyza[0] +1.8758f * xyza[1] +0.0415f * xyza[2];
		float b = +0.0557f * xyza[0] -0.2040f * xyza[1] +1.0570f * xyza[2];
		// Convert from linear RGB to sRGB
		r = (r <= 0.0031308f) ? (12.92f * r) : (float)(1.055 * Math.pow(r, 1.0/2.4) - 0.055);
		g = (g <= 0.0031308f) ? (12.92f * g) : (float)(1.055 * Math.pow(g, 1.0/2.4) - 0.055);
		b = (b <= 0.0031308f) ? (12.92f * b) : (float)(1.055 * Math.pow(b, 1.0/2.4) - 0.055);
		// Constrain to sRGB gamut
		if (r < 0) r = 0; if (r > 1) r = 1;
		if (g < 0) g = 0; if (g > 1) g = 1;
		if (b < 0) b = 0; if (b > 1) b = 1;
		// Compose color
		return new Color(r, g, b, xyza[3]);
	}
	
	@Override
	public final float[] unmakeColor(Color color, float[] channels) {
		// Decompose color
		float[] rgba = color.getRGBComponents(new float[4]);
		// Convert from sRGB to linear RGB
		float r = (rgba[0] <= 0.04045f) ? (rgba[0] / 12.92f) : (float)Math.pow((rgba[0] + 0.055) / 1.055, 2.4);
		float g = (rgba[1] <= 0.04045f) ? (rgba[1] / 12.92f) : (float)Math.pow((rgba[1] + 0.055) / 1.055, 2.4);
		float b = (rgba[2] <= 0.04045f) ? (rgba[2] / 12.92f) : (float)Math.pow((rgba[2] + 0.055) / 1.055, 2.4);
		// Convert from linear RGB to XYZ
		float x = 0.4124f * r + 0.3576f * g + 0.1805f * b;
		float y = 0.2126f * r + 0.7152f * g + 0.0722f * b;
		float z = 0.0193f * r + 0.1192f * g + 0.9505f * b;
		// Convert from XYZ to channels
		return fromXYZ(x, y, z, rgba[3], channels);
	}
	
	public static final ColorModel CIE_XYZ_1 = new CIEXYZ(1, 100);
	public static final ColorModel CIE_XYZ_100 = new CIEXYZ(100, 100);
	public static class CIEXYZ extends CIEColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public CIEXYZ(int max, int steps) {
			this.max = max;
			if (max == 100) this.name = "CIE XYZ";
			else this.name = "CIE XYZ (0-" + max + ")";
			float step = (float)max / (float)steps;
			this.channels = new ColorChannel[] {
				new ColorChannel("X", "X", 0, max, step),
				new ColorChannel("Y", "Y", 0, max, step),
				new ColorChannel("Z", "Z", 0, max, step),
				new ColorChannel("A", "A", 0, max, step)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public float[] toXYZ(float[] channels, float[] xyza) {
			if (xyza == null) xyza = new float[4];
			xyza[0] = channels[0] / max;
			xyza[1] = channels[1] / max;
			xyza[2] = channels[2] / max;
			xyza[3] = channels[3] / max;
			return xyza;
		}
		@Override
		public float[] fromXYZ(float x, float y, float z, float a, float[] channels) {
			if (channels == null) channels = new float[4];
			channels[0] = x * max;
			channels[1] = y * max;
			channels[2] = z * max;
			channels[3] = a * max;
			return channels;
		}
	}
	
	public static final ColorModel CIE_xyY_1 = new CIExyY(1, 100);
	public static final ColorModel CIE_xyY_100 = new CIExyY(100, 100);
	public static class CIExyY extends CIEColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public CIExyY(int max, int steps) {
			this.max = max;
			if (max == 100) this.name = "CIE xyY";
			else this.name = "CIE xyY (0-" + max + ")";
			float step = (float)max / (float)steps;
			this.channels = new ColorChannel[] {
				new ColorChannel("x", "x", step, max, step),
				new ColorChannel("y", "y", step, max, step),
				new ColorChannel("Y", "Y", 0, max, step),
				new ColorChannel("A", "A", 0, max, step)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public float[] toXYZ(float[] channels, float[] xyza) {
			float x = channels[0] / max;
			float y = channels[1] / max;
			float Y = channels[2] / max;
			float A = channels[3] / max;
			if (xyza == null) xyza = new float[4];
			xyza[0] = Y * x / y;
			xyza[1] = Y;
			xyza[2] = Y * (1 - x - y) / y;
			xyza[3] = A;
			return xyza;
		}
		@Override
		public float[] fromXYZ(float X, float Y, float Z, float A, float[] channels) {
			float XYZ = X + Y + Z;
			float x = (XYZ == 0) ? 0.3127f : (X / XYZ);
			float y = (XYZ == 0) ? 0.3290f : (Y / XYZ);
			if (channels == null) channels = new float[4];
			channels[0] = x * max;
			channels[1] = y * max;
			channels[2] = Y * max;
			channels[3] = A * max;
			return channels;
		}
	}
	
	public static final ColorModel CIE_RGB_1 = new CIERGB(1, 100);
	public static final ColorModel CIE_RGB_100 = new CIERGB(100, 100);
	public static class CIERGB extends CIEColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public CIERGB(int max, int steps) {
			this.max = max;
			if (max == 100) this.name = "CIE RGB";
			else this.name = "CIE RGB (0-" + max + ")";
			float step = (float)max / (float)steps;
			this.channels = new ColorChannel[] {
				new ColorChannel("R", "R", 0, max, step),
				new ColorChannel("G", "G", 0, max, step),
				new ColorChannel("B", "B", 0, max, step),
				new ColorChannel("A", "A", 0, max, step)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public float[] toXYZ(float[] channels, float[] xyza) {
			float r = channels[0] / max;
			float g = channels[1] / max;
			float b = channels[2] / max;
			float a = channels[3] / max;
			if (xyza == null) xyza = new float[4];
			xyza[0] = (0.49f    * r + 0.31f    * g + 0.20f    * b) / 0.17697f;
			xyza[1] = (0.17697f * r + 0.81240f * g + 0.01063f * b) / 0.17697f;
			xyza[2] = (0.00f    * r + 0.01f    * g + 0.99f    * b) / 0.17697f;
			xyza[3] = a;
			return xyza;
		}
		@Override
		public float[] fromXYZ(float x, float y, float z, float a, float[] channels) {
			float r = +0.41847f    * x -0.15866f   * y -0.082835f * z;
			float g = -0.091169f   * x +0.25243f   * y +0.015708f * z;
			float b = +0.00092090f * x -0.0025498f * y +0.17860f  * z;
			if (channels == null) channels = new float[4];
			channels[0] = r * max;
			channels[1] = g * max;
			channels[2] = b * max;
			channels[3] = a * max;
			return channels;
		}
	}
	
	public static final ColorModel CIE_Lab_D65 = new CIELab("D65", 0.95047f, 1f, 1.08883f);
	public static class CIELab extends CIEColorModel {
		private final float X, Y, Z;
		private final String name;
		private final ColorChannel[] channels;
		public CIELab(String name, float X, float Y, float Z) {
			this.X = X; this.Y = Y; this.Z = Z;
			this.name = "CIE Lab (" + name + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("L", "L",    0,  100, 1),
				new ColorChannel("a", "a", -100, +100, 1),
				new ColorChannel("b", "b", -100, +100, 1),
				new ColorChannel("A", "A",    0,  100, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public float[] toXYZ(float[] channels, float[] xyza) {
			float l = (channels[0] + 16f) / 116f;
			if (xyza == null) xyza = new float[4];
			xyza[0] = X * f1(l + channels[1] / 500f);
			xyza[1] = Y * f1(l);
			xyza[2] = Z * f1(l - channels[2] / 200f);
			xyza[3] = channels[3] / 100f;
			return xyza;
		}
		@Override
		public float[] fromXYZ(float x, float y, float z, float a, float[] channels) {
			if (channels == null) channels = new float[4];
			channels[0] = 116f * f(y / Y) - 16f;
			channels[1] = 500f * (f(x / X) - f(y / Y));
			channels[2] = 200f * (f(y / Y) - f(z / Z));
			channels[3] = 100f * a;
			return channels;
		}
		private static final float T = 216f / 24389f;
		private static final float M = 841f / 108f;
		private static final float B = 4f / 29f;
		private float f(float t) {
			if (t > T) return (float)Math.cbrt(t);
			else return M * t + B;
		}
		private static final float T1 = 6f / 29f;
		private static final float M1 = 108f / 841f;
		private static final float B1 = 4f / 29f;
		private float f1(float t) {
			if (t > T1) return t * t * t;
			else return M1 * (t - B1);
		}
	}
	
	public static final ColorModel Hunter_Lab_D65 = new HunterLab("D65", 0.95047f, 1f, 1.08883f);
	public static class HunterLab extends CIEColorModel {
		private final float X, Y, Z, Ka, Kb;
		private final String name;
		private final ColorChannel[] channels;
		public HunterLab(String name, float X, float Y, float Z) {
			this.X = X; this.Y = Y; this.Z = Z;
			this.Ka = (X + Y) * 175f / 198.04f;
			this.Kb = (Y + Z) * 70f / 218.11f;
			this.name = "Hunter Lab (" + name + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("L", "L",    0,  100, 1),
				new ColorChannel("a", "a", -100, +100, 1),
				new ColorChannel("b", "b", -100, +100, 1),
				new ColorChannel("A", "A",    0,  100, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public float[] toXYZ(float[] channels, float[] xyza) {
			float l = channels[0] / 100f;
			float a = channels[1] / 100f;
			float b = channels[2] / 100f;
			float yY = l * l;
			if (xyza == null) xyza = new float[4];
			xyza[0] = X * (yY + a * l / Ka);
			xyza[1] = Y * yY;
			xyza[2] = Z * (yY - b * l / Kb);
			xyza[3] = channels[3] / 100f;
			return xyza;
		}
		@Override
		public float[] fromXYZ(float x, float y, float z, float a, float[] channels) {
			float l = (float)Math.sqrt(y / Y);
			if (channels == null) channels = new float[4];
			channels[0] = 100f * l;
			channels[1] = (l == 0) ? 0 : (100f * Ka * ((x / X) - (y / Y)) / l);
			channels[2] = (l == 0) ? 0 : (100f * Kb * ((y / Y) - (z / Z)) / l);
			channels[3] = 100f * a;
			return channels;
		}
	}
}
