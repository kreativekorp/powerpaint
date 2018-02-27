package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class PaletteWriter {
	public abstract boolean isCompatible(RCPXPalette pal);
	public abstract void write(RCPXPalette pal, OutputStream out) throws IOException;
	
	public static class RCPXWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return true;
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			RCPXGenerator.generate(pal, out);
		}
	}
	
	public static class ACTWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			int n = pal.colors.size();
			return (n > 0 && n <= 256);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			int t = -1;
			int n = pal.colors.size();
			if (n > 256) n = 256;
			for (int i = 0; i < n; i++) {
				Color color = pal.colors.get(i).awtColor();
				if (color.getAlpha() < 128) t = i;
				out.write(color.getRed());
				out.write(color.getGreen());
				out.write(color.getBlue());
			}
			for (int i = n; i < 256; i++) {
				out.write(-1);
				out.write(-1);
				out.write(-1);
			}
			if (n < 256 || t >= 0) {
				out.write(n >> 8);
				out.write(n);
				out.write(t >> 8);
				out.write(t);
			}
		}
	}
	
	public static class ACOWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			boolean hasNames = false;
			int n = pal.colors.size();
			out.write(0); out.write(1);
			out.write(n >> 8); out.write(n);
			for (int i = 0; i < n; i++) {
				RCPXColor color = pal.colors.get(i);
				write(color, out);
				String cn = color.name();
				if (cn != null && cn.length() > 0) hasNames = true;
			}
			if (hasNames) {
				out.write(0); out.write(2);
				out.write(n >> 8); out.write(n);
				for (int i = 0; i < n; i++) {
					RCPXColor color = pal.colors.get(i);
					write(color, out);
					String cn = color.name();
					int cnl = (cn == null) ? 1 : (cn.length() + 1);
					out.write(cnl >> 24); out.write(cnl >> 16);
					out.write(cnl >> 8); out.write(cnl);
					if (cn != null) {
						for (char ch : cn.toCharArray()) {
							out.write(ch >> 8); out.write(ch);
						}
					}
					out.write(0); out.write(0);
				}
			}
		}
		private void write(RCPXColor color, OutputStream out) throws IOException {
			if (color instanceof RCPXColor.HSV) {
				RCPXColor.HSV hsv = (RCPXColor.HSV)color;
				int h = (int)Math.round(hsv.h * 65536f / 360f);
				int s = (int)Math.round(hsv.s * 65535f / 100f);
				int v = (int)Math.round(hsv.v * 65535f / 100f);
				out.write(0); out.write(1);
				out.write(h >> 8); out.write(h);
				out.write(s >> 8); out.write(s);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.HSVA) {
				RCPXColor.HSVA hsv = (RCPXColor.HSVA)color;
				int h = (int)Math.round(hsv.h * 65536f / 360f);
				int s = (int)Math.round(hsv.s * 65535f / 100f);
				int v = (int)Math.round(hsv.v * 65535f / 100f);
				out.write(0); out.write(1);
				out.write(h >> 8); out.write(h);
				out.write(s >> 8); out.write(s);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.CMYK) {
				RCPXColor.CMYK cmyk = (RCPXColor.CMYK)color;
				int c = 65535 - (int)Math.round(cmyk.c * 65535f / 100f);
				int m = 65535 - (int)Math.round(cmyk.m * 65535f / 100f);
				int y = 65535 - (int)Math.round(cmyk.y * 65535f / 100f);
				int k = 65535 - (int)Math.round(cmyk.k * 65535f / 100f);
				out.write(0); out.write(2);
				out.write(c >> 8); out.write(c);
				out.write(m >> 8); out.write(m);
				out.write(y >> 8); out.write(y);
				out.write(k >> 8); out.write(k);
			} else if (color instanceof RCPXColor.CIELab) {
				RCPXColor.CIELab lab = (RCPXColor.CIELab)color;
				int l = (int)Math.round(lab.l * 100f);
				int a = (int)Math.round(lab.a * 100f);
				int b = (int)Math.round(lab.b * 100f);
				out.write(0); out.write(7);
				out.write(l >> 8); out.write(l);
				out.write(a >> 8); out.write(a);
				out.write(b >> 8); out.write(b);
				out.write(0); out.write(0);
			} else if (color instanceof RCPXColor.Gray) {
				RCPXColor.Gray gray = (RCPXColor.Gray)color;
				int v = (int)Math.round(gray.gray * 100f);
				out.write(0); out.write(8);
				out.write(v >> 8); out.write(v);
				out.write(0); out.write(0);
				out.write(0); out.write(0);
				out.write(0); out.write(0);
			} else {
				float[] rgb = color.awtColor().getRGBColorComponents(null);
				int r = (int)Math.round(rgb[0] * 65535f);
				int g = (int)Math.round(rgb[1] * 65535f);
				int b = (int)Math.round(rgb[2] * 65535f);
				out.write(0); out.write(0);
				out.write(r >> 8); out.write(r);
				out.write(g >> 8); out.write(g);
				out.write(b >> 8); out.write(b);
				out.write(0); out.write(0);
			}
		}
	}
	
	public static class ASEWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			List<byte[]> blocks = new ArrayList<byte[]>();
			if (pal.name != null) blocks.add(block(0xC001, groupBlockData(pal.name)));
			for (RCPXColor color : pal.colors) blocks.add(block(0x0001, colorBlockData(color)));
			if (pal.name != null) blocks.add(block(0xC002, null));
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeInt(0x41534546);
			dout.writeInt(0x00010000);
			dout.writeInt(blocks.size());
			for (byte[] block : blocks) dout.write(block);
			dout.flush();
		}
		private byte[] block(int type, byte[] data) throws IOException {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			dout.writeShort(type);
			if (data == null) {
				dout.writeInt(0);
			} else {
				dout.writeInt(data.length);
				dout.write(data);
			}
			dout.close();
			bout.close();
			return bout.toByteArray();
		}
		private byte[] groupBlockData(String name) throws IOException {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			if (name == null) {
				dout.writeShort(0);
			} else {
				dout.writeShort(name.length() + 1);
				for (char ch : name.toCharArray()) dout.writeShort(ch);
				dout.writeShort(0);
			}
			dout.close();
			bout.close();
			return bout.toByteArray();
		}
		private byte[] colorBlockData(RCPXColor color) throws IOException {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			String name = color.name();
			if (name == null) {
				dout.writeShort(0);
			} else {
				dout.writeShort(name.length() + 1);
				for (char ch : name.toCharArray()) dout.writeShort(ch);
				dout.writeShort(0);
			}
			if (color instanceof RCPXColor.CMYK) {
				RCPXColor.CMYK cmyk = (RCPXColor.CMYK)color;
				dout.writeInt(0x434D594B); // CMYK
				dout.writeFloat(cmyk.c / 100f);
				dout.writeFloat(cmyk.m / 100f);
				dout.writeFloat(cmyk.y / 100f);
				dout.writeFloat(cmyk.k / 100f);
			} else if (color instanceof RCPXColor.CIELab) {
				RCPXColor.CIELab lab = (RCPXColor.CIELab)color;
				dout.writeInt(0x4C414220); // LAB
				dout.writeFloat(lab.l / 100f);
				dout.writeFloat(lab.a / 100f);
				dout.writeFloat(lab.b / 100f);
			} else if (color instanceof RCPXColor.Gray) {
				RCPXColor.Gray gray = (RCPXColor.Gray)color;
				dout.writeInt(0x47726179); // Gray
				dout.writeFloat(gray.gray / 100f);
			} else {
				float[] rgb = color.awtColor().getRGBColorComponents(null);
				dout.writeInt(0x52474220); // RGB
				dout.writeFloat(rgb[0]);
				dout.writeFloat(rgb[1]);
				dout.writeFloat(rgb[2]);
			}
			dout.writeShort(2);
			dout.close();
			bout.close();
			return bout.toByteArray();
		}
	}
	
	public static class ACBWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			int colorspace = pal.colors.isEmpty() ? 0 : -1;
			for (RCPXColor color : pal.colors) {
				if (color instanceof RCPXColor.CIELab) {
					if (colorspace == -1) colorspace = 7;
					else if (colorspace != 7) colorspace = 0;
				} else if (color instanceof RCPXColor.CMYK) {
					if (colorspace == -1) colorspace = 2;
					else if (colorspace != 2) colorspace = 0;
				} else {
					colorspace = 0;
				}
			}
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeInt(0x38424342); // magic
			dout.writeShort(1); // version
			dout.writeShort(0); // id
			writeString(pal.name, dout);
			dout.writeInt(0); // prefix
			dout.writeInt(0); // suffix
			dout.writeInt(0); // description
			dout.writeShort(pal.colors.size());
			dout.writeShort(0xCCCC); // page size
			dout.writeShort(0xCCCC); // page selector offset
			dout.writeShort(colorspace);
			int index = 0;
			for (RCPXColor color : pal.colors) {
				String cn = color.name();
				if (cn == null || cn.length() == 0) cn = "#" + index;
				writeString(cn, dout);
				dout.writeShort(0x2020); // color
				dout.writeShort(0x2020); // catalog
				dout.writeShort(0x2020); // code
				switch (colorspace) {
					default:
						Color rgb = color.awtColor();
						dout.writeByte(rgb.getRed());
						dout.writeByte(rgb.getGreen());
						dout.writeByte(rgb.getBlue());
						break;
					case 2:
						RCPXColor.CMYK cmyk = (RCPXColor.CMYK)color;
						dout.writeByte(255 - (int)Math.round(cmyk.c * 255f / 100f));
						dout.writeByte(255 - (int)Math.round(cmyk.m * 255f / 100f));
						dout.writeByte(255 - (int)Math.round(cmyk.y * 255f / 100f));
						dout.writeByte(255 - (int)Math.round(cmyk.k * 255f / 100f));
						break;
					case 7:
						RCPXColor.CIELab lab = (RCPXColor.CIELab)color;
						dout.writeByte((int)Math.round(lab.l * 255f / 100f));
						dout.writeByte((int)Math.round(lab.a) + 128);
						dout.writeByte((int)Math.round(lab.b) + 128);
						break;
				}
				index++;
			}
			dout.flush();
		}
		private void writeString(String s, DataOutputStream out) throws IOException {
			if (s == null) {
				out.writeInt(0);
			} else {
				s = s.replace("^", "^^");
				s = s.replace("\u00A9", "^C");
				s = s.replace("\u00AE", "^R");
				char[] ca = s.toCharArray();
				out.writeInt(ca.length);
				for (char ch : ca) out.writeChar(ch);
			}
		}
	}
	
	public static class GPLWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
			pw.println("GIMP Palette");
			if (pal.name != null && pal.name.length() > 0) pw.println("Name: " + pal.name);
			for (RCPXColor color : pal.colors) {
				Color c = color.awtColor();
				String r = "   " + c.getRed()  ; r = r.substring(r.length() - 3);
				String g = "   " + c.getGreen(); g = g.substring(g.length() - 3);
				String b = "   " + c.getBlue() ; b = b.substring(b.length() - 3);
				String s = r + " " + g + " " + b;
				String n = color.name();
				if (n != null && n.length() > 0) s += "\t" + n;
				pw.println(s);
			}
			pw.flush();
		}
	}
	
	public static class PALWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
			pw.println("JASC-PAL");
			pw.println("0100");
			pw.println(pal.colors.size());
			for (RCPXColor color : pal.colors) {
				Color c = color.awtColor();
				String s = c.getRed() + " " + c.getGreen() + " " + c.getBlue();
				pw.println(s);
			}
			pw.flush();
		}
	}
	
	public static class CLUTWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			DataOutputStream dout = new DataOutputStream(out);
			int n = pal.colors.size();
			dout.writeInt(0); // seed
			dout.writeShort(0); // flags
			dout.writeShort(n - 1);
			for (int i = 0; i < n; i++) {
				Color color = pal.colors.get(i).awtColor();
				float[] rgb = color.getRGBColorComponents(null);
				dout.writeShort(i);
				dout.writeShort((int)Math.round(rgb[0] * 65535f));
				dout.writeShort((int)Math.round(rgb[1] * 65535f));
				dout.writeShort((int)Math.round(rgb[2] * 65535f));
			}
			dout.flush();
		}
	}
	
	public static class PLTTWriter extends PaletteWriter {
		public boolean isCompatible(RCPXPalette pal) {
			return (pal.colors.size() > 0);
		}
		public void write(RCPXPalette pal, OutputStream out) throws IOException {
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeShort(pal.colors.size());
			dout.writeInt(0); // reserved
			dout.writeShort(0); // reserved
			dout.writeInt(0); // reserved
			dout.writeInt(0); // reserved
			for (RCPXColor color : pal.colors) {
				float[] rgb = color.awtColor().getRGBColorComponents(null);
				dout.writeShort((int)Math.round(rgb[0] * 65535f));
				dout.writeShort((int)Math.round(rgb[1] * 65535f));
				dout.writeShort((int)Math.round(rgb[2] * 65535f));
				dout.writeShort(0); // usage
				dout.writeShort(0); // tolerance
				dout.writeShort(0); // flags
				dout.writeInt(0); // reserved
			}
			dout.flush();
		}
	}
}
