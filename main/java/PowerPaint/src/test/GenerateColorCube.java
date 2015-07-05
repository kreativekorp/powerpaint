package test;

public class GenerateColorCube {
	public static void main(String[] args) {
		for (String arg : args) {
			int s = Integer.parseInt(arg);
			generateCubePalette(s);
		}
	}
	
	private static void generateCubePalette(int s) {
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		System.out.println("<!DOCTYPE palette PUBLIC \"-//Kreative//DTD ResplendentColor 1.0//EN\" \"rcpx.dtd\">");
		System.out.println("<palette name=\"" + cubePaletteName(s) + "\"" + cubePaletteSize(s) + ">");
		System.out.println("\t<colors ordered=\"unordered\">");
		generateCubeColors("\t\t", s - 1);
		System.out.println("\t</colors>");
		System.out.println("\t<layout>");
		System.out.println("\t\t<oriented>");
		System.out.println("\t\t\t<horizontal>");
		System.out.println("\t\t\t\t<row>");
		generateCubeLayout("\t\t\t\t\t", s);
		System.out.println("\t\t\t\t</row>");
		System.out.println("\t\t\t</horizontal>");
		System.out.println("\t\t\t<square>");
		generateCubeLayoutSquare("\t\t\t\t", s);
		System.out.println("\t\t\t</square>");
		System.out.println("\t\t\t<vertical>");
		System.out.println("\t\t\t\t<column>");
		generateCubeLayout("\t\t\t\t\t", s);
		System.out.println("\t\t\t\t</column>");
		System.out.println("\t\t\t</vertical>");
		System.out.println("\t\t</oriented>");
		System.out.println("\t</layout>");
		System.out.println("</palette>");
	}
	
	private static String cubePaletteName(int s) {
		String ss = s + " Cubed";
		switch (s) {
			case 2: ss += " (Process)"; break;
			case 6: ss += " (Web-Safe)"; break;
		}
		return ss;
	}
	
	private static String cubePaletteSize(int n) {
		int hch = (int)Math.round(72.0 / (double)n); if (hch < 7) hch = 7;
		int hcw = (int)Math.round(288.0 / (double)(n * n)); if (hcw < 7) hcw = 7;
		int hh = hch * n + 1;
		int hw = hcw * n * n + 1;
		int r = (int)Math.floor(Math.sqrt((double)n)); if (r < 2) r = 2;
		int c = (int)Math.ceil((double)n / (double)r);
		int sch = (int)Math.round(144.0 / (double)(r * n)); if (sch < 7) sch = 7;
		int scw = (int)Math.round(144.0 / (double)(c * n)); if (scw < 7) scw = 7;
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
	
	private static void generateCubeColors(String prefix, int m) {
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
					System.out.println(prefix + "<rgb16 r=" + rs + " g=" + gs + " b=" + bs + "/>");
				}
			}
		}
	}
	
	private static void generateCubeLayout(String prefix, int n) {
		int l = Integer.toString(n * n * n).length() + 2;
		int i = 0;
		for (int z = 0; z < n; z++) {
			i = generateCubeFace(prefix, n, l, i);
		}
	}
	
	private static void generateCubeLayoutSquare(String prefix, int n) {
		int n3 = n * n * n;
		int l = Integer.toString(n3).length() + 2;
		int i = 0;
		int r = (int)Math.floor(Math.sqrt((double)n));
		if (r < 2) r = 2;
		int c = (int)Math.ceil((double)n / (double)r);
		System.out.println(prefix + "<column>");
		for (int y = 0; y < r; y++) {
			System.out.println(prefix + "\t<row>");
			for (int x = 0; x < c; x++) {
				if (i < n3) {
					i = generateCubeFace(prefix + "\t\t", n, l, i);
				} else {
					System.out.println(prefix + "\t\t<empty/>");
				}
			}
			System.out.println(prefix + "\t</row>");
		}
		System.out.println(prefix + "</column>");
	}
	
	private static int generateCubeFace(String prefix, int n, int l, int i) {
		System.out.println(prefix + "<column>");
		for (int y = 0; y < n; y++) {
			String ss = "        \"" + i + "\"";
			ss = ss.substring(ss.length() - l);
			i += n;
			String es = "        \"" + i + "\"";
			es = es.substring(es.length() - l);
			System.out.println(prefix + "\t<row><range start=" + ss + " end=" + es + "/></row>");
		}
		System.out.println(prefix + "</column>");
		return i;
	}
}
