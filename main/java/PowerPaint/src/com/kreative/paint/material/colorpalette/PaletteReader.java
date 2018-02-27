package com.kreative.paint.material.colorpalette;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class PaletteReader {
	public abstract RCPXPalette read(String name, InputStream in) throws IOException;
	
	public static class RCPXReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			return RCPXParser.parse(name, in);
		}
	}
	
	public static class ACTReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			for (int i = 0; i < 256; i++) {
				int r = in.read(); if (r < 0) break;
				int g = in.read(); if (g < 0) break;
				int b = in.read(); if (b < 0) break;
				colors.add(new RCPXColor.RGB(r, g, b, null));
			}
			int n = in.read() << 8; n |= in.read();
			if (n >= 0 && n < colors.size()) {
				colors.subList(n, colors.size()).clear();
			}
			int t = in.read() << 8; t |= in.read();
			if (t >= 0 && t < colors.size()) {
				RCPXColor.RGB rgb = (RCPXColor.RGB)colors.get(t);
				colors.set(t, new RCPXColor.RGBA(rgb.r, rgb.g, rgb.b, 0, null));
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
	
	public static class ACOReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			while (true) {
				int v = in.read() << 8; v |= in.read(); if (v < 0) break;
				int n = in.read() << 8; n |= in.read(); if (n < 0) break;
				colors.clear();
				for (int i = 0; i < n; i++) {
					int s = in.read() << 8; s |= in.read(); if (s < 0) break;
					int w = in.read() << 8; w |= in.read(); if (w < 0) break;
					int x = in.read() << 8; x |= in.read(); if (x < 0) break;
					int y = in.read() << 8; y |= in.read(); if (y < 0) break;
					int z = in.read() << 8; z |= in.read(); if (z < 0) break;
					String cn = null;
					if (v >= 2) {
						int cnl = in.read() << 24; cnl |= in.read() << 16;
						cnl |= in.read() << 8; cnl |= in.read(); if (cnl < 0) break;
						StringBuffer sb = new StringBuffer(cnl);
						for (int j = 0; j < cnl; j++) {
							int ch = in.read(); ch |= in.read(); if (ch <= 0) break;
							sb.append((char)ch);
						}
						if (sb.length() > 0) {
							cn = sb.toString();
							if (cn.startsWith("$$$")) {
								int o = cn.lastIndexOf('=');
								if (o >= 0) cn = cn.substring(o + 1);
							}
						}
					}
					switch (s) {
						case 0: colors.add(new RCPXColor.RGB16(w, x, y, cn)); break;
						case 1: colors.add(new RCPXColor.HSV(w*360f/65536f, x*100f/65535f, y*100f/65535f, cn)); break;
						case 2: colors.add(new RCPXColor.CMYK((65535-w)*100f/65535f, (65535-x)*100f/65535f, (65535-y)*100f/65535f, (65535-z)*100f/65535f, cn)); break;
						case 7: colors.add(new RCPXColor.CIELab(w/100f, ((x<<16)>>16)/100f, ((y<<16)>>16)/100f, cn)); break;
						case 8: colors.add(new RCPXColor.Gray(w/100f, cn)); break;
						case 9: colors.add(new RCPXColor.CMYK(w/100f, x/100f, y/100f, z/100f, cn)); break;
						default: System.err.println("Warning: Unknown color space " + s + " in ACO " + name + "."); break;
					}
				}
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, false);
		}
	}
	
	public static class ASEReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			List<String> blockNames = new ArrayList<String>();
			DataInputStream din = new DataInputStream(in);
			if (din.readInt() != 0x41534546) throw new IOException("Bad magic number");
			if (din.readInt() != 0x00010000) throw new IOException("Bad version number");
			int blockCount = din.readInt();
			for (int i = 0; i < blockCount; i++) {
				int blockType = din.readUnsignedShort();
				int blockLength = din.readInt();
				byte[] blockData = new byte[blockLength];
				din.readFully(blockData);
				if (blockType == 0xC001 || blockType == 0x0001) {
					DataInputStream bin = new DataInputStream(new ByteArrayInputStream(blockData));
					int nameLength = bin.readUnsignedShort();
					StringBuffer cn = new StringBuffer(nameLength);
					for (int j = 0; j < nameLength; j++) {
						int ch = bin.readUnsignedShort();
						if (ch != 0) cn.append((char)ch);
					}
					if (blockType == 0xC001) {
						blockNames.add(cn.toString());
					} else if (blockType == 0x0001) {
						int colorSpace = bin.readInt();
						switch (colorSpace) {
							case 0x52474220: // RGB
								float r = bin.readFloat();
								float g = bin.readFloat();
								float b = bin.readFloat();
								colors.add(new RCPXColor.RGBD(r, g, b, cn.toString()));
								break;
							case 0x434D594B: // CMYK
								float c = bin.readFloat() * 100f;
								float m = bin.readFloat() * 100f;
								float y = bin.readFloat() * 100f;
								float k = bin.readFloat() * 100f;
								colors.add(new RCPXColor.CMYK(c, m, y, k, cn.toString()));
								break;
							case 0x4C414220: // LAB
								float ll = bin.readFloat() * 100f;
								float aa = bin.readFloat() * 100f;
								float bb = bin.readFloat() * 100f;
								colors.add(new RCPXColor.CIELab(ll, aa, bb, cn.toString()));
								break;
							case 0x47726179: // Gray
								float w = bin.readFloat() * 100f;
								colors.add(new RCPXColor.Gray(w, cn.toString()));
								break;
						}
					}
				}
			}
			if (blockNames.size() == 1) name = blockNames.get(0);
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, false);
		}
	}
	
	public static class ACBReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			DataInputStream din = new DataInputStream(in);
			if (din.readInt() != 0x38424342) throw new IOException("Bad magic number");
			if (din.readShort() != 1) throw new IOException("Bad version number");
			din.readShort(); // id
			String title = readString(din);
			String prefix = readString(din);
			String suffix = readString(din);
			readString(din); // description
			int n = din.readUnsignedShort();
			din.readShort(); // page size
			din.readShort(); // page selector offset
			int colorspace = din.readShort();
			for (int i = 0; i < n; i++) {
				String cn = readString(din);
				if (cn != null) {
					if (prefix != null) cn = prefix + cn;
					if (suffix != null) cn = cn + suffix;
				}
				din.readShort(); // color
				din.readShort(); // catalog
				din.readShort(); // code
				switch (colorspace) {
					case 0:
						int r = din.readUnsignedByte();
						int g = din.readUnsignedByte();
						int b = din.readUnsignedByte();
						if (cn != null) colors.add(new RCPXColor.RGB(r, g, b, cn));
						break;
					case 2:
						float c = (255 - din.readUnsignedByte()) * 100f / 255f;
						float m = (255 - din.readUnsignedByte()) * 100f / 255f;
						float y = (255 - din.readUnsignedByte()) * 100f / 255f;
						float k = (255 - din.readUnsignedByte()) * 100f / 255f;
						if (cn != null) colors.add(new RCPXColor.CMYK(c, m, y, k, cn));
						break;
					case 7:
						float ll = din.readUnsignedByte() * 100f / 255f;
						float aa = din.readUnsignedByte() - 128;
						float bb = din.readUnsignedByte() - 128;
						if (cn != null) colors.add(new RCPXColor.CIELab(ll, aa, bb, cn));
						break;
					default:
						throw new IOException("Unknown color space " + colorspace);
				}
			}
			if (title != null) name = title;
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, false);
		}
		private String readString(DataInputStream din) throws IOException {
			int n = din.readInt();
			if (n <= 0) return null;
			StringBuffer sb = new StringBuffer(n);
			for (int i = 0; i < n; i++) sb.append(din.readChar());
			String s = sb.toString();
			if (s.startsWith("$$$")) {
				int o = s.lastIndexOf('=');
				if (o >= 0) s = s.substring(o + 1);
			}
			s = s.replace("^C", "\u00A9");
			s = s.replace("^R", "\u00AE");
			s = s.replace("^^", "^");
			return s;
		}
	}
	
	public static class GPLReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			Scanner s = new Scanner(in, "UTF-8");
			while (s.hasNextLine()) {
				String line = s.nextLine().trim();
				if (line.startsWith("Name:")) {
					name = line.substring(5).trim();
				} else {
					String[] f = line.split("\\s+", 4);
					if (f.length < 3) continue;
					try {
						int r = Integer.parseInt(f[0]);
						int g = Integer.parseInt(f[1]);
						int b = Integer.parseInt(f[2]);
						String n = (f.length > 3) ? f[3] : null;
						colors.add(new RCPXColor.RGB(r, g, b, n));
					} catch (NumberFormatException e) {
						continue;
					}
				}
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
	
	public static class PALReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			Scanner s = new Scanner(in, "UTF-8");
			if (!s.hasNextLine() || !s.nextLine().trim().equals("JASC-PAL")) throw new IOException("Bad magic number");
			if (!s.hasNextLine() || !s.nextLine().trim().equals("0100")) throw new IOException("Bad version number");
			if (!s.hasNextLine()) throw new IOException("Bad color count");
			int n; try { n = Integer.parseInt(s.nextLine().trim()); }
			catch (NumberFormatException e) { throw new IOException("Bad color count"); }
			for (int i = 0; i < n; i++) {
				if (!s.hasNextLine()) throw new IOException("Bad color line");
				String[] f = s.nextLine().trim().split("\\s+");
				if (f.length != 3) throw new IOException("Bad color line");
				try {
					int r = Integer.parseInt(f[0]);
					int g = Integer.parseInt(f[1]);
					int b = Integer.parseInt(f[2]);
					colors.add(new RCPXColor.RGB(r, g, b, null));
				} catch (NumberFormatException e) {
					throw new IOException("Bad color line");
				}
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
	
	public static class CLUTReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			DataInputStream din = new DataInputStream(in);
			din.readInt(); // seed
			din.readUnsignedShort(); // flags
			int n = din.readShort() + 1;
			for (int i = 0; i < n; i++) {
				din.readUnsignedShort(); // index
				int r = din.readUnsignedShort();
				int g = din.readUnsignedShort();
				int b = din.readUnsignedShort();
				colors.add(new RCPXColor.RGB16(r, g, b, null));
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
	
	public static class PLTTReader extends PaletteReader {
		public RCPXPalette read(String name, InputStream in) throws IOException {
			List<RCPXColor> colors = new ArrayList<RCPXColor>();
			DataInputStream din = new DataInputStream(in);
			int n = din.readShort();
			din.readInt(); // reserved
			din.readShort(); // reserved
			din.readInt(); // reserved
			din.readInt(); // reserved
			for (int i = 0; i < n; i++) {
				int r = din.readUnsignedShort();
				int g = din.readUnsignedShort();
				int b = din.readUnsignedShort();
				din.readUnsignedShort(); // usage
				din.readUnsignedShort(); // tolerance
				din.readUnsignedShort(); // flags
				din.readInt(); // reserved
				colors.add(new RCPXColor.RGB16(r, g, b, null));
			}
			PaletteDimensions pd = PaletteDimensions.forColorCount(colors.size());
			return pd.createPalette(name, RCPXOrientation.HORIZONTAL, colors, true);
		}
	}
}
