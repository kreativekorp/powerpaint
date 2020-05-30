package com.kreative.paint.util;

import java.awt.Color;

public abstract class ColorModel {
	public static class ColorChannel {
		public final String symbol, name;
		public final float min, max, step;
		public final boolean hex;
		public ColorChannel(
			String symbol, String name,
			float min, float max, float step
		) {
			this.symbol = symbol; this.name = name;
			this.min = min; this.max = max; this.step = step;
			this.hex = (
				(min == 0) && (max == (int)max) &&
				((((int)max + 1) & (int)max) == 0) &&
				(step == (int)step)
			);
		}
	}
	
	public abstract String getName();
	public abstract ColorChannel[] getChannels();
	public abstract Color makeColor(float[] channels);
	public abstract float[] unmakeColor(Color color, float[] channels);
	@Override public final String toString() { return getName(); }
	
	public static final ColorModel GRAY_4 = new Gray(15);
	public static final ColorModel GRAY_8 = new Gray(255);
	public static final ColorModel GRAY_16 = new Gray(65535);
	public static final ColorModel GRAY_100 = new Gray(100);
	public static class Gray extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public Gray(int max) {
			this.max = max;
			this.name = "Gray (0-" + max + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("Y", "Gray" , 0, max, 1),
				new ColorChannel("A", "Alpha", 0, max, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float x = channels[0] / max;
			float a = channels[1] / max;
			return new Color(x, x, x, a);
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			float[] rgba = color.getRGBComponents(null);
			float x = rgba[0] * 0.30f + rgba[1] * 0.59f + rgba[2] * 0.11f;
			if (channels == null) channels = new float[2];
			channels[0] = x * max;
			channels[1] = rgba[3] * max;
			return channels;
		}
	}
	
	public static final ColorModel RGB_4 = new RGB(15);
	public static final ColorModel RGB_8 = new RGB(255);
	public static final ColorModel RGB_16 = new RGB(65535);
	public static final ColorModel RGB_100 = new RGB(100);
	public static class RGB extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public RGB(int max) {
			this.max = max;
			this.name = "RGB (0-" + max + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("R", "Red"  , 0, max, 1),
				new ColorChannel("G", "Green", 0, max, 1),
				new ColorChannel("B", "Blue" , 0, max, 1),
				new ColorChannel("A", "Alpha", 0, max, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float r = channels[0] / max;
			float g = channels[1] / max;
			float b = channels[2] / max;
			float a = channels[3] / max;
			return new Color(r, g, b, a);
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			for (int i = 0; i < 4; i++) channels[i] *= max;
			return channels;
		}
	}
	
	public static final ColorModel HSV_360_100 = new HSV(360, 100, 100, 100);
	public static final ColorModel HSV_360_255 = new HSV(360, 255, 255, 255);
	public static final ColorModel HSV_30_15 = new HSV(30, 15, 15, 15);
	public static class HSV extends ColorModel {
		private final float hmax;
		private final float smax;
		private final float vmax;
		private final float amax;
		private final String name;
		private final ColorChannel[] channels;
		public HSV(int hmax, int smax, int vmax, int amax) {
			this.hmax = hmax;
			this.smax = smax;
			this.vmax = vmax;
			this.amax = amax;
			this.name = "HSV (" + hmax + "/" + smax + "/" + vmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"       , 0, hmax, 1),
				new ColorChannel("S", "Saturation", 0, smax, 1),
				new ColorChannel("V", "Value"     , 0, vmax, 1),
				new ColorChannel("A", "Alpha"     , 0, amax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float s = channels[1] / smax;
			float v = channels[2] / vmax;
			float a = channels[3] / amax;
			int rgba = Color.HSBtoRGB(h, s, v) & 0xFFFFFF;
			rgba |= ((int)Math.round(a * 255f) << 24);
			return new Color(rgba, true);
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			channels = Color.RGBtoHSB(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				channels
			);
			channels[0] *= hmax;
			channels[1] *= smax;
			channels[2] *= vmax;
			channels[3] *= amax;
			return channels;
		}
	}
	
	public static final ColorModel HSL_360_100 = new HSL(360, 100, 100, 100);
	public static final ColorModel HSL_360_255 = new HSL(360, 255, 510, 255);
	public static final ColorModel HSL_30_15 = new HSL(30, 15, 30, 15);
	public static class HSL extends ColorModel {
		private final float hmax;
		private final float smax;
		private final float lmax;
		private final float amax;
		private final String name;
		private final ColorChannel[] channels;
		public HSL(int hmax, int smax, int lmax, int amax) {
			this.hmax = hmax;
			this.smax = smax;
			this.lmax = lmax;
			this.amax = amax;
			this.name = "HSL (" + hmax + "/" + smax + "/" + lmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"       , 0, hmax, 1),
				new ColorChannel("S", "Saturation", 0, smax, 1),
				new ColorChannel("L", "Lightness" , 0, lmax, 1),
				new ColorChannel("A", "Alpha"     , 0, amax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float s = channels[1] / smax;
			float l = channels[2] / lmax;
			float a = channels[3] / amax;
			s *= (l <= 0.5f) ? l : (1 - l);
			float v = l + s;
			s = 2 * s / v;
			int rgba = Color.HSBtoRGB(h, s, v) & 0xFFFFFF;
			rgba |= ((int)Math.round(a * 255f) << 24);
			return new Color(rgba, true);
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			channels = Color.RGBtoHSB(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				channels
			);
			float l = (2 - channels[1]) * channels[2];
			float s = channels[1] * channels[2];
			     if (l > 1) s /= (2 - l);
			else if (l > 0) s /= l;
			else            s  = 0;
			l /= 2;
			channels[0] *= hmax;
			channels[1] = s * smax;
			channels[2] = l * lmax;
			channels[3] *= amax;
			return channels;
		}
	}
	
	public static final ColorModel HWB_360_100 = new HWB(360, 100, 100, 100);
	public static final ColorModel HWB_360_255 = new HWB(360, 255, 255, 255);
	public static final ColorModel HWB_30_15 = new HWB(30, 15, 15, 15);
	public static class HWB extends ColorModel {
		private final float hmax;
		private final float wmax;
		private final float bmax;
		private final float amax;
		private final String name;
		private final ColorChannel[] channels;
		public HWB(int hmax, int wmax, int bmax, int amax) {
			this.hmax = hmax;
			this.wmax = wmax;
			this.bmax = bmax;
			this.amax = amax;
			this.name = "HWB (" + hmax + "/" + wmax + "/" + bmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"  , 0, hmax, 1),
				new ColorChannel("W", "White", 0, wmax, 1),
				new ColorChannel("B", "Black", 0, bmax, 1),
				new ColorChannel("A", "Alpha", 0, amax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float w = channels[1] / wmax;
			float k = channels[2] / bmax;
			float a = channels[3] / amax;
			if ((w + k) >= 1) {
				float x = w / (w + k);
				return new Color(x, x, x, a);
			} else {
				int rgb = Color.HSBtoRGB(h, 1, 1);
				float m = 1 - w - k;
				float r = (((rgb >> 16) & 0xFF) / 255f) * m + w;
				float g = (((rgb >>  8) & 0xFF) / 255f) * m + w;
				float b = (((rgb >>  0) & 0xFF) / 255f) * m + w;
				return new Color(r, g, b, a);
			}
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			float w = Math.min(Math.min(channels[0], channels[1]), channels[2]);
			float k = 1 - Math.max(Math.max(channels[0], channels[1]), channels[2]);
			channels = Color.RGBtoHSB(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				channels
			);
			channels[0] *= hmax;
			channels[1] = w * wmax;
			channels[2] = k * bmax;
			channels[3] *= amax;
			return channels;
		}
	}
	
	public static final ColorModel NEW_HORIZONS = new NewHorizons();
	public static class NewHorizons extends ColorModel {
		private final String name;
		private final ColorChannel[] channels;
		public NewHorizons() {
			this.name = "New Horizons";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"       , 0, 30, 1),
				new ColorChannel("V", "Vividness" , 0, 14, 1),
				new ColorChannel("B", "Brightness", 0, 14, 1),
				new ColorChannel("A", "Alpha"     , 0, 14, 1),
			};
		}
		public String getName() { return name; }
		public ColorChannel[] getChannels() { return channels; }
		public Color makeColor(float[] channels) {
			float h = channels[0] / 30;
			float s = channels[1] / 15;
			float v = (channels[2] * 3 + 4) / 51;
			float a = channels[3] / 14;
			int rgba = Color.HSBtoRGB(h, s, v) & 0xFFFFFF;
			rgba |= ((int)Math.round(a * 255f) << 24);
			return new Color(rgba, true);
		}
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			channels = Color.RGBtoHSB(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				channels
			);
			channels[0] *= 30;
			channels[1] *= 15;
			channels[2] *= 51;
			channels[2] -= 4;
			channels[2] /= 3;
			channels[3] *= 14;
			return channels;
		}
	}
	
	public static final ColorModel NAIVE_CMYK_100 = new NaiveCMYK(100);
	public static class NaiveCMYK extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public NaiveCMYK(int max) {
			this.max = max;
			if (max == 100) this.name = "Naive CMYK";
			else this.name = "Naive CMYK (0-" + max + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("C", "Cyan"   , 0, max, 1),
				new ColorChannel("M", "Magenta", 0, max, 1),
				new ColorChannel("Y", "Yellow" , 0, max, 1),
				new ColorChannel("K", "Black"  , 0, max, 1),
				new ColorChannel("A", "Alpha"  , 0, max, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float c = channels[0] / max;
			float m = channels[1] / max;
			float y = channels[2] / max;
			float k = channels[3] / max;
			float a = channels[4] / max;
			float r = 1 - Math.min(1, c * (1 - k) + k);
			float g = 1 - Math.min(1, m * (1 - k) + k);
			float b = 1 - Math.min(1, y * (1 - k) + k);
			return new Color(r, g, b, a);
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			float[] rgba = color.getRGBComponents(null);
			float k = 1 - Math.max(Math.max(rgba[0], rgba[1]), rgba[2]);
			float c = (k >= 1) ? 0 : ((1 - rgba[0] - k) / (1 - k));
			float m = (k >= 1) ? 0 : ((1 - rgba[1] - k) / (1 - k));
			float y = (k >= 1) ? 0 : ((1 - rgba[2] - k) / (1 - k));
			if (channels == null) channels = new float[5];
			channels[0] = c * max;
			channels[1] = m * max;
			channels[2] = y * max;
			channels[3] = k * max;
			channels[4] = rgba[3] * max;
			return channels;
		}
	}
	
	public static final ColorModel YUV_SDTV = new YUV("YUV (SDTV)", "U", "V", 0.299f, 0.114f, 0.436f, 0.615f);
	public static final ColorModel YUV_HDTV = new YUV("YUV (HDTV)", "U", "V", 0.2126f, 0.0722f, 0.436f, 0.615f);
	public static final ColorModel Y_CB_CR_SDTV = new YUV("YCbCr (SDTV)", "Cb", "Cr", 0.299f, 0.114f, 0.5f, 0.5f);
	public static final ColorModel Y_CB_CR_HDTV = new YUV("YCbCr (HDTV)", "Cb", "Cr", 0.2126f, 0.0722f, 0.5f, 0.5f);
	public static final ColorModel Y_DB_DR = new YUV("YDbDr", "Db", "Dr", 0.299f, 0.114f, 1.333f, -1.333f);
	public static class YUV extends ColorModel {
		private final float wr, wg, wb;
		private final float umax, vmax;
		private final String name;
		private final ColorChannel[] channels;
		public YUV(String name, String U, String V, float wr, float wb, float umax, float vmax) {
			this.wr = wr; this.wg = 1 - wr - wb; this.wb = wb;
			this.umax = umax; this.vmax = vmax;
			this.name = name;
			if (umax < 0) umax = -umax;
			if (vmax < 0) vmax = -vmax;
			this.channels = new ColorChannel[] {
				new ColorChannel("Y", "Y",     0,     1, 0.001f),
				new ColorChannel( U ,  U , -umax, +umax, 0.001f),
				new ColorChannel( V ,  V , -vmax, +vmax, 0.001f),
				new ColorChannel("A", "A",     0,     1, 0.001f)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float y = channels[0];
			float r_y = channels[2] * (1 - wr) / vmax;
			float b_y = channels[1] * (1 - wb) / umax;
			float g = y - b_y * wb / wg - r_y * wr / wg;
			float r = y + r_y;
			float b = y + b_y;
			float a = channels[3];
			return new Color(clamp(r), clamp(g), clamp(b), clamp(a));
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			float y = wr * channels[0] + wg * channels[1] + wb * channels[2];
			float u = umax * (channels[2] - y) / (1 - wb);
			float v = vmax * (channels[0] - y) / (1 - wr);
			float a = channels[3];
			channels[0] = y;
			channels[1] = u;
			channels[2] = v;
			channels[3] = a;
			return channels;
		}
		private static float clamp(float v) {
			if (v <= 0) return 0;
			if (v >= 1) return 1;
			return v;
		}
	}
	
	public static final ColorModel YIQ = new MatrixColorModel(
		"YIQ",
		"Y", 0, 1, 0.001f,
		"I", -0.5957f, +0.5957f, 0.001f,
		"Q", -0.5226f, +0.5226f, 0.001f,
		+0.299f, +0.587f, +0.114f,
		+0.596f, -0.274f, -0.322f,
		+0.211f, -0.523f, +0.312f,
		+1, +0.956f, +0.621f,
		+1, -0.272f, -0.647f,
		+1, -1.106f, +1.703f
	);
	public static final ColorModel Y_CG_CO = new MatrixColorModel(
		"YCgCo",
		"Y", 0, 1, 0.001f,
		"Cg", -0.5f, +0.5f, 0.001f,
		"Co", -0.5f, +0.5f, 0.001f,
		+1/4f, +1/2f, +1/4f,
		-1/4f, +1/2f, -1/4f,
		+1/2f,   0  , -1/2f,
		+1, -1, +1,
		+1, +1,  0,
		+1, -1, -1
	);
	public static class MatrixColorModel extends ColorModel {
		private final float fa, fb, fc;
		private final float fd, fe, ff;
		private final float fg, fh, fi;
		private final float ra, rb, rc;
		private final float rd, re, rf;
		private final float rg, rh, ri;
		private final String name;
		private final ColorChannel[] channels;
		public MatrixColorModel(
			String name,
			String c1, float c1min, float c1max, float c1step,
			String c2, float c2min, float c2max, float c2step,
			String c3, float c3min, float c3max, float c3step,
			float fa, float fb, float fc,
			float fd, float fe, float ff,
			float fg, float fh, float fi,
			float ra, float rb, float rc,
			float rd, float re, float rf,
			float rg, float rh, float ri
		) {
			this.fa = fa; this.fb = fb; this.fc = fc;
			this.fd = fd; this.fe = fe; this.ff = ff;
			this.fg = fg; this.fh = fh; this.fi = fi;
			this.ra = ra; this.rb = rb; this.rc = rc;
			this.rd = rd; this.re = re; this.rf = rf;
			this.rg = rg; this.rh = rh; this.ri = ri;
			this.name = name;
			this.channels = new ColorChannel[] {
				new ColorChannel(c1, c1, c1min, c1max, c1step),
				new ColorChannel(c2, c2, c2min, c2max, c2step),
				new ColorChannel(c3, c3, c3min, c3max, c3step),
				new ColorChannel("A", "A", 0, 1, 0.001f)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float r = channels[0] * ra + channels[1] * rb + channels[2] * rc;
			float g = channels[0] * rd + channels[1] * re + channels[2] * rf;
			float b = channels[0] * rg + channels[1] * rh + channels[2] * ri;
			float a = channels[3];
			return new Color(clamp(r), clamp(g), clamp(b), clamp(a));
		}
		@Override
		public float[] unmakeColor(Color color, float[] channels) {
			channels = color.getRGBComponents(channels);
			float x = channels[0] * fa + channels[1] * fb + channels[2] * fc;
			float y = channels[0] * fd + channels[1] * fe + channels[2] * ff;
			float z = channels[0] * fg + channels[1] * fh + channels[2] * fi;
			float a = channels[3];
			channels[0] = x;
			channels[1] = y;
			channels[2] = z;
			channels[3] = a;
			return channels;
		}
		private static float clamp(float v) {
			if (v <= 0) return 0;
			if (v >= 1) return 1;
			return v;
		}
	}
}
