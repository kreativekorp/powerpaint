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

package com.kreative.paint.format;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.io.Monitor;

public class SICFormat implements Format {
	public String getName() { return "Sabine Icon"; }
	public String getExpandedName() { return "Sabine Icon"; }
	public String getExtension() { return "sic"; }
	public int getMacFileType() { return 0x53494320; }
	public int getMacResourceType() { return 0x53494320; }
	public long getDFFType() { return 0x49636F6E20534943L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.INDEXED_OTHER; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE_AND_TRANSPARENT; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 0; }
	public boolean acceptsMagic(byte[] start, long length) { return false; }
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("sic"); }
	public boolean acceptsMacFileType(int type) { return type == 0x53494320 || type == 0x53494366 || type == 0x53494363; }
	public boolean acceptsMacResourceType(int type) { return type == 0x53494320 || type == 0x53494366 || type == 0x53494363; }
	public boolean acceptsDFFType(long type) { return type == 0x49636F6E20534943L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		int w = 24;
		int h = 24;
		int[] pixels = null;
		int pp = 0;
		int repeat = 0;
		while (pixels == null || pp < pixels.length) try {
			int b = in.readByte() & 0xFF;
			if (b < 0x20) {
				if (b >= 0x10) {
					if (repeat < 0) repeat = 0;
					repeat = (repeat << 4) | (b & 0xF);
				} else if (b == 0x0E && pixels == null) {
					w = 0;
					w = (w << 7) | (in.readByte() & 0x7F);
					w = (w << 7) | (in.readByte() & 0x7F);
					w = (w << 7) | (in.readByte() & 0x7F);
				} else if (b == 0x0F && pixels == null) {
					h = 0;
					h = (h << 7) | (in.readByte() & 0x7F);
					h = (h << 7) | (in.readByte() & 0x7F);
					h = (h << 7) | (in.readByte() & 0x7F);
				} else if (b == 0) {
					if (pixels == null) pixels = new int[w*h];
					if (repeat > 0) {
						while (repeat-->0) pixels[pp++] = 0xFF000000;
					} else {
						pixels[pp++] = 0xFF000000;
					}
				}
			} else {
				if (pixels == null) pixels = new int[w*h];
				int c = getRGB(b);
				if (repeat > 0) {
					while (repeat-->0) pixels[pp++] = c;
				} else {
					pixels[pp++] = c;
				}
			}
		} catch (EOFException eof) {
			if (pixels == null) pixels = new int[w*h];
		}
		Canvas c = new Canvas(w, h);
		c.get(0).setRGB(0, 0, w, h, pixels, 0, w);
		return c;
	}
	
	private static int colors = 84;
	private static boolean compressed = false;
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return true; }
	public Form getWriteOptionForm() {
		Form f = new Form();
		f.add(new IntegerEnumOption() {
			public String getName() { return FormatUtilities.messages.getString("sic.Colors"); }
			public int getValue() { return colors; }
			public void setValue(int v) { colors = v; }
			public int[] values() { return new int[] { 16, 84, 125 }; }
			public String getLabel(int v) {
				return FormatUtilities.messages.getString("sic.Colors." + v);
			}
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public boolean getValue() { return compressed; }
			public void setValue(boolean v) { compressed = v; }
			public boolean useTrueFalseLabels() { return false; }
			public String getLabel(boolean v) { return FormatUtilities.messages.getString("sic.Compressed"); }
		});
		return f;
	}
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight();
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		int w = c.getWidth();
		int h = c.getHeight();
		int d = colors;
		boolean k = compressed;
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		c.paint(g);
		g.dispose();
		int l = w*h;
		int[] pixels = new int[l];
		bi.getRGB(0, 0, w, h, pixels, 0, w);
		byte[] data = new byte[l];
		switch (d) {
		case 16:
			for (int i = 0; i < l; i++) data[i] = quantize16(pixels[i]);
			break;
		case 84:
			for (int i = 0; i < l; i++) data[i] = quantize84(pixels[i]);
			break;
		case 125:
			for (int i = 0; i < l; i++) data[i] = quantize125(pixels[i]);
			break;
		default:
			for (int i = 0; i < l; i++) data[i] = quantize84(pixels[i]);
			break;
		}
		if (k) {
			int sp = 0;
			int dp = 0;
			while (sp < l) {
				byte cv = data[sp++];
				int cr = 1;
				while (sp < l && data[sp] == cv) { sp++; cr++; }
				if (cr <= 2) {
					while (cr-->0) data[dp++] = cv;
				} else {
					if (cr >= 0x10000000) data[dp++] = (byte)(0x10 | ((cr >>> 28) & 0xF));
					if (cr >= 0x01000000) data[dp++] = (byte)(0x10 | ((cr >>> 24) & 0xF));
					if (cr >= 0x00100000) data[dp++] = (byte)(0x10 | ((cr >>> 20) & 0xF));
					if (cr >= 0x00010000) data[dp++] = (byte)(0x10 | ((cr >>> 16) & 0xF));
					if (cr >= 0x00001000) data[dp++] = (byte)(0x10 | ((cr >>> 12) & 0xF));
					if (cr >= 0x00000100) data[dp++] = (byte)(0x10 | ((cr >>>  8) & 0xF));
					if (cr >= 0x00000010) data[dp++] = (byte)(0x10 | ((cr >>>  4) & 0xF));
					if (cr >= 0x00000001) data[dp++] = (byte)(0x10 | ((cr >>>  0) & 0xF));
					data[dp++] = cv;
				}
			}
			l = dp;
		}
		if (w == 24 && h == 24) {
			out.write(data, 0, l);
		} else {
			out.writeByte(0x0E);
			out.writeByte(((w >>> 14) & 0x7F) | 0x80);
			out.writeByte(((w >>>  7) & 0x7F) | 0x80);
			out.writeByte(((w >>>  0) & 0x7F) | 0x80);
			out.writeByte(0x0F);
			out.writeByte(((h >>> 14) & 0x7F) | 0x80);
			out.writeByte(((h >>>  7) & 0x7F) | 0x80);
			out.writeByte(((h >>>  0) & 0x7F) | 0x80);
			out.write(data, 0, l);
		}
	}
	
	public static int getRGB(int i) {
		switch (i) {
		case 'T': return 0x00000000;
		case 't': return 0x00000000;
		
		case '+': return 0x80000000;
		case '4': return 0x80808080;
		case '7': return 0x80FFFFFF;
		
		case 'W': return 0xFFFFFFFF;
		case ' ': return 0xFFC0C0C0;
		case 'w': return 0xFFAAAAAA;
		case '`': return 0xFF808080;
		case 'k': return 0xFF555555;
		case '@': return 0xFF404040;
		case 'K': return 0xFF000000;
		
		case '?': return 0xFFEEEEEE;
		case '>': return 0xFFDDDDDD;
		case '=': return 0xFFCCCCCC;
		case '<': return 0xFFBBBBBB;
		case ';': return 0xFFAAAAAA;
		case '~': return 0xFF999999;
		case '}': return 0xFF888888;
		case '|': return 0xFF777777;
		case '{': return 0xFF666666;
		case '_': return 0xFF555555;
		case '^': return 0xFF444444;
		case ']': return 0xFF333333;
		case'\\': return 0xFF222222;
		case '[': return 0xFF111111;
		
		case 'R': return 0xFF800000;
		case 'r': return 0xFFFF0000;
		case '2': return 0xFFFF8080;
		
		case 'O': return 0xFF804000;
		case 'o': return 0xFFFF8000;
		case '/': return 0xFFFFC080;
		
		case 'Y': return 0xFF808000;
		case 'y': return 0xFFFFFF00;
		case '9': return 0xFFFFFF80;
		
		case 'L': return 0xFF408000;
		case 'l': return 0xFF80FF00;
		case ',': return 0xFFC0FF80;
		
		case 'G': return 0xFF008000;
		case 'g': return 0xFF00FF00;
		case'\'': return 0xFF80FF80;
		
		case 'A': return 0xFF008040;
		case 'a': return 0xFF00FF80;
		case '!': return 0xFF80FFC0;
		
		case 'C': return 0xFF008080;
		case 'c': return 0xFF00FFFF;
		case '#': return 0xFF80FFFF;
		
		case 'D': return 0xFF004080;
		case 'd': return 0xFF0080FF;
		case '$': return 0xFF80C0FF;
		
		case 'B': return 0xFF000080;
		case 'b': return 0xFF0000FF;
		case'\"': return 0xFF8080FF;
		
		case 'V': return 0xFF400080;
		case 'v': return 0xFF8000FF;
		case '6': return 0xFFC080FF;
		
		case 'P': return 0xFF800080;
		case 'p': return 0xFFFF00FF;
		case '0': return 0xFFFF80FF;
		
		case 'M': return 0xFF800080;
		case 'm': return 0xFFFF00FF;
		case '-': return 0xFFFF80FF;
		
		case 'S': return 0xFF800040;
		case 's': return 0xFFFF0080;
		case '3': return 0xFFFF80C0;
		
		case 'Q': return 0xFF663300;
		case 'q': return 0xFF996633;
		case '1': return 0xFFCC9966;
		
		case 'U': return 0xFF660099;
		case 'u': return 0xFF9933CC;
		case '5': return 0xFFCC66FF;
		
		case 'E': return 0xFF806C00;
		case 'e': return 0xFFFFDD00;
		case '%': return 0xFFFFEE80;
		
		case 'F': return 0xFFCC9966;
		case 'f': return 0xFFFFDDAA;
		case '&': return 0xFFFFEEDD;
		
		case 'N': return 0xFF6666CC;
		case 'n': return 0xFF9999FF;
		case '.': return 0xFFCCCCFF;
		
		case 'H': return 0xFF990011;
		case 'h': return 0xFFFF0077;
		case '(': return 0xFFFF66DD;
		
		case 'Z': return 0xFF999966;
		case 'z': return 0xFFEEDD99;
		case ':': return 0xFFFFFFDD;
		
		case 'I': return 0xFF7799CC;
		case 'i': return 0xFFAAEEFF;
		case ')': return 0xFFDDFFFF;
		
		case 'J': return 0xFFAA88CC;
		case 'j': return 0xFFDDBBFF;
		case '*': return 0xFFFFEEFF;
		
		case 'X': return 0xFF806000;
		case 'x': return 0xFFFFC000;
		case '8': return 0xFFFFE080;
		}
		
		if (i >= 128 && i < 253) {
			int r = ((i-128) % 5) * 0x40; if (r > 0xFF) r = 0xFF;
			int g = (((i-128) / 5) % 5) * 0x40; if (g > 0xFF) g = 0xFF;
			int b = (((i-128) / 25) % 5) * 0x40; if (b > 0xFF) b = 0xFF;
			return 0xFF000000 | (r << 16) | (g << 8) | b;
		}
		
		return 0;
	}
	
	public static Color getColor(int i) {
		return new Color(getRGB(i),true);
	}
	
	private static final byte[] QUANTIZE_16_PALETTE = new byte[] {
		'K','k','w','W','R','r','Y','y','G','g','C','c','B','b','P','p'
	};
	public static byte quantize16(int color) {
		int a = (color >>> 24) & 0xFF;
		int r = (color >>> 16) & 0xFF;
		int g = (color >>>  8) & 0xFF;
		int b = (color >>>  0) & 0xFF;
		if (a < 0x80) {
			return 'T';
		} else {
			byte q = 'T';
			int diff = Integer.MAX_VALUE;
			for (byte pq : QUANTIZE_16_PALETTE) {
				int pc = getRGB(pq & 0xFF);
				int pa = (pc >>> 24) & 0xFF;
				if (pa < 0xAA) continue;
				int pr = (pc >>> 16) & 0xFF;
				int pg = (pc >>>  8) & 0xFF;
				int pb = (pc >>>  0) & 0xFF;
				int pd = Math.abs(pr-r) + Math.abs(pg-g) + Math.abs(pb-b);
				if (pd < diff) {
					q = pq;
					diff = pd;
				}
			}
			return q;
		}
	}
	
	public static byte quantize84(int color) {
		int a = (color >>> 24) & 0xFF;
		int r = (color >>> 16) & 0xFF;
		int g = (color >>>  8) & 0xFF;
		int b = (color >>>  0) & 0xFF;
		if (a < 0x55) {
			return 'T';
		} else if (a < 0xAA) {
			int k = (30*r + 59*g + 11*b) / 100;
			if (k < 0x55) return '+';
			else if (k < 0xAA) return '4';
			else return '7';
		} else {
			byte q = 'T';
			int diff = Integer.MAX_VALUE;
			for (byte pq = 0x20; pq < 0x7F; pq++) {
				int pc = getRGB(pq & 0xFF);
				int pa = (pc >>> 24) & 0xFF;
				if (pa < 0xAA) continue;
				int pr = (pc >>> 16) & 0xFF;
				int pg = (pc >>>  8) & 0xFF;
				int pb = (pc >>>  0) & 0xFF;
				int pd = Math.abs(pr-r) + Math.abs(pg-g) + Math.abs(pb-b);
				if (pd < diff) {
					q = pq;
					diff = pd;
				}
			}
			return q;
		}
	}
	
	public static byte quantize125(int color) {
		int a = (color >>> 24) & 0xFF;
		int r = (color >>> 16) & 0xFF;
		int g = (color >>>  8) & 0xFF;
		int b = (color >>>  0) & 0xFF;
		if (a < 0x55) {
			return 'T';
		} else if (a < 0xAA) {
			int k = (30*r + 59*g + 11*b) / 100;
			if (k < 0x55) return '+';
			else if (k < 0xAA) return '4';
			else return '7';
		} else {
			int q = 128;
			if (r < 0x33) q += 0;
			else if (r < 0x66) q += 1;
			else if (r < 0x99) q += 2;
			else if (r < 0xCC) q += 3;
			else q += 4;
			if (g < 0x33) q += 0;
			else if (g < 0x66) q += 5;
			else if (g < 0x99) q += 10;
			else if (g < 0xCC) q += 15;
			else q += 20;
			if (b < 0x33) q += 0;
			else if (b < 0x66) q += 25;
			else if (b < 0x99) q += 50;
			else if (b < 0xCC) q += 75;
			else q += 100;
			return (byte)q;
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			JFrame f = new JFrame("Sabine Color Palette");
			JPanel p = new JPanel(new GridLayout(0,32,2,2));
			for (int b=64; b<96; b++) {
				JLabel l = new JLabel(new String(new char[]{(char)b}));
				l.setOpaque(true);
				l.setBackground(getColor(b));
				l.setHorizontalAlignment(JLabel.CENTER);
				p.add(l);
			}
			for (int b=96; b<128; b++) {
				JLabel l = new JLabel(new String(new char[]{(char)b}));
				l.setOpaque(true);
				l.setBackground(getColor(b));
				l.setHorizontalAlignment(JLabel.CENTER);
				p.add(l);
			}
			for (int b=32; b<64; b++) {
				JLabel l = new JLabel(new String(new char[]{(char)b}));
				l.setOpaque(true);
				l.setBackground(getColor(b));
				l.setHorizontalAlignment(JLabel.CENTER);
				p.add(l);
			}
			for (int b=128; b<256; b++) {
				JLabel l = new JLabel(new String(new char[]{(char)b}));
				l.setOpaque(true);
				l.setBackground(getColor(b));
				l.setHorizontalAlignment(JLabel.CENTER);
				p.add(l);
			}
			p.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			f.setContentPane(p);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.pack();
			f.setVisible(true);
		} else {
			for (int i=0; i<args.length; i++) {
				try {
					RandomAccessFile raf = new RandomAccessFile(args[i],"r");
					byte[] stuff = new byte[(int)raf.length()];
					raf.read(stuff);
					raf.close();
					int s = (int)Math.ceil(Math.sqrt(stuff.length));
					JFrame f = new JFrame(args[i]);
					JPanel p = new JPanel(new GridLayout(s,s,0,0));
					for (int j=0; j<stuff.length; j++) {
						JPanel l = new JPanel();
						l.setOpaque(true);
						l.setBackground(getColor(stuff[j]));
						p.add(l);
					}
					f.setContentPane(p);
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					f.pack();
					f.setVisible(true);
				} catch (IOException ioe) {
					System.out.println("Could not read "+args[i]+".");
				}
			}
		}
	}
}
