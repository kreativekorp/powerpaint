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

import java.awt.image.*;
import java.io.*;
import java.util.*;

public class PICTUtilities {
	private PICTUtilities() {}
	
	public static byte[] makeByteArray(Object... objs) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		for (Object o : objs) {
			if (o instanceof Byte) dos.writeByte((Byte)o);
			else if (o instanceof Short) dos.writeShort((Short)o);
			else if (o instanceof Integer) dos.writeInt((Integer)o);
			else if (o instanceof Long) dos.writeLong((Long)o);
			else if (o instanceof Float) dos.writeFloat((Float)o);
			else if (o instanceof Double) dos.writeDouble((Double)o);
			else if (o instanceof Boolean) dos.writeBoolean((Boolean)o);
			else if (o instanceof Character) dos.writeChar((Character)o);
			else if (o instanceof String) {
				dos.writeInt(((String)o).length());
				dos.writeChars((String)o);
			}
			else if (o instanceof StringBuffer) {
				dos.writeInt((o.toString()).length());
				dos.writeChars(o.toString());
			}
			else if (o instanceof byte[]) {
				dos.writeInt(((byte[])o).length);
				dos.write((byte[])o);
			}
			else if (o instanceof ColorSpec) ((ColorSpec)o).write(dos);
			else if (o instanceof ColorTable) ((ColorTable)o).write(dos);
			else if (o instanceof PixMap) ((PixMap)o).write(dos, true);
			else if (o instanceof Point) ((Point)o).write(dos);
			else if (o instanceof Polygon) ((Polygon)o).write(dos);
			else if (o instanceof Rect) ((Rect)o).write(dos);
			else if (o instanceof Region) ((Region)o).write(dos);
			else if (o instanceof RGBColor) ((RGBColor)o).write(dos);
		}
		dos.close();
		bos.close();
		return bos.toByteArray();
	}
	
	public static Object[] unmakeByteArray(byte[] data, Class<?>... classes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		List<Object> objs = new Vector<Object>();
		for (Class<?> c : classes) {
			if (c == byte.class || c == Byte.class) objs.add(dis.readByte());
			else if (c == short.class || c == Short.class) objs.add(dis.readShort());
			else if (c == int.class || c == Integer.class) objs.add(dis.readInt());
			else if (c == long.class || c == Long.class) objs.add(dis.readLong());
			else if (c == float.class || c == Float.class) objs.add(dis.readFloat());
			else if (c == double.class || c == Double.class) objs.add(dis.readDouble());
			else if (c == boolean.class || c == Boolean.class) objs.add(dis.readBoolean());
			else if (c == char.class || c == Character.class) objs.add(dis.readChar());
			else if (c == String.class) {
				int len = dis.readInt();
				StringBuffer sb = new StringBuffer(len);
				while (len-->0) sb.append(dis.readChar());
				objs.add(sb.toString());
			}
			else if (c == StringBuffer.class) {
				int len = dis.readInt();
				StringBuffer sb = new StringBuffer(len);
				while (len-->0) sb.append(dis.readChar());
				objs.add(sb);
			}
			else if (c == byte[].class) {
				byte[] d = new byte[dis.readInt()];
				dis.readFully(d);
				objs.add(d);
			}
			else if (c == ColorSpec.class) objs.add(ColorSpec.read(dis));
			else if (c == ColorTable.class) objs.add(ColorTable.read(dis));
			else if (c == PixMap.class) objs.add(PixMap.read(dis, true));
			else if (c == Point.class) objs.add(Point.read(dis));
			else if (c == Polygon.class) objs.add(Polygon.read(dis));
			else if (c == Rect.class) objs.add(Rect.read(dis));
			else if (c == Region.class) objs.add(Region.read(dis));
			else if (c == RGBColor.class) objs.add(RGBColor.read(dis));
		}
		dis.close();
		bis.close();
		return objs.toArray(new Object[0]);
	}
	
	public static byte[] packBits(byte[] data) {
		byte[] runs = new byte[data.length*2];
		int runsPtr = 0;
		for (byte b : data) {
			if (runsPtr >= 2 && runs[runsPtr-1] == b && runs[runsPtr-2] < 127) {
				runs[runsPtr-2]++;
			} else {
				runs[runsPtr++] = 1;
				runs[runsPtr++] = b;
			}
		}
		byte[] comp = new byte[data.length*2];
		int compPtr = 0;
		int compCntPtr = 0;
		for (int i = 0; i < runsPtr; i += 2) {
			int cnt = runs[i];
			byte b = runs[i+1];
			if (cnt > 1) {
				comp[compPtr++] = (byte)(-cnt+1);
				comp[compPtr++] = b;
				compCntPtr = compPtr;
			} else {
				if (compCntPtr == compPtr || comp[compCntPtr] >= 127) {
					comp[compPtr++] = 0;
					comp[compPtr++] = b;
				} else {
					comp[compCntPtr]++;
					comp[compPtr++] = b;
				}
			}
		}
		byte[] fin = new byte[compPtr];
		for (int i = 0; i < compPtr; i++) fin[i] = comp[i];
		return fin;
	}
	
	public static byte[] unpackBits(byte[] data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int dataPtr = 0;
		while (dataPtr < data.length) {
			int v = data[dataPtr++];
			if (v < 0) {
				int count = -v+1;
				byte d = data[dataPtr++];
				while (count-->0) bos.write(d);
			} else {
				int count = v+1;
				while (count-->0) bos.write(data[dataPtr++]);
			}
		}
		return bos.toByteArray();
	}
	
	public static PICTInstruction makeBitsRect(int x, int y, BufferedImage img, int matte, float hRes, float vRes) {
		int w = img.getWidth();
		int h = img.getHeight();
		int[] pixels = new int[w*h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		Set<Integer> colors = new HashSet<Integer>();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] >= 0) {
				pixels[i] = matte | 0xFF000000;
			} else {
				pixels[i] |= 0xFF000000;
			}
			colors.add(pixels[i] | 0xFF000000);
		}
		if (colors.size() <= 256) {
			// indexed
			PixMap pm = new PixMap();
			pm.baseAddr = 0;
			pm.rowBytes = w | 0x8000; while ((pm.rowBytes & 3) != 0) pm.rowBytes++;
			pm.bounds = new Rect(x, y, w, h);
			pm.pmVersion = 0;
			pm.packType = PixMap.PACK_TYPE_PACKBITS;
			pm.packSize = 0;
			pm.hRes = hRes;
			pm.vRes = vRes;
			pm.pixelType = PixMap.PIXEL_TYPE_INDEXED;
			pm.pixelSize = PixMap.PIXEL_SIZE_8BIT;
			pm.cmpCount = PixMap.COMPONENT_COUNT_INDEXED;
			pm.cmpSize = PixMap.COMPONENT_SIZE_8BIT;
			pm.planeBytes = 0;
			pm.pmTable = 0;
			pm.pmReserved = 0;
			ColorTable ct = new ColorTable();
			ct.ctSeed = 0;
			ct.ctFlags = 0;
			ct.ctSize = colors.size()-1;
			Integer[] ca = colors.toArray(new Integer[0]);
			Arrays.sort(ca, new Comparator<Integer>() {
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			});
			for (int c : ca) {
				ct.ctTable.add(new ColorSpec(c));
			}
			BufferedImage pdi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, ct.toIndexColorModel(8));
			pdi.setRGB(0, 0, w, h, pixels, 0, w);
			byte[] pddata = ((DataBufferByte)pdi.getData().getDataBuffer()).getData();
			ByteArrayOutputStream pdb = new ByteArrayOutputStream();
			DataOutputStream pdd = new DataOutputStream(pdb);
			try {
				byte[] pdscanline = new byte[(pm.rowBytes & 0x7FFF)];
				if ((pm.rowBytes & 0x7FFF) < 8) {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = pddata[pddx];
						}
						pdd.write(pdscanline);
					}
				} else if ((pm.rowBytes & 0x7FFF) > 250) {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = pddata[pddx];
						}
						byte[] csl = packBits(pdscanline);
						pdd.writeShort(csl.length);
						pdd.write(csl);
					}
				} else {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = pddata[pddx];
						}
						byte[] csl = packBits(pdscanline);
						pdd.writeByte(csl.length);
						pdd.write(csl);
					}
				}
				pdd.close();
				pdb.close();
			} catch (IOException ex) {}
			byte[] pd = pdb.toByteArray();
			return new PICTInstruction.PackBitsRect(pm, ct, pm.bounds, pm.bounds, PICTInstruction.ModeConstants.SRC_COPY, pd);
		} else {
			// rgb
			PixMap pm = new PixMap();
			pm.baseAddr = 0;
			pm.rowBytes = (w*4) | 0x8000;
			pm.bounds = new Rect(x, y, w, h);
			pm.pmVersion = 0;
			pm.packType = PixMap.PACK_TYPE_BY_COMPONENT;
			pm.packSize = 0;
			pm.hRes = hRes;
			pm.vRes = vRes;
			pm.pixelType = PixMap.PIXEL_TYPE_RGBDIRECT;
			pm.pixelSize = PixMap.PIXEL_SIZE_32BIT;
			pm.cmpCount = PixMap.COMPONENT_COUNT_RGB;
			pm.cmpSize = PixMap.COMPONENT_SIZE_32BIT;
			pm.planeBytes = 0;
			pm.pmTable = 0;
			pm.pmReserved = 0;
			ByteArrayOutputStream pdb = new ByteArrayOutputStream();
			DataOutputStream pdd = new DataOutputStream(pdb);
			try {
				byte[] pdscanline = new byte[w*3];
				if ((pm.rowBytes & 0x7FFF) < 8) {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
						}
						pdd.write(pdscanline);
					}
				} else if ((pm.rowBytes & 0x7FFF) > 250) {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
						}
						byte[] csl = packBits(pdscanline);
						pdd.writeShort(csl.length);
						pdd.write(csl);
					}
				} else {
					for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
						}
						for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
							pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
						}
						byte[] csl = packBits(pdscanline);
						pdd.writeByte(csl.length);
						pdd.write(csl);
					}
				}
				pdd.close();
				pdb.close();
			} catch (IOException ex) {}
			byte[] pd = pdb.toByteArray();
			pm.packSize = pd.length;
			return new PICTInstruction.DirectBitsRect(pm, pm.bounds, pm.bounds, PICTInstruction.ModeConstants.SRC_COPY, pd);
		}
	}
	
	public static PICTInstruction makeBitsRgn(int x, int y, BufferedImage img, int matte, float hRes, float vRes) {
		int w = img.getWidth();
		int h = img.getHeight();
		int[] pixels = new int[w*h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		Region rgn = Region.fromAlpha(x, y, w, h, pixels, 0, w);
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] >= 0) {
				pixels[i] = matte | 0xFF000000;
			} else {
				pixels[i] |= 0xFF000000;
			}
		}
		PixMap pm = new PixMap();
		pm.baseAddr = 0;
		pm.rowBytes = (w*4) | 0x8000;
		pm.bounds = new Rect(x, y, w, h);
		pm.pmVersion = 0;
		pm.packType = PixMap.PACK_TYPE_BY_COMPONENT;
		pm.packSize = 0;
		pm.hRes = hRes;
		pm.vRes = vRes;
		pm.pixelType = PixMap.PIXEL_TYPE_RGBDIRECT;
		pm.pixelSize = PixMap.PIXEL_SIZE_32BIT;
		pm.cmpCount = PixMap.COMPONENT_COUNT_RGB;
		pm.cmpSize = PixMap.COMPONENT_SIZE_32BIT;
		pm.planeBytes = 0;
		pm.pmTable = 0;
		pm.pmReserved = 0;
		ByteArrayOutputStream pdb = new ByteArrayOutputStream();
		DataOutputStream pdd = new DataOutputStream(pdb);
		try {
			byte[] pdscanline = new byte[w*3];
			if ((pm.rowBytes & 0x7FFF) < 8) {
				for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
					}
					pdd.write(pdscanline);
				}
			} else if ((pm.rowBytes & 0x7FFF) > 250) {
				for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
					}
					byte[] csl = packBits(pdscanline);
					pdd.writeShort(csl.length);
					pdd.write(csl);
				}
			} else {
				for (int ly = 0, pddy = 0; ly < h; ly++, pddy += w) {
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx] = (byte)((pixels[pddx] >>> 16) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w] = (byte)((pixels[pddx] >>> 8) & 0xFF);
					}
					for (int lx = 0, pddx = pddy; lx < w; lx++, pddx++) {
						pdscanline[lx+w+w] = (byte)((pixels[pddx] >>> 0) & 0xFF);
					}
					byte[] csl = packBits(pdscanline);
					pdd.writeByte(csl.length);
					pdd.write(csl);
				}
			}
			pdd.close();
			pdb.close();
		} catch (IOException ex) {}
		byte[] pd = pdb.toByteArray();
		pm.packSize = pd.length;
		return new PICTInstruction.DirectBitsRgn(pm, pm.bounds, pm.bounds, PICTInstruction.ModeConstants.SRC_COPY, rgn, pd);
	}
	
	public static BufferedImage pixmapToImage(PixMap pm, ColorTable ct, byte[] data, int bg, int fg, boolean pixpat, boolean packed) {
		// DECOMPRESS THE DATA TO GET THE RAW PIXEL DATA
		if (((pm.packType == PixMap.PACK_TYPE_PACKBITS) && !packed) || (pm.packType == PixMap.PACK_TYPE_UNPACKED) || ((pm.rowBytes & 0x7FFF) < 8)) {
			// no decompression necessary
		} else if (pm.packType == PixMap.PACK_TYPE_UNPACKED_NO_PADDING) {
			byte[] ndata = new byte[data.length*4/3];
			for (int di = 0, ndi = 0; di < data.length && ndi < ndata.length; di += 3, ndi += 4) {
				ndata[ndi+0] = 0;
				ndata[ndi+1] = data[di+0];
				ndata[ndi+2] = data[di+1];
				ndata[ndi+3] = data[di+2];
			}
			data = ndata;
		} else if ((pm.rowBytes & 0x7FFF) > 250) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(data);
				DataInputStream din = new DataInputStream(bin);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				for (int y = pm.bounds.top; y < pm.bounds.bottom; y++) {
					byte[] scanline = new byte[din.readUnsignedShort()];
					din.readFully(scanline);
					scanline = unpackBits(scanline);
					if (pm.packType == PixMap.PACK_TYPE_BY_COMPONENT) {
						if (pm.cmpCount == PixMap.COMPONENT_COUNT_ARGB) {
							byte[] nscanline = new byte[scanline.length];
							int sli = 0;
							for (int nsli = 0; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 1; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 2; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 3; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							scanline = nscanline;
						} else if (pm.cmpCount == PixMap.COMPONENT_COUNT_RGB) {
							byte[] nscanline = new byte[scanline.length*4/3];
							int sli = 0;
							for (int nsli = 0; nsli < nscanline.length; nsli += 4) {
								nscanline[nsli] = 0;
							}
							for (int nsli = 1; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 2; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 3; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							scanline = nscanline;
						}
					}
					dout.write(scanline);
				}
				dout.close();
				bout.close();
				din.close();
				bin.close();
				data = bout.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(data);
				DataInputStream din = new DataInputStream(bin);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				for (int y = pm.bounds.top; y < pm.bounds.bottom; y++) {
					byte[] scanline = new byte[din.readUnsignedByte()];
					din.readFully(scanline);
					scanline = unpackBits(scanline);
					if (pm.packType == PixMap.PACK_TYPE_BY_COMPONENT) {
						if (pm.cmpCount == PixMap.COMPONENT_COUNT_ARGB) {
							byte[] nscanline = new byte[scanline.length];
							int sli = 0;
							for (int nsli = 0; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 1; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 2; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 3; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							scanline = nscanline;
						} else if (pm.cmpCount == PixMap.COMPONENT_COUNT_RGB) {
							byte[] nscanline = new byte[scanline.length*4/3];
							int sli = 0;
							for (int nsli = 0; nsli < nscanline.length; nsli += 4) {
								nscanline[nsli] = 0;
							}
							for (int nsli = 1; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 2; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							for (int nsli = 3; nsli < nscanline.length; nsli += 4, sli++) {
								nscanline[nsli] = scanline[sli];
							}
							scanline = nscanline;
						}
					}
					dout.write(scanline);
				}
				dout.close();
				bout.close();
				din.close();
				bin.close();
				data = bout.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		}
		// CREATE THE COLOR TABLE
		int[] colors;
		if (ct != null) {
			colors = ct.toIntArray();
			if (colors.length < (1 << pm.pixelSize)) {
				int[] newcolors = new int[1 << pm.pixelSize];
				for (int i = 0; i < colors.length; i++) {
					newcolors[i] = colors[i];
				}
				int k = newcolors.length - colors.length - 1;
				if (k == 0) {
					for (int i = colors.length; i < newcolors.length; i++) {
						newcolors[i] = fg;
					}
				} else {
					int bga = (bg >>> 24) & 0xFF;
					int bgr = (bg >>> 16) & 0xFF;
					int bgg = (bg >>>  8) & 0xFF;
					int bgb = (bg >>>  0) & 0xFF;
					int fga = (fg >>> 24) & 0xFF;
					int fgr = (fg >>> 16) & 0xFF;
					int fgg = (fg >>>  8) & 0xFF;
					int fgb = (fg >>>  0) & 0xFF;
					for (int i = colors.length, j = 0; i < newcolors.length; i++, j++) {
						int a = bga + (fga-bga)*j/k;
						int r = bgr + (fgr-bgr)*j/k;
						int g = bgg + (fgg-bgg)*j/k;
						int b = bgb + (fgb-bgb)*j/k;
						newcolors[i] = (a << 24) | (r << 16) | (g << 8) | (b << 0);
					}
				}
				colors = newcolors;
			}
		} else if (pixpat) {
			colors = new int[1 << pm.pixelSize];
			int k = colors.length-1;
			int bga = (bg >>> 24) & 0xFF;
			int bgr = (bg >>> 16) & 0xFF;
			int bgg = (bg >>>  8) & 0xFF;
			int bgb = (bg >>>  0) & 0xFF;
			int fga = (fg >>> 24) & 0xFF;
			int fgr = (fg >>> 16) & 0xFF;
			int fgg = (fg >>>  8) & 0xFF;
			int fgb = (fg >>>  0) & 0xFF;
			for (int i = 0; i < colors.length; i++) {
				int a = bga + (fga-bga)*i/k;
				int r = bgr + (fgr-bgr)*i/k;
				int g = bgg + (fgg-bgg)*i/k;
				int b = bgb + (fgb-bgb)*i/k;
				colors[i] = (a << 24) | (r << 16) | (g << 8) | (b << 0);
			}
		} else {
			if (pm.pixelSize <= 1) colors = COLORS_1BIT;
			else if (pm.pixelSize <= 2) colors = COLORS_2BIT;
			else if (pm.pixelSize <= 4) colors = COLORS_4BIT;
			else colors = COLORS_8BIT;
		}
		// CREATE THE IMAGE
		int w = pm.bounds.right-pm.bounds.left;
		int h = pm.bounds.bottom-pm.bounds.top;
		int bw = (pm.rowBytes & 0x7FFF) * 8 / pm.pixelSize;
		int[] pixels = new int[bw*h];
		switch (pm.pixelSize) {
		case PixMap.PIXEL_SIZE_32BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px++, dx += 4) {
					if (pm.cmpCount == PixMap.COMPONENT_COUNT_ARGB) {
						pixels[px] = ((data[dx+0] & 0xFF) << 24) | ((data[dx+1] & 0xFF) << 16) | ((data[dx+2] & 0xFF) << 8) | (data[dx+3] & 0xFF);
					} else {
						pixels[px] = 0xFF000000 | ((data[dx+1] & 0xFF) << 16) | ((data[dx+2] & 0xFF) << 8) | (data[dx+3] & 0xFF);
					}
				}
			}
			break;
		case PixMap.PIXEL_SIZE_16BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px++, dx += 2) {
					int sh = ((data[dx+0] & 0xFF) << 8) | (data[dx+1] & 0xFF);
					int r = ((sh >>> 10) & 0x1F) * 255 / 31;
					int g = ((sh >>> 5) & 0x1F) * 255 / 31;
					int b = (sh & 0x1F) * 255 / 31;
					pixels[px] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
			break;
		case PixMap.PIXEL_SIZE_8BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px++, dx++) {
					pixels[px] = colors[data[dx] & 0xFF];
				}
			}
			break;
		case PixMap.PIXEL_SIZE_4BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px += 2, dx++) {
					pixels[px] = colors[(data[dx] >>> 4) & 0xF];
					pixels[px+1] = colors[data[dx] & 0xF];
				}
			}
			break;
		case PixMap.PIXEL_SIZE_2BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px += 4, dx++) {
					pixels[px] = colors[(data[dx] >>> 6) & 0x3];
					pixels[px+1] = colors[(data[dx] >>> 4) & 0x3];
					pixels[px+2] = colors[(data[dx] >>> 2) & 0x3];
					pixels[px+3] = colors[data[dx] & 0x3];
				}
			}
			break;
		case PixMap.PIXEL_SIZE_1BIT:
			for (int ly = 0, py = 0, dy = 0; ly < h && py < pixels.length && dy < data.length; ly++, py += bw, dy += (pm.rowBytes & 0x7FFF)) {
				for (int lx = 0, px = py, dx = dy; lx < w && px < pixels.length && dx < data.length; lx++, px += 8, dx++) {
					pixels[px] = colors[(data[dx] >>> 7) & 0x1];
					pixels[px+1] = colors[(data[dx] >>> 6) & 0x1];
					pixels[px+2] = colors[(data[dx] >>> 5) & 0x1];
					pixels[px+3] = colors[(data[dx] >>> 4) & 0x1];
					pixels[px+4] = colors[(data[dx] >>> 3) & 0x1];
					pixels[px+5] = colors[(data[dx] >>> 2) & 0x1];
					pixels[px+6] = colors[(data[dx] >>> 1) & 0x1];
					pixels[px+7] = colors[data[dx] & 0x1];
				}
			}
			break;
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, pixels, 0, bw);
		return img;
	}
	
	public static byte[] encodeString(String s) {
		try {
			return s.getBytes("MACROMAN");
		} catch (UnsupportedEncodingException uee) {
			ByteArrayOutputStream bs = new ByteArrayOutputStream(s.length());
			for (char ch : s.toCharArray()) {
				if (ch < 0x80) bs.write(ch);
				else {
					boolean found = false;
					for (int i = 0x80; i < 0x100; i++) {
						if (MACROMAN[i] == ch) {
							found = true;
							bs.write(i);
							break;
						}
					}
					if (!found) bs.write('?');
				}
			}
			return bs.toByteArray();
		}
	}
	
	public static String decodeString(byte[] data) {
		try {
			return new String(data, "MACROMAN");
		} catch (UnsupportedEncodingException uee) {
			StringBuffer sb = new StringBuffer(data.length);
			for (byte b : data) {
				if (b >= 0) sb.append((char)b);
				else sb.append(MACROMAN[b & 0xFF]);
			}
			return sb.toString();
		}
	}
	
	public static final int[] COLORS_8BIT = {
		0xFFFFFFFF, 0xFFFFFFCC, 0xFFFFFF99, 0xFFFFFF66, 0xFFFFFF33, 0xFFFFFF00, 0xFFFFCCFF, 0xFFFFCCCC,
		0xFFFFCC99, 0xFFFFCC66, 0xFFFFCC33, 0xFFFFCC00, 0xFFFF99FF, 0xFFFF99CC, 0xFFFF9999, 0xFFFF9966,
		0xFFFF9933, 0xFFFF9900, 0xFFFF66FF, 0xFFFF66CC, 0xFFFF6699, 0xFFFF6666, 0xFFFF6633, 0xFFFF6600,
		0xFFFF33FF, 0xFFFF33CC, 0xFFFF3399, 0xFFFF3366, 0xFFFF3333, 0xFFFF3300, 0xFFFF00FF, 0xFFFF00CC,
		0xFFFF0099, 0xFFFF0066, 0xFFFF0033, 0xFFFF0000, 0xFFCCFFFF, 0xFFCCFFCC, 0xFFCCFF99, 0xFFCCFF66,
		0xFFCCFF33, 0xFFCCFF00, 0xFFCCCCFF, 0xFFCCCCCC, 0xFFCCCC99, 0xFFCCCC66, 0xFFCCCC33, 0xFFCCCC00,
		0xFFCC99FF, 0xFFCC99CC, 0xFFCC9999, 0xFFCC9966, 0xFFCC9933, 0xFFCC9900, 0xFFCC66FF, 0xFFCC66CC,
		0xFFCC6699, 0xFFCC6666, 0xFFCC6633, 0xFFCC6600, 0xFFCC33FF, 0xFFCC33CC, 0xFFCC3399, 0xFFCC3366,
		0xFFCC3333, 0xFFCC3300, 0xFFCC00FF, 0xFFCC00CC, 0xFFCC0099, 0xFFCC0066, 0xFFCC0033, 0xFFCC0000,
		0xFF99FFFF, 0xFF99FFCC, 0xFF99FF99, 0xFF99FF66, 0xFF99FF33, 0xFF99FF00, 0xFF99CCFF, 0xFF99CCCC,
		0xFF99CC99, 0xFF99CC66, 0xFF99CC33, 0xFF99CC00, 0xFF9999FF, 0xFF9999CC, 0xFF999999, 0xFF999966,
		0xFF999933, 0xFF999900, 0xFF9966FF, 0xFF9966CC, 0xFF996699, 0xFF996666, 0xFF996633, 0xFF996600,
		0xFF9933FF, 0xFF9933CC, 0xFF993399, 0xFF993366, 0xFF993333, 0xFF993300, 0xFF9900FF, 0xFF9900CC,
		0xFF990099, 0xFF990066, 0xFF990033, 0xFF990000, 0xFF66FFFF, 0xFF66FFCC, 0xFF66FF99, 0xFF66FF66,
		0xFF66FF33, 0xFF66FF00, 0xFF66CCFF, 0xFF66CCCC, 0xFF66CC99, 0xFF66CC66, 0xFF66CC33, 0xFF66CC00,
		0xFF6699FF, 0xFF6699CC, 0xFF669999, 0xFF669966, 0xFF669933, 0xFF669900, 0xFF6666FF, 0xFF6666CC,
		0xFF666699, 0xFF666666, 0xFF666633, 0xFF666600, 0xFF6633FF, 0xFF6633CC, 0xFF663399, 0xFF663366,
		0xFF663333, 0xFF663300, 0xFF6600FF, 0xFF6600CC, 0xFF660099, 0xFF660066, 0xFF660033, 0xFF660000,
		0xFF33FFFF, 0xFF33FFCC, 0xFF33FF99, 0xFF33FF66, 0xFF33FF33, 0xFF33FF00, 0xFF33CCFF, 0xFF33CCCC,
		0xFF33CC99, 0xFF33CC66, 0xFF33CC33, 0xFF33CC00, 0xFF3399FF, 0xFF3399CC, 0xFF339999, 0xFF339966,
		0xFF339933, 0xFF339900, 0xFF3366FF, 0xFF3366CC, 0xFF336699, 0xFF336666, 0xFF336633, 0xFF336600,
		0xFF3333FF, 0xFF3333CC, 0xFF333399, 0xFF333366, 0xFF333333, 0xFF333300, 0xFF3300FF, 0xFF3300CC,
		0xFF330099, 0xFF330066, 0xFF330033, 0xFF330000, 0xFF00FFFF, 0xFF00FFCC, 0xFF00FF99, 0xFF00FF66,
		0xFF00FF33, 0xFF00FF00, 0xFF00CCFF, 0xFF00CCCC, 0xFF00CC99, 0xFF00CC66, 0xFF00CC33, 0xFF00CC00,
		0xFF0099FF, 0xFF0099CC, 0xFF009999, 0xFF009966, 0xFF009933, 0xFF009900, 0xFF0066FF, 0xFF0066CC,
		0xFF006699, 0xFF006666, 0xFF006633, 0xFF006600, 0xFF0033FF, 0xFF0033CC, 0xFF003399, 0xFF003366,
		0xFF003333, 0xFF003300, 0xFF0000FF, 0xFF0000CC, 0xFF000099, 0xFF000066, 0xFF000033, 0xFFEE0000,
		0xFFDD0000, 0xFFBB0000, 0xFFAA0000, 0xFF880000, 0xFF770000, 0xFF550000, 0xFF440000, 0xFF220000,
		0xFF110000, 0xFF00EE00, 0xFF00DD00, 0xFF00BB00, 0xFF00AA00, 0xFF008800, 0xFF007700, 0xFF005500,
		0xFF004400, 0xFF002200, 0xFF001100, 0xFF0000EE, 0xFF0000DD, 0xFF0000BB, 0xFF0000AA, 0xFF000088,
		0xFF000077, 0xFF000055, 0xFF000044, 0xFF000022, 0xFF000011, 0xFFEEEEEE, 0xFFDDDDDD, 0xFFBBBBBB,
		0xFFAAAAAA, 0xFF888888, 0xFF777777, 0xFF555555, 0xFF444444, 0xFF222222, 0xFF111111, 0xFF000000
	};
	
	public static final int[] COLORS_4BIT = {
		0xFFFFFFFF, 0xFFFCF305, 0xFFFF6503, 0xFFDD0907, 0xFFF30885, 0xFF4700A5, 0xFF0000D4, 0xFF02ABEB,
		0xFF1FB814, 0xFF006512, 0xFF562D05, 0xFF91713A, 0xFFC0C0C0, 0xFF808080, 0xFF404040, 0xFF000000
	};
	
	public static final int[] COLORS_2BIT = {
		0xFFFFFFFF, 0xFF808080, 0xFFCCCCFF, 0xFF000000
	};
	
	public static final int[] COLORS_1BIT = {
		0xFFFFFFFF, 0xFF000000
	};
	
	private static final char[] MACROMAN = {
		'\u0000','\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\u0007',
		'\u0008','\u0009','\n','\u000B','\u000C','\r','\u000E','\u000F',
		'\u0010','\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017',
		'\u0018','\u0019','\u001A','\u001B','\u001C','\u001D','\u001E','\u001F',
		'\u0020','\u0021','\u0022','\u0023','\u0024','\u0025','\u0026','\'',
		'\u0028','\u0029','\u002A','\u002B','\u002C','\u002D','\u002E','\u002F',
		'\u0030','\u0031','\u0032','\u0033','\u0034','\u0035','\u0036','\u0037',
		'\u0038','\u0039','\u003A','\u003B','\u003C','\u003D','\u003E','\u003F',
		'\u0040','\u0041','\u0042','\u0043','\u0044','\u0045','\u0046','\u0047',
		'\u0048','\u0049','\u004A','\u004B','\u004C','\u004D','\u004E','\u004F',
		'\u0050','\u0051','\u0052','\u0053','\u0054','\u0055','\u0056','\u0057',
		'\u0058','\u0059','\u005A','\u005B','\\','\u005D','\u005E','\u005F',
		'\u0060','\u0061','\u0062','\u0063','\u0064','\u0065','\u0066','\u0067',
		'\u0068','\u0069','\u006A','\u006B','\u006C','\u006D','\u006E','\u006F',
		'\u0070','\u0071','\u0072','\u0073','\u0074','\u0075','\u0076','\u0077',
		'\u0078','\u0079','\u007A','\u007B','\u007C','\u007D','\u007E','\u007F',
		'\u00C4','\u00C5','\u00C7','\u00C9','\u00D1','\u00D6','\u00DC','\u00E1',
		'\u00E0','\u00E2','\u00E4','\u00E3','\u00E5','\u00E7','\u00E9','\u00E8',
		'\u00EA','\u00EB','\u00ED','\u00EC','\u00EE','\u00EF','\u00F1','\u00F3',
		'\u00F2','\u00F4','\u00F6','\u00F5','\u00FA','\u00F9','\u00FB','\u00FC',
		'\u2020','\u00B0','\u00A2','\u00A3','\u00A7','\u2022','\u00B6','\u00DF',
		'\u00AE','\u00A9','\u2122','\u00B4','\u00A8','\u2260','\u00C6','\u00D8',
		'\u221E','\u00B1','\u2264','\u2265','\u00A5','\u00B5','\u2202','\u2211',
		'\u220F','\u03C0','\u222B','\u00AA','\u00BA','\u03A9','\u00E6','\u00F8',
		'\u00BF','\u00A1','\u00AC','\u221A','\u0192','\u2248','\u2206','\u00AB',
		'\u00BB','\u2026','\u00A0','\u00C0','\u00C3','\u00D5','\u0152','\u0153',
		'\u2013','\u2014','\u201C','\u201D','\u2018','\u2019','\u00F7','\u25CA',
		'\u00FF','\u0178','\u2044','\u20AC','\u2039','\u203A','\uFB01','\uFB02',
		'\u2021','\u00B7','\u201A','\u201E','\u2030','\u00C2','\u00CA','\u00C1',
		'\u00CB','\u00C8','\u00CD','\u00CE','\u00CF','\u00CC','\u00D3','\u00D4',
		'\uF8FF','\u00D2','\u00DA','\u00DB','\u00D9','\u0131','\u02C6','\u02DC',
		'\u00AF','\u02D8','\u02D9','\u02DA','\u00B8','\u02DD','\u02DB','\u02C7',
	};
}
