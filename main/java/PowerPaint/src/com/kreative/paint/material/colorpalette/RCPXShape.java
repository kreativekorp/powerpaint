package com.kreative.paint.material.colorpalette;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public abstract class RCPXShape {
	public abstract Shape awtShape(Rectangle r, int lw, int lh);
	public abstract int getIndex();
	
	public static class Rect extends RCPXShape {
		public final float x, y, w, h;
		public final int i;
		public Rect(float x, float y, float w, float h, int i) {
			this.x = x; this.y = y; this.w = w; this.h = h;
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int x1 = r.x + (int)Math.round((r.width - 1) * this.x / lw);
			int y1 = r.y + (int)Math.round((r.height - 1) * this.y / lh);
			int x2 = r.x + (int)Math.round((r.width - 1) * (this.x + this.w) / lw);
			int y2 = r.y + (int)Math.round((r.height - 1) * (this.y + this.h) / lh);
			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}
		public int getIndex() {
			return this.i;
		}
	}
	
	public static class Diam extends RCPXShape {
		public final float cx, cy, w, h;
		public final int i;
		public Diam(float cx, float cy, float w, float h, int i) {
			this.cx = cx; this.cy = cy; this.w = w; this.h = h;
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int x1 = r.x + (int)Math.round((r.width - 1) * (this.cx - this.w / 2) / lw);
			int y1 = r.y + (int)Math.round((r.height - 1) * (this.cy - this.h / 2) / lh);
			int x2 = r.x + (int)Math.round((r.width - 1) * this.cx / lw);
			int y2 = r.y + (int)Math.round((r.height - 1) * this.cy / lh);
			int x3 = r.x + (int)Math.round((r.width - 1) * (this.cx + this.w / 2) / lw);
			int y3 = r.y + (int)Math.round((r.height - 1) * (this.cy + this.h / 2) / lh);
			return new Polygon(new int[]{x2, x3, x2, x1}, new int[]{y1, y2, y3, y2}, 4);
		}
		public int getIndex() {
			return this.i;
		}
	}
	
	public static class Tri extends RCPXShape {
		public static enum Direction { UP, DOWN, LEFT, RIGHT; }
		public final float bcx, bcy, w, h;
		public final Direction dir;
		public final int i;
		public Tri(float bcx, float bcy, float w, float h, Direction dir, int i) {
			this.bcx = bcx; this.bcy = bcy; this.w = w; this.h = h;
			this.dir = dir;
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int x1, y1, x2, y2, x3, y3;
			switch (dir) {
				default:
					x1 = r.x + (int)Math.round((r.width - 1) * (this.bcx - this.w / 2) / lw);
					x2 = r.x + (int)Math.round((r.width - 1) * this.bcx / lw);
					x3 = r.x + (int)Math.round((r.width - 1) * (this.bcx + this.w / 2) / lw);
					y1 = y3 = r.y + (int)Math.round((r.height - 1) * this.bcy / lh);
					y2 = r.y + (int)Math.round((r.height - 1) * (this.bcy - this.h) / lh);
					break;
				case DOWN:
					x1 = r.x + (int)Math.round((r.width - 1) * (this.bcx + this.w / 2) / lw);
					x2 = r.x + (int)Math.round((r.width - 1) * this.bcx / lw);
					x3 = r.x + (int)Math.round((r.width - 1) * (this.bcx - this.w / 2) / lw);
					y1 = y3 = r.y + (int)Math.round((r.height - 1) * this.bcy / lh);
					y2 = r.y + (int)Math.round((r.height - 1) * (this.bcy + this.h) / lh);
					break;
				case LEFT:
					y1 = r.y + (int)Math.round((r.height - 1) * (this.bcy + this.h / 2) / lh);
					y2 = r.y + (int)Math.round((r.height - 1) * this.bcy / lh);
					y3 = r.y + (int)Math.round((r.height - 1) * (this.bcy - this.h / 2) / lh);
					x1 = x3 = r.x + (int)Math.round((r.width - 1) * this.bcx / lw);
					x2 = r.x + (int)Math.round((r.width - 1) * (this.bcx - this.w) / lw);
					break;
				case RIGHT:
					y1 = r.y + (int)Math.round((r.height - 1) * (this.bcy - this.h / 2) / lh);
					y2 = r.y + (int)Math.round((r.height - 1) * this.bcy / lh);
					y3 = r.y + (int)Math.round((r.height - 1) * (this.bcy + this.h / 2) / lh);
					x1 = x3 = r.x + (int)Math.round((r.width - 1) * this.bcx / lw);
					x2 = r.x + (int)Math.round((r.width - 1) * (this.bcx + this.w) / lw);
					break;
			}
			return new Polygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
		}
		public int getIndex() {
			return this.i;
		}
	}
	
	public static class Hex extends RCPXShape {
		public static enum Direction { HORIZONTAL, VERTICAL; }
		public final float cx, cy, w, h;
		public final Direction dir;
		public final int i;
		public Hex(float cx, float cy, float w, float h, Direction dir, int i) {
			this.cx = cx; this.cy = cy; this.w = w; this.h = h;
			this.dir = dir;
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int x1, y1, x2, y2, x3, y3, x4, y4, x5, y5, x6, y6;
			switch (dir) {
				default:
					x1 = r.x + (int)Math.round((r.width - 1) * (this.cx - this.w / 2) / lw);
					x2 = x6 = r.x + (int)Math.round((r.width - 1) * (this.cx - this.w / 4) / lw);
					x3 = x5 = r.x + (int)Math.round((r.width - 1) * (this.cx + this.w / 4) / lw);
					x4 = r.x + (int)Math.round((r.width - 1) * (this.cx + this.w / 2) / lw);
					y1 = y4 = r.y + (int)Math.round((r.height - 1) * this.cy / lh);
					y2 = y3 = r.y + (int)Math.round((r.height - 1) * (this.cy - this.h / 2) / lh);
					y5 = y6 = r.y + (int)Math.round((r.height - 1) * (this.cy + this.h / 2) / lh);
					break;
				case VERTICAL:
					y1 = r.y + (int)Math.round((r.height - 1) * (this.cy - this.h / 2) / lh);
					y2 = y6 = r.y + (int)Math.round((r.height - 1) * (this.cy - this.h / 4) / lh);
					y3 = y5 = r.y + (int)Math.round((r.height - 1) * (this.cy + this.h / 4) / lh);
					y4 = r.y + (int)Math.round((r.height - 1) * (this.cy + this.h / 2) / lh);
					x1 = x4 = r.x + (int)Math.round((r.width - 1) * this.cx / lw);
					x2 = x3 = r.x + (int)Math.round((r.width - 1) * (this.cx + this.w / 2) / lw);
					x5 = x6 = r.x + (int)Math.round((r.width - 1) * (this.cx - this.w / 2) / lw);
					break;
			}
			return new Polygon(new int[]{x1, x2, x3, x4, x5, x6}, new int[]{y1, y2, y3, y4, y5, y6}, 6);
		}
		public int getIndex() {
			return this.i;
		}
	}
	
	public static class Poly extends RCPXShape {
		public final float[] xPoints;
		public final float[] yPoints;
		public final int nPoints;
		public final int i;
		public Poly(float[] xPoints, float[] yPoints, int nPoints, int i) {
			this.xPoints = xPoints;
			this.yPoints = yPoints;
			this.nPoints = nPoints;
			this.i = i;
		}
		public Poly(String points, int i) {
			String[] pa = points.trim().split("(\\s|,)+");
			this.nPoints = pa.length / 2;
			this.xPoints = new float[nPoints];
			this.yPoints = new float[nPoints];
			for (int pi = 0, xi = 0, yi = 1; pi < nPoints; pi++, xi += 2, yi += 2) {
				try { this.xPoints[pi] = Float.parseFloat(pa[xi]); } catch (NumberFormatException e) {}
				try { this.yPoints[pi] = Float.parseFloat(pa[yi]); } catch (NumberFormatException e) {}
			}
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int[] x = new int[xPoints.length];
			for (int i = 0; i < x.length; i++) {
				x[i] = r.x + (int)Math.round((r.width - 1) * xPoints[i] / lw);
			}
			int[] y = new int[yPoints.length];
			for (int i = 0; i < y.length; i++) {
				y[i] = r.y + (int)Math.round((r.height - 1) * yPoints[i] / lh);
			}
			return new Polygon(x, y, nPoints);
		}
		public int getIndex() {
			return this.i;
		}
	}
	
	public static class Ellipse extends RCPXShape {
		public final float cx, cy, w, h;
		public final int i;
		public Ellipse(float cx, float cy, float w, float h, int i) {
			this.cx = cx; this.cy = cy; this.w = w; this.h = h;
			this.i = i;
		}
		public Shape awtShape(Rectangle r, int lw, int lh) {
			int x1 = r.x + (int)Math.round((r.width - 1) * (this.cx - this.w / 2) / lw);
			int y1 = r.y + (int)Math.round((r.height - 1) * (this.cy - this.h / 2) / lh);
			int x2 = r.x + (int)Math.round((r.width - 1) * (this.cx + this.w / 2) / lw);
			int y2 = r.y + (int)Math.round((r.height - 1) * (this.cy + this.h / 2) / lh);
			return new Ellipse2D.Float(x1, y1, x2, y2);
		}
		public int getIndex() {
			return this.i;
		}
	}
}
