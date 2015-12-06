package com.kreative.paint.material.sprite;

import java.awt.Rectangle;

public class SpriteSheetSlice {
	public final int startX, startY;
	public final int cellWidth, cellHeight;
	public final int hotspotX, hotspotY;
	public final int deltaX, deltaY;
	public final int columns, rows;
	public final ArrayOrdering order;
	public final ColorTransform transform;
	
	public SpriteSheetSlice(
		int startX, int startY,
		int cellWidth, int cellHeight,
		int hotspotX, int hotspotY,
		int deltaX, int deltaY,
		int columns, int rows,
		ArrayOrdering order,
		ColorTransform transform
	) {
		this.startX = startX; this.startY = startY;
		this.cellWidth = cellWidth; this.cellHeight = cellHeight;
		this.hotspotX = hotspotX; this.hotspotY = hotspotY;
		this.deltaX = deltaX; this.deltaY = deltaY;
		this.columns = columns; this.rows = rows;
		this.order = order;
		this.transform = transform;
	}
	
	public int getSliceCount() {
		return rows * columns;
	}
	
	public Rectangle getSliceRect(int i) {
		if (i >= 0 && i < (rows * columns)) {
			int[] yx = order.getYX(rows, columns, i, new int[2]);
			int x = startX + deltaX * yx[1];
			int y = startY + deltaY * yx[0];
			return new Rectangle(x, y, cellWidth, cellHeight);
		} else {
			return null;
		}
	}
	
	public SpriteSheetSlice getSlice(int i) {
		if (i >= 0 && i < (rows * columns)) {
			int[] yx = order.getYX(rows, columns, i, new int[2]);
			int x = startX + deltaX * yx[1];
			int y = startY + deltaY * yx[0];
			return new SpriteSheetSlice(
				x, y, cellWidth, cellHeight,
				hotspotX, hotspotY, 0, 0, 1, 1,
				order, transform
			);
		} else {
			return null;
		}
	}
}
