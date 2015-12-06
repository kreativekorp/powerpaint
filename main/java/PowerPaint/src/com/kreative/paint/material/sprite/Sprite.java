package com.kreative.paint.material.sprite;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sprite {
	private final SpriteSheet spriteSheet;
	private final SpriteTreeNode treeNode;
	private final SpriteSheetSlice slice;
	
	public Sprite(BufferedImage image, int hx, int hy, ColorTransform transform) {
		this.spriteSheet = new SpriteSheet(image, hx, hy, transform);
		this.treeNode = this.spriteSheet.root.children.get(0);
		this.slice = this.spriteSheet.slices.get(0);
	}
	
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
	
	public ColorTransform getColorTransform() {
		return slice.transform;
	}
	
	private Sprite[] spriteCache = null;
	
	private void makeSpriteCache() {
		if (treeNode instanceof SpriteTreeNode.Branch) {
			SpriteTreeNode.Branch b = (SpriteTreeNode.Branch)treeNode;
			int n = b.getChildCount();
			spriteCache = new Sprite[n];
			for (int i = 0; i < n; i++) {
				SpriteTreeNode c = b.getChild(i);
				spriteCache[i] = new Sprite(spriteSheet, c);
			}
		} else {
			spriteCache = new Sprite[0];
		}
	}
	
	public int getChildCount() {
		if (spriteCache == null) makeSpriteCache();
		return spriteCache.length;
	}
	
	public Sprite getChild(int index) {
		if (spriteCache == null) makeSpriteCache();
		if (index < 0 || index >= spriteCache.length) return this;
		return spriteCache[index];
	}
	
	public Sprite getChildByPath(int... path) {
		Sprite c = this;
		for (int i : path) c = c.getChild(i);
		return c;
	}
	
	public List<Sprite> getChildren() {
		if (spriteCache == null) makeSpriteCache();
		List<Sprite> children = new ArrayList<Sprite>();
		children.addAll(Arrays.asList(spriteCache));
		return children;
	}
	
	private int[] rawPixels = null;
	private Image rawImage = null;
	private Cursor rawVCursor = null;
	private Cursor rawOCursor = null;
	private int[] preparedPixels = null;
	private Image preparedImage = null;
	private Cursor preparedVCursor = null;
	private Cursor preparedOCursor = null;
	
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
	
	public Cursor getRawCursor(boolean outline) {
		Cursor c = (outline ? rawOCursor : rawVCursor);
		if (c == null) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = createCursorDimension(tk, outline);
			if (d == null) {
				c = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			} else {
				int[] ca = getRawPixels();
				String name = super.toString() + "-raw";
				c = createCursor(tk, d, ca, name, outline);
			}
			if (outline) rawOCursor = c;
			else rawVCursor = c;
		}
		return c;
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
	
	public Cursor getPreparedCursor(boolean outline) {
		Cursor c = (outline ? preparedOCursor : preparedVCursor);
		if (c == null) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = createCursorDimension(tk, outline);
			if (d == null) {
				c = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			} else {
				int[] ca = getPreparedPixels();
				String name = super.toString() + "-prep";
				c = createCursor(tk, d, ca, name, outline);
			}
			if (outline) preparedOCursor = c;
			else preparedVCursor = c;
		}
		return c;
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
	
	public Cursor getCursor(
		int k, int w, int r, int y,
		int g, int c, int b, int m,
		boolean outline
	) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = createCursorDimension(tk, outline);
		if (d == null) {
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		} else {
			int[] ca = getPixels(k, w, r, y, g, c, b, m);
			String name = super.toString()
				+ "-" + k + "-" + w + "-" + r + "-" + y
				+ "-" + g + "-" + c + "-" + b + "-" + m;
			return createCursor(tk, d, ca, name, outline);
		}
	}
	
	public void paint(Graphics2D gr, int gx, int gy) {
		paint(gr, gx, gy, gr.getPaint(), gr.getBackground());
	}
	
	public void paint(Graphics2D gr, int gx, int gy, Paint fg, Paint bg) {
		paint(gr, gx, gy, fg, bg, null, null, null, null, null, null);
	}
	
	public void paint(
		Graphics2D gr, int gx, int gy,
		Paint k, Paint w, Paint r, Paint y,
		Paint g, Paint c, Paint b, Paint m
	) {
		gx -= slice.hotspotX;
		gy -= slice.hotspotY;
		int cw = slice.cellWidth;
		int ch = slice.cellHeight;
		int[] prep = getPreparedPixels();
		int[] ret = new int[prep.length];
		slice.transform.replacePixels(
			ret, 0, cw, prep, 0, cw,
			new Rectangle(gx, gy, cw, ch),
			gr.getTransform(),
			gr.getRenderingHints(),
			k, w, r, y, g, c, b, m
		);
		int bt = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bi = new BufferedImage(cw, ch, bt);
		bi.setRGB(0, 0, cw, ch, ret, 0, cw);
		gr.drawImage(bi, null, gx, gy);
	}
	
	public void invalidate() {
		spriteCache = null;
		rawPixels = null;
		rawImage = null;
		rawVCursor = null;
		rawOCursor = null;
		preparedPixels = null;
		preparedImage = null;
		preparedVCursor = null;
		preparedOCursor = null;
	}
	
	private Dimension createCursorDimension(Toolkit tk, boolean outline) {
		int cw = slice.cellWidth;
		int ch = slice.cellHeight;
		if (outline) { cw += 2; ch += 2; }
		Dimension d = tk.getBestCursorSize(cw, ch);
		if (d.width < cw || d.height < ch) {
			d = tk.getBestCursorSize(cw + cw, ch + ch);
			if (d.width < cw || d.height < ch) {
				return null;
			}
		}
		return d;
	}
	
	private Cursor createCursor(Toolkit tk, Dimension d, int[] ca, String name, boolean outline) {
		int cw = slice.cellWidth;
		int ch = slice.cellHeight;
		int hx = slice.hotspotX;
		int hy = slice.hotspotY;
		if (outline) {
			cw += 2; ch += 2; hx++; hy++;
			ca = createCursorOutline(ca);
			name += "-outline";
		}
		int bt = BufferedImage.TYPE_INT_ARGB;
		BufferedImage ci = new BufferedImage(d.width, d.height, bt);
		ci.setRGB(0, 0, cw, ch, ca, 0, cw);
		return tk.createCustomCursor(ci, new Point(hx, hy), name);
	}
	
	private int[] createCursorOutline(int[] pixels) {
		int cw = slice.cellWidth;
		int ch = slice.cellHeight;
		int[] ca = new int[(cw + 2) * (ch + 2)];
		for (int sy = 0, dy = cw + 2, iy = 0; iy < ch; sy += cw, dy += cw + 2, iy++) {
			for (int sx = sy, dx = dy + 1, ix = 0; ix < cw; sx++, dx++, ix++) {
				if ((pixels[sx] >>> 24) != 0) {
					ca[dx] = pixels[sx];
				}
			}
		}
		for (int ay = cw + 2, iy = 0; iy < ch; ay += cw + 2, iy++) {
			for (int ax = ay + 1, ix = 0; ix < cw; ax++, ix++) {
				if ((ca[ax] >>> 24) != 0) {
					if ((ca[ax - 1] >>> 24) == 0) ca[ax - 1] = 0xBECC1E;
					if ((ca[ax + 1] >>> 24) == 0) ca[ax + 1] = 0xBECC1E;
					if ((ca[ax - cw - 2] >>> 24) == 0) ca[ax - cw - 2] = 0xBECC1E;
					if ((ca[ax + cw + 2] >>> 24) == 0) ca[ax + cw + 2] = 0xBECC1E;
				}
			}
		}
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] == 0xBECC1E) {
				ca[i] = -1;
			}
		}
		return ca;
	}
}
