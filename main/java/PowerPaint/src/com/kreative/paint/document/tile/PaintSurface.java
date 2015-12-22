package com.kreative.paint.document.tile;

import java.awt.Graphics2D;
import java.awt.Shape;

public interface PaintSurface {
	public int getMinX();
	public int getMinY();
	public int getMaxX();
	public int getMaxY();
	public boolean contains(int x, int y);
	public boolean contains(int x, int y, int width, int height);
	public int getRGB(int x, int y);
	public int[] getRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount);
	public void setRGB(int x, int y, int rgb);
	public void setRGB(int x, int y, int rgb, Shape clip);
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount);
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount, Shape clip);
	public void clear(int x, int y, int width, int height);
	public void clear(int x, int y, int width, int height, Shape clip);
	public void clearAll();
	public Graphics2D createPaintGraphics();
}
