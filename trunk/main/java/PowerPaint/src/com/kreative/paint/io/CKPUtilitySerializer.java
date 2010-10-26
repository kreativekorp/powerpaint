/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

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
import com.kreative.paint.util.*;

public class CKPUtilitySerializer extends Serializer {
	private static final int TYPE_BITMAP = fcc("Bmap");
	private static final int TYPE_DITHER_ALGORITHM = fcc("Dith");
	private static final int TYPE_FRAME = fcc("Fram");
	private static final int TYPE_FRAMEINFO = fcc("FrIn");
	private static final int TYPE_PAIR = fcc("Pair");
	private static final int TYPE_PAIRLIST = fcc("PLst");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BITMAP, 1, Bitmap.class);
		addTypeAndClass(TYPE_DITHER_ALGORITHM, 1, DitherAlgorithm.class);
		addTypeAndClass(TYPE_FRAME, 1, Frame.class);
		addTypeAndClass(TYPE_FRAMEINFO, 1, FrameInfo.class);
		addTypeAndClass(TYPE_PAIR, 1, Pair.class);
		addTypeAndClass(TYPE_PAIRLIST, 1, PairList.class);
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
		}
		else if (o instanceof DitherAlgorithm) {
			DitherAlgorithm v = (DitherAlgorithm)o;
			int[][] m = v.getMatrix();
			stream.writeByte(m.length);
			for (int[] r : m) {
				stream.writeByte(r.length);
				for (int c : r) {
					stream.writeByte(c);
				}
			}
			stream.writeByte(v.getDenom());
		}
		else if (o instanceof Frame) {
			Frame v = (Frame)o;
			SerializationManager.writeObject(v.getRawImage(), stream);
			SerializationManager.writeObject(v.getRawFrameInfo(), stream);
		}
		else if (o instanceof FrameInfo) {
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
		}
		else if (o instanceof Pair) {
			Pair<?,?> v = (Pair<?,?>)o;
			SerializationManager.writeObject(v.getFormer(), stream);
			SerializationManager.writeObject(v.getLatter(), stream);
		}
		else if (o instanceof PairList) {
			PairList<?,?> v = (PairList<?,?>)o;
			stream.writeInt(v.size());
			for (Pair<?,?> p : v) {
				SerializationManager.writeObject(p.getFormer(), stream);
				SerializationManager.writeObject(p.getLatter(), stream);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (!(version == 1)) throw new IOException("Invalid version number.");
		else if (type == TYPE_BITMAP) {
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
		}
		else if (type == TYPE_DITHER_ALGORITHM) {
			int[][] m = new int[stream.readUnsignedByte()][];
			for (int i = 0; i < m.length; i++) {
				m[i] = new int[stream.readUnsignedByte()];
				for (int j = 0; j < m[i].length; j++) {
					m[i][j] = stream.readUnsignedByte();
				}
			}
			int d = stream.readUnsignedByte();
			return new DitherAlgorithm(m, d);
		}
		else if (type == TYPE_FRAME) {
			Image i = (Image)SerializationManager.readObject(stream);
			FrameInfo f = (FrameInfo)SerializationManager.readObject(stream);
			return new Frame(i, f);
		}
		else if (type == TYPE_FRAMEINFO) {
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
		}
		else if (type == TYPE_PAIR) {
			Object f = SerializationManager.readObject(stream);
			Object l = SerializationManager.readObject(stream);
			return new Pair<Object,Object>(f,l);
		}
		else if (type == TYPE_PAIRLIST) {
			PairList<Object,Object> c = new PairList<Object,Object>();
			int size = stream.readInt();
			for (int i = 0; i < size; i++) {
				Object f = SerializationManager.readObject(stream);
				Object l = SerializationManager.readObject(stream);
				c.add(f, l);
			}
			return c;
		}
		else return null;
	}
}
