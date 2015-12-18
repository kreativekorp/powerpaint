package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class GenerateColorCube {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			int s = Integer.parseInt(arg);
			File file = new File(cubePaletteFileName(s));
			FileOutputStream fout = new FileOutputStream(file);
			PrintStream out = new PrintStream(fout);
			generateCubePalette(out, s);
			out.flush();
			out.close();
			fout.close();
		}
	}
	
	private static String cubePaletteFileName(int s) {
		String ss = "00" + s;
		ss = ss.substring(ss.length() - 2);
		ss = "0" + ss + "Cubic";
		switch (s) {
			case 2: ss += "Process"; break;
			case 6: ss += "WebSafe"; break;
		}
		return ss + ".rcpx";
	}
	
	private static void generateCubePalette(PrintStream out, int s) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE palette PUBLIC \"-//Kreative//DTD ResplendentColor 1.0//EN\" \"http://www.kreativekorp.com/dtd/rcpx.dtd\">");
		out.println("<palette name=\"" + cubePaletteName(s) + "\"" + cubePaletteSize(s) + ">");
		out.println("\t<colors ordered=\"unordered\">");
		generateCubeColors(out, "\t\t", s - 1);
		out.println("\t</colors>");
		out.println("\t<layout>");
		out.println("\t\t<oriented>");
		out.println("\t\t\t<horizontal>");
		out.println("\t\t\t\t<row>");
		generateCubeLayout(out, "\t\t\t\t\t", s);
		out.println("\t\t\t\t</row>");
		out.println("\t\t\t</horizontal>");
		out.println("\t\t\t<square>");
		generateCubeLayoutSquare(out, "\t\t\t\t", s);
		out.println("\t\t\t</square>");
		out.println("\t\t\t<vertical>");
		out.println("\t\t\t\t<column>");
		generateCubeLayout(out, "\t\t\t\t\t", s);
		out.println("\t\t\t\t</column>");
		out.println("\t\t\t</vertical>");
		out.println("\t\t</oriented>");
		out.println("\t</layout>");
		out.println("</palette>");
	}
	
	private static String cubePaletteName(int s) {
		String ss = "Cubic " + s + "x" + s + "x" + s;
		switch (s) {
			case 2: ss += " (Process)"; break;
			case 6: ss += " (Web-Safe)"; break;
		}
		return ss;
	}
	
	private static String cubePaletteSize(int n) {
		int hch = (int)Math.round(72.0 / (double)n); if (hch < 8) hch = 8;
		int hcw = (int)Math.round(288.0 / (double)(n * n)); if (hcw < 8) hcw = 8;
		int hh = hch * n + 1;
		int hw = hcw * n * n + 1;
		int r = (int)Math.floor(Math.sqrt((double)n)); if (r < 2) r = 2;
		int c = (int)Math.ceil((double)n / (double)r);
		int sch = (int)Math.round(144.0 / (double)(r * n)); if (sch < 8) sch = 8;
		int scw = (int)Math.round(144.0 / (double)(c * n)); if (scw < 8) scw = 8;
		int sh = sch * r * n + 1;
		int sw = scw * c * n + 1;
		while (sw > (sh + sh / 2)) {
			sh += r * n;
		}
		int l = Integer.toString(Math.max(Math.max(hh, hw), Math.max(sh, sw))).length() + 2;
		String hhs = "        \"" + hh + "\""; hhs = hhs.substring(hhs.length() - l);
		String hws = "        \"" + hw + "\""; hws = hws.substring(hws.length() - l);
		String shs = "        \"" + sh + "\""; shs = shs.substring(shs.length() - l);
		String sws = "        \"" + sw + "\""; sws = sws.substring(sws.length() - l);
		return "\n         hwidth=" + hws + " hheight=" + hhs
		     + "\n         swidth=" + sws + " sheight=" + shs
		     + "\n         vwidth=" + hhs + " vheight=" + hws;
	}
	
	private static void generateCubeColors(PrintStream out, String prefix, int m) {
		for (int z = 0; z <= m; z++) {
			int b = (int)Math.round(65535.0 * (double)z / (double)m);
			String bs = "        \"" + b + "\"";
			bs = bs.substring(bs.length() - 7);
			for (int y = 0; y <= m; y++) {
				int g = (int)Math.round(65535.0 * (double)y / (double)m);
				String gs = "        \"" + g + "\"";
				gs = gs.substring(gs.length() - 7);
				for (int x = 0; x <= m; x++) {
					int r = (int)Math.round(65535.0 * (double)x / (double)m);
					String rs = "        \"" + r + "\"";
					rs = rs.substring(rs.length() - 7);
					out.println(prefix + "<rgb16 r=" + rs + " g=" + gs + " b=" + bs + "/>");
				}
			}
		}
	}
	
	private static void generateCubeLayout(PrintStream out, String prefix, int n) {
		int l = Integer.toString(n * n * n).length() + 2;
		int i = 0;
		for (int z = 0; z < n; z++) {
			i = generateCubeFace(out, prefix, n, l, i);
		}
	}
	
	private static void generateCubeLayoutSquare(PrintStream out, String prefix, int n) {
		int n3 = n * n * n;
		int l = Integer.toString(n3).length() + 2;
		int i = 0;
		int r = (int)Math.floor(Math.sqrt((double)n));
		if (r < 2) r = 2;
		int c = (int)Math.ceil((double)n / (double)r);
		out.println(prefix + "<column>");
		for (int y = 0; y < r; y++) {
			out.println(prefix + "\t<row>");
			for (int x = 0; x < c; x++) {
				if (i < n3) {
					i = generateCubeFace(out, prefix + "\t\t", n, l, i);
				} else {
					out.println(prefix + "\t\t<empty/>");
				}
			}
			out.println(prefix + "\t</row>");
		}
		out.println(prefix + "</column>");
	}
	
	private static int generateCubeFace(PrintStream out, String prefix, int n, int l, int i) {
		out.println(prefix + "<column>");
		for (int y = 0; y < n; y++) {
			String ss = "        \"" + i + "\"";
			ss = ss.substring(ss.length() - l);
			i += n;
			String es = "        \"" + i + "\"";
			es = es.substring(es.length() - l);
			out.println(prefix + "\t<row><range start=" + ss + " end=" + es + "/></row>");
		}
		out.println(prefix + "</column>");
		return i;
	}
}
