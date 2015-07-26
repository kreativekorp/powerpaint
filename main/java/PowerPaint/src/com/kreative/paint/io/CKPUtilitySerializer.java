package com.kreative.paint.io;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import com.kreative.paint.dither.*;
import com.kreative.paint.util.*;

public class CKPUtilitySerializer extends Serializer {
	private static final int TYPE_BITMAP = fcc("Bmap");
	private static final int TYPE_FRAME = fcc("Fram");
	private static final int TYPE_FRAMEINFO = fcc("FrIn");
	private static final int TYPE_PAIR = fcc("Pair");
	private static final int TYPE_PAIRLIST = fcc("PLst");
	private static final int TYPE_DIFFUSION_DITHER_ALGORITHM = fcc("Dith");
	private static final int TYPE_ORDERED_DITHER_ALGORITHM = fcc("Orth");
	private static final int TYPE_RANDOM_DITHER_ALGORITHM = fcc("Rath");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BITMAP, 1, Bitmap.class);
		addTypeAndClass(TYPE_FRAME, 1, Frame.class);
		addTypeAndClass(TYPE_FRAMEINFO, 1, FrameInfo.class);
		addTypeAndClass(TYPE_PAIR, 1, Pair.class);
		addTypeAndClass(TYPE_PAIRLIST, 1, PairList.class);
		addTypeAndClass(TYPE_DIFFUSION_DITHER_ALGORITHM, 2, DiffusionDitherAlgorithm.class);
		addTypeAndClass(TYPE_ORDERED_DITHER_ALGORITHM, 2, OrderedDitherAlgorithm.class);
		addTypeAndClass(TYPE_RANDOM_DITHER_ALGORITHM, 2, RandomDitherAlgorithm.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof Bitmap) {
			Bitmap v = (Bitmap)o;
			stream.writeInt(v.getWidth());
			stream.writeInt(v.getHeight());
			int[] rgb = v.getRGB();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			for (int p : rgb) dos.write((p >> 24) & 0xFF);
			for (int p : rgb) dos.write((p >> 16) & 0xFF);
			for (int p : rgb) dos.write((p >> 8) & 0xFF);
			for (int p : rgb) dos.write(p & 0xFF);
			dos.finish();
			dos.close();
			bos.close();
			byte crgb[] = bos.toByteArray();
			stream.writeInt(crgb.length);
			stream.write(crgb);
		} else if (o instanceof Frame) {
			Frame v = (Frame)o;
			SerializationManager.writeObject(v.getRawImage(), stream);
			SerializationManager.writeObject(v.getRawFrameInfo(), stream);
		} else if (o instanceof FrameInfo) {
			FrameInfo v = (FrameInfo)o;
			stream.writeShort(v.outerRect == null ? 0 : v.outerRect.x);
			stream.writeShort(v.outerRect == null ? 0 : v.outerRect.y);
			stream.writeShort(v.outerRect == null ? 0 : v.outerRect.width);
			stream.writeShort(v.outerRect == null ? 0 : v.outerRect.height);
			stream.writeShort(v.innerRect == null ? 0 : v.innerRect.x);
			stream.writeShort(v.innerRect == null ? 0 : v.innerRect.y);
			stream.writeShort(v.innerRect == null ? 0 : v.innerRect.width);
			stream.writeShort(v.innerRect == null ? 0 : v.innerRect.height);
			stream.writeShort(v.roundOffMultipleX);
			stream.writeShort(v.roundOffOffsetX);
			stream.writeShort(v.roundOffMultipleY);
			stream.writeShort(v.roundOffOffsetY);
		} else if (o instanceof Pair) {
			Pair<?,?> v = (Pair<?,?>)o;
			SerializationManager.writeObject(v.getFormer(), stream);
			SerializationManager.writeObject(v.getLatter(), stream);
		} else if (o instanceof PairList) {
			PairList<?,?> v = (PairList<?,?>)o;
			stream.writeInt(v.size());
			for (Pair<?,?> p : v) {
				SerializationManager.writeObject(p.getFormer(), stream);
				SerializationManager.writeObject(p.getLatter(), stream);
			}
		} else if (o instanceof DiffusionDitherAlgorithm) {
			DiffusionDitherAlgorithm v = (DiffusionDitherAlgorithm)o;
			stream.writeUTF((v.name != null) ? v.name : "");
			stream.writeInt(v.rows);
			stream.writeInt(v.columns);
			stream.writeInt(v.denominator);
			for (int y = 0; y < v.rows; y++) {
				for (int x = 0; x < v.columns; x++) {
					stream.writeInt(v.values[y][x]);
				}
			}
		} else if (o instanceof OrderedDitherAlgorithm) {
			OrderedDitherAlgorithm v = (OrderedDitherAlgorithm)o;
			stream.writeUTF((v.name != null) ? v.name : "");
			stream.writeInt(v.rows);
			stream.writeInt(v.columns);
			stream.writeInt(v.denominator);
			for (int y = 0; y < v.rows; y++) {
				for (int x = 0; x < v.columns; x++) {
					stream.writeInt(v.values[y][x]);
				}
			}
		} else if (o instanceof RandomDitherAlgorithm) {
			RandomDitherAlgorithm v = (RandomDitherAlgorithm)o;
			stream.writeUTF((v.name != null) ? v.name : "");
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_BITMAP) {
			if (version != 1) throw new IOException("Invalid version number.");
			int w = stream.readInt();
			int h = stream.readInt();
			int l = stream.readInt();
			byte[] crgb = new byte[l];
			stream.read(crgb);
			int[] rgb = new int[w*h];
			ByteArrayInputStream bis = new ByteArrayInputStream(crgb);
			InflaterInputStream iis = new InflaterInputStream(bis);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 24) & 0xFF000000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 16) & 0x00FF0000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 8) & 0xFF00);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= (iis.read() & 0xFF);
			iis.close();
			bis.close();
			return new Bitmap(w, h, rgb);
		} else if (type == TYPE_FRAME) {
			if (version != 1) throw new IOException("Invalid version number.");
			Image i = (Image)SerializationManager.readObject(stream);
			FrameInfo f = (FrameInfo)SerializationManager.readObject(stream);
			return new Frame(i, f);
		} else if (type == TYPE_FRAMEINFO) {
			if (version != 1) throw new IOException("Invalid version number.");
			FrameInfo f = new FrameInfo();
			int x, y, w, h;
			x = stream.readShort();
			y = stream.readShort();
			w = stream.readShort();
			h = stream.readShort();
			if (x != 0 || y != 0 || w != 0 || h != 0)
				f.outerRect = new Rectangle(x,y,w,h);
			else
				f.outerRect = null;
			x = stream.readShort();
			y = stream.readShort();
			w = stream.readShort();
			h = stream.readShort();
			if (x != 0 || y != 0 || w != 0 || h != 0)
				f.innerRect = new Rectangle(x,y,w,h);
			else
				f.innerRect = null;
			f.roundOffMultipleX = stream.readShort();
			f.roundOffOffsetX = stream.readShort();
			f.roundOffMultipleY = stream.readShort();
			f.roundOffOffsetY = stream.readShort();
			return f;
		} else if (type == TYPE_PAIR) {
			if (version != 1) throw new IOException("Invalid version number.");
			Object f = SerializationManager.readObject(stream);
			Object l = SerializationManager.readObject(stream);
			return new Pair<Object,Object>(f,l);
		} else if (type == TYPE_PAIRLIST) {
			if (version != 1) throw new IOException("Invalid version number.");
			PairList<Object,Object> c = new PairList<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object f = SerializationManager.readObject(stream);
				Object l = SerializationManager.readObject(stream);
				c.add(f, l);
			}
			return c;
		} else if (type == TYPE_DIFFUSION_DITHER_ALGORITHM) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version >= 2) {
				String name = stream.readUTF();
				int rows = stream.readInt();
				int columns = stream.readInt();
				int denominator = stream.readInt();
				DiffusionDitherAlgorithm a = new DiffusionDitherAlgorithm(rows, columns, denominator, name);
				for (int y = 0; y < rows; y++) {
					for (int x = 0; x < columns; x++) {
						a.values[y][x] = stream.readInt();
					}
				}
				return a;
			} else {
				int rows = stream.readUnsignedByte();
				int columns = 256;
				int[][] values = new int[rows][];
				for (int y = 0; y < rows; y++) {
					int cols = stream.readUnsignedByte();
					if (columns > cols) columns = cols;
					values[y] = new int[cols];
					for (int x = 0; x < cols; x++) {
						values[y][x] = stream.readUnsignedByte();
					}
				}
				int denominator = stream.readUnsignedByte();
				DiffusionDitherAlgorithm a = new DiffusionDitherAlgorithm(rows, columns, denominator, null);
				for (int y = 0; y < rows; y++) {
					for (int x = 0; x < columns; x++) {
						a.values[y][x] = values[y][x];
					}
				}
				return a;
			}
		} else if (type == TYPE_ORDERED_DITHER_ALGORITHM) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version >= 2) {
				String name = stream.readUTF();
				int rows = stream.readInt();
				int columns = stream.readInt();
				int denominator = stream.readInt();
				OrderedDitherAlgorithm a = new OrderedDitherAlgorithm(rows, columns, denominator, name);
				for (int y = 0; y < rows; y++) {
					for (int x = 0; x < columns; x++) {
						a.values[y][x] = stream.readInt();
					}
				}
				return a;
			} else {
				int rows = stream.readUnsignedByte();
				int columns = 256;
				int[][] values = new int[rows][];
				for (int y = 0; y < rows; y++) {
					int cols = stream.readUnsignedByte();
					if (columns > cols) columns = cols;
					values[y] = new int[cols];
					for (int x = 0; x < cols; x++) {
						values[y][x] = stream.readUnsignedByte();
					}
				}
				int denominator = stream.readUnsignedByte();
				OrderedDitherAlgorithm a = new OrderedDitherAlgorithm(rows, columns, denominator, null);
				for (int y = 0; y < rows; y++) {
					for (int x = 0; x < columns; x++) {
						a.values[y][x] = values[y][x];
					}
				}
				return a;
			}
		} else if (type == TYPE_RANDOM_DITHER_ALGORITHM) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version >= 2) {
				String name = stream.readUTF();
				return new RandomDitherAlgorithm(name);
			} else {
				return new RandomDitherAlgorithm(null);
			}
		} else {
			return null;
		}
	}
}
