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

package com.kreative.paint.res;

public enum ResourceCategory {
	COLORS		("RCP ","RsplCPlt","Color Palettes"),
	FONTSETS	("RFP ","FontList","Font Collections"),
	PATTERNS	("PAT#","Mac PAT#","Patterns"),
	FONTS		("sfnt","Font TTF","Fonts"),
	ALPHABETS	("ppAL","CKPAlpha","Alphabets"),
	BRUSHES		("ppBR","CKPBrush","Brushes"),
	CALLIGRAPHY	("ppCB","CKPCalBr","Calligraphy Brushes"),
	CHARCOALS	("ppCC","CKPChrcl","Charcoal Brushes"),
	DITHERERS	("ppDI","CKPDithr","Dither Algorithms"),
	FORMATS		("ppFF","CKPFlFmt","File Formats"),
	FILTERS		("ppFL","CKPFiltr","Filters"),
	FRAMES		("ppFR","CKPFrame","Frames"),
	GRADIENTS	("ppGR","CKPGrdnt","Gradients"),
	LINES		("ppLN","CKPLines","Lines"),
	SHAPES		("ppSH","CKPShape","Shapes"),
	SPRINKLES	("ppSP","CKPSpnkl","Sprinkles"),
	STAMPS		("ppRS","CKPStamp","Rubber Stamps"),
	TEXTURES	("ppTX","CKPTextr","Textures"),
	TOOLS		("ppTL","CKPTool ","Tools"),
	OPTIONS		("ppOP","CKPOptns","Tool Options");
	
	private int resType;
	private long dffType;
	private String directoryName;
	
	private ResourceCategory(int rt, long dt, String dir) {
		this.resType = rt;
		this.dffType = dt;
		this.directoryName = dir;
	}
	
	private ResourceCategory(String rt, String dt, String dir) {
		this.resType = fcc(rt);
		this.dffType = ecc(dt);
		this.directoryName = dir;
	}
	
	public int getResType() {
		return resType;
	}
	
	public long getDFFType() {
		return dffType;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	private static int fcc(byte[] type) {
		switch (type.length) {
		case 0:
			return 0x20202020;
		case 1:
			return (((type[0] & 0xFF)<<24)|0x202020);
		case 2:
			return (((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<16)|0x2020);
		case 3:
			return (((((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<8)|(type[2] & 0xFF))<<8)|0x20);
		default:
			return (((((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<8)|(type[2] & 0xFF))<<8)|(type[3] & 0xFF));
		}
	}
	
	private static int fcc(String type) {
		try {
			return fcc(type.getBytes("MACROMAN"));
		} catch (java.io.UnsupportedEncodingException e) {
			return fcc(type.getBytes());
		}
	}
	
	private static long ecc(byte[] type) {
		long l = 0l;
		int i = 0;
		while (i < type.length && i < 8) {
			l <<= 8;
			l |= (type[i] & 0xFFl);
			i++;
		}
		while (i < 8) {
			l <<= 8;
			l |= 0x20l;
			i++;
		}
		return l;
	}
	
	private static long ecc(String type) {
		try {
			if (type.length() > 4) return ecc(type.getBytes("ISO-8859-1"));
			else return ecc(type.getBytes("UTF-16BE"));
		} catch (java.io.UnsupportedEncodingException e) {
			return ecc(type.getBytes());
		}
	}
}
