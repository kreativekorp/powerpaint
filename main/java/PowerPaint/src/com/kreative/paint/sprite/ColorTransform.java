package com.kreative.paint.sprite;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTransform {
	public static final ColorTransform NONE = new ColorTransform(0);
	public static final ColorTransform ALL = new ColorTransform(-1);
	
	public final boolean replaceColors;
	public final boolean replaceGrays;
	public final boolean grayscale;
	public final boolean whiteToAlpha;
	public final boolean alphaThreshold;
	public final boolean colorThreshold;
	public final int intValue;
	
	public ColorTransform(
		boolean replaceColors,
		boolean replaceGrays,
		boolean grayscale,
		boolean whiteToAlpha,
		boolean alphaThreshold,
		boolean colorThreshold
	) {
		this.replaceColors  = replaceColors ;
		this.replaceGrays   = replaceGrays  ;
		this.grayscale      = grayscale     ;
		this.whiteToAlpha   = whiteToAlpha  ;
		this.alphaThreshold = alphaThreshold;
		this.colorThreshold = colorThreshold;
		int intValue = 0;
		if (replaceColors ) intValue |= 0x20;
		if (replaceGrays  ) intValue |= 0x10;
		if (grayscale     ) intValue |= 0x08;
		if (whiteToAlpha  ) intValue |= 0x04;
		if (alphaThreshold) intValue |= 0x02;
		if (colorThreshold) intValue |= 0x01;
		this.intValue = intValue;
	}
	
	public ColorTransform(int intValue) {
		this.replaceColors  = ((intValue & 0x20) != 0);
		this.replaceGrays   = ((intValue & 0x10) != 0);
		this.grayscale      = ((intValue & 0x08) != 0);
		this.whiteToAlpha   = ((intValue & 0x04) != 0);
		this.alphaThreshold = ((intValue & 0x02) != 0);
		this.colorThreshold = ((intValue & 0x01) != 0);
		this.intValue = intValue & 0x3F;
	}
	
	public int preparePixel(int p) {
		if (grayscale) {
			int a = (p >> 24) & 0xFF;
			int r = (p >> 16) & 0xFF;
			int g = (p >>  8) & 0xFF;
			int b = (p >>  0) & 0xFF;
			int k = (30*r + 59*g + 11*b) / 100;
			if (colorThreshold) k = (k < 128) ? 0 : 255;
			if (alphaThreshold) a = (a < 128) ? 0 : 255;
			if (whiteToAlpha) { a = a * (255 - k) / 255; k = 0; }
			return (a << 24) | (k << 16) | (k << 8) | k;
		} else {
			if (colorThreshold) {
				if ((p & 0x00800000) == 0) p &=~ 0x00FF0000; else p |= 0x00FF0000;
				if ((p & 0x00008000) == 0) p &=~ 0x0000FF00; else p |= 0x0000FF00;
				if ((p & 0x00000080) == 0) p &=~ 0x000000FF; else p |= 0x000000FF;
			}
			if (alphaThreshold) {
				if ((p & 0x80000000) == 0) p &=~ 0xFF000000; else p |= 0xFF000000;
			}
			if (whiteToAlpha) {
				if ((p & 0x00FFFFFF) == 0x00FFFFFF) return 0;
			}
			return p;
		}
	}
	
	public void preparePixels(int[] dst, int dstOffset, int[] src, int srcOffset, int length) {
		while (length > 0) {
			dst[dstOffset] = preparePixel(src[srcOffset]);
			dstOffset++; srcOffset++; length--;
		}
	}
	
	private int blendPixels(int p0, int p1, int f) {
		if (f <=   0) return p0;
		if (f >= 255) return p1;
		int a = (((p0 >> 24) & 0xFF) * (255-f) + ((p1 >> 24) & 0xFF) * f) / 255;
		int r = (((p0 >> 16) & 0xFF) * (255-f) + ((p1 >> 16) & 0xFF) * f) / 255;
		int g = (((p0 >>  8) & 0xFF) * (255-f) + ((p1 >>  8) & 0xFF) * f) / 255;
		int b = (((p0 >>  0) & 0xFF) * (255-f) + ((p1 >>  0) & 0xFF) * f) / 255;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	private int multiplyAlpha(int p, int a) {
		if (a <=   0) return 0;
		if (a >= 255) return p;
		a = ((p >> 24) & 0xFF) * a / 255;
		return (a << 24) | (p & 0xFFFFFF);
	}
	
	public int replacePixel(int p, int k, int w, int r, int y, int g, int c, int b, int m) {
		if (replaceColors) {
			switch (p) {
				case 0xFF000000: return replaceGrays ? k : p;
				case 0xFFFFFFFF: return replaceGrays ? w : p;
				case 0xFFFF0000: return r;
				case 0xFFFFFF00: return y;
				case 0xFF00FF00: return g;
				case 0xFF00FFFF: return c;
				case 0xFF0000FF: return b;
				case 0xFFFF00FF: return m;
				case 0:          return 0;
			}
			int pa = (p >> 24) & 0xFF;
			int pr = (p >> 16) & 0xFF;
			int pg = (p >>  8) & 0xFF;
			int pb = (p >>  0) & 0xFF;
			if (pr == pg && pg == pb) {
				if (replaceGrays) {
					p = blendPixels(k, w, pb);
					return multiplyAlpha(p, pa);
				} else {
					return p;
				}
			} else {
				float[] hsv = Color.RGBtoHSB(pr, pg, pb, null);
				int hi = (int)Math.floor(hsv[0] * 6.0f);
				int hf = (int)Math.round((hsv[0] * 6.0f - hi) * 255.0f);
				switch (hi) {
					case 0: p = blendPixels(r, y, hf); break;
					case 1: p = blendPixels(y, g, hf); break;
					case 2: p = blendPixels(g, c, hf); break;
					case 3: p = blendPixels(c, b, hf); break;
					case 4: p = blendPixels(b, m, hf); break;
					case 5: p = blendPixels(m, r, hf); break;
				}
				int s = (int)Math.round(hsv[1] * 255.0f);
				p = blendPixels(replaceGrays ? w : 0xFFFFFFFF, p, s);
				int v = (int)Math.round(hsv[2] * 255.0f);
				p = blendPixels(replaceGrays ? k : 0xFF000000, p, v);
				return multiplyAlpha(p, pa);
			}
		} else if (replaceGrays) {
			switch (p) {
				case 0xFF000000: return k;
				case 0xFFFFFFFF: return w;
				case 0:          return 0;
			}
			int pa = (p >> 24) & 0xFF;
			int pr = (p >> 16) & 0xFF;
			int pg = (p >>  8) & 0xFF;
			int pb = (p >>  0) & 0xFF;
			if (pr == pg && pg == pb) {
				p = blendPixels(k, w, pb);
				return multiplyAlpha(p, pa);
			} else {
				return p;
			}
		} else {
			return p;
		}
	}
	
	public void replacePixels(
		int[] dst, int dstOffset, int[] src, int srcOffset, int length,
		int k, int w, int r, int y, int g, int c, int b, int m
	) {
		while (length > 0) {
			dst[dstOffset] = replacePixel(src[srcOffset], k, w, r, y, g, c, b, m);
			dstOffset++; srcOffset++; length--;
		}
	}
	
	public void replacePixels(
		int[] dst, int dstOffset, int dstScan,
		int[] src, int srcOffset, int srcScan,
		Rectangle rect, AffineTransform tx, RenderingHints rh,
		Paint k, Paint w, Paint r, Paint y,
		Paint g, Paint c, Paint b, Paint m
	) {
		int kc = (k == null) ? 0xFF000000 : (k instanceof Color) ? ((Color)k).getRGB() : 0;
		int wc = (w == null) ? 0xFFFFFFFF : (w instanceof Color) ? ((Color)w).getRGB() : 0;
		int rc = (r == null) ? 0xFFFF0000 : (r instanceof Color) ? ((Color)r).getRGB() : 0;
		int yc = (y == null) ? 0xFFFFFF00 : (y instanceof Color) ? ((Color)y).getRGB() : 0;
		int gc = (g == null) ? 0xFF00FF00 : (g instanceof Color) ? ((Color)g).getRGB() : 0;
		int cc = (c == null) ? 0xFF00FFFF : (c instanceof Color) ? ((Color)c).getRGB() : 0;
		int bc = (b == null) ? 0xFF0000FF : (b instanceof Color) ? ((Color)b).getRGB() : 0;
		int mc = (m == null) ? 0xFFFF00FF : (m instanceof Color) ? ((Color)m).getRGB() : 0;
		PaintContext kpc = (k == null || k instanceof Color) ? null : k.createContext(null, rect, rect, tx, rh);
		PaintContext wpc = (w == null || w instanceof Color) ? null : w.createContext(null, rect, rect, tx, rh);
		PaintContext rpc = (r == null || r instanceof Color) ? null : r.createContext(null, rect, rect, tx, rh);
		PaintContext ypc = (y == null || y instanceof Color) ? null : y.createContext(null, rect, rect, tx, rh);
		PaintContext gpc = (g == null || g instanceof Color) ? null : g.createContext(null, rect, rect, tx, rh);
		PaintContext cpc = (c == null || c instanceof Color) ? null : c.createContext(null, rect, rect, tx, rh);
		PaintContext bpc = (b == null || b instanceof Color) ? null : b.createContext(null, rect, rect, tx, rh);
		PaintContext mpc = (m == null || m instanceof Color) ? null : m.createContext(null, rect, rect, tx, rh);
		ColorModel kcm = (kpc == null) ? null : kpc.getColorModel();
		ColorModel wcm = (wpc == null) ? null : wpc.getColorModel();
		ColorModel rcm = (rpc == null) ? null : rpc.getColorModel();
		ColorModel ycm = (ypc == null) ? null : ypc.getColorModel();
		ColorModel gcm = (gpc == null) ? null : gpc.getColorModel();
		ColorModel ccm = (cpc == null) ? null : cpc.getColorModel();
		ColorModel bcm = (bpc == null) ? null : bpc.getColorModel();
		ColorModel mcm = (mpc == null) ? null : mpc.getColorModel();
		Raster kr = (kpc == null) ? null : kpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster wr = (wpc == null) ? null : wpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster rr = (rpc == null) ? null : rpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster yr = (ypc == null) ? null : ypc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster gr = (gpc == null) ? null : gpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster cr = (cpc == null) ? null : cpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster br = (bpc == null) ? null : bpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		Raster mr = (mpc == null) ? null : mpc.getRaster(rect.x, rect.y, rect.width, rect.height);
		for (int iy = 0, dy = dstOffset, sy = srcOffset; iy < rect.height; iy++, dy += dstScan, sy += srcScan) {
			for (int ix = 0, dx = dy, sx = sy; ix < rect.width; ix++, dx++, sx++) {
				if (kpc != null) kc = kcm.getRGB(kr.getDataElements(ix, iy, null));
				if (wpc != null) wc = wcm.getRGB(wr.getDataElements(ix, iy, null));
				if (rpc != null) rc = rcm.getRGB(rr.getDataElements(ix, iy, null));
				if (ypc != null) yc = ycm.getRGB(yr.getDataElements(ix, iy, null));
				if (gpc != null) gc = gcm.getRGB(gr.getDataElements(ix, iy, null));
				if (cpc != null) cc = ccm.getRGB(cr.getDataElements(ix, iy, null));
				if (bpc != null) bc = bcm.getRGB(br.getDataElements(ix, iy, null));
				if (mpc != null) mc = mcm.getRGB(mr.getDataElements(ix, iy, null));
				dst[dx] = replacePixel(src[sx], kc, wc, rc, yc, gc, cc, bc, mc);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (replaceColors ) s.append(" replace-colors" );
		if (replaceGrays  ) s.append(" replace-grays"  );
		if (grayscale     ) s.append(" grayscale"      );
		if (whiteToAlpha  ) s.append(" white-to-alpha" );
		if (alphaThreshold) s.append(" alpha-threshold");
		if (colorThreshold) s.append(" color-threshold");
		return s.toString().trim();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ColorTransform) {
			ColorTransform that = (ColorTransform)o;
			return this.replaceColors  == that.replaceColors
			    && this.replaceGrays   == that.replaceGrays
			    && this.grayscale      == that.grayscale
			    && this.whiteToAlpha   == that.whiteToAlpha
			    && this.alphaThreshold == that.alphaThreshold
			    && this.colorThreshold == that.colorThreshold;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.intValue;
	}
	
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("([0-9]+)");
	private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("0[Xx]([0-9A-Fa-f]+)");
	
	private static final String SPP = "(\\s|[!-~&&[^0-9A-Za-z]])?";
	private static final Pattern REPLACE_COLORS_PATTERN = Pattern.compile(
		"[Rr]eplace" + SPP + "[Cc]olou?rs"
	);
	private static final Pattern REPLACE_GRAYS_PATTERN = Pattern.compile(
		"[Rr]eplace" + SPP + "[Gg]r[ae]ys"
	);
	private static final Pattern GRAYSCALE_PATTERN = Pattern.compile(
		"[Gg]r[ae]y" + SPP + "[Ss]cale"
	);
	private static final Pattern WHITE_TO_ALPHA_PATTERN = Pattern.compile(
		"[Ww]hite" + SPP + "[Tt]o" + SPP + "[Aa]lpha"
	);
	private static final Pattern ALPHA_THRESHOLD = Pattern.compile(
		"[Aa]lpha" + SPP + "[Tt]hreshold"
	);
	private static final Pattern COLOR_THRESHOLD = Pattern.compile(
		"[Cc]olou?r" + SPP + "[Tt]hreshold"
	);
	
	public static ColorTransform fromString(String s) {
		Matcher m;
		s = s.trim().toLowerCase();
		m = DECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try {
				int i = Integer.parseInt(m.group(1), 10);
				return new ColorTransform(i);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		m = HEXADECIMAL_PATTERN.matcher(s);
		if (m.matches()) {
			try {
				int i = Integer.parseInt(m.group(1), 16);
				return new ColorTransform(i);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		m = REPLACE_COLORS_PATTERN.matcher(s);
		boolean replaceColors = m.find();
		m = REPLACE_GRAYS_PATTERN.matcher(s);
		boolean replaceGrays = m.find();
		m = GRAYSCALE_PATTERN.matcher(s);
		boolean grayscale = m.find();
		m = WHITE_TO_ALPHA_PATTERN.matcher(s);
		boolean whiteToAlpha = m.find();
		m = ALPHA_THRESHOLD.matcher(s);
		boolean alphaThreshold = m.find();
		m = COLOR_THRESHOLD.matcher(s);
		boolean colorThreshold = m.find();
		return new ColorTransform(
			replaceColors, replaceGrays,
			grayscale, whiteToAlpha,
			alphaThreshold, colorThreshold
		);
	}
	
	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				ColorTransform ct = fromString(arg);
				if (ct != null) {
					System.out.println(ct.intValue + "\t" + ct.toString());
				} else {
					System.out.println("?\t(null)");
				}
			}
		} else {
			for (int i = 0; i < 0x40; i++) {
				ColorTransform ct = new ColorTransform(i);
				System.out.println(ct.intValue + "\t" + ct.toString());
				assert(ct.replaceColors  == ((i & 0x20) != 0));
				assert(ct.replaceGrays   == ((i & 0x10) != 0));
				assert(ct.grayscale      == ((i & 0x08) != 0));
				assert(ct.whiteToAlpha   == ((i & 0x04) != 0));
				assert(ct.alphaThreshold == ((i & 0x02) != 0));
				assert(ct.colorThreshold == ((i & 0x01) != 0));
				assert(ct.intValue == i);
				assert(ct.hashCode() == i);
				ColorTransform ct2 = new ColorTransform(
					ct.replaceColors, ct.replaceGrays,
					ct.grayscale, ct.whiteToAlpha,
					ct.alphaThreshold, ct.colorThreshold
				);
				assert(ct2.replaceColors  == ct.replaceColors );
				assert(ct2.replaceGrays   == ct.replaceGrays  );
				assert(ct2.grayscale      == ct.grayscale     );
				assert(ct2.whiteToAlpha   == ct.whiteToAlpha  );
				assert(ct2.alphaThreshold == ct.alphaThreshold);
				assert(ct2.colorThreshold == ct.colorThreshold);
				assert(ct2.intValue == ct.intValue);
				assert(ct2.intValue == i);
				assert(ct2.hashCode() == ct.hashCode());
				assert(ct2.hashCode() == i);
				assert(ct2.equals(ct));
				ColorTransform ct3 = fromString(ct.toString());
				assert(ct3.replaceColors  == ct.replaceColors );
				assert(ct3.replaceGrays   == ct.replaceGrays  );
				assert(ct3.grayscale      == ct.grayscale     );
				assert(ct3.whiteToAlpha   == ct.whiteToAlpha  );
				assert(ct3.alphaThreshold == ct.alphaThreshold);
				assert(ct3.colorThreshold == ct.colorThreshold);
				assert(ct3.intValue == ct.intValue);
				assert(ct3.intValue == i);
				assert(ct3.hashCode() == ct.hashCode());
				assert(ct3.hashCode() == i);
				assert(ct3.equals(ct));
			}
		}
	}
}
