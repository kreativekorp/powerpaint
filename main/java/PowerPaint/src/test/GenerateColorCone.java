package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GenerateColorCone {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			int s = Integer.parseInt(arg);
			File file = new File(conePaletteFileName(s));
			FileOutputStream fout = new FileOutputStream(file);
			PrintStream out = new PrintStream(fout);
			generateConePalette(out, s);
			out.flush();
			out.close();
			fout.close();
		}
	}
	
	private static String conePaletteFileName(int s) {
		String ss = "00" + s;
		ss = ss.substring(ss.length() - 2);
		ss = "1" + ss + "Conic";
		switch (s) {
			case 2: ss += "Process"; break;
			case 6: ss += "WebSafe"; break;
		}
		return ss + ".rcpx";
	}
	
	private static void generateConePalette(PrintStream out, int s) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE palette PUBLIC \"-//Kreative//DTD ResplendentColor 1.0//EN\" \"rcpx.dtd\">");
		out.println("<palette name=\"" + conePaletteName(s) + "\"" + conePaletteSize(s) + ">");
		out.println("\t<colors ordered=\"unordered\">");
		generateConeColors(out, "\t\t", s - 1);
		out.println("\t</colors>");
		out.println("\t<layout>");
		out.println("\t\t<oriented>");
		out.println("\t\t\t<horizontal>");
		out.println("\t\t\t\t<row>");
		generateConeLayout(out, "\t\t\t\t\t", s, false);
		out.println("\t\t\t\t</row>");
		out.println("\t\t\t</horizontal>");
		out.println("\t\t\t<square>");
		out.println("\t\t\t\t<row>");
		generateConeLayout(out, "\t\t\t\t\t", s, false);
		out.println("\t\t\t\t</row>");
		out.println("\t\t\t</square>");
		out.println("\t\t\t<vertical>");
		out.println("\t\t\t\t<column>");
		generateConeLayout(out, "\t\t\t\t\t", s, true);
		out.println("\t\t\t\t</column>");
		out.println("\t\t\t</vertical>");
		out.println("\t\t</oriented>");
		out.println("\t</layout>");
		out.println("</palette>");
	}
	
	private static String conePaletteName(int s) {
		String ss = "Conic " + s + "x" + s + "x" + s;
		switch (s) {
			case 2: ss += " (Process)"; break;
			case 6: ss += " (Web-Safe)"; break;
		}
		return ss;
	}
	
	private static String conePaletteSize(int n) {
		int nr = n*2 - 1;
		int nc = (n*n + n - 1) * 2;
		int hch = (int)Math.round(72.0 / (double)nr); if (hch < 7) hch = 7;
		int hcw = (int)Math.round(288.0 / (double)nc); if (hcw < 4) hcw = 4;
		int hh = hch * nr + 1;
		int hw = hcw * nc + 1;
		int l = Integer.toString(Math.max(hh, hw)).length() + 2;
		String hhs = "        \"" + hh + "\""; hhs = hhs.substring(hhs.length() - l);
		String hws = "        \"" + hw + "\""; hws = hws.substring(hws.length() - l);
		return "\n         hwidth=" + hws + " hheight=" + hhs
		     + "\n         swidth=" + hws + " sheight=" + hhs
		     + "\n         vwidth=" + hhs + " vheight=" + hws;
	}
	
	private static void generateConeColors(PrintStream out, String prefix, int m) {
		for (int z = 0; z <= m; z++) {
			int r = (int)Math.round(65535.0 * (double)z / (double)m);
			String rs = "        \"" + r + "\"";
			rs = rs.substring(rs.length() - 7);
			for (int y = 0; y <= m; y++) {
				int g = (int)Math.round(65535.0 * (double)y / (double)m);
				String gs = "        \"" + g + "\"";
				gs = gs.substring(gs.length() - 7);
				for (int x = 0; x <= m; x++) {
					int b = (int)Math.round(65535.0 * (double)x / (double)m);
					String bs = "        \"" + b + "\"";
					bs = bs.substring(bs.length() - 7);
					out.println(prefix + "<rgb16 r=" + rs + " g=" + gs + " b=" + bs + "/>");
				}
			}
		}
	}
	
	private static void generateConeLayout(PrintStream out, String prefix, int n, boolean vertical) {
		int l = Integer.toString(n * n * n).length() + 2;
		for (int i = 0; i < n; i++) {
			if (i > 0) out.println(prefix + "<empty/>");
			if (i < n-1) {
				out.println(prefix + "<weighted weight=\"" + ((n-i) * 2 - 1) + "\">");
				generateConeSliceWrapper1(out, prefix + "\t", n, l, i, vertical);
				out.println(prefix + "</weighted>");
			} else {
				generateConeSliceWrapper1(out, prefix, n, l, i, vertical);
			}
		}
	}
	
	private static void generateConeSliceWrapper1(PrintStream out, String prefix, int n, int l, int i, boolean vertical) {
		if (i > 0) {
			out.println(prefix + "<" + (vertical ? "row" : "column") + ">");
			generateConeSliceWrapper2(out, prefix + "\t", n, l, i, vertical);
			out.println(prefix + "</" + (vertical ? "row" : "column") + ">");
		} else {
			generateConeSliceWrapper2(out, prefix, n, l, i, vertical);
		}
	}
	
	private static void generateConeSliceWrapper2(PrintStream out, String prefix, int n, int l, int i, boolean vertical) {
		generateEmpty(out, prefix, i);
		if (i > 0 && i < n-1) {
			out.println(prefix + "<weighted weight=\"" + ((n-i) * 2 - 1) + "\">");
			generateConeSlice(out, prefix + "\t", n, l, n-i-1);
			out.println(prefix + "</weighted>");
		} else {
			generateConeSlice(out, prefix, n, l, n-i-1);
		}
		generateEmpty(out, prefix, i);
	}
	
	private static void generateConeSlice(PrintStream out, String prefix, int n, int l, int v) {
		if (v > 0) {
			out.println(prefix + "<column>");
			List<List<Integer>> rows = new ArrayList<List<Integer>>();
			for (int i = ~v; i < v; i++) rows.add(new ArrayList<Integer>());
			// Green/Yellow & Blue/Magenta
			for (int y1 = 0, y2 = v*2; y1 < v; y1++, y2--) {
				for (int x = 0; x <= v; x++) {
					rows.get(y1).add(n*n*x + n*v + y1);
					rows.get(y2).add(n*n*x + n*y1 + v);
				}
			}
			// Cyan
			for (int x = 0; x <= v; x++) {
				rows.get(v).add(n*n*x + n*v + v);
			}
			// Red
			for (int x1 = 1, x2 = v-1; x1 <= v; x1++, x2--) {
				for (int y = 0; y < v; y++) {
					rows.get(x1+y).add(n*n*v + n*x2 + y);
				}
			}
			for (List<Integer> row : rows) {
				out.println(prefix + "\t<row>");
				generateEmpty(out, prefix + "\t\t", v*2+1-row.size());
				for (int cell : row) {
					String cs = "        \"" + cell + "\"";
					cs = cs.substring(cs.length() - l);
					out.println(prefix + "\t\t<index i=" + cs + " repeat=\"2\" border-first=\"lh\" border-last=\"hr\"/>");
				}
				generateEmpty(out, prefix + "\t\t", v*2+1-row.size());
				out.println(prefix + "\t</row>");
			}
			out.println(prefix + "</column>");
		} else {
			out.println(prefix + "<index i=\"0\"/>");
		}
	}
	
	private static void generateEmpty(PrintStream out, String prefix, int repeat) {
		if (repeat > 1) {
			out.println(prefix + "<empty repeat=\"" + repeat + "\"/>");
		} else if (repeat > 0) {
			out.println(prefix + "<empty/>");
		}
	}
}
