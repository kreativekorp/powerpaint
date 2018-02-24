package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

public abstract class PaletteWriter {
	public abstract boolean isCompatible(RCPXPalette pal);
	public abstract void write(RCPXPalette pal, OutputStream out) throws IOException;
	
	public static class RCPXWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return true;
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			RCPXGenerator.generate(pal, out);
		}
	}
	
	public static class ACTWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			int n = pal.colors.size();
			return (n > 0 && n <= 256);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			int t = -1;
			int n = pal.colors.size();
			if (n > 256) n = 256;
			for (int i = 0; i < n; i++) {
				Color color = pal.colors.get(i).awtColor();
				if (color.getAlpha() < 128) t = i;
				out.write(color.getRed());
				out.write(color.getGreen());
				out.write(color.getBlue());
			}
			for (int i = n; i < 256; i++) {
				out.write(-1);
				out.write(-1);
				out.write(-1);
			}
			if (n < 256 || t >= 0) {
				out.write(n >> 8);
				out.write(n);
				out.write(t >> 8);
				out.write(t);
			}
		}
	}
	
	public static class ACOWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			boolean hasNames = false;
			int n = pal.colors.size();
			out.write(0); out.write(1);
			out.write(n >> 8); out.write(n);
			for (int i = 0; i < n; i++) {
				RCPXColor color = pal.colors.get(i);
				write(color, out);
				String cn = color.name();
				if (cn != null && cn.length() > 0) hasNames = true;
			}
			if (hasNames) {
				out.write(0); out.write(2);
				out.write(n >> 8); out.write(n);
				for (int i = 0; i < n; i++) {
					RCPXColor color = pal.colors.get(i);
					write(color, out);
					String cn = color.name();
					int cnl = (cn == null) ? 1 : (cn.length() + 1);
					out.write(cnl >> 24); out.write(cnl >> 16);
					out.write(cnl >> 8); out.write(cnl);
					if (cn != null) {
						for (char ch : cn.toCharArray()) {
							out.write(ch >> 8); out.write(ch);
						}
					}
					out.write(0); out.write(0);
				}
			}
		}
		private void write(RCPXColor color, OutputStream out) throws IOException {
			if (color instanceof RCPXColor.HSV) {
				RCPXColor.HSV hsv = (RCPXColor.HSV)color;
				int h = (int)Math.round(hsv.h * 65536f / 360f);
				int s = (int)Math.round(hsv.s * 65535f / 100f);
				int v = (int)Math.round(hsv.v * 65535f / 100f);
				out.write(0); out.write(1);
				out.write(h >> 8); out.write(h);
				out.write(s >> 8); out.write(s);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.HSVA) {
				RCPXColor.HSVA hsv = (RCPXColor.HSVA)color;
				int h = (int)Math.round(hsv.h * 65536f / 360f);
				int s = (int)Math.round(hsv.s * 65535f / 100f);
				int v = (int)Math.round(hsv.v * 65535f / 100f);
				out.write(0); out.write(1);
				out.write(h >> 8); out.write(h);
				out.write(s >> 8); out.write(s);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.CMYK) {
				RCPXColor.CMYK cmyk = (RCPXColor.CMYK)color;
				int c = 65535 - (int)Math.round(cmyk.c * 65535f / 100f);
				int m = 65535 - (int)Math.round(cmyk.m * 65535f / 100f);
				int y = 65535 - (int)Math.round(cmyk.y * 65535f / 100f);
				int k = 65535 - (int)Math.round(cmyk.k * 65535f / 100f);
				out.write(0); out.write(2);
				out.write(c >> 8); out.write(c);
				out.write(m >> 8); out.write(m);
				out.write(y >> 8); out.write(y);
				out.write(k >> 8); out.write(k);
			} else if (color instanceof RCPXColor.CIELab) {
				RCPXColor.CIELab lab = (RCPXColor.CIELab)color;
				int l = (int)Math.round(lab.l * 100f);
				int a = (int)Math.round(lab.a * 100f);
				int b = (int)Math.round(lab.b * 100f);
				out.write(0); out.write(7);
				out.write(l >> 8); out.write(l);
				out.write(a >> 8); out.write(a);
				out.write(b >> 8); out.write(b);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.Gray) {
				RCPXColor.Gray gray = (RCPXColor.Gray)color;
				int v = (int)Math.round(gray.gray * 100f);
				out.write(0); out.write(8);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
				out.write(0); out.write(0);
				out.write(0); out.write(0);
			} else {
				float[] rgb = color.awtColor().getRGBColorComponents(null);
				int r = (int)Math.round(rgb[0] * 65535f);
				int g = (int)Math.round(rgb[1] * 65535f);
				int b = (int)Math.round(rgb[2] * 65535f);
				out.write(0); out.write(0);
				out.write(r >> 8); out.write(r);
				out.write(g >> 8); out.write(g);
				out.write(b >> 8); out.write(b);
				out.write(0); out.write(0);
			}
		}
	}
}
