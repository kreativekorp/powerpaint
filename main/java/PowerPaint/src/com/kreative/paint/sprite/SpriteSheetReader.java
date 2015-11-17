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
	
	public static SpriteSheet readSpriteSheet(File file) throws IOException {
		String name = file.getName();
		name = name.replaceFirst("^#[0-9]+ ", "");
		name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
		name = name.trim();
		BufferedImage image = ImageIO.read(file);
		return readSpriteSheet(name, file, image);
	}
	
	public static SpriteSheet readSpriteSheet(String name, byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(in);
		in.close();
		return readSpriteSheet(name, data, image);
	}
	
	public static SpriteSheet readSpriteSheet(String name, InputStream in) throws IOException {
		in.mark(0x100000);
		BufferedImage image = ImageIO.read(in);
		in.reset();
		return readSpriteSheet(name, in, image);
	}
	
	public static SpriteSheet readSpriteSheet(String name, File file, BufferedImage image) throws IOException {
		InputStream in = new FileInputStream(file);
		SpriteSheet sheet = readSpriteSheet(name, in, image);
		in.close();
		return sheet;
	}
	
	public static SpriteSheet readSpriteSheet(String name, byte[] data, BufferedImage image) throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		SpriteSheet sheet = readSpriteSheet(name, in, image);
		in.close();
		return sheet;
	}
	
	public static SpriteSheet readSpriteSheet(String name, InputStream in, BufferedImage image) throws IOException {
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
		return createSpriteSheet(name, image);
	}
	
	private static SpriteSheet createSpriteSheet(String name, BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int cw = Math.min(w, h);
		int ch = Math.min(w, h);
		int chx = cw / 2;
		int chy = ch / 2;
		int cols = w / cw;
		int rows = h / ch;
		ArrayOrdering order = ArrayOrdering.LTR_TTB;
		ColorTransform tx = ColorTransform.NONE;
		SpriteSheet sheet = new SpriteSheet(image, name, 0, cols, rows, order);
		sheet.slices.add(new SpriteSheetSlice(0, 0, cw, ch, chx, chy, cw, ch, cols, rows, order, tx));
		sheet.root.children.add(new SpriteTreeNode.Leaf("", 0, 0, cols * rows));
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
