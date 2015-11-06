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
	
	public static final ColorModel HSV_360_100 = new HSV(360, 100);
	public static class HSV extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HSV(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HSV";
			else if (hmax == 360) this.name = "HSV (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HSV (H:0-" + hmax + ")";
			else this.name = "HSV (H:0-" + hmax + "/V:0-" + vmax + ")";
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
	
	public static final ColorModel HSL_360_100 = new HSL(360, 100);
	public static class HSL extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HSL(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HSL";
			else if (hmax == 360) this.name = "HSL (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HSL (H:0-" + hmax + ")";
			else this.name = "HSL (H:0-" + hmax + "/V:0-" + vmax + ")";
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
	
	public static final ColorModel HWB_360_100 = new HWB(360, 100);
	public static class HWB extends ColorModel {
		private final float hmax;
		private final float vmax;
		private final String name;
		private final ColorChannel[] channels;
		public HWB(int hmax, int vmax) {
			this.hmax = hmax;
			this.vmax = vmax;
			if (hmax == 360 && vmax == 100) this.name = "HWB";
			else if (hmax == 360) this.name = "HWB (V:0-" + vmax + ")";
			else if (vmax == 100) this.name = "HWB (H:0-" + hmax + ")";
			else this.name = "HWB (H:0-" + hmax + "/V:0-" + vmax + ")";
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
}
