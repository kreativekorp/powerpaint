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
}
