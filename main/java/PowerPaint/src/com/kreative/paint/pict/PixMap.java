/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.pict;

import java.io.*;

// NOTE: Both BitMap and PixMap are represented by this structure.
public class PixMap {
	public static final int PACK_TYPE_PACKBITS = 0;
	public static final int PACK_TYPE_UNPACKED = 1;
	public static final int PACK_TYPE_UNPACKED_NO_PADDING = 2;
	public static final int PACK_TYPE_BY_PIXEL = 3;
	public static final int PACK_TYPE_BY_COMPONENT = 4;
	
	public static final int PIXEL_TYPE_INDEXED = 0;
	public static final int PIXEL_TYPE_RGBDIRECT = 16;
	
	public static final int PIXEL_SIZE_1BIT = 1;
	public static final int PIXEL_SIZE_2BIT = 2;
	public static final int PIXEL_SIZE_4BIT = 4;
	public static final int PIXEL_SIZE_8BIT = 8;
	public static final int PIXEL_SIZE_16BIT = 16;
	public static final int PIXEL_SIZE_32BIT = 32;
	
	public static final int COMPONENT_COUNT_INDEXED = 1;
	public static final int COMPONENT_COUNT_RGB = 3;
	public static final int COMPONENT_COUNT_ARGB = 4;
	
	public static final int COMPONENT_SIZE_1BIT = 1;
	public static final int COMPONENT_SIZE_2BIT = 2;
	public static final int COMPONENT_SIZE_4BIT = 4;
	public static final int COMPONENT_SIZE_8BIT = 8;
	public static final int COMPONENT_SIZE_16BIT = 5;
	public static final int COMPONENT_SIZE_32BIT = 8;
	
	public int baseAddr; // pixel image
	public int rowBytes; // flags and row width
	public Rect bounds; // boundary rectangle
	public int pmVersion; // PixMap record version number
	public int packType; // packing format
	public int packSize; // size of data in packed state
	public float hRes; // horizontal resolution
	public float vRes; // vertical resolution
	public int pixelType; // format of pixel image
	public int pixelSize; // physical bits per pixel
	public int cmpCount; // logical components per pixel
	public int cmpSize; // logical bits per component
	public int planeBytes; // offset to next plane
	public int pmTable; // handle to the ColorTable record for this image
	public int pmReserved; // reserved for future expansion
	
	public static PixMap read(DataInputStream in, boolean withBaseAddr) throws IOException {
		PixMap p = new PixMap();
		p.baseAddr = withBaseAddr ? in.readInt() : 0;
		p.rowBytes = in.readUnsignedShort();
		p.bounds = Rect.read(in);
		if ((p.rowBytes & 0x8000) != 0) {
			// pixmap
			p.pmVersion = in.readShort();
			p.packType = in.readShort();
			p.packSize = in.readInt();
			p.hRes = in.readInt() / 65536.0f;
			p.vRes = in.readInt() / 65536.0f;
			p.pixelType = in.readShort();
			p.pixelSize = in.readShort();
			p.cmpCount = in.readShort();
			p.cmpSize = in.readShort();
			p.planeBytes = in.readInt();
			p.pmTable = in.readInt();
			p.pmReserved = in.readInt();
		} else {
			// bitmap
			p.pmVersion = 0;
			p.packType = PACK_TYPE_PACKBITS;
			p.packSize = 0;
			p.hRes = 72;
			p.vRes = 72;
			p.pixelType = PIXEL_TYPE_INDEXED;
			p.pixelSize = PIXEL_SIZE_1BIT;
			p.cmpCount = COMPONENT_COUNT_INDEXED;
			p.cmpSize = COMPONENT_SIZE_1BIT;
			p.planeBytes = 0;
			p.pmTable = 0;
			p.pmReserved = 0;
		}
		return p;
	}
	
	public PixMap() {
		this.baseAddr = 0;
		this.rowBytes = 0;
		this.bounds = new Rect();
		this.pmVersion = 0;
		this.packType = PACK_TYPE_PACKBITS;
		this.packSize = 0;
		this.hRes = 72;
		this.vRes = 72;
		this.pixelType = PIXEL_TYPE_INDEXED;
		this.pixelSize = PIXEL_SIZE_1BIT;
		this.cmpCount = COMPONENT_COUNT_INDEXED;
		this.cmpSize = COMPONENT_SIZE_1BIT;
		this.planeBytes = 0;
		this.pmTable = 0;
		this.pmReserved = 0;
	}
	
	public void write(DataOutputStream out, boolean withBaseAddr) throws IOException {
		if ((rowBytes & 0x8000) == 0) {
			// Is it really a bitmap?
			if (pmVersion != 0 || packType != PACK_TYPE_PACKBITS || packSize != 0 || hRes != 72 || vRes != 72 ||
					pixelType != PIXEL_TYPE_INDEXED || pixelSize != PIXEL_SIZE_1BIT ||
					cmpCount != COMPONENT_COUNT_INDEXED || cmpSize != COMPONENT_SIZE_1BIT ||
					planeBytes != 0 || pmTable != 0 || pmReserved != 0) {
				// No, so mark it as a pixmap.
				rowBytes |= 0x8000;
			}
		}
		if (withBaseAddr) out.writeInt(baseAddr);
		out.writeShort(rowBytes);
		bounds.write(out);
		if ((rowBytes & 0x8000) != 0) {
			out.writeShort(pmVersion);
			out.writeShort(packType);
			out.writeInt(packSize);
			out.writeInt((int)(hRes*65536.0f));
			out.writeInt((int)(vRes*65536.0f));
			out.writeShort(pixelType);
			out.writeShort(pixelSize);
			out.writeShort(cmpCount);
			out.writeShort(cmpSize);
			out.writeInt(planeBytes);
			out.writeInt(pmTable);
			out.writeInt(pmReserved);
		}
	}
	
	public String toString() {
		return (((rowBytes & 0x8000) == 0) ? "Bitmap" : "Pixmap")+"["+bounds.toString()+"]";
	}
	
	public boolean hasColorTable() {
		return ((pixelType == PIXEL_TYPE_INDEXED) && ((rowBytes & 0x8000) != 0));
	}
	
	public byte[] readPixData(DataInputStream in, boolean packed) throws IOException {
		if (((packType == PixMap.PACK_TYPE_PACKBITS) && !packed) || (packType == PACK_TYPE_UNPACKED) || ((rowBytes & 0x7FFF) < 8)) {
			byte[] data = new byte[(rowBytes & 0x7FFF) * (bounds.bottom-bounds.top)];
			in.readFully(data);
			return data;
		} else if (packType == PACK_TYPE_UNPACKED_NO_PADDING) {
			byte[] data = new byte[(rowBytes & 0x7FFF) * (bounds.bottom-bounds.top) * 3 / 4];
			in.readFully(data);
			return data;
		} else if ((rowBytes & 0x7FFF) > 250) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			for (int y = bounds.top; y < bounds.bottom; y++) {
				byte[] scanline = new byte[in.readUnsignedShort()];
				in.readFully(scanline);
				dout.writeShort(scanline.length);
				dout.write(scanline);
			}
			dout.close();
			bout.close();
			return bout.toByteArray();
		} else {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			for (int y = bounds.top; y < bounds.bottom; y++) {
				byte[] scanline = new byte[in.readUnsignedByte()];
				in.readFully(scanline);
				dout.writeByte(scanline.length);
				dout.write(scanline);
			}
			dout.close();
			bout.close();
			return bout.toByteArray();
		}
	}
	
	public void writePixData(DataOutputStream out, byte[] data) throws IOException {
		out.write(data);
	}
}
