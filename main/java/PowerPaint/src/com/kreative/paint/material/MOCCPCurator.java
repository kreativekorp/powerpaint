package com.kreative.paint.material;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.kreative.paint.material.colorpalette.PaletteWriter;
import com.kreative.paint.material.colorpalette.RCPXColor;
import com.kreative.paint.material.colorpalette.RCPXPalette;
import com.kreative.paint.material.colorpalette.RCPXParser;

public class MOCCPCurator {
	public static void main(String[] args) throws IOException {
		List<PaletteInfoSection> info = new ArrayList<PaletteInfoSection>();
		File infoFile = new File(new File("src-materials", "rcpx"), "moccp.txt");
		if (infoFile.isFile()) {
			PaletteInfoSection section = null;
			Scanner scan = new Scanner(infoFile);
			while (scan.hasNextLine()) {
				String line = scan.nextLine().trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				} else if (line.startsWith("[") && line.endsWith("]")) {
					line = line.substring(1, line.length() - 1);
					section = new PaletteInfoSection(line);
					info.add(section);
				} else if (section != null) {
					section.items.add(parse(line));
				}
			}
			scan.close();
		}
		
		File moccp = new File("moccp"); moccp.mkdir();
		File hdir = new File(moccp, "h"); hdir.mkdir();
		File sdir = new File(moccp, "s"); sdir.mkdir();
		File vdir = new File(moccp, "v"); vdir.mkdir();
		for (Format f : Format.values()) f.mkdir(moccp);
		rcpxdir(moccp).mkdir();
		
		File html = new File(moccp, "index.html");
		PrintWriter htmlout = new PrintWriter(html, "UTF-8");
		htmlout.println("<html>");
		htmlout.println("<head>");
		htmlout.println("<title>MoCCP - Museum of Computer &amp; Video Game Color Palettes</title>");
		htmlout.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		htmlout.println("<style>");
		htmlout.println("* { margin: 0; padding: 0; }");
		htmlout.println("body { font-family: sans-serif; background: #eeeeee; }");
		htmlout.println("article { max-width: 800px; margin: 50px auto 0; padding: 1px; background: #ffffff; }");
		htmlout.println("h2 { margin: 16px 0; }");
		htmlout.println("h3 { margin: 16px 0; }");
		htmlout.println("ul { list-style: none; }");
		htmlout.println(".appheader { position: fixed; top: 0; left: 0; right: 0; height: 50px; background: #990099; color: #ffffff; font-size: 1px; box-shadow: 0px 1px 2px 1px rgba(0,0,0,0.5); z-index: 1; }");
		htmlout.println(".applogo { display: inline-block; font-weight: bold; font-size: 18px; margin: 0 8px; vertical-align: middle; }");
		htmlout.println(".applogo span { display: inline-block; width: 32px; height: 32px; line-height: 32px; text-align: center; border: 1px solid black; margin: 8px 2px; }");
		htmlout.println("#m { background: #e00; } #o { background: #fd0; } #c1 { background: #0d0; } #c2 { background: #0df; } #p { background: #00e; }");
		htmlout.println(".apptitle { display: inline-block; font-weight: normal; font-size: 12px; vertical-align: middle; }");
		htmlout.println(".apptitle span { display: block; }");
		htmlout.println(".search-container { position: fixed; top: 9px; left: 361px; right: 9px; height: 32px; line-height: 32px; z-index: 2; }");
		htmlout.println(".search-input { background: rgba(0,0,0,0.2); border: 1px solid black; box-sizing: border-box; color: #ffffff; font-family: sans-serif; font-size: 16px; font-weight: 300; line-height: 30px; height: 32px; width: 100%; padding: 0 6px; }");
		htmlout.println(".palette { margin: 24px; padding: 24px; box-shadow: 0 2px 4px 2px rgba(0, 0, 0, 0.2); }");
		htmlout.println(".moreinfo { margin-top: 16px; }");
		htmlout.println(".swatch { display: inline-block; position: relative; top: 1px; width: 12px; height: 12px; border: solid 1px rgba(0, 0, 0, 0.6); }");
		htmlout.println(".indices .swatch { margin: 2px; }");
		htmlout.println(".named .swatch { width: 24px; }");
		htmlout.println(".hidden { display: none !important; }");
		htmlout.println("</style>");
		htmlout.println("</head>");
		htmlout.println("<body>");
		htmlout.println("<article>");
		htmlout.println("<h1 class=\"appheader\">");
		htmlout.println("<span class=\"applogo\"><span id=\"m\">M</span><span id=\"o\">o</span><span id=\"c1\">C</span><span id=\"c2\">C</span><span id=\"p\">P</span></span>");
		htmlout.println("<span class=\"apptitle\"><span>Museum of</span> <span>Computer &amp; Video Game</span> <span>Color Palettes</span></span>");
		htmlout.println("</h1>");
		htmlout.println("<div class=\"search-container\">");
		htmlout.println("<input type=\"text\" class=\"search-input\" placeholder=\"e.g. &quot;apple ii&quot;, &quot;commodore&quot;, &quot;web-safe&quot;, &quot;monochrome&quot;\">");
		htmlout.println("</div>");
		
		MaterialLocator locator = new MaterialLocator("Kreative", "PowerPaint");
		MaterialLoader loader = locator.getMaterialLoader();
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("rcpx", false)) {
				try {
					InputStream in = r.getInputStream();
					RCPXPalette rcpx = RCPXParser.parse(r.getResourceName(), in);
					in.close();
					MaterialList<Color> list = new MaterialList<Color>();
					List<Color> array = new ArrayList<Color>();
					for (RCPXColor color : rcpx.colors) {
						String n = color.name();
						Color c = color.awtColor();
						if (n != null) list.add(n, c);
						array.add(c);
					}
					
					int width = rcpx.hwidth;
					int height = rcpx.hheight;
					String odir = "h";
					String pngName = r.getResourceName() + ".png";
					if (rcpx.orientation != null) {
						switch (rcpx.orientation) {
							case HORIZONTAL: width = rcpx.hwidth; height = rcpx.hheight; odir = "h"; break;
							case SQUARE:     width = rcpx.swidth; height = rcpx.sheight; odir = "s"; break;
							case VERTICAL:   width = rcpx.vwidth; height = rcpx.vheight; odir = "v"; break;
						}
					}
					writeImage(rcpx, rcpx.hwidth, rcpx.hheight, new File(hdir, pngName));
					writeImage(rcpx, rcpx.swidth, rcpx.sheight, new File(sdir, pngName));
					writeImage(rcpx, rcpx.vwidth, rcpx.vheight, new File(vdir, pngName));
					
					String name = ((rcpx.name != null) ? rcpx.name : r.getResourceName());
					String id = name.replaceAll("[^A-Za-z0-9]+", " ").trim().replaceAll(" ", "-");
					PaletteInfo p = new PaletteInfo();
					for (PaletteInfoSection section : info) {
						if (section.pattern.matcher(name).matches()) {
							for (PaletteInfoTransform tx : section.items) {
								tx.apply(p);
							}
						}
					}
					
					htmlout.println("<div class=\"palette\" id=\"" + id + "\">");
					htmlout.println("<div class=\"preview\">");
					
					htmlout.println("<div class=\"image\">");
					htmlout.println("<img src=\"" + odir + "/" + pngName + "\" width=\"" + width + "\" height=\"" + height + "\">");
					htmlout.println("</div>");
					
					htmlout.println("<div class=\"summary\">");
					htmlout.println("<div class=\"title\">");
					htmlout.println("<h2>" + htmlSpecialChars(name) + "</h2>");
					htmlout.println("</div>");
					if (p.description.length() > 0) {
						htmlout.println("<div class=\"description\">");
						htmlout.println(p.description);
						htmlout.println("</div>");
					}
					htmlout.println("<div class=\"moreinfo\">");
					htmlout.println("<a href=\"#" + id + "\">show more</a>");
					htmlout.println("</div>");
					htmlout.println("</div>");
					
					htmlout.println("</div>");
					htmlout.println("<div class=\"fullview hidden\">");
					
					htmlout.println("<div class=\"download\">");
					htmlout.println("<h3>Download</h3>");
					htmlout.println(rcpxcopy(moccp, r));
					for (Format f : Format.values()) {
						String fl = f.write(moccp, r.getResourceName(), rcpx);
						if (fl != null) htmlout.println(fl);
					}
					htmlout.println("</div>");
					
					htmlout.println("<div class=\"properties\">");
					htmlout.println("<h3>Properties</h3>");
					htmlout.println("<ul>");
					String path = "https://raw.githubusercontent.com/kreativekorp/powerpaint/master/main/java/PowerPaint/src-materials/rcpx/" + r.getResourceName() + ".rcpx";
					htmlout.println("<li><b>File Name:</b> <a href=\"" + path + "\" target=\"_blank\">" + r.getResourceName() + ".rcpx</a></li>");
					htmlout.println("<li><b>Number of Colors:</b> " + rcpx.colors.size() + "</li>");
					for (Map.Entry<String,String> e : p.properties.entrySet()) {
						if (e.getKey().length() > 0 && e.getValue().length() > 0) {
							htmlout.println("<li><b>" + e.getKey() + ":</b> " + e.getValue() + "</li>");
						}
					}
					htmlout.println("<li><b>Permalink:</b> <a href=\"#" + id + "\">" + htmlSpecialChars(name) + "</a></li>");
					htmlout.println("</ul>");
					htmlout.println("</div>");
					
					htmlout.println("<div class=\"orientations\">");
					htmlout.println("<h3>Horizontal Orientation</h3>");
					htmlout.println("<div class=\"image\">");
					htmlout.println("<img src=\"h/" + pngName + "\" width=\"" + rcpx.hwidth + "\" height=\"" + rcpx.hheight + "\">");
					htmlout.println("</div>");
					htmlout.println("<h3>Square Orientation</h3>");
					htmlout.println("<div class=\"image\">");
					htmlout.println("<img src=\"s/" + pngName + "\" width=\"" + rcpx.swidth + "\" height=\"" + rcpx.sheight + "\">");
					htmlout.println("</div>");
					htmlout.println("<h3>Vertical Orientation</h3>");
					htmlout.println("<div class=\"image\">");
					htmlout.println("<img src=\"v/" + pngName + "\" width=\"" + rcpx.vwidth + "\" height=\"" + rcpx.vheight + "\">");
					htmlout.println("</div>");
					htmlout.println("</div>");
					
					if (!array.isEmpty() && rcpx.colorsOrdered) {
						htmlout.println("<div class=\"array\">");
						htmlout.println("<h3>Indexed Colors</h3>");
						htmlout.println("<div class=\"indices\"><!--");
						for (int i = 0, n = array.size(); i < n; i++) {
							Color c = array.get(i);
							String css = "background: rgba(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ", " + (c.getAlpha() / 255.0) + ");";
							String title = "I=" + decAndHex(i) + "\nR=" + decAndHex(c.getRed()) + "\nG=" + decAndHex(c.getGreen()) + "\nB=" + decAndHex(c.getBlue()) + "\nA=" + decAndHex(c.getAlpha());
							htmlout.println("--><div class=\"swatch\" style=\"" + css + "\" title=\"" + title + "\"></div><!--");
						}
						htmlout.println("--></div>");
						htmlout.println("</div>");
					}
					
					if (!list.isEmpty()) {
						htmlout.println("<div class=\"list\">");
						htmlout.println("<h3>Named Colors</h3>");
						htmlout.println("<div class=\"names\">");
						for (int i = 0, n = list.size(); i < n; i++) {
							Color c = list.getValue(i);
							String css = "background: rgba(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ", " + (c.getAlpha() / 255.0) + ");";
							String title = "R=" + decAndHex(c.getRed()) + "\nG=" + decAndHex(c.getGreen()) + "\nB=" + decAndHex(c.getBlue()) + "\nA=" + decAndHex(c.getAlpha());
							String cn = list.getName(i);
							if (cn != null) cn = cn.trim();
							if (cn == null || cn.length() == 0) cn = "&nbsp;";
							else cn = htmlSpecialChars(cn);
							htmlout.println("<div class=\"named\">");
							htmlout.println("<div class=\"swatch\" style=\"" + css + "\" title=\"" + title + "\"></div>");
							htmlout.println(cn);
							htmlout.println("</div>");
						}
						htmlout.println("</div>");
						htmlout.println("</div>");
					}
					
					htmlout.println("</div>");
					htmlout.println("</div>");
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile color palette " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
		
		htmlout.println("</article>");
		htmlout.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>");
		htmlout.println("<script>");
		htmlout.println("$(document).ready(function() {");
		htmlout.println("var searchInput = $('.search-input');");
		htmlout.println("var searchString = '';");
		htmlout.println("var searchTimeout = null;");
		htmlout.println("var searchUpdate = function() {");
		htmlout.println("var s = searchInput.val();");
		htmlout.println("if (searchString === s) return;");
		htmlout.println("searchString = s;");
		htmlout.println("if (searchTimeout) window.clearTimeout(searchTimeout);");
		htmlout.println("searchTimeout = window.setTimeout(function() {");
		htmlout.println("var s = searchString.toLowerCase();");
		htmlout.println("$('.palette').each(function() {");
		htmlout.println("var e = $(this);");
		htmlout.println("var t = e.text().toLowerCase();");
		htmlout.println("if (t.indexOf(s) >= 0) {");
		htmlout.println("e.removeClass('hidden');");
		htmlout.println("} else {");
		htmlout.println("e.addClass('hidden');");
		htmlout.println("e.find('.fullview').addClass('hidden');");
		htmlout.println("}");
		htmlout.println("});");
		htmlout.println("window.scrollTo(0,0);");
		htmlout.println("}, 200);");
		htmlout.println("};");
		htmlout.println("$('.palette').each(function() {");
		htmlout.println("var e = $(this);");
		htmlout.println("e.find('.moreinfo a').click(function(evt) {");
		htmlout.println("e.find('.fullview').toggleClass('hidden');");
		htmlout.println("evt.preventDefault();");
		htmlout.println("});");
		htmlout.println("});");
		htmlout.println("$('body').bind('keydown', function(e) {");
		htmlout.println("if (e.which === 27) {");
		htmlout.println("searchInput.val('');");
		htmlout.println("searchInput.focus();");
		htmlout.println("e.preventDefault();");
		htmlout.println("e.stopPropagation();");
		htmlout.println("return;");
		htmlout.println("}");
		htmlout.println("});");
		htmlout.println("searchInput.bind('change', searchUpdate);");
		htmlout.println("searchInput.bind('keydown', searchUpdate);");
		htmlout.println("searchInput.bind('keyup', searchUpdate);");
		htmlout.println("searchInput.focus();");
		htmlout.println("searchUpdate();");
		htmlout.println("});");
		htmlout.println("</script>");
		htmlout.println("</body>");
		htmlout.println("</html>");
		htmlout.flush();
		htmlout.close();
	}
	
	private static enum Format {
		ACT("ACT", "act", ".act", new PaletteWriter.ACTWriter()),
		ACO("ACO", "aco", ".aco", new PaletteWriter.ACOWriter()),
		ASE("ASE", "ase", ".ase", new PaletteWriter.ASEWriter()),
		ACB("ACB", "acb", ".acb", new PaletteWriter.ACBWriter()),
		GPL("GPL", "gpl", ".gpl", new PaletteWriter.GPLWriter()),
		PAL("PAL", "pal", ".pal", new PaletteWriter.PALWriter()),
		CLR("CLR", "clr", ".clr", new PaletteWriter.CLRWriter()),
		CLUT("CLUT", "clut", ".clut", new PaletteWriter.CLUTWriter()),
		PLTT("PLTT", "pltt", ".pltt", new PaletteWriter.PLTTWriter());
		public final String label;
		public final String dir;
		public final String ext;
		public final PaletteWriter writer;
		private Format(String label, String dir, String ext, PaletteWriter writer) {
			this.label = label;
			this.dir = dir;
			this.ext = ext;
			this.writer = writer;
		}
		public File getdir(File root) {
			return new File(root, dir);
		}
		public boolean mkdir(File root) {
			return getdir(root).mkdir();
		}
		public String write(File root, String name, RCPXPalette rcpx) throws IOException {
			if (writer.isCompatible(rcpx)) {
				File outfile = new File(getdir(root), name + ext);
				FileOutputStream out = new FileOutputStream(outfile);
				writer.write(rcpx, out);
				out.close();
				return "<a href=\"" + dir + "/" + name + ext + "\">" + label + "</a>";
			} else {
				return null;
			}
		}
	}
	
	private static class PaletteInfo {
		public String description;
		public final Map<String,String> properties;
		public PaletteInfo() {
			this.description = "";
			this.properties = new LinkedHashMap<String,String>();
		}
	}
	
	private static class PaletteInfoSection {
		public final Pattern pattern;
		public final List<PaletteInfoTransform> items;
		public PaletteInfoSection(String pattern) {
			this.pattern = Pattern.compile(pattern);
			this.items = new ArrayList<PaletteInfoTransform>();
		}
	}
	
	private static abstract class PaletteInfoTransform {
		public abstract void apply(PaletteInfo p);
		public static class SetDescription extends PaletteInfoTransform {
			private final String description;
			public SetDescription(String description) {
				this.description = description;
			}
			public void apply(PaletteInfo p) {
				p.description = this.description;
			}
		}
		public static class PrependDescription extends PaletteInfoTransform {
			private final String description;
			public PrependDescription(String description) {
				this.description = description;
			}
			public void apply(PaletteInfo p) {
				p.description = (this.description + " " + p.description).trim();
			}
		}
		public static class AppendDescription extends PaletteInfoTransform {
			private final String description;
			public AppendDescription(String description) {
				this.description = description;
			}
			public void apply(PaletteInfo p) {
				p.description = (p.description + " " + this.description).trim();
			}
		}
		public static class ReplaceDescription extends PaletteInfoTransform {
			private final String search, replace;
			public ReplaceDescription(String search, String replace) {
				this.search = search; this.replace = replace;
			}
			public void apply(PaletteInfo p) {
				p.description = p.description.replaceAll(search, replace);
			}
		}
		public static class SetProperty extends PaletteInfoTransform {
			private final String key, value;
			public SetProperty(String key, String value) {
				this.key = key; this.value = value;
			}
			public void apply(PaletteInfo p) {
				p.properties.put(this.key, this.value);
			}
		}
		public static class ClearProperty extends PaletteInfoTransform {
			private final String key;
			public ClearProperty(String key) {
				this.key = key;
			}
			public void apply(PaletteInfo p) {
				p.properties.remove(this.key);
			}
		}
		public static class ReplaceProperty extends PaletteInfoTransform {
			private final String key, search, replace;
			public ReplaceProperty(String key, String search, String replace) {
				this.key = key; this.search = search; this.replace = replace;
			}
			public void apply(PaletteInfo p) {
				if (p.properties.containsKey(this.key)) {
					String value = p.properties.get(this.key);
					value = value.replaceAll(this.search, this.replace);
					p.properties.put(this.key, value);
				}
			}
		}
		public static class ReplaceAll extends PaletteInfoTransform {
			private final String search, replace;
			public ReplaceAll(String search, String replace) {
				this.search = search; this.replace = replace;
			}
			public void apply(PaletteInfo p) {
				p.description = p.description.replaceAll(search, replace);
				for (Map.Entry<String,String> e : p.properties.entrySet()) {
					String value = e.getValue();
					value = value.replaceAll(this.search, this.replace);
					e.setValue(value);
				}
			}
		}
	}
	
	private static String[] grepSplit(String s) {
		List<String> strings = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		char[] ch = s.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == '/') {
				strings.add(sb.toString());
				sb = new StringBuffer();
			} else if (ch[i] == '\\') {
				i++;
				if (i < ch.length) {
					if (ch[i] == '/') {
						sb.append(ch[i]);
					} else {
						sb.append('\\');
						sb.append(ch[i]);
					}
				} else {
					sb.append('\\');
				}
			} else {
				sb.append(ch[i]);
			}
		}
		strings.add(sb.toString());
		return strings.toArray(new String[strings.size()]);
	}
	
	private static PaletteInfoTransform parse(String line) {
		if (line.startsWith("d=")) {
			return new PaletteInfoTransform.SetDescription(line.substring(2).trim());
		} else if (line.startsWith("d^") || line.startsWith("d-")) {
			return new PaletteInfoTransform.PrependDescription(line.substring(2).trim());
		} else if (line.startsWith("d$") || line.startsWith("d+")) {
			return new PaletteInfoTransform.AppendDescription(line.substring(2).trim());
		} else if (line.startsWith("d/")) {
			String[] fields = grepSplit(line);
			String search = (fields.length > 1) ? fields[1] : "";
			String replace = (fields.length > 2) ? fields[2] : "";
			return new PaletteInfoTransform.ReplaceDescription(search, replace);
		} else if (line.startsWith("p+") || line.startsWith("p=")) {
			String[] fields = line.substring(2).trim().split(":",2);
			String key = (fields.length > 0) ? fields[0].trim() : "";
			String value = (fields.length > 1) ? fields[1].trim() : "";
			return new PaletteInfoTransform.SetProperty(key, value);
		} else if (line.startsWith("p-")) {
			return new PaletteInfoTransform.ClearProperty(line.substring(2).trim());
		} else if (line.startsWith("p/")) {
			String[] fields = grepSplit(line);
			String key = (fields.length > 1) ? fields[1] : "";
			String search = (fields.length > 2) ? fields[2] : "";
			String replace = (fields.length > 3) ? fields[3] : "";
			return new PaletteInfoTransform.ReplaceProperty(key, search, replace);
		} else if (line.startsWith("s/")) {
			String[] fields = grepSplit(line);
			String search = (fields.length > 1) ? fields[1] : "";
			String replace = (fields.length > 2) ? fields[2] : "";
			return new PaletteInfoTransform.ReplaceAll(search, replace);
		} else if (line.contains(":")) {
			String[] fields = line.split(":",2);
			String key = (fields.length > 0) ? fields[0].trim() : "";
			String value = (fields.length > 1) ? fields[1].trim() : "";
			return new PaletteInfoTransform.SetProperty(key, value);
		} else {
			return new PaletteInfoTransform.AppendDescription(line);
		}
	}
	
	private static void writeImage(RCPXPalette rcpx, int width, int height, File out) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		rcpx.layout.paint(rcpx.colors, 0, g, new Rectangle(0, 0, width, height), null);
		g.dispose();
		ImageIO.write(image, "png", out);
	}
	
	private static String htmlSpecialChars(String s) {
		s = s.replace("&", "&amp;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		return s;
	}
	
	private static String decAndHex(int i) {
		String h = Integer.toHexString(i).toUpperCase();
		while (h.length() < 2) h = "0" + h;
		return Integer.toString(i) + " [0x" + h + "]";
	}
	
	private static File rcpxdir(File root) {
		return new File(root, "rcpx");
	}
	
	private static String rcpxcopy(File root, MaterialResource r) throws IOException {
		File outfile = new File(rcpxdir(root), r.getResourceName() + ".rcpx");
		FileOutputStream out = new FileOutputStream(outfile);
		InputStream in = r.getInputStream();
		int i; byte[] buf = new byte[65536];
		while ((i = in.read(buf)) > 0) out.write(buf, 0, i);
		in.close();
		out.close();
		return "<a href=\"rcpx/" + r.getResourceName() + ".rcpx\">RCPX</a>";
	}
}
