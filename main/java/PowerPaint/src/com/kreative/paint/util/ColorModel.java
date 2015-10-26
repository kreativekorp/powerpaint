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
	
	public static final ColorModel GRAY_ALPHA_4 = new GrayAlpha(15);
	public static final ColorModel GRAY_ALPHA_8 = new GrayAlpha(255);
	public static final ColorModel GRAY_ALPHA_16 = new GrayAlpha(65535);
	public static final ColorModel GRAY_ALPHA_100 = new GrayAlpha(100);
	public static class GrayAlpha extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public GrayAlpha(int max) {
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
	
	public static final ColorModel RGBA_4 = new RGBA(15);
	public static final ColorModel RGBA_8 = new RGBA(255);
	public static final ColorModel RGBA_16 = new RGBA(65535);
	public static final ColorModel RGBA_100 = new RGBA(100);
	public static class RGBA extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public RGBA(int max) {
			this.max = max;
			this.name = "RGBA (0-" + max + ")";
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
	
	public static final ColorModel HSVA_360_100 = new HSVA(360, 100);
	public static class HSVA extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HSVA(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HSVA";
			else if (hmax == 360) this.name = "HSVA (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HSVA (H:0-" + hmax + ")";
			else this.name = "HSVA (H:0-" + hmax + "/V:0-" + vmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"       , 0, hmax, 1),
				new ColorChannel("S", "Saturation", 0, vmax, 1),
				new ColorChannel("V", "Value"     , 0, vmax, 1),
				new ColorChannel("A", "Alpha"     , 0, vmax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float s = channels[1] / vmax;
			float v = channels[2] / vmax;
			float a = channels[3] / vmax;
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
			channels[1] *= vmax;
			channels[2] *= vmax;
			channels[3] *= vmax;
			return channels;
		}
	}
	
	public static final ColorModel HSLA_360_100 = new HSLA(360, 100);
	public static class HSLA extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HSLA(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HSLA";
			else if (hmax == 360) this.name = "HSLA (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HSLA (H:0-" + hmax + ")";
			else this.name = "HSLA (H:0-" + hmax + "/V:0-" + vmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"       , 0, hmax, 1),
				new ColorChannel("S", "Saturation", 0, vmax, 1),
				new ColorChannel("L", "Lightness" , 0, vmax, 1),
				new ColorChannel("A", "Alpha"     , 0, vmax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float s = channels[1] / vmax;
			float l = channels[2] / vmax;
			float a = channels[3] / vmax;
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
			channels[1] = s * vmax;
			channels[2] = l * vmax;
			channels[3] *= vmax;
			return channels;
		}
	}
	
	public static final ColorModel HWBA_360_100 = new HWBA(360, 100);
	public static class HWBA extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HWBA(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HWBA";
			else if (hmax == 360) this.name = "HWBA (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HWBA (H:0-" + hmax + ")";
			else this.name = "HWBA (H:0-" + hmax + "/V:0-" + vmax + ")";
			this.channels = new ColorChannel[] {
				new ColorChannel("H", "Hue"  , 0, hmax, 1),
				new ColorChannel("W", "White", 0, vmax, 1),
				new ColorChannel("B", "Black", 0, vmax, 1),
				new ColorChannel("A", "Alpha", 0, vmax, 1)
			};
		}
		@Override public String getName() { return name; }
		@Override public ColorChannel[] getChannels() { return channels; }
		@Override
		public Color makeColor(float[] channels) {
			float h = channels[0] / hmax;
			float w = channels[1] / vmax;
			float k = channels[2] / vmax;
			float a = channels[3] / vmax;
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
			channels[1] = w * vmax;
			channels[2] = k * vmax;
			channels[3] *= vmax;
			return channels;
		}
	}
	
	public static final ColorModel NAIVE_CMYKA_100 = new NaiveCMYKA(100);
	public static class NaiveCMYKA extends ColorModel {
		private final float max;
		private final String name;
		private final ColorChannel[] channels;
		public NaiveCMYKA(int max) {
			this.max = max;
			if (max == 100) this.name = "Naive CMYKA";
			else this.name = "Naive CMYKA (0-" + max + ")";
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
}
