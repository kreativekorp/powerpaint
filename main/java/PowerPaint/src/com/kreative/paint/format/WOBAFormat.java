/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.format;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class WOBAFormat implements Format {
	public String getName() { return "WOBA"; }
	public String getExpandedName() { return "Wrath of Bill Atkinson"; }
	public String getExtension() { return "woba"; }
	public int getMacFileType() { return 0x574F4241; }
	public int getMacResourceType() { return 0x574F4241; }
	public long getDFFType() { return 0x496D6720574F4241L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.BLACK_AND_WHITE; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE_AND_TRANSPARENT; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 24; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			if (in.readInt() < 64) return false;
			if (in.readInt() != 0x424D4150) return false;
			in.readInt();
			if (in.readInt() != 0) return false;
			if (in.readShort() != 0) return false;
			if (in.readShort() != 0) return false;
			if (in.readShort() != 1) return false;
			if (in.readShort() != 0) return false;
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("woba"); }
	public boolean acceptsMacFileType(int type) { return type == 0x574F4241; }
	public boolean acceptsMacResourceType(int type) { return type == 0x574F4241; }
	public boolean acceptsDFFType(long type) { return type == 0x496D6720574F4241L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		if (in.readInt() < 64) throw new NotThisFormatException();
		if (in.readInt() != 0x424D4150) throw new NotThisFormatException();
		in.readInt();
		if (in.readInt() != 0) throw new NotThisFormatException();
		if (in.readShort() != 0) throw new NotThisFormatException();
		if (in.readShort() != 0) throw new NotThisFormatException();
		if (in.readShort() != 1) throw new NotThisFormatException();
		if (in.readShort() != 0) throw new NotThisFormatException();
		short bt = in.readShort();
		short bl = in.readShort();
		short bb = in.readShort();
		short br = in.readShort();
		Rectangle boundRect = new Rectangle(bl, bt, br-bl, bb-bt);
		short mt = in.readShort();
		short ml = in.readShort();
		short mb = in.readShort();
		short mr = in.readShort();
		Rectangle maskRect = new Rectangle(ml, mt, mr-ml, mb-mt);
		short it = in.readShort();
		short il = in.readShort();
		short ib = in.readShort();
		short ir = in.readShort();
		Rectangle imageRect = new Rectangle(il, it, ir-il, ib-it);
		in.readInt();
		in.readInt();
		int msize = in.readInt();
		int isize = in.readInt();
		byte[] mdata = new byte[msize];
		in.readFully(mdata);
		byte[] idata = new byte[isize];
		in.readFully(idata);
		byte[] mbytes = decodeWOBA(boundRect, maskRect, mdata, 0, msize);
		byte[] ibytes = decodeWOBA(boundRect, imageRect, idata, 0, isize);
		int[] pixels = new int[ibytes.length*8];
		for (int ii=0, mi=0, pi=0; ii<ibytes.length && mi<mbytes.length && pi<pixels.length; ii++, mi++, pi+=8) {
			byte ibt = ibytes[ii];
			byte mbt = mbytes[mi];
			pixels[pi+0] = ((ibt & 0x80)>0)?0xFF000000:((mbt & 0x80)>0)?0xFFFFFFFF:0;
			pixels[pi+1] = ((ibt & 0x40)>0)?0xFF000000:((mbt & 0x40)>0)?0xFFFFFFFF:0;
			pixels[pi+2] = ((ibt & 0x20)>0)?0xFF000000:((mbt & 0x20)>0)?0xFFFFFFFF:0;
			pixels[pi+3] = ((ibt & 0x10)>0)?0xFF000000:((mbt & 0x10)>0)?0xFFFFFFFF:0;
			pixels[pi+4] = ((ibt & 0x08)>0)?0xFF000000:((mbt & 0x08)>0)?0xFFFFFFFF:0;
			pixels[pi+5] = ((ibt & 0x04)>0)?0xFF000000:((mbt & 0x04)>0)?0xFFFFFFFF:0;
			pixels[pi+6] = ((ibt & 0x02)>0)?0xFF000000:((mbt & 0x02)>0)?0xFFFFFFFF:0;
			pixels[pi+7] = ((ibt & 0x01)>0)?0xFF000000:((mbt & 0x01)>0)?0xFFFFFFFF:0;
		}
		Canvas c = new Canvas(boundRect.width, boundRect.height);
		c.get(0).setRGB(0, 0, boundRect.width, boundRect.height, pixels, 0, snap32(boundRect).width);
		return c;
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight()/8;
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		int w = c.getWidth();
		int h = c.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		c.paint(g);
		g.dispose();
		Rectangle boundRect = new Rectangle(0, 0, w, h);
		int sw = snap32(boundRect).width;
		int[] pixels = new int[sw*h];
		bi.getRGB(0, 0, w, h, pixels, 0, sw);
		byte[] mbytes = new byte[pixels.length >>> 3];
		byte[] ibytes = new byte[pixels.length >>> 3];
		for (int ii=0, mi=0, pi=0; ii<ibytes.length && mi<mbytes.length && pi<pixels.length; ii++, mi++, pi+=8) {
			mbytes[mi] = 0;
			ibytes[ii] = 0;
			for (int p=0, pp=pi; p<8 && pp<pixels.length; p++, pp++) {
				mbytes[mi] <<= 1;
				ibytes[ii] <<= 1;
				if (isOpaque(pixels[pp])) {
					mbytes[mi] |= 1;
					if (isBlack(pixels[pp])) {
						ibytes[ii] |= 1;
					}
				}
			}
		}
		byte[] mdata = encodeWOBA(boundRect, mbytes);
		byte[] idata = encodeWOBA(boundRect, ibytes);
		out.writeInt(64+mdata.length+idata.length);		
		out.writeInt(0x424D4150);
		out.writeInt(0x0000126B);
		out.writeInt(0);
		out.writeShort(0);
		out.writeShort(0);
		out.writeShort(1);
		out.writeShort(0);
		out.writeShort(boundRect.y);
		out.writeShort(boundRect.x);
		out.writeShort(boundRect.y+boundRect.height);
		out.writeShort(boundRect.x+boundRect.width);
		out.writeShort(boundRect.y);
		out.writeShort(boundRect.x);
		out.writeShort(boundRect.y+boundRect.height);
		out.writeShort(boundRect.x+boundRect.width);
		out.writeShort(boundRect.y);
		out.writeShort(boundRect.x);
		out.writeShort(boundRect.y+boundRect.height);
		out.writeShort(boundRect.x+boundRect.width);
		out.writeInt(0);
		out.writeInt(0);
		out.writeInt(mdata.length);
		out.writeInt(idata.length);
		out.write(mdata);
		out.write(idata);
	}
	
	private static Rectangle snap32(Rectangle r) {
		int left = r.x & ~0x1F;
		int right = r.x+r.width;
		if ((right & 0x1F) != 0) {
			right |= 0x1F;
			right++;
		}
		return new Rectangle(left, r.y, right-left, r.height);
	}
	
	private static byte[] decodeWOBA(Rectangle totr, Rectangle r, byte[] data, int offset, int l) {
		Rectangle tr = snap32(totr);
		int trw = tr.width >> 3;
		Rectangle rf = snap32(r);
		int rw = rf.width >> 3;
		byte[] stuff = new byte[trw*tr.height];
		try {
			if (l == 0) {
				if (r.width > 0 && r.height > 0) {
					int sbyte = r.x >> 3;
					int sbit = r.x & 0x7;
					int ebyte = (r.x+r.width) >> 3;
					int ebit = (r.x+r.width) & 0x7;
					int base = trw*r.y;
					for (int y=r.y; y<r.y+r.height; y++) {
						stuff[base+sbyte] = (byte)(0xFF >> sbit);
						for (int x=sbyte+1; x<ebyte; x++) {
							stuff[base+x] = (byte)0xFF;
						}
						if (ebit>0) stuff[base+ebyte] = (byte)(0xFF << (8-ebit));
						base += trw;
					}
				}
			} else {
				int p = offset;
				int y = rf.y-tr.y;
				int base = trw*y + ((rf.x-tr.x) >> 3);
				int pp = base;
				int repeat = 1;
				int dh = 0, dv = 0;
				byte[] patt = new byte[]{
						(byte)0xAA, (byte)0x55, (byte)0xAA, (byte)0x55,
						(byte)0xAA, (byte)0x55, (byte)0xAA, (byte)0x55
				};
				while (y<rf.y-tr.y+rf.height && p<data.length) {
					byte opcode = data[p++];
					if ((opcode & 0x80) == 0) {
						int d = (opcode & 0x70) >> 4;
						int z = opcode & 0x0F;
						byte[] dat = new byte[d];
						for (int i=0; i<d; i++) dat[i] = data[p++];
						while ((repeat--) > 0) {
							pp += z;
							for (int i=0; i<d; i++) stuff[pp++] = dat[i];
						}
					} else if ((opcode & 0xE0) == 0xA0) {
						repeat = (opcode & 0x1F);
						continue;
					} else if ((opcode & 0xE0) == 0xC0) {
						int d = (opcode & 0x1F) << 3;
						byte[] dat = new byte[d];
						for (int i=0; i<d; i++) dat[i] = data[p++];
						while ((repeat--) > 0) {
							for (int i=0; i<d; i++) stuff[pp++] = dat[i];
						}
					} else if ((opcode & 0xE0) == 0xE0) {
						pp += ((opcode & 0x1F) << 4)*repeat;
					} else {
						switch (opcode) {
						case (byte)0x80: {
							byte[] dat = new byte[rw];
							for (int i=0; i<rw; i++) dat[i] = data[p++];
							while ((repeat--) > 0) {
								for (int i=0; i<rw; i++) stuff[pp++] = dat[i];
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x81: {
							y += repeat;
							base += trw*repeat;
							pp = base;
							repeat = 1;
						}
						break;
						case (byte)0x82: {
							while ((repeat--) > 0) {
								for (int i=0; i<rw; i++) stuff[pp++] = -1;
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x83: {
							byte pb = data[p++];
							while ((repeat--) > 0) {
								patt[y & 0x7] = pb;
								for (int i=0; i<rw; i++) stuff[pp++] = pb;
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x84: {
							while ((repeat--) > 0) {
								byte pb = patt[y & 0x7];
								for (int i=0; i<rw; i++) stuff[pp++] = pb;
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x85: {
							while ((repeat--) > 0) {
								for (int i=0; i<rw; i++) {
									stuff[pp] = stuff[pp-trw];
									pp++;
								}
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x86: {
							while ((repeat--) > 0) {
								for (int i=0; i<rw; i++) {
									stuff[pp] = stuff[pp-(trw*2)];
									pp++;
								}
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x87: {
							while ((repeat--) > 0) {
								for (int i=0; i<rw; i++) {
									stuff[pp] = stuff[pp-(trw*3)];
									pp++;
								}
								y++;
								base += trw;
								pp = base;
							}
							repeat = 1;
						}
						break;
						case (byte)0x88: dh = 16; dv = 0; break;
						case (byte)0x89: dh = 0; dv = 0; break;
						case (byte)0x8A: dh = 0; dv = 1; break;
						case (byte)0x8B: dh = 0; dv = 2; break;
						case (byte)0x8C: dh = 1; dv = 0; break;
						case (byte)0x8D: dh = 1; dv = 1; break;
						case (byte)0x8E: dh = 2; dv = 2; break;
						case (byte)0x8F: dh = 8; dv = 0; break;
						}
						continue;
					}

					repeat = 1;
					if (pp >= base+rw) {
						if (dh != 0) {
							byte[] row = new byte[rw];
							for (int i=0; i<rw; i++) row[i] = stuff[base+i];
							int numshifts = (rw << 3)/dh;
							while ((numshifts--) > 0) {
								int acc = 0;
								for (int i=0; i<rw; i+=4) {
									int tmp = ((row[i] & 0xFF) << 24) | ((row[i+1] & 0xFF) << 16) | ((row[i+2] & 0xFF) << 8) | (row[i+3] & 0xFF);
									int rowi = acc | (tmp >>> dh);
									row[i] = (byte)((rowi >>> 24) & 0xFF);
									row[i+1] = (byte)((rowi >>> 16) & 0xFF);
									row[i+2] = (byte)((rowi >>> 8) & 0xFF);
									row[i+3] = (byte)(rowi & 0xFF);
									acc = tmp << (32-dh);
								}
								for (int i=0; i<rw; i++) stuff[base+i] ^= row[i];
							}
						}
						if (dv != 0 && y-dv >= 0) {
							for (int i=0; i<rw; i++) stuff[base+i] = (byte)(stuff[base+i] ^ stuff[(base-(trw*dv))+i]);
						}
						y++;
						base += trw;
						pp = base;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			//Malformed data did this to us.
			//HyperCard would just crashy crashy with malformed WOBA data;
			//we'll be better than that.
		}
		return stuff;
	}

	private static boolean isBlack(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		return (k < 0x80);
	}
	
	private static boolean isOpaque(int pixel) {
		int a = ((pixel >>> 24) & 0xFF);
		return (a >= 0x80);
	}
	
	private static byte[] encodeWOBA(Rectangle totr, byte[] stuff) throws IOException {
		/*
		 * NOTE - For encoding, we do not currently implement all the features
		 * necessary to get the smallest possible compression. We just implement
		 * a smaller subset of possible operations.
		 */
		int bpr = snap32(totr).width >> 3;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] patt = new byte[]{
				(byte)0xAA, (byte)0x55, (byte)0xAA, (byte)0x55,
				(byte)0xAA, (byte)0x55, (byte)0xAA, (byte)0x55
		};
		byte[] prevprevrow = null;
		byte[] prevrow = null;
		for (int y = 0, sy = 0; y < totr.height && sy < stuff.length; y++, sy += bpr) {
			byte[] row = new byte[bpr];
			byte npatt = stuff[sy];
			boolean isBlack = true;
			boolean isNewPattern = true;
			boolean isOldPattern = true;
			boolean isWhite = true;
			boolean isPrevRow = true;
			boolean isPrevPrevRow = true;
			for (int x = 0, sx = sy; x < bpr && sx < stuff.length; x++, sx++) {
				row[x] = stuff[sx];
				if (row[x] != (byte)0xFF) isBlack = false;
				if (row[x] != npatt) isNewPattern = false;
				if (row[x] != patt[y & 7]) isOldPattern = false;
				if (row[x] != (byte)0x00) isWhite = false;
				if (prevrow == null || row[x] != prevrow[x]) isPrevRow = false;
				if (prevprevrow == null || row[x] != prevprevrow[x]) isPrevPrevRow = false;
			}
			if (isWhite) out.write(0x81);
			else if (isBlack) out.write(0x82);
			else if (isPrevRow) out.write(0x85);
			else if (isPrevPrevRow) out.write(0x86);
			else if (isOldPattern) out.write(0x84);
			else if (isNewPattern) {
				out.write(0x83);
				out.write(npatt);
				patt[y & 7] = npatt;
			}
			else {
				out.write(0x80);
				out.write(row);
			}
			prevprevrow = prevrow;
			prevrow = row;
		}
		return out.toByteArray();
	}
}
