package com.kreative.paint.material.colorpalette;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class PaletteReader {
	public abstract RCPXPalette read(String name, InputStream in) throws IOException;
	
	public static class RCPXReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			return RCPXParser.parse(name, in);
		}
	}
	
	public static class ACTReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			for (int i = 0; i < 256; i++) {
				int r = in.read(); if (r < 0) break;
				int g = in.read(); if (g < 0) break;
				int b = in.read(); if (b < 0) break;
				colors.add(new RCPXColor.RGB(r, g, b, null));
			}
			int n = in.read() << 8; n |= in.read();
			if (n >= 0 && n < colors.size()) {
				colors.subList(n, colors.size()).clear();
			}
			int t = in.read() << 8; t |= in.read();
			if (t >= 0 && t < colors.size()) {
				RCPXColor.RGB rgb = (RCPXColor.RGB)colors.get(t);
				colors.set(t, new RCPXColor.RGBA(rgb.r, rgb.g, rgb.b, 0, null));
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
	
	public static class ACOReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			while (true) {
				int v = in.read() << 8; v |= in.read(); if (v < 0) break;
				int n = in.read() << 8; n |= in.read(); if (n < 0) break;
				colors.clear();
				for (int i = 0; i < n; i++) {
					int s = in.read() << 8; s |= in.read(); if (s < 0) break;
					int w = in.read() << 8; w |= in.read(); if (w < 0) break;
					int x = in.read() << 8; x |= in.read(); if (x < 0) break;
					int y = in.read() << 8; y |= in.read(); if (y < 0) break;
					int z = in.read() << 8; z |= in.read(); if (z < 0) break;
					String cn = null;
					if (v >= 2) {
						int cnl = in.read() << 24; cnl |= in.read() << 16;
						cnl |= in.read() << 8; cnl |= in.read(); if (cnl < 0) break;
						StringBuffer sb = new StringBuffer(cnl);
						for (int j = 0; j < cnl; j++) {
							int ch = in.read(); ch |= in.read(); if (ch <= 0) break;
							sb.append((char)ch);
						}
						if (sb.length() > 0) {
							cn = sb.toString();
							if (cn.startsWith("$$$")) {
								int o = cn.lastIndexOf('=');
								if (o >= 0) cn = cn.substring(o + 1);
							}
						}
					}
					switch (s) {
						case 0: colors.add(new RCPXColor.RGB16(w, x, y, cn)); break;
						case 1: colors.add(new RCPXColor.HSV(w*360f/65536f, x*100f/65535f, y*100f/65535f, cn)); break;
						case 2: colors.add(new RCPXColor.CMYK((65535-w)*100f/65535f, (65535-x)*100f/65535f, (65535-y)*100f/65535f, (65535-z)*100f/65535f, cn)); break;
						case 7: colors.add(new RCPXColor.CIELab(w/100f, ((x<<16)>>16)/100f, ((y<<16)>>16)/100f, cn)); break;
						case 8: colors.add(new RCPXColor.Gray(w/100f, cn)); break;
						case 9: colors.add(new RCPXColor.CMYK(w/100f, x/100f, y/100f, z/100f, cn)); break;
						default: System.err.println("Warning: Unknown color space " + s + " in ACO " + name + "."); break;
					}
				}
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
}
