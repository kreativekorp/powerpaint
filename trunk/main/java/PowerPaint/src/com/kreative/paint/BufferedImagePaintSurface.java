package com.kreative.paint;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BufferedImagePaintSurface implements PaintSurface {
	private BufferedImage bi;
	
	public BufferedImagePaintSurface(BufferedImage bi) {
		this.bi = bi;
	}
	
	public void clear(int x, int y, int width, int height) {
		bi.setRGB(x, y, width, height, new int[width*height], 0, width);
	}
	
	public void clearAll() {
		bi.setRGB(0, 0, bi.getWidth(), bi.getHeight(), new int[bi.getWidth()*bi.getHeight()], 0, bi.getWidth());
	}
	
	public boolean contains(int x, int y) {
		return x >= 0 && y >= 0 && x < bi.getWidth() && y < bi.getHeight();
	}
	
	public boolean contains(int x, int y, int width, int height) {
		return x >= 0 && y >= 0 && x+width <= bi.getWidth() && y+height <= bi.getHeight();
	}
	
	public Graphics2D createPaintGraphics() {
		return bi.createGraphics();
	}
	
	public int getMinX() {
		return 0;
	}
	
	public int getMinY() {
		return 0;
	}
	
	public int getMaxX() {
		return bi.getWidth();
	}
	
	public int getMaxY() {
		return bi.getHeight();
	}
	
	public int getRGB(int x, int y) {
		return bi.getRGB(x, y);
	}
	
	public int[] getRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		return bi.getRGB(bx, by, width, height, rgb, offset, rowCount);
	}
	
	public void setRGB(int x, int y, int rgb) {
		bi.setRGB(x, y, rgb);
	}
	
	public void setRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		bi.setRGB(bx, by, width, height, rgb, offset, rowCount);
	}
}
