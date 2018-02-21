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
}
