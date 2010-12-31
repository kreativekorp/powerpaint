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

package com.kreative.paint.rcp;

import static com.kreative.paint.rcp.RCPSwatch.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RCPAssembler {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File f = new File(arg);
				Scanner in = new Scanner(f, "UTF-8");
				DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(f.getParentFile(), f.getName().replaceFirst("\\.txt$", "")+".rcp")));
				assemble(in, out);
				in.close();
				out.close();
			} catch (IOException ioe) {
				System.err.println("Failed to convert "+arg+": "+ioe.getClass().getSimpleName()+": "+ioe.getMessage());
			}
		}
	}
	
	public static void assemble(Scanner in, DataOutputStream out) throws IOException {
		Map<String,String> defs = new HashMap<String,String>();
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] fields = line.split("\t");
				String ops = ((fields.length > 0) ? fields[0] : "").trim();
				String bs = ((fields.length > 1) ? fields[1] : "").trim();
				String args = ((fields.length > 2) ? fields[2] : "").trim();
				if (ops.equalsIgnoreCase(".define")) {
					defs.put("<"+bs.toLowerCase()+">", args);
				} else {
					if (args.startsWith("<") && args.endsWith(">") && defs.containsKey(args.toLowerCase())) {
						args = defs.get(args.toLowerCase());
					}
					String[] arga = args.split(",");
					int op = stringToOp(ops);
					int b = stringToBorder(bs);
					long i = ((long)op << INST_TYPE_SHIFT) | ((long)b << INST_BORDER_SHIFT);
					byte[] d;
					switch (op) {
					case INST_TYPE_RGB:
						i |= parseRGB(args);
						out.writeLong(i);
						break;
					case INST_TYPE_HSV:
					case INST_TYPE_LAB:
					case INST_TYPE_XYZ:
						i |= (long)parseInt((arga.length > 0) ? arga[0].trim() : "") << 32l;
						i |= (long)parseInt((arga.length > 1) ? arga[1].trim() : "") << 16l;
						i |= (long)parseInt((arga.length > 2) ? arga[2].trim() : "");
						out.writeLong(i);
						break;
					case INST_TYPE_ARGB:
						i |= parseARGB(args) << 16l;
						out.writeLong(i);
						break;
					case INST_TYPE_IMAGE:
						d = stringToBinary(args);
						i |= (long)d.length;
						out.writeLong(i);
						out.write(d);
						if ((d.length % 8) != 0) out.write(new byte[8 - (d.length % 8)]);
						break;
					case INST_TYPE_RGB_SWEEP:
					case INST_TYPE_HSV_SWEEP:
					case INST_TYPE_LAB_SWEEP:
					case INST_TYPE_XYZ_SWEEP:
						int xc = 0;
						int xs = 0;
						int xe = 0;
						int yc = 0;
						int ys = 0;
						int ye = 0;
						int zv = 0;
						for (String arg : arga) {
							arg = arg.trim();
							if (arg.startsWith("X:")) {
								arg = arg.substring(2).trim();
								if (arg.startsWith("R:") || arg.startsWith("H:") || arg.startsWith("L:") || arg.startsWith("X:")) {
									xc = 0;
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("G:") || arg.startsWith("S:") || arg.startsWith("A:") || arg.startsWith("Y:")) {
									xc = 1;
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("B:") || arg.startsWith("V:") || arg.startsWith("Z:")) {
									xc = 2;
									arg = arg.substring(2).trim();
								}
								String[] rs = arg.split("-");
								xs = parseInt((rs.length > 0) ? rs[0] : "");
								xe = parseInt((rs.length > 1) ? rs[1] : "");
							}
							else if (arg.startsWith("Y:")) {
								arg = arg.substring(2).trim();
								if (arg.startsWith("R:") || arg.startsWith("H:") || arg.startsWith("L:") || arg.startsWith("X:")) {
									yc = 0;
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("G:") || arg.startsWith("S:") || arg.startsWith("A:") || arg.startsWith("Y:")) {
									yc = 1;
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("B:") || arg.startsWith("V:") || arg.startsWith("Z:")) {
									yc = 2;
									arg = arg.substring(2).trim();
								}
								String[] rs = arg.split("-");
								ys = parseInt((rs.length > 0) ? rs[0] : "");
								ye = parseInt((rs.length > 1) ? rs[1] : "");
							}
							else if (arg.startsWith("Z:")) {
								arg = arg.substring(2).trim();
								if (arg.startsWith("R:") || arg.startsWith("H:") || arg.startsWith("L:") || arg.startsWith("X:")) {
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("G:") || arg.startsWith("S:") || arg.startsWith("A:") || arg.startsWith("Y:")) {
									arg = arg.substring(2).trim();
								}
								else if (arg.startsWith("B:") || arg.startsWith("V:") || arg.startsWith("Z:")) {
									arg = arg.substring(2).trim();
								}
								zv = parseInt(arg);
							}
						}
						i |= (long)xc << 44l;
						i |= (long)yc << 40l;
						i |= (long)xs << 32l;
						i |= (long)xe << 24l;
						i |= (long)ys << 16l;
						i |= (long)ye << 8l;
						i |= (long)zv;
						out.writeLong(i);
						break;
					case INST_TYPE_WRAPPER:
						out.writeLong(i);
						break;
					case INST_TYPE_BORDERLAYOUT:
						int nn = 0, nd = 0;
						int wn = 0, wd = 0;
						int en = 0, ed = 0;
						int sn = 0, sd = 0;
						for (String arg : arga) {
							arg = arg.trim();
							if (arg.startsWith("N:")) {
								String[] fs = arg.substring(2).split("/");
								nn = parseInt((fs.length > 0) ? fs[0].trim() : "");
								nd = parseInt((fs.length > 1) ? fs[1].trim() : "");
							}
							else if (arg.startsWith("W:")) {
								String[] fs = arg.substring(2).split("/");
								wn = parseInt((fs.length > 0) ? fs[0].trim() : "");
								wd = parseInt((fs.length > 1) ? fs[1].trim() : "");
							}
							else if (arg.startsWith("E:")) {
								String[] fs = arg.substring(2).split("/");
								en = parseInt((fs.length > 0) ? fs[0].trim() : "");
								ed = parseInt((fs.length > 1) ? fs[1].trim() : "");
							}
							else if (arg.startsWith("S:")) {
								String[] fs = arg.substring(2).split("/");
								sn = parseInt((fs.length > 0) ? fs[0].trim() : "");
								sd = parseInt((fs.length > 1) ? fs[1].trim() : "");
							}
						}
						i |= (long)nn << 44l;
						i |= (long)wn << 40l;
						i |= (long)en << 36l;
						i |= (long)sn << 32l;
						i |= (long)nd << 24l;
						i |= (long)wd << 16l;
						i |= (long)ed << 8l;
						i |= (long)sd;
						out.writeLong(i);
						break;
					case INST_TYPE_FRAME:
					case INST_TYPE_GRID:
					case INST_TYPE_DIAGONAL_GRID:
						i |= (long)parseInt((arga.length > 0) ? arga[0].trim() : "") << 32l;
						i |= (long)parseInt((arga.length > 1) ? arga[1].trim() : "") << 16l;
						out.writeLong(i);
						break;
					case INST_TYPE_ORIENTATION:
						out.writeLong(i);
						break;
					case INST_TYPE_SQUARE_GRID:
					case INST_TYPE_SQ_DIAGONAL:
						i |= (long)parseInt((arga.length > 0) ? arga[0].trim() : "") << 32l;
						i |= (long)parseInt((arga.length > 1) ? arga[1].trim() : "") << 16l;
						out.writeLong(i);
						break;
					case INST_TYPE_HORIZ_DIVIDER:
					case INST_TYPE_VERT_DIVIDER:
						int[] ns = new int[5];
						int nsp = 0;
						int ds = 0;
						for (String arg : arga) {
							if (nsp < ns.length) {
								if (arg.contains("/")) {
									String[] ac = arg.split("/");
									ns[nsp++] = parseInt(ac[0].trim());
									ds = parseInt(ac[1].trim());
								} else {
									ns[nsp++] = parseInt(arg);
								}
							}
						}
						i |= (long)ns[0] << 40l;
						i |= (long)ns[1] << 32l;
						i |= (long)ns[2] << 24l;
						i |= (long)ns[3] << 16l;
						i |= (long)ns[4] << 8l;
						i |= (long)ds;
						out.writeLong(i);
						break;
					case INST_TYPE_NAME:
						if (args.startsWith("\"") && args.endsWith("\"")) {
							args = args.substring(1, args.length()-1);
						}
						d = args.getBytes("UTF-8");
						i |= (long)d.length;
						out.writeLong(i);
						out.write(d);
						if ((d.length % 8) != 0) out.write(new byte[8 - (d.length % 8)]);
						break;
					case INST_TYPE_SIZE:
					case INST_TYPE_SIZE_HORIZ:
					case INST_TYPE_SIZE_SQUARE:
					case INST_TYPE_SIZE_VERT:
					case INST_TYPE_SPACER:
						i |= (long)parseInt((arga.length > 0) ? arga[0].trim() : "") << 32l;
						i |= (long)parseInt((arga.length > 1) ? arga[1].trim() : "") << 16l;
						out.writeLong(i);
						break;
					case INST_TYPE_HORIZ_SPACER:
					case INST_TYPE_VERT_SPACER:
						i |= (long)parseInt(args) << 32l;
						out.writeLong(i);
						break;
					case INST_TYPE_1PX_WIDTH:
					case INST_TYPE_1PX_HEIGHT:
						out.writeLong(i);
						break;
					default:
						i |= hexl(args);
					out.writeLong(i);
					break;
					}
				}
			}
		}
	}
	
	private static int parseInt(String s) {
		if (s.startsWith("0x")) return hex(s.substring(2));
		else try { return Integer.parseInt(s); }
		catch (NumberFormatException nfe) { return 0; }
	}
	
	private static long parseRGB(String s) {
		if (s.startsWith("#")) {
			if (s.length() > 12) {
				long l = hexl(s.substring(1));
				long r = (l >>> 32l) & 0xFFFFl;
				long g = (l >>> 16l) & 0xFFFFl;
				long b = l & 0xFFFFl;
				return (r << 32l) | (g << 16l) | b;
			}
			else if (s.length() > 6) {
				long l = hexl(s.substring(1));
				long r = ((l >>> 16l) & 0xFFl) * 257l;
				long g = ((l >>> 8l) & 0xFFl) * 257l;
				long b = (l & 0xFFl) * 257l;
				return (r << 32l) | (g << 16l) | b;
			}
			else if (s.length() > 3) {
				long i = hexl(s.substring(1));
				long r = ((i >>> 8l) & 0xFl) * 17l * 257l;
				long g = ((i >>> 4l) & 0xFl) * 17l * 257l;
				long b = (i & 0xFl) * 17l * 257l;
				return (r << 32l) | (g << 16l) | b;
			}
			else {
				return -1L;
			}
		} else {
			String[] ss = s.split(",");
			if (ss.length >= 4) {
				long r = (long)parseInt(ss[1].trim());
				long g = (long)parseInt(ss[2].trim());
				long b = (long)parseInt(ss[3].trim());
				return (r << 32l) | (g << 16l) | b;
			} else {
				long r = (long)parseInt((ss.length > 0) ? ss[0].trim() : "");
				long g = (long)parseInt((ss.length > 1) ? ss[1].trim() : "");
				long b = (long)parseInt((ss.length > 2) ? ss[2].trim() : "");
				return (r << 32l) | (g << 16l) | b;
			}
		}
	}
	
	private static long parseARGB(String s) {
		if (s.startsWith("#")) {
			if (s.length() > 16) {
				long l = hexl(s.substring(1));
				long a = ((l >>> 48l) & 0xFFFFl) / 257l;
				long r = ((l >>> 32l) & 0xFFFFl) / 257l;
				long g = ((l >>> 16l) & 0xFFFFl) / 257l;
				long b = (l & 0xFFFFl) / 257l;
				return (a << 24l) | (r << 16l) | (g << 8l) | b;
			}
			else if (s.length() > 12) {
				long l = hexl(s.substring(1));
				long r = ((l >>> 32l) & 0xFFFFl) / 257l;
				long g = ((l >>> 16l) & 0xFFFFl) / 257l;
				long b = (l & 0xFFFFl) / 257l;
				return 0xFF000000l | (r << 16l) | (g << 8l) | b;
			}
			else if (s.length() > 8) {
				long i = hexl(s.substring(1)) & 0xFFFFFFFFl;
				return i;
			}
			else if (s.length() > 6) {
				long i = hexl(s.substring(1)) & 0xFFFFFFFFl;
				return 0xFF000000l | i;
			}
			else if (s.length() > 4) {
				long i = hexl(s.substring(1));
				long a = ((i >>> 12l) & 0xFl) * 17l;
				long r = ((i >>> 8l) & 0xFl) * 17l;
				long g = ((i >>> 4l) & 0xFl) * 17l;
				long b = (i & 0xFl) * 17l;
				return (a << 24l) | (r << 16l) | (g << 8l) | b;
			}
			else if (s.length() > 3) {
				long i = hexl(s.substring(1));
				long r = ((i >>> 8l) & 0xFl) * 17l;
				long g = ((i >>> 4l) & 0xFl) * 17l;
				long b = (i & 0xFl) * 17l;
				return 0xFF000000l | (r << 16l) | (g << 8l) | b;
			}
			else {
				return -1L;
			}
		} else {
			String[] ss = s.split(",");
			if (ss.length >= 4) {
				long a = (long)parseInt(ss[0].trim()) / 257l;
				long r = (long)parseInt(ss[1].trim()) / 257l;
				long g = (long)parseInt(ss[2].trim()) / 257l;
				long b = (long)parseInt(ss[3].trim()) / 257l;
				return (a << 24l) | (r << 16l) | (g << 8l) | b;
			} else {
				long r = (long)parseInt((ss.length > 0) ? ss[0].trim() : "") / 257l;
				long g = (long)parseInt((ss.length > 1) ? ss[1].trim() : "") / 257l;
				long b = (long)parseInt((ss.length > 2) ? ss[2].trim() : "") / 257l;
				return 0xFF000000l | (r << 16l) | (g << 8l) | b;
			}
		}
	}
	
	private static int stringToOp(String s) {
		s = s.trim().toLowerCase();
		if (s.equals("empty")) return INST_TYPE_EMPTY;
		if (s.equals("rgb")) return INST_TYPE_RGB;
		if (s.equals("hsv")) return INST_TYPE_HSV;
		if (s.equals("lab")) return INST_TYPE_LAB;
		if (s.equals("xyz")) return INST_TYPE_XYZ;
		if (s.equals("argb")) return INST_TYPE_ARGB;
		if (s.equals("image")) return INST_TYPE_IMAGE;
		if (s.equals("rgbsw")) return INST_TYPE_RGB_SWEEP;
		if (s.equals("hsvsw")) return INST_TYPE_HSV_SWEEP;
		if (s.equals("labsw")) return INST_TYPE_LAB_SWEEP;
		if (s.equals("xyzsw")) return INST_TYPE_XYZ_SWEEP;
		if (s.equals("wrap")) return INST_TYPE_WRAPPER;
		if (s.equals("bdrlyt")) return INST_TYPE_BORDERLAYOUT;
		if (s.equals("frame")) return INST_TYPE_FRAME;
		if (s.equals("grid")) return INST_TYPE_GRID;
		if (s.equals("diag")) return INST_TYPE_DIAGONAL_GRID;
		if (s.equals("orient")) return INST_TYPE_ORIENTATION;
		if (s.equals("sqgrid")) return INST_TYPE_SQUARE_GRID;
		if (s.equals("sqdiag")) return INST_TYPE_SQ_DIAGONAL;
		if (s.equals("hdiv")) return INST_TYPE_HORIZ_DIVIDER;
		if (s.equals("vdiv")) return INST_TYPE_VERT_DIVIDER;
		if (s.equals("name")) return INST_TYPE_NAME;
		if (s.equals("size")) return INST_TYPE_SIZE;
		if (s.equals("sizeh")) return INST_TYPE_SIZE_HORIZ;
		if (s.equals("sizesq")) return INST_TYPE_SIZE_SQUARE;
		if (s.equals("sizev")) return INST_TYPE_SIZE_VERT;
		if (s.equals("sp")) return INST_TYPE_SPACER;
		if (s.equals("hsp")) return INST_TYPE_HORIZ_SPACER;
		if (s.equals("vsp")) return INST_TYPE_VERT_SPACER;
		if (s.equals("hsp1")) return INST_TYPE_1PX_WIDTH;
		if (s.equals("vsp1")) return INST_TYPE_1PX_HEIGHT;
		if (s.equals("nop")) return INST_TYPE_NO_OP;
		if (s.startsWith("unk")) return hex(s.substring(3));
		return INST_TYPE_NO_OP;
	}
	
	private static int stringToBorder(String s) {
		boolean t = false, l = false, b = false, r = false;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			switch (ch) {
			case '0': return 0x00;
			case '1': return 0x01;
			case '2': return 0x02;
			case '3': return 0x03;
			case '4': return 0x04;
			case '5': return 0x05;
			case '6': return 0x06;
			case '7': return 0x07;
			case '8': return 0x08;
			case '9': return 0x09;
			case 'A': case 'a': return 0x0A;
			case 'C': case 'c': return 0x0C;
			case 'D': case 'd': return 0x0D;
			case 'E': case 'e': return 0x0E;
			case 'F': case 'f': return 0x0F;
			case 'T': case 't': case '~': t = true; break;
			case 'L': case 'l': case '[': l = true; break;
			case 'B': case 'b': case '_': b = true; break;
			case 'R': case 'r': case ']': r = true; break;
			// here's a sneaky thing: 'B' -> INST_BORDER_B == 0x0B <- 'B' :]
			}
		}
		return (
			t ? (
				l ? (
					b ? (
						r ? INST_BORDER_TLBR : INST_BORDER_TLB
					) : (
						r ? INST_BORDER_TLR : INST_BORDER_TL
					)
				) : (
					b ? (
						r ? INST_BORDER_TBR : INST_BORDER_TB
					) : (
						r ? INST_BORDER_TR : INST_BORDER_T
					)
				)
			) : (
				l ? (
					b ? (
						r ? INST_BORDER_LBR : INST_BORDER_LB
					) : (
						r ? INST_BORDER_LR : INST_BORDER_L
					)
				) : (
					b ? (
						r ? INST_BORDER_BR : INST_BORDER_B
					) : (
						r ? INST_BORDER_R : INST_BORDER_NONE
					)
				)
			)
		);
	}
	
	private static int hex(String s) {
		int i = 0;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch >= '0' && ch <= '9') {
				i = (i << 4l) | (int)(ch - '0');
			} else if (ch >= 'A' && ch <= 'F') {
				i = (i << 4l) | (int)(ch - 'A' + 10);
			} else if (ch >= 'a' && ch <= 'f') {
				i = (i << 4l) | (int)(ch - 'a' + 10);
			}
		}
		return i;
	}
	
	private static long hexl(String s) {
		long l = 0;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch >= '0' && ch <= '9') {
				l = (l << 4l) | (long)(ch - '0');
			} else if (ch >= 'A' && ch <= 'F') {
				l = (l << 4l) | (long)(ch - 'A' + 10);
			} else if (ch >= 'a' && ch <= 'f') {
				l = (l << 4l) | (long)(ch - 'a' + 10);
			}
		}
		return l;
	}
	
	private static byte[] stringToBinary(String s) {
		byte[] b = new byte[s.length() / 2];
		int bi = 0;
		boolean bstart = true;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch >= '0' && ch <= '9') {
				if (bstart) {
					b[bi] = (byte)(((ch - '0') << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - '0') & 0xF);
					bi++;
					bstart = true;
				}
			} else if (ch >= 'A' && ch <= 'F') {
				if (bstart) {
					b[bi] = (byte)(((ch - 'A' + 10) << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - 'A' + 10) & 0xF);
					bi++;
					bstart = true;
				}
			} else if (ch >= 'a' && ch <= 'f') {
				if (bstart) {
					b[bi] = (byte)(((ch - 'a' + 10) << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - 'a' + 10) & 0xF);
					bi++;
					bstart = true;
				}
			}
		}
		return b;
	}
}
