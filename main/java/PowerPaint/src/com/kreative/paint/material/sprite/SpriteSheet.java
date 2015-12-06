package com.kreative.paint.material.sprite;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
	public final BufferedImage image;
	public final String name;
	public final int intent;
	public final int columns;
	public final int rows;
	public final ArrayOrdering order;
	public final List<SpriteSheetSlice> slices;
	public final SpriteTreeNode.Branch root;
	
	public SpriteSheet(
		BufferedImage image,
		int hotspotX,
		int hotspotY,
		ColorTransform transform
	) {
		this(image, "", 0, 1, 1, ArrayOrdering.LTR_TTB);
		int w = image.getWidth(), h = image.getHeight();
		this.slices.add(new SpriteSheetSlice(
			0, 0, w, h, hotspotX, hotspotY,
			0, 0, 1, 1, ArrayOrdering.LTR_TTB,
			transform
		));
		this.root.children.add(new SpriteTreeNode.Leaf("", 0, 0, 1));
	}
	
	public SpriteSheet(
		BufferedImage image,
		String name,
		int intent,
		int columns,
		int rows,
		ArrayOrdering order
	) {
		this.image = image;
		this.name = name;
		this.intent = intent;
		this.columns = columns;
		this.rows = rows;
		this.order = order;
		this.slices = new ArrayList<SpriteSheetSlice>();
		this.root = new SpriteTreeNode.Branch(name, 0, 0);
	}
	
	public int getSliceCount() {
		int count = 0;
		for (SpriteSheetSlice slice : slices) {
			count += slice.getSliceCount();
		}
		return count;
	}
	
	public Rectangle getSliceRect(int i) {
		if (i < 0) return null;
		for (SpriteSheetSlice slice : slices) {
			int count = slice.getSliceCount();
			if (i < count) {
				return slice.getSliceRect(i);
			} else {
				i -= count;
			}
		}
		return null;
	}
	
	public SpriteSheetSlice getSlice(int i) {
		if (i < 0) return null;
		for (SpriteSheetSlice slice : slices) {
			int count = slice.getSliceCount();
			if (i < count) {
				return slice.getSlice(i);
			} else {
				i -= count;
			}
		}
		return null;
	}
	
	public int getNodeCount() {
		return root.getChildCount();
	}
	
	public SpriteTreeNode getNode(int index) {
		return root.getChild(index);
	}
	
	public SpriteTreeNode getNodeByPath(int... path) {
		return root.getChildByPath(path);
	}
	
	public int getSpriteCount() {
		return root.getChildCount();
	}
	
	public Sprite getSprite(int index) {
		SpriteTreeNode c = root.getChild(index);
		return (c == null) ? null : new Sprite(this, c);
	}
	
	public Sprite getSpriteByPath(int... path) {
		SpriteTreeNode c = root.getChildByPath(path);
		return (c == null) ? null : new Sprite(this, c);
	}
	
	public List<Sprite> getSprites() {
		int n = root.getChildCount();
		List<Sprite> sprites = new ArrayList<Sprite>(n);
		for (int i = 0; i < n; i++) {
			SpriteTreeNode c = root.getChild(i);
			sprites.add(new Sprite(this, c));
		}
		return sprites;
	}
}
