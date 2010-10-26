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

package com.kreative.paint.rcp;

import static com.kreative.paint.rcp.RCPSwatch.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RCPDisassembler {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File f = new File(arg);
				DataInputStream in = new DataInputStream(new FileInputStream(f));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(f.getParentFile(), f.getName().replaceFirst("\\.rcp$", "")+".txt")), "UTF-8"));
				disassemble(in, out);
				in.close();
				out.close();
			} catch (IOException ioe) {
				System.err.println("Failed to convert "+arg+": "+ioe.getClass().getSimpleName()+": "+ioe.getMessage());
			}
		}
	}
	
	public static void disassemble(DataInputStream in, PrintWriter out) throws IOException {
		while (in.available() >= 8) {
			long i = in.readLong();
			int op = (int)((i & INST_TYPE_MASK) >>> INST_TYPE_SHIFT);
			int b = (int)((i & INST_BORDER_MASK) >>> INST_BORDER_SHIFT);
			int n1 = (int)((i >>> 44l) & 0xFl);
			int n2 = (int)((i >>> 40l) & 0xFl);
			int n3 = (int)((i >>> 36l) & 0xFl);
			int n4 = (int)((i >>> 32l) & 0xFl);
			int b1 = (int)((i >>> 40l) & 0xFFl);
			int b2 = (int)((i >>> 32l) & 0xFFl);
			int b3 = (int)((i >>> 24l) & 0xFFl);
			int b4 = (int)((i >>> 16l) & 0xFFl);
			int b5 = (int)((i >>>  8l) & 0xFFl);
			int b6 = (int)((i >>>  0l) & 0xFFl);
			int s1 = (int)((i >>> 32l) & 0xFFFFl);
			int s2 = (int)((i >>> 16l) & 0xFFFFl);
			int s3 = (int)((i >>>  0l) & 0xFFFFl);
			String c; byte[] d;
			switch (op) {
			case INST_TYPE_RGB:
				if (((s1 >>> 8) == (s1 & 0xFF)) && ((s2 >>> 8) == (s2 & 0xFF)) && ((s3 >>> 8) == (s3 & 0xFF))) {
					out.println(
							opToString(op) + "\t" +
							borderToString(b) + "\t" +
							"#"+hex(s1,2)+hex(s2,2)+hex(s3,2)
					);
				} else {
					out.println(
							opToString(op) + "\t" +
							borderToString(b) + "\t" +
							"#"+hex(s1,4)+hex(s2,4)+hex(s3,4)
					);
				}
				break;
			case INST_TYPE_HSV:
			case INST_TYPE_LAB:
			case INST_TYPE_XYZ:
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						s1 + "," + s2 + "," + s3
				);
				break;
			case INST_TYPE_ARGB:
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						"#"+hex(b1,2)+hex(b2,2)+hex(b3,2)+hex(b4,2)
				);
				break;
			case INST_TYPE_IMAGE:
				d = new byte[(int)(i & 0x7FFFFFFFl)];
				in.readFully(d);
				if ((d.length % 8) != 0) in.readFully(new byte[8 - (d.length % 8)]);
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						binaryToString(d)
				);
				break;
			case INST_TYPE_RGB_SWEEP:
				c = "RGB";
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						"X:" + c.charAt(n1) + ":" + b2 + "-" + b3 + "," +
						"Y:" + c.charAt(n2) + ":" + b4 + "-" + b5 + "," +
						"Z:" + c.charAt((n1 != 0 && n2 != 0) ? 0 : (n1 != 1 && n2 != 1) ? 1 : (n1 != 2 && n2 != 2) ? 2 : 0) + ":" + b6
				);
				break;
			case INST_TYPE_HSV_SWEEP:
				c = "HSV";
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						"X:" + c.charAt(n1) + ":" + b2 + "-" + b3 + "," +
						"Y:" + c.charAt(n2) + ":" + b4 + "-" + b5 + "," +
						"Z:" + c.charAt((n1 != 0 && n2 != 0) ? 0 : (n1 != 1 && n2 != 1) ? 1 : (n1 != 2 && n2 != 2) ? 2 : 0) + ":" + b6
				);
				break;
			case INST_TYPE_LAB_SWEEP:
				c = "LAB";
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						"X:" + c.charAt(n1) + ":" + b2 + "-" + b3 + "," +
						"Y:" + c.charAt(n2) + ":" + b4 + "-" + b5 + "," +
						"Z:" + c.charAt((n1 != 0 && n2 != 0) ? 0 : (n1 != 1 && n2 != 1) ? 1 : (n1 != 2 && n2 != 2) ? 2 : 0) + ":" + b6
				);
				break;
			case INST_TYPE_XYZ_SWEEP:
				c = "XYZ";
				out.println(
						opToString(op) + "\t" +
						borderToString(b) + "\t" +
						"X:" + c.charAt(n1) + ":" + b2 + "-" + b3 + "," +
						"Y:" + c.charAt(n2) + ":" + b4 + "-" + b5 + "," +
						"Z:" + c.charAt((n1 != 0 && n2 != 0) ? 0 : (n1 != 1 && n2 != 1) ? 1 : (n1 != 2 && n2 != 2) ? 2 : 0) + ":" + b6
				);
				break;
			case INST_TYPE_WRAPPER:
				out.println(
						opToString(op) + "\t" +
						hex(b,1)
				);
				break;
			case INST_TYPE_BORDERLAYOUT:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						"N:"+n1+"/"+b3 + "," + "W:"+n2+"/"+b4 + "," + "E:"+n3+"/"+b5 + "," + "S:"+n4+"/"+b6
				);
				break;
			case INST_TYPE_FRAME:
			case INST_TYPE_GRID:
			case INST_TYPE_DIAGONAL_GRID:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						s1 + "," + s2
				);
				break;
			case INST_TYPE_ORIENTATION:
				out.println(
						opToString(op) + "\t" +
						hex(b,1)
				);
				break;
			case INST_TYPE_SQUARE_GRID:
			case INST_TYPE_SQ_DIAGONAL:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						s1 + "," + s2
				);
				break;
			case INST_TYPE_HORIZ_DIVIDER:
			case INST_TYPE_VERT_DIVIDER:
				StringBuffer ds = new StringBuffer();
				if (b1 != 0) ds.append(","+b1);
				if (b2 != 0) ds.append(","+b2);
				if (b3 != 0) ds.append(","+b3);
				if (b4 != 0) ds.append(","+b4);
				if (b5 != 0) ds.append(","+b5);
				if (b1+b2+b3+b4+b5 < b6) ds.append(","+(b6-(b1+b2+b3+b4+b5)));
				ds.append("/"+b6);
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						ds.toString().substring(1)
				);
				break;
			case INST_TYPE_NAME:
				d = new byte[(int)(i & 0x7FFFFFFFl)];
				in.readFully(d);
				if ((d.length % 8) != 0) in.readFully(new byte[8 - (d.length % 8)]);
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						"\"" + new String(d, "UTF-8") + "\""
				);
				break;
			case INST_TYPE_SIZE:
			case INST_TYPE_SIZE_HORIZ:
			case INST_TYPE_SIZE_SQUARE:
			case INST_TYPE_SIZE_VERT:
			case INST_TYPE_SPACER:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						s1 + "," + s2
				);
				break;
			case INST_TYPE_HORIZ_SPACER:
			case INST_TYPE_VERT_SPACER:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						s1
				);
				break;
			case INST_TYPE_1PX_WIDTH:
			case INST_TYPE_1PX_HEIGHT:
				out.println(
						opToString(op) + "\t" +
						hex(b,1)
				);
				break;
			default:
				out.println(
						opToString(op) + "\t" +
						hex(b,1) + "\t" +
						hex(i,12)
				);
				break;
			}
		}
	}
	
	private static String opToString(int op) {
		switch (op) {
		case INST_TYPE_EMPTY: return "empty";
		case INST_TYPE_RGB: return "rgb";
		case INST_TYPE_HSV: return "hsv";
		case INST_TYPE_LAB: return "lab";
		case INST_TYPE_XYZ: return "xyz";
		case INST_TYPE_ARGB: return "argb";
		case INST_TYPE_IMAGE: return "image";
		case INST_TYPE_RGB_SWEEP: return "rgbsw";
		case INST_TYPE_HSV_SWEEP: return "hsvsw";
		case INST_TYPE_LAB_SWEEP: return "labsw";
		case INST_TYPE_XYZ_SWEEP: return "xyzsw";
		case INST_TYPE_WRAPPER: return "wrap";
		case INST_TYPE_BORDERLAYOUT: return "bdrlyt";
		case INST_TYPE_FRAME: return "frame";
		case INST_TYPE_GRID: return "grid";
		case INST_TYPE_DIAGONAL_GRID: return "diag";
		case INST_TYPE_ORIENTATION: return "orient";
		case INST_TYPE_SQUARE_GRID: return "sqgrid";
		case INST_TYPE_SQ_DIAGONAL: return "sqdiag";
		case INST_TYPE_HORIZ_DIVIDER: return "hdiv";
		case INST_TYPE_VERT_DIVIDER: return "vdiv";
		case INST_TYPE_NAME: return "name";
		case INST_TYPE_SIZE: return "size";
		case INST_TYPE_SIZE_HORIZ: return "sizeh";
		case INST_TYPE_SIZE_SQUARE: return "sizesq";
		case INST_TYPE_SIZE_VERT: return "sizev";
		case INST_TYPE_SPACER: return "sp";
		case INST_TYPE_HORIZ_SPACER: return "hsp";
		case INST_TYPE_VERT_SPACER: return "vsp";
		case INST_TYPE_1PX_WIDTH: return "hsp1";
		case INST_TYPE_1PX_HEIGHT: return "vsp1";
		case INST_TYPE_NO_OP: return "nop";
		default:
			String h = "000"+Integer.toHexString(op).toUpperCase();
			return "unk"+h.substring(h.length()-3);
		}
	}
	
	private static String borderToString(int b) {
		switch (b) {
		case INST_BORDER_TLBR: return "[~_]";
		case INST_BORDER_LBR: return "[_]";
		case INST_BORDER_TBR: return "~_]";
		case INST_BORDER_BR: return "_]";
		case INST_BORDER_TLR: return "[~]";
		case INST_BORDER_LR: return "[]";
		case INST_BORDER_TR: return "~]";
		case INST_BORDER_R: return "]";
		case INST_BORDER_TLB: return "[~_";
		case INST_BORDER_LB: return "[_";
		case INST_BORDER_TB: return "~_";
		case INST_BORDER_B: return "_";
		case INST_BORDER_TL: return "[~";
		case INST_BORDER_L: return "[";
		case INST_BORDER_T: return "~";
		case INST_BORDER_NONE: return "x";
		default: return "[~_]";
		}
	}
	
	private static String hex(int v, int l) {
		String h = "0";
		while (h.length() < l) h += h;
		h += Integer.toHexString(v).toUpperCase();
		return h.substring(h.length()-l);
	}
	
	private static String hex(long v, int l) {
		String h = "0";
		while (h.length() < l) h += h;
		h += Long.toHexString(v).toUpperCase();
		return h.substring(h.length()-l);
	}
	
	private static final String[] LOOKUP_HEX = new String[] {
		"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
		"10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
		"20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
		"30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
		"40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
		"50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
		"60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
		"70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
		"80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
		"90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
		"A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
		"B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
		"C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
		"D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
		"E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
		"F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"
	};
	
	private static String binaryToString(byte[] theData) {
		if (theData == null) return "";
		StringBuffer theString = new StringBuffer(theData.length*2);
		for (byte b : theData) theString.append(LOOKUP_HEX[b & 0xFF]);
		return theString.toString();
	}
}
