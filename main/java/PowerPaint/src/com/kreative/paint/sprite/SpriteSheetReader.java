package com.kreative.paint.sprite;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SpriteSheetReader {
	public static final long PNG_MAGIC_NUMBER = 0x89504E470D0A1A0AL;
	public static final int PNG_CHUNK_SPNF = 0x73704E46;
	
	public static class Options {
		public static final int HOTSPOT_CENTER = Integer.MIN_VALUE;
		private int slicingType;
		private int slicingWidth;
		private int slicingHeight;
		private int hotspotX;
		private int hotspotY;
		private ArrayOrdering slicingOrder;
		private ColorTransform transform;
		private int presentationColumns;
		private int presentationRows;
		private ArrayOrdering presentationOrder;
		private int intent;
		private int parentMultiplicity;
		private int firstChildOffset;
		public Options() {
			setDefaultSlicingStrip();
			setDefaultHotspot(HOTSPOT_CENTER, HOTSPOT_CENTER);
			setDefaultSlicingOrder(ArrayOrdering.LTR_TTB);
			setDefaultColorTransform(ColorTransform.NONE);
			setDefaultPresentationAuto();
			setDefaultIntent(0);
			setDefaultStructureFlat();
		}
		public Options setDefaultSlicingNone() {
			this.slicingType = 0;
			return this;
		}
		public Options setDefaultSlicingStrip() {
			this.slicingType = 1;
			return this;
		}
		public Options setDefaultSlicingFixed(int cw, int ch) {
			this.slicingType = 2;
			this.slicingWidth = cw;
			this.slicingHeight = ch;
			return this;
		}
		public Options setDefaultHotspot(int hx, int hy) {
			this.hotspotX = hx;
			this.hotspotY = hy;
			return this;
		}
		public Options setDefaultSlicingOrder(ArrayOrdering order) {
			this.slicingOrder = order;
			return this;
		}
		public Options setDefaultColorTransform(ColorTransform transform) {
			this.transform = transform;
			return this;
		}
		public Options setDefaultPresentationAuto() {
			this.presentationColumns = -1;
			this.presentationRows = -1;
			this.presentationOrder = ArrayOrdering.LTR_TTB;
			return this;
		}
		public Options setDefaultPresentation(int columns, int rows, ArrayOrdering order) {
			this.presentationColumns = columns;
			this.presentationRows = rows;
			this.presentationOrder = order;
			return this;
		}
		public Options setDefaultIntent(int intent) {
			this.intent = intent;
			return this;
		}
		public Options setDefaultStructureFlat() {
			this.parentMultiplicity = 0;
			this.firstChildOffset = 0;
			return this;
		}
		public Options setDefaultStructureSingleParent(boolean excludeParent) {
			this.parentMultiplicity = 1;
			this.firstChildOffset = excludeParent ? 1 : 0;
			return this;
		}
		public Options setDefaultStructureMultipleParents(boolean excludeParents) {
			this.parentMultiplicity = 2;
			this.firstChildOffset = excludeParents ? 1 : 0;
			return this;
		}
	}
	
	public static SpriteSheet readSpriteSheet(File file, Options o) throws IOException {
		String name = file.getName();
		name = name.replaceFirst("^#[0-9]+ ", "");
		name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
		name = name.trim();
		BufferedImage image = ImageIO.read(file);
		return readSpriteSheet(name, file, image, o);
	}
	
	public static SpriteSheet readSpriteSheet(String name, byte[] data, Options o) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(in);
		in.close();
		return readSpriteSheet(name, data, image, o);
	}
	
	public static SpriteSheet readSpriteSheet(String name, InputStream in, Options o) throws IOException {
		in.mark(0x100000);
		BufferedImage image = ImageIO.read(in);
		in.reset();
		return readSpriteSheet(name, in, image, o);
	}
	
	public static SpriteSheet readSpriteSheet(String name, File file, BufferedImage image, Options o) throws IOException {
		InputStream in = new FileInputStream(file);
		SpriteSheet sheet = readSpriteSheet(name, in, image, o);
		in.close();
		return sheet;
	}
	
	public static SpriteSheet readSpriteSheet(String name, byte[] data, BufferedImage image, Options o) throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		SpriteSheet sheet = readSpriteSheet(name, in, image, o);
		in.close();
		return sheet;
	}
	
	public static SpriteSheet readSpriteSheet(String name, InputStream in, BufferedImage image, Options o) throws IOException {
		DataInputStream data = new DataInputStream(in);
		if (data.readLong() == PNG_MAGIC_NUMBER) {
			while (data.available() > 0) {
				int cl = data.readInt();
				int ct = data.readInt();
				byte[] cd = new byte[cl];
				data.read(cd);
				data.readInt();
				if (ct == PNG_CHUNK_SPNF) {
					DataInputStream chunk = new DataInputStream(new ByteArrayInputStream(cd));
					SpriteSheet sheet = readSpriteSheet(chunk, image);
					chunk.close();
					return sheet;
				}
			}
		}
		return createSpriteSheet(name, image, o);
	}
	
	public static SpriteSheet createSpriteSheet(String name, BufferedImage image, Options o) {
		if (o == null) o = new Options();
		int w = image.getWidth();
		int h = image.getHeight();
		int cw, ch;
		switch (o.slicingType) {
			default:
			case 0: cw = w; ch = h; break;
			case 1: cw = ch = Math.min(w, h); break;
			case 2: cw = o.slicingWidth; ch = o.slicingHeight; break;
		}
		int chx = (o.hotspotX == Options.HOTSPOT_CENTER) ? (cw / 2) :
		          (o.hotspotX < 0) ? (cw + o.hotspotX) : o.hotspotX;
		int chy = (o.hotspotY == Options.HOTSPOT_CENTER) ? (ch / 2) :
		          (o.hotspotY < 0) ? (ch + o.hotspotY) : o.hotspotY;
		int cols = w / cw;
		int rows = h / ch;
		int pcols = o.presentationColumns;
		int prows = o.presentationRows;
		if (pcols < 0 && prows < 0) {
			pcols = cols;
			prows = rows;
		} else if (pcols < 0) {
			pcols = (prows == 0) ? cols : (((cols * rows) + prows - 1) / prows);
		} else if (prows < 0) {
			prows = (pcols == 0) ? rows : (((cols * rows) + pcols - 1) / pcols);
		}
		SpriteSheet sheet = new SpriteSheet(
			image, name, o.intent,
			pcols, prows, o.presentationOrder
		);
		sheet.slices.add(new SpriteSheetSlice(
			0, 0, cw, ch, chx, chy, cw, ch, cols, rows,
			o.slicingOrder, o.transform
		));
		if (o.parentMultiplicity <= 0) {
			int fc = o.firstChildOffset;
			int ns = cols * rows;
			SpriteTreeNode.Leaf l = new SpriteTreeNode.Leaf(name, fc, 0, ns - fc);
			sheet.root.children.add(l);
		} else if (o.parentMultiplicity == 1) {
			int fc = o.firstChildOffset;
			int ns = cols * rows;
			SpriteTreeNode.Branch b = new SpriteTreeNode.Branch(name, 0, 0);
			SpriteTreeNode.Leaf l = new SpriteTreeNode.Leaf(name, fc, 0, ns - fc);
			b.children.add(l);
			sheet.root.children.add(b);
		} else if (o.parentMultiplicity >= 2) {
			int fc = o.firstChildOffset;
			int np = o.slicingOrder.vertical ? cols : rows;
			int nc = o.slicingOrder.vertical ? rows : cols;
			for (int p = 0, i = 0; p < np; p++, i += nc) {
				SpriteTreeNode.Branch b = new SpriteTreeNode.Branch(name, i, 0);
				SpriteTreeNode.Leaf l = new SpriteTreeNode.Leaf(name, i + fc, 0, nc - fc);
				b.children.add(l);
				sheet.root.children.add(b);
			}
		}
		return sheet;
	}
	
	private static SpriteSheet readSpriteSheet(DataInputStream chunk, BufferedImage image) throws IOException {
		String name = (chunk.available() > 0) ? chunk.readUTF() : "";
		int intent = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int cols = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int rows = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		ArrayOrdering order = ArrayOrdering.fromIntValue((chunk.available() > 0) ? chunk.readUnsignedShort() : 0);
		SpriteSheet sheet = new SpriteSheet(image, name, intent, cols, rows, order);
		int sliceCount = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		for (int i = 0; i < sliceCount; i++) sheet.slices.add(readSlice(chunk, image));
		int nodeCount = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		for (int i = 0; i < nodeCount; i++) sheet.root.children.add(readTreeNode(chunk));
		return sheet;
	}
	
	private static SpriteSheetSlice readSlice(DataInputStream chunk, BufferedImage image) throws IOException {
		int w = image.getWidth();
		int h = image.getHeight();
		int sx = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int sy = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int cw = (chunk.available() > 0) ? chunk.readUnsignedShort() : Math.min(w - sx, h - sy);
		int ch = (chunk.available() > 0) ? chunk.readUnsignedShort() : Math.min(w - sx, h - sy);
		int chx = (chunk.available() > 0) ? chunk.readUnsignedShort() : (cw / 2);
		int chy = (chunk.available() > 0) ? chunk.readUnsignedShort() : (ch / 2);
		int cdx = (chunk.available() > 0) ? chunk.readUnsignedShort() : cw;
		int cdy = (chunk.available() > 0) ? chunk.readUnsignedShort() : ch;
		int cols = (chunk.available() > 0) ? chunk.readUnsignedShort() : ((w - sx + cdx - cw) / cdx);
		int rows = (chunk.available() > 0) ? chunk.readUnsignedShort() : ((h - sy + cdy - ch) / cdy);
		ArrayOrdering order = ArrayOrdering.fromIntValue((chunk.available() > 0) ? chunk.readUnsignedShort() : 0);
		ColorTransform transform = new ColorTransform((chunk.available() > 0) ? chunk.readUnsignedShort() : 0);
		return new SpriteSheetSlice(sx, sy, cw, ch, chx, chy, cdx, cdy, cols, rows, order, transform);
	}
	
	private static SpriteTreeNode readTreeNode(DataInputStream chunk) throws IOException {
		String name = (chunk.available() > 0) ? chunk.readUTF() : "";
		int index = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int duration = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		int count = (chunk.available() > 0) ? chunk.readUnsignedShort() : 0;
		if (count < 0x8000) {
			return new SpriteTreeNode.Leaf(name, index, duration, count);
		} else {
			SpriteTreeNode.Branch node = new SpriteTreeNode.Branch(name, index, duration);
			for (int i = 0x8000; i < count; i++) node.children.add(readTreeNode(chunk));
			return node;
		}
	}
}
