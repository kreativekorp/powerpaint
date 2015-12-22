package com.kreative.paint.document.tile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class BufferedImagePaintSurface implements PaintSurface {
	private final BufferedImage bi;
	private final int matte;
	
	public BufferedImagePaintSurface(BufferedImage bi, int matte) {
		this.bi = bi;
		this.matte = matte;
	}
	
	public BufferedImage getImage() { return bi; }
	public int getMatte() { return matte; }
	
	@Override public int getMinX() { return 0; }
	@Override public int getMinY() { return 0; }
	@Override public int getMaxX() { return bi.getWidth(); }
	@Override public int getMaxY() { return bi.getHeight(); }
	
	@Override
	public boolean contains(int x, int y) {
		return x >= 0 && y >= 0
		    && x < bi.getWidth()
		    && y < bi.getHeight();
	}
	
	@Override
	public boolean contains(int x, int y, int width, int height) {
		return x >= 0 && y >= 0
		    && x + width <= bi.getWidth()
		    && y + height <= bi.getHeight();
	}
	
	@Override
	public int getRGB(int x, int y) {
		return bi.getRGB(x, y);
	}
	
	@Override
	public int[] getRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		return bi.getRGB(x, y, width, height, rgb, offset, rowCount);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb) {
		bi.setRGB(x, y, rgb);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb, Shape clip) {
		if (clip == null || clip.contains(x, y)) {
			bi.setRGB(x, y, rgb);
		}
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		bi.setRGB(x, y, width, height, rgb, offset, rowCount);
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount, Shape clip) {
		if (clip == null || clip.contains(x, y, width, height)) {
			bi.setRGB(x, y, width, height, rgb, offset, rowCount);
		} else {
			int[] oldRGB = new int[width * height];
			bi.getRGB(x, y, width, height, oldRGB, 0, width);
			for (int oy = 0, ny = offset, ay = y, iy = 0; iy < height; oy += width, ny += rowCount, ay++, iy++) {
				for (int ox = oy, nx = ny, ax = x, ix = 0; ix < width; oy++, ny++, ax++, ix++) {
					if (clip.contains(ax, ay)) oldRGB[ox] = rgb[nx];
				}
			}
			bi.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	@Override
	public void clear(int x, int y, int width, int height) {
		int[] rgb = new int[width * height];
		for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
		bi.setRGB(x, y, width, height, rgb, 0, width);
	}
	
	@Override
	public void clear(int x, int y, int width, int height, Shape clip) {
		if (clip == null || clip.contains(x, y, width, height)) {
			int[] rgb = new int[width * height];
			for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
			bi.setRGB(x, y, width, height, rgb, 0, width);
		} else {
			int[] oldRGB = new int[width * height];
			bi.getRGB(x, y, width, height, oldRGB, 0, width);
			for (int oy = 0, ay = y, iy = 0; iy < height; oy += width, ay++, iy++) {
				for (int ox = oy, ax = x, ix = 0; ix < width; oy++, ax++, ix++) {
					if (clip.contains(ax, ay)) oldRGB[ox] = matte;
				}
			}
			bi.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	@Override
	public void clearAll() {
		int width = bi.getWidth();
		int height = bi.getHeight();
		int[] rgb = new int[width * height];
		for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
		bi.setRGB(0, 0, width, height, rgb, 0, width);
	}
	
	@Override
	public Graphics2D createPaintGraphics() {
		return bi.createGraphics();
	}
}
