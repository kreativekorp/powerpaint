package com.kreative.paint.sprite;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Sprite {
	private final SpriteSheet spriteSheet;
	private final SpriteTreeNode treeNode;
	private final SpriteSheetSlice slice;
	
	public Sprite(SpriteSheet spriteSheet, SpriteTreeNode treeNode) {
		this.spriteSheet = spriteSheet;
		this.treeNode = treeNode;
		this.slice = spriteSheet.getSlice(treeNode.index);
	}
	
	public String getName() {
		return treeNode.name;
	}
	
	public int getDuration() {
		return treeNode.duration;
	}
	
	public int getWidth() {
		return slice.cellWidth;
	}
	
	public int getHeight() {
		return slice.cellHeight;
	}
	
	public int getHotspotX() {
		return slice.hotspotX;
	}
	
	public int getHotspotY() {
		return slice.hotspotY;
	}
	
	public int getChildCount() {
		if (treeNode instanceof SpriteTreeNode.Branch) {
			SpriteTreeNode.Branch b = (SpriteTreeNode.Branch)treeNode;
			return b.getChildCount();
		} else {
			return 0;
		}
	}
	
	public Sprite getChild(int index) {
		if (treeNode instanceof SpriteTreeNode.Branch) {
			SpriteTreeNode.Branch b = (SpriteTreeNode.Branch)treeNode;
			SpriteTreeNode c = b.getChild(index);
			return (c == null) ? null : new Sprite(spriteSheet, c);
		} else {
			return this;
		}
	}
	
	public Sprite getChildByPath(int... path) {
		if (treeNode instanceof SpriteTreeNode.Branch) {
			SpriteTreeNode.Branch b = (SpriteTreeNode.Branch)treeNode;
			SpriteTreeNode c = b.getChildByPath(path);
			return (c == null) ? null : new Sprite(spriteSheet, c);
		} else {
			return this;
		}
	}
	
	private int[] rawPixels = null;
	private Image rawImage = null;
	private int[] preparedPixels = null;
	private Image preparedImage = null;
	
	public int[] getRawPixels() {
		if (rawPixels == null) {
			int sx = slice.startX;
			int sy = slice.startY;
			int cw = slice.cellWidth;
			int ch = slice.cellHeight;
			BufferedImage bi = spriteSheet.image;
			int[] raw = new int[cw * ch];
			bi.getRGB(sx, sy, cw, ch, raw, 0, cw);
			rawPixels = raw;
		}
		return rawPixels;
	}
	
	public Image getRawImage() {
		if (rawImage == null) {
			int cw = slice.cellWidth;
			int ch = slice.cellHeight;
			int bt = BufferedImage.TYPE_INT_ARGB;
			BufferedImage bi = new BufferedImage(cw, ch, bt);
			int[] raw = getRawPixels();
			bi.setRGB(0, 0, cw, ch, raw, 0, cw);
			rawImage = bi;
		}
		return rawImage;
	}
	
	public int[] getPreparedPixels() {
		if (preparedPixels == null) {
			ColorTransform tx = slice.transform;
			int[] raw = getRawPixels();
			int[] prep = new int[raw.length];
			tx.preparePixels(prep, 0, raw, 0, raw.length);
			preparedPixels = prep;
		}
		return preparedPixels;
	}
	
	public Image getPreparedImage() {
		if (preparedImage == null) {
			int cw = slice.cellWidth;
			int ch = slice.cellHeight;
			int bt = BufferedImage.TYPE_INT_ARGB;
			BufferedImage bi = new BufferedImage(cw, ch, bt);
			int[] prep = getPreparedPixels();
			bi.setRGB(0, 0, cw, ch, prep, 0, cw);
			preparedImage = bi;
		}
		return preparedImage;
	}
	
	public int[] getPixels(
		int k, int w, int r, int y,
		int g, int c, int b, int m
	) {
		ColorTransform tx = slice.transform;
		int[] prep = getPreparedPixels();
		int[] ret = new int[prep.length];
		tx.replacePixels(
			ret, 0, prep, 0, prep.length,
			k, w, r, y, g, c, b, m
		);
		return ret;
	}
	
	public Image getImage(
		int k, int w, int r, int y,
		int g, int c, int b, int m
	) {
		int cw = slice.cellWidth;
		int ch = slice.cellHeight;
		int bt = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bi = new BufferedImage(cw, ch, bt);
		int[] ret = getPixels(k, w, r, y, g, c, b, m);
		bi.setRGB(0, 0, cw, ch, ret, 0, cw);
		return bi;
	}
}
