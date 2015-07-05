package com.kreative.paint.rcp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;

public abstract class RCPXSwatch extends RCPXLayoutOrSwatch {
	private static final Image SWEEP_MARKER;
	static {
		Toolkit tk = Toolkit.getDefaultToolkit();
		SWEEP_MARKER = tk.createImage(RCPXSwatch.class.getResource("ColorCursor.png"));
		tk.prepareImage(SWEEP_MARKER, -1, -1, null);
	}
	
	@Override public final boolean isLayout() { return false; }
	@Override public final RCPXLayout asLayout() { return null; }
	@Override public final boolean isSwatch() { return true; }
	@Override public final RCPXSwatch asSwatch() { return this; }
	
	public static class Empty extends RCPXSwatch {
		public final int repeat;
		public Empty(int repeat) {
			this.repeat = repeat;
		}
		@Override
		public int repeatCount() {
			return repeat;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return null;
		}
		@Override
		public String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return null;
		}
		@Override
		public void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol) {
			return;
		}
	}
	
	public static class Index extends RCPXSwatch {
		public final int i;
		public final int repeat;
		public final RCPXBorder border_only;
		public final RCPXBorder border_first;
		public final RCPXBorder border_middle;
		public final RCPXBorder border_last;
		public Index(
			int i, int repeat,
			RCPXBorder border_only,
			RCPXBorder border_first,
			RCPXBorder border_middle,
			RCPXBorder border_last
		) {
			this.i = i;
			this.repeat = repeat;
			this.border_only = border_only;
			this.border_first = border_first;
			this.border_middle = border_middle;
			this.border_last = border_last;
		}
		public RCPXBorder getBorder(int i) {
			if (repeat <= 1) return border_only;
			if (i <= 0) return border_first;
			if (i >= (repeat-1)) return border_last;
			return border_middle;
		}
		@Override
		public int repeatCount() {
			return repeat;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return colors.get(i).awtColor();
		}
		@Override
		public String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return colors.get(i).name();
		}
		@Override
		public void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol) {
			Color c = colors.get(i).awtColor();
			if (c.getAlpha() < 255 && g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(CheckerboardPaint.LIGHT);
				g2.fillRect(r.x, r.y, r.width, r.height);
			}
			g.setColor(c);
			g.fillRect(r.x, r.y, r.width, r.height);
			getBorder(repeatIndex).paint(g, r, c.equals(currCol) ? c : null);
		}
	}
	
	public static class Range extends RCPXSwatch {
		public final int start;
		public final int end;
		public final RCPXBorder border_only;
		public final RCPXBorder border_first;
		public final RCPXBorder border_middle;
		public final RCPXBorder border_last;
		public Range(
			int start, int end,
			RCPXBorder border_only,
			RCPXBorder border_first,
			RCPXBorder border_middle,
			RCPXBorder border_last
		) {
			this.start = Math.min(start, end);
			this.end = Math.max(start, end);
			this.border_only = border_only;
			this.border_first = border_first;
			this.border_middle = border_middle;
			this.border_last = border_last;
		}
		public RCPXBorder getBorder(int i) {
			int count = end - start;
			if (count <= 1) return border_only;
			if (i <= 0) return border_first;
			if (i >= (count-1)) return border_last;
			return border_middle;
		}
		@Override
		public int repeatCount() {
			return end - start;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return colors.get(start + repeatIndex).awtColor();
		}
		@Override
		public String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return colors.get(start + repeatIndex).name();
		}
		@Override
		public void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol) {
			Color c = colors.get(start + repeatIndex).awtColor();
			if (c.getAlpha() < 255 && g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(CheckerboardPaint.LIGHT);
				g2.fillRect(r.x, r.y, r.width, r.height);
			}
			g.setColor(c);
			g.fillRect(r.x, r.y, r.width, r.height);
			getBorder(repeatIndex).paint(g, r, c.equals(currCol) ? c : null);
		}
	}
	
	public static class RGBSweep extends RCPXSwatch {
		public final RGBChannel xchan;
		public final int xmin, xmax;
		public final RGBChannel ychan;
		public final int ymin, ymax;
		public final int r, g, b;
		public final RCPXBorder border;
		public RGBSweep(
			RGBChannel xchan, int xmin, int xmax,
			RGBChannel ychan, int ymin, int ymax,
			int r, int g, int b,
			RCPXBorder border
		) {
			this.xchan = xchan;
			this.xmin = xmin;
			this.xmax = xmax;
			this.ychan = ychan;
			this.ymin = ymin;
			this.ymax = ymax;
			this.r = r;
			this.g = g;
			this.b = b;
			this.border = border;
		}
		@Override
		public int repeatCount() {
			return 1;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			float cr = this.r / 255.0f;
			float cg = this.g / 255.0f;
			float cb = this.b / 255.0f;
			float xv = ((float)(p.x - r.x) / (float)(r.width - 1));
			float yv = ((float)(p.y - r.y) / (float)(r.height - 1));
			xv = (xmax/255.0f)*xv + (xmin/255.0f)*(1.0f-xv);
			yv = (ymax/255.0f)*yv + (ymin/255.0f)*(1.0f-yv);
			switch (xchan) {
				case RED  : cr = xv; break;
				case GREEN: cg = xv; break;
				case BLUE : cb = xv; break;
			}
			switch (ychan) {
				case RED  : cr = yv; break;
				case GREEN: cg = yv; break;
				case BLUE : cb = yv; break;
			}
			return new Color(cr, cg, cb);
		}
		@Override
		public String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return null;
		}
		@Override
		public void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol) {
			Point p = new Point();
			int x, y;
			for (p.y = r.y, y = 0; y < r.height; y++, p.y++) {
				for (p.x = r.x, x = 0; x < r.width; x++, p.x++) {
					g.setColor(awtColor(colors, repeatIndex, r, p));
					g.fillRect(p.x, p.y, 1, 1);
				}
			}
			if (currCol != null) {
				int cr = currCol.getRed();
				int cg = currCol.getGreen();
				int cb = currCol.getBlue();
				boolean rok = (cr == this.r);
				boolean gok = (cg == this.g);
				boolean bok = (cb == this.b);
				int xamin = Math.min(xmin, xmax);
				int xamax = Math.max(xmin, xmax);
				int yamin = Math.min(ymin, ymax);
				int yamax = Math.max(ymin, ymax);
				float xv = 0.5f;
				float yv = 0.5f;
				switch (xchan) {
					case RED:
						rok = (cr >= xamin && cr <= xamax);
						xv = (float)(cr - xmin) / (float)(xmax - xmin);
						break;
					case GREEN:
						gok = (cg >= xamin && cg <= xamax);
						xv = (float)(cg - xmin) / (float)(xmax - xmin);
						break;
					case BLUE:
						bok = (cb >= xamin && cb <= xamax);
						xv = (float)(cb - xmin) / (float)(xmax - xmin);
						break;
				}
				switch (ychan) {
					case RED:
						rok = (cr >= yamin && cr <= yamax);
						yv = (float)(cr - ymin) / (float)(ymax - ymin);
						break;
					case GREEN:
						gok = (cg >= yamin && cg <= yamax);
						yv = (float)(cg - ymin) / (float)(ymax - ymin);
						break;
					case BLUE:
						bok = (cb >= yamin && cb <= yamax);
						yv = (float)(cb - ymin) / (float)(ymax - ymin);
						break;
				}
				if (rok && gok && bok) {
					x = r.x + (int)Math.round((r.width - 1) * xv);
					y = r.y + (int)Math.round((r.height - 1) * yv);
					g.drawImage(SWEEP_MARKER, x - 3, y - 3, null);
				}
			}
			border.paint(g, r, null);
		}
	}
	
	public static class HSVSweep extends RCPXSwatch {
		public final HSVChannel xchan;
		public final float xmin, xmax;
		public final HSVChannel ychan;
		public final float ymin, ymax;
		public final float h, s, v;
		public final RCPXBorder border;
		public HSVSweep(
			HSVChannel xchan, float xmin, float xmax,
			HSVChannel ychan, float ymin, float ymax,
			float h, float s, float v,
			RCPXBorder border
		) {
			this.xchan = xchan;
			this.xmin = xmin;
			this.xmax = xmax;
			this.ychan = ychan;
			this.ymin = ymin;
			this.ymax = ymax;
			this.h = h;
			this.s = s;
			this.v = v;
			this.border = border;
		}
		@Override
		public int repeatCount() {
			return 1;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			float ch = this.h / 360.0f;
			float cs = this.s / 100.0f;
			float cv = this.v / 100.0f;
			float xv = ((float)(p.x - r.x) / (float)(r.width - 1));
			float yv = ((float)(p.y - r.y) / (float)(r.height - 1));
			switch (xchan) {
				case HUE       : ch = (xmax/360.0f)*xv + (xmin/360.0f)*(1.0f-xv); break;
				case SATURATION: cs = (xmax/100.0f)*xv + (xmin/100.0f)*(1.0f-xv); break;
				case VALUE     : cv = (xmax/100.0f)*xv + (xmin/100.0f)*(1.0f-xv); break;
			}
			switch (ychan) {
				case HUE       : ch = (ymax/360.0f)*yv + (ymin/360.0f)*(1.0f-yv); break;
				case SATURATION: cs = (ymax/100.0f)*yv + (ymin/100.0f)*(1.0f-yv); break;
				case VALUE     : cv = (ymax/100.0f)*yv + (ymin/100.0f)*(1.0f-yv); break;
			}
			return Color.getHSBColor(ch, cs, cv);
		}
		@Override
		public String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p) {
			return null;
		}
		@Override
		public void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol) {
			Point p = new Point();
			int x, y;
			for (p.y = r.y, y = 0; y < r.height; y++, p.y++) {
				for (p.x = r.x, x = 0; x < r.width; x++, p.x++) {
					g.setColor(awtColor(colors, repeatIndex, r, p));
					g.fillRect(p.x, p.y, 1, 1);
				}
			}
			if (currCol != null) {
				float[] chsv = Color.RGBtoHSB(
					currCol.getRed(),
					currCol.getGreen(),
					currCol.getBlue(),
					null
				);
				chsv[0] *= 360.0f;
				chsv[1] *= 100.0f;
				chsv[2] *= 100.0f;
				boolean hok = (Math.abs(chsv[0] - this.h) < 0.1f);
				boolean sok = (Math.abs(chsv[1] - this.s) < 0.1f);
				boolean vok = (Math.abs(chsv[2] - this.v) < 0.1f);
				float xamin = Math.min(xmin, xmax);
				float xamax = Math.max(xmin, xmax);
				float yamin = Math.min(ymin, ymax);
				float yamax = Math.max(ymin, ymax);
				float xv = 0.5f;
				float yv = 0.5f;
				switch (xchan) {
					case HUE:
						hok = (chsv[0] >= xamin && chsv[0] <= xamax);
						xv = (float)(chsv[0] - xmin) / (float)(xmax - xmin);
						break;
					case SATURATION:
						sok = (chsv[1] >= xamin && chsv[1] <= xamax);
						xv = (float)(chsv[1] - xmin) / (float)(xmax - xmin);
						break;
					case VALUE:
						vok = (chsv[2] >= xamin && chsv[2] <= xamax);
						xv = (float)(chsv[2] - xmin) / (float)(xmax - xmin);
						break;
				}
				switch (ychan) {
					case HUE:
						hok = (chsv[0] >= yamin && chsv[0] <= yamax);
						yv = (float)(chsv[0] - ymin) / (float)(ymax - ymin);
						break;
					case SATURATION:
						sok = (chsv[1] >= yamin && chsv[1] <= yamax);
						yv = (float)(chsv[1] - ymin) / (float)(ymax - ymin);
						break;
					case VALUE:
						vok = (chsv[2] >= yamin && chsv[2] <= yamax);
						yv = (float)(chsv[2] - ymin) / (float)(ymax - ymin);
						break;
				}
				if (hok && sok && vok) {
					x = r.x + (int)Math.round((r.width - 1) * xv);
					y = r.y + (int)Math.round((r.height - 1) * yv);
					g.drawImage(SWEEP_MARKER, x - 3, y - 3, null);
				}
			}
			border.paint(g, r, null);
		}
	}
}
