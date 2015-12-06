package com.kreative.paint.material.frame;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Frame {
	public final BufferedImage image;
	public final int contentStartX, contentStartY, contentExtentX, contentExtentY;
	public final int repeatStartX, repeatStartY, repeatExtentX, repeatExtentY;
	public final int widthMultiplier, widthBase, heightMultiplier, heightBase;
	public final String name;
	
	public Frame(
		BufferedImage image,
		int contentStartX, int contentStartY, int contentExtentX, int contentExtentY,
		int repeatStartX, int repeatStartY, int repeatExtentX, int repeatExtentY,
		int widthMultiplier, int widthBase, int heightMultiplier, int heightBase,
		String name
	) {
		this.image = image;
		this.contentStartX = contentStartX; this.contentStartY = contentStartY;
		this.contentExtentX = contentExtentX; this.contentExtentY = contentExtentY;
		this.repeatStartX = repeatStartX; this.repeatStartY = repeatStartY;
		this.repeatExtentX = repeatExtentX; this.repeatExtentY = repeatExtentY;
		this.widthMultiplier = widthMultiplier; this.widthBase = widthBase;
		this.heightMultiplier = heightMultiplier; this.heightBase = heightBase;
		this.name = name;
	}
	
	public Frame(
		BufferedImage image,
		Rectangle contentRect,
		Rectangle repeatRect,
		Rectangle restrictRect,
		String name
	) {
		this.image = image;
		if (contentRect == null) contentRect = calculateContentRect(image);
		this.contentStartX = contentRect.x; this.contentStartY = contentRect.y;
		this.contentExtentX = contentRect.width; this.contentExtentY = contentRect.height;
		if (repeatRect == null) repeatRect = calculateRepeatRect(image, contentRect);
		this.repeatStartX = repeatRect.x; this.repeatStartY = repeatRect.y;
		this.repeatExtentX = repeatRect.width; this.repeatExtentY = repeatRect.height;
		if (restrictRect == null) restrictRect = new Rectangle(0, 0, 0, 0);
		this.widthMultiplier = restrictRect.width; this.widthBase = restrictRect.x;
		this.heightMultiplier = restrictRect.height; this.heightBase = restrictRect.y;
		this.name = name;
	}
	
	public Frame(BufferedImage image, String name) {
		this(image, null, null, null, name);
	}
	
	public void paint(Graphics g, int x1, int y1, int x2, int y2) {
		int sx = Math.min(x1, x2);
		int sy = Math.min(y1, y2);
		int x = Math.max(x1, x2);
		int y = Math.max(y1, y2);
		int lox = sx - contentStartX;
		int toy = sy - contentStartY;
		int lix = sx + repeatStartX - contentStartX;
		int tiy = sy + repeatStartY - contentStartY;
		int rix = x + repeatStartX + repeatExtentX - contentStartX - contentExtentX;
		int biy = y + repeatStartY + repeatExtentY - contentStartY - contentExtentY;
		int rox = x + image.getWidth() - contentStartX - contentExtentX;
		int boy = y + image.getHeight() - contentStartY - contentExtentY;
		// top/bottom edges
		for (int xx = lix; xx < rix; xx += repeatExtentX) {
			g.drawImage(
				image, xx, toy, xx + Math.min(repeatExtentX, rix-xx), tiy,
				repeatStartX, 0,
				repeatStartX + Math.min(repeatExtentX, rix-xx), repeatStartY,
				null
			);
			g.drawImage(
				image, xx, biy, xx + Math.min(repeatExtentX, rix-xx), boy,
				repeatStartX, repeatStartY + repeatExtentY,
				repeatStartX + Math.min(repeatExtentX, rix-xx), image.getHeight(),
				null
			);
		}
		// left/right edges
		for (int yy = tiy; yy < biy; yy += repeatExtentY) {
			g.drawImage(
				image, lox, yy, lix, yy + Math.min(repeatExtentY, biy-yy),
				0, repeatStartY,
				repeatStartX, repeatStartY + Math.min(repeatExtentY, biy-yy),
				null
			);
			g.drawImage(
				image, rix, yy, rox, yy + Math.min(repeatExtentY, biy-yy),
				repeatStartX + repeatExtentX, repeatStartY,
				image.getWidth(), repeatStartY + Math.min(repeatExtentY, biy-yy),
				null
			);
		}
		// top left corner
		g.drawImage(
			image, lox, toy, lix, tiy,
			0, 0, repeatStartX, repeatStartY, null
		);
		// top right corner
		g.drawImage(
			image, rix, toy, rox, tiy,
			repeatStartX + repeatExtentX, 0,
			image.getWidth(), repeatStartY, null
		);
		// bottom left corner
		g.drawImage(
			image, lox, biy, lix, boy,
			0, repeatStartY + repeatExtentY,
			repeatStartX, image.getHeight(), null
		);
		// bottom right corner
		g.drawImage(
			image, rix, biy, rox, boy,
			repeatStartX + repeatExtentX,
			repeatStartY + repeatExtentY,
			image.getWidth(), image.getHeight(), null
		);
	}
	
	public void paintRestricted(Graphics g, int x1, int y1, int x2, int y2, boolean filled) {
		int w = x2 - x1;
		int h = y2 - y1;
		int mw = contentExtentX - repeatExtentX;
		int mh = contentExtentY - repeatExtentY;
		boolean nw = (w < 0);
		boolean nh = (h < 0);
		if (nw) w =- w;
		if (nh) h =- h;
		if (widthMultiplier > 0) {
			w -= widthBase;
			w = widthMultiplier * (int)Math.round((double)w / (double)widthMultiplier);
			w += widthBase;
		}
		if (heightMultiplier > 0) {
			h -= heightBase;
			h = heightMultiplier * (int)Math.round((double)h / (double)heightMultiplier);
			h += heightBase;
		}
		if (w <= mw) w = mw + 1;
		if (h <= mh) h = mh + 1;
		if (nw) w =- w;
		if (nh) h =- h;
		if (filled) g.fillRect((nw ? (x1+w) : x1), (nh ? (y1+h) : y1), Math.abs(w), Math.abs(h));
		paint(g, x1, y1, x1 + w, y1 + h);
	}
	
	public void paintWithin(Graphics g, int x, int y, int w, int h) {
		paint(
			g, x + contentStartX, y + contentStartY,
			x + w - (image.getWidth() - contentStartX - contentExtentX),
			y + h - (image.getHeight() - contentStartY - contentExtentY)
		);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Frame) {
			return this.equals((Frame)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(Frame that, boolean withName) {
		if (!this.image.equals(that.image)) return false;
		if (this.contentStartX != that.contentStartX) return false;
		if (this.contentStartY != that.contentStartY) return false;
		if (this.contentExtentX != that.contentExtentX) return false;
		if (this.contentExtentY != that.contentExtentY) return false;
		if (this.repeatStartX != that.repeatStartX) return false;
		if (this.repeatStartY != that.repeatStartY) return false;
		if (this.repeatExtentX != that.repeatExtentX) return false;
		if (this.repeatExtentY != that.repeatExtentY) return false;
		if (this.widthMultiplier != that.widthMultiplier) return false;
		if (this.widthBase != that.widthBase) return false;
		if (this.heightMultiplier != that.heightMultiplier) return false;
		if (this.heightBase != that.heightBase) return false;
		if (!withName) return true;
		if (this.name == null) return (that.name == null);
		if (that.name == null) return (this.name == null);
		return this.name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		int ihc = image.hashCode();
		int conhc = contentStartX + contentStartY + contentExtentX + contentExtentY;
		int rephc = repeatStartX + repeatStartY + repeatExtentX + repeatExtentY;
		int reshc = widthMultiplier + widthBase + heightMultiplier + heightBase;
		int nhc = (name != null) ? name.hashCode() : 0;
		return ihc ^ (conhc + rephc + reshc) ^ nhc;
	}
	
	public static Rectangle calculateContentRect(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int x = w/2;
		int y = h/2;
		int x1 = x, x2 = x, y1 = y, y2 = y;
		while (x1 > 0 && (image.getRGB(x1-1, y) & 0xFF000000) == 0) x1--;
		while (x2 < w-1 && (image.getRGB(x2+1, y) & 0xFF000000) == 0) x2++;
		while (y1 > 0 && (image.getRGB(x, y1-1) & 0xFF000000) == 0) y1--;
		while (y2 < h-1 && (image.getRGB(x, y2+1) & 0xFF000000) == 0) y2++;
		return new Rectangle(x1, y1, x2-x1+1, y2-y1+1);
	}
	
	public static Rectangle calculateRepeatRect(BufferedImage image, Rectangle contentRect) {
		int x3 = contentRect.x, x4 = contentRect.x+contentRect.width-1;
		int y3 = contentRect.y, y4 = contentRect.y+contentRect.height-1;
		while (y3 < y4) {
			int[] rgb = new int[contentRect.width];
			image.getRGB(contentRect.x, y3, contentRect.width, 1, rgb, 0, contentRect.width);
			if (isEmpty(rgb)) break; else y3++;
		}
		while (y4 > y3) {
			int[] rgb = new int[contentRect.width];
			image.getRGB(contentRect.x, y4, contentRect.width, 1, rgb, 0, contentRect.width);
			if (isEmpty(rgb)) break; else y4--;
		}
		while (x3 < x4) {
			int[] rgb = new int[contentRect.height];
			image.getRGB(x3, contentRect.y, 1, contentRect.height, rgb, 0, 1);
			if (isEmpty(rgb)) break; else x3++;
		}
		while (x4 > x3) {
			int[] rgb = new int[contentRect.height];
			image.getRGB(x4, contentRect.y, 1, contentRect.height, rgb, 0, 1);
			if (isEmpty(rgb)) break; else x4--;
		}
		return new Rectangle(x3, y3, x4-x3+1, y4-y3+1);
	}
	
	private static boolean isEmpty(int[] pixels) {
		for (int pixel : pixels) {
			if ((pixel & 0xFF000000) != 0) {
				return false;
			}
		}
		return true;
	}
}
