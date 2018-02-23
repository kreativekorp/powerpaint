package com.kreative.paint.material.colorpalette;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RCPXGenerator {
	public static void generate(RCPXPalette pal, OutputStream out) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<!DOCTYPE palette PUBLIC \"-//Kreative//DTD ResplendentColor 1.0//EN\" \"http://www.kreativekorp.com/dtd/rcpx.dtd\">");
		for (String line : generatePalette(pal)) pw.println(line);
		pw.flush();
	}
	
	private static List<String> generatePalette(RCPXPalette pal) {
		List<String> colors = new ArrayList<String>();
		for (RCPXColor color : pal.colors) colors.add(generateColor(color));
		List<String> content = new ArrayList<String>();
		content.addAll(b("colors", colors, (colors.isEmpty() ? null : "ordered"), (pal.colorsOrdered ? "ordered" : "unordered")));
		content.addAll(b("layout", generateLayout(pal.layout)));
		boolean defSize = (
			pal.hwidth == 289 && pal.hheight ==  73 &&
			pal.swidth == 145 && pal.sheight == 145 &&
			pal.vwidth ==  73 && pal.vheight == 289
		);
		return b(
			"palette", content, ((pal.name == null || pal.name.length() == 0) ? null : "name"), pal.name,
			((pal.orientation == null || pal.orientation == RCPXOrientation.HORIZONTAL) ? null : "orientation"),
			((pal.orientation == null) ? null : pal.orientation.name().toLowerCase()),
			(defSize ? null : "hwidth"), pal.hwidth, (defSize ? null : "hheight"), pal.hheight,
			(defSize ? null : "swidth"), pal.swidth, (defSize ? null : "sheight"), pal.sheight,
			(defSize ? null : "vwidth"), pal.vwidth, (defSize ? null : "vheight"), pal.vheight
		);
	}
	
	private static List<String> generateLayout(RCPXLayout layout) {
		if (layout instanceof RCPXLayout.Oriented) {
			RCPXLayout.Oriented l = (RCPXLayout.Oriented)layout;
			List<String> content = new ArrayList<String>();
			content.addAll(b("horizontal", generateLayoutOrSwatch(l.horizontal)));
			content.addAll(b("square"    , generateLayoutOrSwatch(l.square    )));
			content.addAll(b("vertical"  , generateLayoutOrSwatch(l.vertical  )));
			return b("oriented", content);
		} else if (layout instanceof RCPXLayout.Row) {
			RCPXLayout.Row l = (RCPXLayout.Row)layout;
			List<String> content = new ArrayList<String>();
			for (int i = 0; i < l.size(); i++) {
				int w = l.getWeight(i);
				List<String> c = generateLayoutOrSwatch(l.get(i));
				content.addAll((w == 1) ? c : b("weighted", c, "weight", w));
			}
			return b("row", content);
		} else if (layout instanceof RCPXLayout.Column) {
			RCPXLayout.Column l = (RCPXLayout.Column)layout;
			List<String> content = new ArrayList<String>();
			for (int i = 0; i < l.size(); i++) {
				int w = l.getWeight(i);
				List<String> c = generateLayoutOrSwatch(l.get(i));
				content.addAll((w == 1) ? c : b("weighted", c, "weight", w));
			}
			return b("column", content);
		} else if (layout instanceof RCPXLayout.Diagonal) {
			RCPXLayout.Diagonal l = (RCPXLayout.Diagonal)layout;
			List<String> content = new ArrayList<String>();
			for (RCPXSwatch c : l) content.add(generateSwatch(c));
			return b(
				"diagonal", content, "cols", l.cols, "rows", l.rows,
				(l.square ? "aspect" : null), (l.square ? "square" : null)
			);
		} else {
			throw new IllegalArgumentException("Unknown RCPXLayout class: " + layout.getClass());
		}
	}
	
	private static List<String> generateLayoutOrSwatch(RCPXLayoutOrSwatch thing) {
		if (thing == null) return Collections.emptyList();
		if (thing.isLayout()) return generateLayout(thing.asLayout());
		if (thing.isSwatch()) return Arrays.asList(generateSwatch(thing.asSwatch()));
		throw new IllegalArgumentException("Misbehaving RCPXLayoutOrSwatch class: " + thing.getClass());
	}
	
	private static String generateSwatch(RCPXSwatch swatch) {
		if (swatch instanceof RCPXSwatch.Empty) {
			RCPXSwatch.Empty s = (RCPXSwatch.Empty)swatch;
			return t("empty", true, ((s.repeat == 1) ? null : "repeat"), s.repeat);
		} else if (swatch instanceof RCPXSwatch.Index) {
			RCPXSwatch.Index s = (RCPXSwatch.Index)swatch;
			List<Object> args = new ArrayList<Object>();
			args.add("i"); args.add(s.i);
			args.add((s.repeat == 1) ? null : "repeat"); args.add(s.repeat);
			generateBorder(s.border_only, s.border_first, s.border_middle, s.border_last, args);
			return t("index", true, args.toArray());
		} else if (swatch instanceof RCPXSwatch.Range) {
			RCPXSwatch.Range s = (RCPXSwatch.Range)swatch;
			List<Object> args = new ArrayList<Object>();
			args.add("start"); args.add(s.start);
			args.add("end"); args.add(s.end);
			generateBorder(s.border_only, s.border_first, s.border_middle, s.border_last, args);
			return t("range", true, args.toArray());
		} else if (swatch instanceof RCPXSwatch.RGBSweep) {
			RCPXSwatch.RGBSweep s = (RCPXSwatch.RGBSweep)swatch;
			List<Object> args = new ArrayList<Object>();
			if (s.xchan != null && s.xchan != RGBChannel.NONE) {
				args.add("xchan"); args.add(s.xchan.name().toLowerCase());
				args.add("xmin"); args.add(s.xmin);
				args.add("xmax"); args.add(s.xmax);
			}
			if (s.ychan != null && s.ychan != RGBChannel.NONE) {
				args.add("ychan"); args.add(s.ychan.name().toLowerCase());
				args.add("ymin"); args.add(s.ymin);
				args.add("ymax"); args.add(s.ymax);
			}
			if (s.xchan != RGBChannel.RED   && s.ychan != RGBChannel.RED  ) { args.add("r"); args.add(s.r); }
			if (s.xchan != RGBChannel.GREEN && s.ychan != RGBChannel.GREEN) { args.add("g"); args.add(s.g); }
			if (s.xchan != RGBChannel.BLUE  && s.ychan != RGBChannel.BLUE ) { args.add("b"); args.add(s.b); }
			generateBorder(s.border, s.border, s.border, s.border, args);
			return t("rgb-sweep", true, args.toArray());
		} else if (swatch instanceof RCPXSwatch.HSVSweep) {
			RCPXSwatch.HSVSweep s = (RCPXSwatch.HSVSweep)swatch;
			List<Object> args = new ArrayList<Object>();
			if (s.xchan != null && s.xchan != HSVChannel.NONE) {
				args.add("xchan"); args.add(s.xchan.name().toLowerCase());
				args.add("xmin"); args.add(s.xmin);
				args.add("xmax"); args.add(s.xmax);
			}
			if (s.ychan != null && s.ychan != HSVChannel.NONE) {
				args.add("ychan"); args.add(s.ychan.name().toLowerCase());
				args.add("ymin"); args.add(s.ymin);
				args.add("ymax"); args.add(s.ymax);
			}
			if (s.xchan != HSVChannel.HUE        && s.ychan != HSVChannel.HUE       ) { args.add("h"); args.add(s.h); }
			if (s.xchan != HSVChannel.SATURATION && s.ychan != HSVChannel.SATURATION) { args.add("s"); args.add(s.s); }
			if (s.xchan != HSVChannel.VALUE      && s.ychan != HSVChannel.VALUE     ) { args.add("v"); args.add(s.v); }
			generateBorder(s.border, s.border, s.border, s.border, args);
			return t("hsv-sweep", true, args.toArray());
		} else {
			throw new IllegalArgumentException("Unknown RCPXSwatch class: " + swatch.getClass());
		}
	}
	
	private static String generateColor(RCPXColor color) {
		if (color instanceof RCPXColor.RGB) {
			RCPXColor.RGB c = (RCPXColor.RGB)color;
			return t("rgb", true, "r", c.r, "g", c.g, "b", c.b,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.RGB16) {
			RCPXColor.RGB16 c = (RCPXColor.RGB16)color;
			return t("rgb16", true, "r", c.r, "g", c.g, "b", c.b,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.RGBD) {
			RCPXColor.RGBD c = (RCPXColor.RGBD)color;
			return t("rgbd", true, "r", c.r, "g", c.g, "b", c.b,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.RGBA) {
			RCPXColor.RGBA c = (RCPXColor.RGBA)color;
			return t("rgba", true, "r", c.r, "g", c.g, "b", c.b, "a", c.a,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.RGBA16) {
			RCPXColor.RGBA16 c = (RCPXColor.RGBA16)color;
			return t("rgba16", true, "r", c.r, "g", c.g, "b", c.b, "a", c.a,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.RGBAD) {
			RCPXColor.RGBAD c = (RCPXColor.RGBAD)color;
			return t("rgbad", true, "r", c.r, "g", c.g, "b", c.b, "a", c.a,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.HSV) {
			RCPXColor.HSV c = (RCPXColor.HSV)color;
			return t("hsv", true, "h", c.h, "s", c.s, "v", c.v,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.HSVA) {
			RCPXColor.HSVA c = (RCPXColor.HSVA)color;
			return t("hsva", true, "h", c.h, "s", c.s, "v", c.v, "a", c.a,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.Gray) {
			RCPXColor.Gray c = (RCPXColor.Gray)color;
			return t("gray", true, "v", c.gray,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.CMYK) {
			RCPXColor.CMYK c = (RCPXColor.CMYK)color;
			return t("cmyk", true, "c", c.c, "m", c.m, "y", c.y, "k", c.k,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else if (color instanceof RCPXColor.CIELab) {
			RCPXColor.CIELab c = (RCPXColor.CIELab)color;
			return t("lab", true, "l", c.l, "a", c.a, "b", c.b,
				((c.name == null || c.name.length() == 0) ? null : "name"), c.name);
		} else {
			throw new IllegalArgumentException("Unknown RCPXColor class: " + color.getClass());
		}
	}
	
	private static void generateBorder(RCPXBorder only, RCPXBorder first, RCPXBorder middle, RCPXBorder last, List<Object> args) {
		int ov = borderValue(only), fv = borderValue(first), mv = borderValue(middle), lv = borderValue(last);
		if (ov == fv && fv == mv && mv == lv) {
			if (ov != 15) { args.add("border"); args.add(borderString[ov]); }
		} else {
			if (ov != 15) { args.add("border-only");   args.add(borderString[ov]); }
			if (fv != 15) { args.add("border-first");  args.add(borderString[fv]); }
			if (mv != 15) { args.add("border-middle"); args.add(borderString[mv]); }
			if (lv != 15) { args.add("border-last");   args.add(borderString[lv]); }
		}
	}
	
	private static int borderValue(RCPXBorder b) {
		return (b.top ? 1 : 0) | (b.left ? 2 : 0) | (b.bottom ? 4 : 0) | (b.right ? 8 : 0);
	}
	
	private static final String[] borderString = {
		"0", "t", "l", "tl", "b", "h", "bl", "hl", "r", "tr", "v", "vt", "br", "hr", "vb", "a"
	};
	
	private static List<String> b(String tag, List<String> content, Object... attr) {
		if (content == null || content.isEmpty()) {
			return Arrays.asList(t(tag, true, attr));
		} else if (content.size() == 1) {
			return Arrays.asList(t(tag, false, attr) + content.get(0) + "</" + tag + ">");
		} else {
			List<String> block = new ArrayList<String>();
			block.add(t(tag, false, attr));
			for (String line : content) block.add("\t" + line);
			block.add("</" + tag + ">");
			return block;
		}
	}
	
	private static String t(String tag, boolean empty, Object... attr) {
		StringBuffer sb = new StringBuffer();
		sb.append('<');
		sb.append(tag);
		for (int i = 0, j = 1; j < attr.length; j += 2, i += 2) {
			if (attr[i] != null) {
				sb.append(' ');
				sb.append(attr[i].toString());
				if (attr[j] != null) {
					sb.append('=');
					sb.append('"');
					sb.append(q(attr[j].toString()));
					sb.append('"');
				}
			}
		}
		if (empty) sb.append('/');
		sb.append('>');
		return sb.toString();
	}
	
	private static String q(String s) {
		StringBuffer sb = new StringBuffer();
		CharacterIterator it = new StringCharacterIterator(s);
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			switch (ch) {
				case '"': sb.append("&quot;"); break;
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				default: sb.append(ch); break;
			}
		}
		return sb.toString();
	}
}
