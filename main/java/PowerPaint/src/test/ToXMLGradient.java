package test;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Scanner;

import com.kreative.paint.gradient.GradientColor;
import com.kreative.paint.gradient.GradientColorMap;
import com.kreative.paint.gradient.GradientColorStop;
import com.kreative.paint.gradient.GradientPreset;
import com.kreative.paint.gradient.GradientShape;

public class ToXMLGradient {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			InputStream in = new FileInputStream(file);
			GradientManager gm = new GradientManager();
			gm.loadGradients(in);
			in.close();
			System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			System.out.println("<!DOCTYPE gradients PUBLIC \"-//Kreative//DTD PowerGradient 1.0//EN\" \"grdx.dtd\">");
			String name = file.getName()
					.replaceAll("^#[0-9]+\\s+", "")
					.replaceAll("\\.grd$", "");
			System.out.println("<gradients name=\"" + xmls(name) + "\">");
			for (GradientShape shape : gm.gradientShapes.values()) {
				System.out.println("\t<shape name=\"" + xmls(shape.name) + "\">");
				System.out.println("\t\t" + shapeToString(shape, "\t\t"));
				System.out.println("\t</shape>");
			}
			for (GradientColorMap map : gm.gradientColors.values()) {
				System.out.println("\t<map name=\"" + xmls(map.name) + "\">");
				boolean simplify = allSimplifiable(map);
				for (GradientColorStop stop : map) {
					System.out.println(
						"\t\t<stop at=\"" + stop.position + "\">" +
						colorToString(stop.color, simplify) + "</stop>"
					);
				}
				System.out.println("\t</map>");
			}
			for (GradientPreset preset : gm.gradientPresets.values()) {
				System.out.println("\t<gradient name=\"" + xmls(preset.name) + "\">");
				System.out.println("\t\t<shape>");
				System.out.println("\t\t\t" + shapeToString(preset.shape, "\t\t\t"));
				System.out.println("\t\t</shape>");
				System.out.println("\t\t<map>");
				boolean simplify = allSimplifiable(preset.colorMap);
				for (GradientColorStop stop : preset.colorMap) {
					System.out.println(
						"\t\t\t<stop at=\"" + stop.position + "\">" +
						colorToString(stop.color, simplify) + "</stop>"
					);
				}
				System.out.println("\t\t</map>");
				System.out.println("\t</gradient>");
			}
			System.out.println("</gradients>");
		}
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;");
	}
	
	private static String shapeToString(GradientShape shape, String pfx) {
		StringBuffer s = new StringBuffer();
		s.append("<");
		if (shape instanceof GradientShape.Linear) {
			GradientShape.Linear sh = (GradientShape.Linear)shape;
			s.append("linear x0=\"" + sh.x0 + "\" y0=\"" + sh.y0 + "\""
			             + " x1=\"" + sh.x1 + "\" y1=\"" + sh.y1 + "\"");
		} else if (shape instanceof GradientShape.Angular) {
			GradientShape.Angular sh = (GradientShape.Angular)shape;
			s.append("angular cx=\"" + sh.cx + "\" cy=\"" + sh.cy + "\""
			              + " px=\"" + sh.px + "\" py=\"" + sh.py + "\"");
		} else if (shape instanceof GradientShape.Radial) {
			GradientShape.Radial sh = (GradientShape.Radial)shape;
			s.append("radial cx=\"" + sh.cx + "\" cy=\"" + sh.cy + "\""
			             + " x0=\"" + sh.x0 + "\" y0=\"" + sh.y0 + "\""
			             + " x1=\"" + sh.x1 + "\" y1=\"" + sh.y1 + "\"");
		} else if (shape instanceof GradientShape.Rectangular) {
			GradientShape.Rectangular sh = (GradientShape.Rectangular)shape;
			s.append("rectangular t0=\"" + sh.t0 + "\" l0=\"" + sh.l0 + "\""
			                  + " b0=\"" + sh.b0 + "\" r0=\"" + sh.r0 + "\"");
			if (pfx != null) s.append("\n" + pfx + "            ");
			s.append(" t1=\"" + sh.t1 + "\" l1=\"" + sh.l1 + "\""
			       + " b1=\"" + sh.b1 + "\" r1=\"" + sh.r1 + "\"");
		} else {
			s.append(shape.getClass().getSimpleName());
		}
		if (shape.repeat) s.append(" repeat=\"yes\"");
		if (shape.reflect) s.append(" reflect=\"yes\"");
		if (shape.reverse) s.append(" reverse=\"yes\"");
		s.append("/>");
		return s.toString();
	}
	
	private static boolean allSimplifiable(GradientColorMap map) {
		for (GradientColorStop stop : map) {
			GradientColor color = stop.color;
			if (color instanceof GradientColor.RGB16) {
				GradientColor.RGB16 c = (GradientColor.RGB16)color;
				if (c.r % 257 == 0 && c.g % 257 == 0 && c.b % 257 == 0) {
					continue;
				} else {
					return false;
				}
			} else if (color instanceof GradientColor.RGBA16) {
				GradientColor.RGBA16 c = (GradientColor.RGBA16)color;
				if (c.r % 257 == 0 && c.g % 257 == 0 && c.b % 257 == 0 && c.a % 257 == 0) {
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	private static String colorToString(GradientColor color, boolean simplify) {
		if (color instanceof GradientColor.RGB) {
			GradientColor.RGB c = (GradientColor.RGB)color;
			return "<rgb r=\"" + c.r + "\" g=\"" + c.g + "\" b=\"" + c.b + "\"/>";
		} else if (color instanceof GradientColor.RGB16) {
			GradientColor.RGB16 c = (GradientColor.RGB16)color;
			if (simplify && c.r % 257 == 0 && c.g % 257 == 0 && c.b % 257 == 0) {
				return "<rgb r=\"" + (c.r/257) + "\" g=\"" + (c.g/257) + "\" b=\"" + (c.b/257) + "\"/>";
			} else {
				return "<rgb16 r=\"" + c.r + "\" g=\"" + c.g + "\" b=\"" + c.b + "\"/>";
			}
		} else if (color instanceof GradientColor.RGBA) {
			GradientColor.RGBA c = (GradientColor.RGBA)color;
			return "<rgba r=\"" + c.r + "\" g=\"" + c.g + "\" b=\"" + c.b + "\" a=\"" + c.a + "\"/>";
		} else if (color instanceof GradientColor.RGBA16) {
			GradientColor.RGBA16 c = (GradientColor.RGBA16)color;
			if (simplify && c.r % 257 == 0 && c.g % 257 == 0 && c.b % 257 == 0 && c.a % 257 == 0) {
				return "<rgba r=\"" + (c.r/257) + "\" g=\"" + (c.g/257) + "\" b=\"" + (c.b/257) + "\" a=\"" + (c.a/257) + "\"/>";
			} else {
				return "<rgba16 r=\"" + c.r + "\" g=\"" + c.g + "\" b=\"" + c.b + "\" a=\"" + c.a + "\"/>";
			}
		} else if (color instanceof GradientColor.HSV) {
			GradientColor.HSV c = (GradientColor.HSV)color;
			return "<hsv h=\"" + c.h + "\" s=\"" + c.s + "\" v=\"" + c.v + "\"/>";
		} else if (color instanceof GradientColor.HSVA) {
			GradientColor.HSVA c = (GradientColor.HSVA)color;
			return "<hsv h=\"" + c.h + "\" s=\"" + c.s + "\" v=\"" + c.v + "\" a=\"" + c.a + "\"/>";
		} else {
			Color c = color.awtColor();
			if (c.getAlpha() == 255) {
				return "<rgb r=\"" + c.getRed() + "\" g=\"" + c.getGreen() + "\" b=\"" + c.getBlue() + "\"/>";
			} else {
				return "<rgba r=\"" + c.getRed() + "\" g=\"" + c.getGreen() + "\" b=\"" + c.getBlue() + "\" a=\"" + c.getAlpha() + "\"/>";
			}
		}
	}
	
	private static class GradientManager {
		public final LinkedHashMap<String,GradientPreset> gradientPresets;
		public final LinkedHashMap<String,GradientShape> gradientShapes;
		public final LinkedHashMap<String,GradientColorMap> gradientColors;
		
		public GradientManager() {
			gradientPresets = new LinkedHashMap<String,GradientPreset>();
			gradientShapes = new LinkedHashMap<String,GradientShape>();
			gradientColors = new LinkedHashMap<String,GradientColorMap>();
		}
		
		public void loadGradients(InputStream in) {
			Scanner sc = new Scanner(in);
			String gradientName = null;
			GradientShape gradientShape = null;
			GradientColorMap gradientColor = null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line.length() == 0 || line.startsWith("#")) {
					// blank line or comment
				} else if (line.startsWith("$")) {
					// shape
					String[] fields = line.split("[ ,]+");
					if (fields[0].startsWith("$Lin")) {
						double[] lf = parseDoubles(fields, 1, 4);
						boolean rep = false, ref = false, rev = false;
						for (int i = 5; i < fields.length; i++) {
							if (fields[i].startsWith("Rep")) rep = true;
							if (fields[i].startsWith("Ref")) ref = true;
							if (fields[i].startsWith("Rev")) rev = true;
						}
						gradientShape = new GradientShape.Linear(
							lf[0], lf[1], lf[2], lf[3],
							rep, ref, rev, gradientName
						);
					} else if (fields[0].startsWith("$Ang")) {
						double[] af = parseDoubles(fields, 1, 4);
						boolean rep = false, ref = false, rev = false;
						for (int i = 5; i < fields.length; i++) {
							if (fields[i].startsWith("Rep")) rep = true;
							if (fields[i].startsWith("Ref")) ref = true;
							if (fields[i].startsWith("Rev")) rev = true;
						}
						gradientShape = new GradientShape.Angular(
							af[0], af[1], af[2], af[3],
							rep, ref, rev, gradientName
						);
					} else if (fields[0].startsWith("$Rad")) {
						double[] rf = parseDoubles(fields, 1, 6);
						boolean rep = false, ref = false, rev = false;
						for (int i = 7; i < fields.length; i++) {
							if (fields[i].startsWith("Rep")) rep = true;
							if (fields[i].startsWith("Ref")) ref = true;
							if (fields[i].startsWith("Rev")) rev = true;
						}
						gradientShape = new GradientShape.Radial(
							rf[0], rf[1], rf[2], rf[3], rf[4], rf[5],
							rep, ref, rev, gradientName
						);
					} else if (fields[0].startsWith("$Rec")) {
						double[] rf = parseDoubles(fields, 1, 8);
						boolean rep = false, ref = false, rev = false;
						for (int i = 9; i < fields.length; i++) {
							if (fields[i].startsWith("Rep")) rep = true;
							if (fields[i].startsWith("Ref")) ref = true;
							if (fields[i].startsWith("Rev")) rev = true;
						}
						gradientShape = new GradientShape.Rectangular(
							rf[0], rf[1], rf[2], rf[3],
							rf[4], rf[5], rf[6], rf[7],
							rep, ref, rev, gradientName
						);
					} else {
						System.err.println(
							"Warning: Unknown gradient shape " + fields[0] + ". " +
							"Ignoring gradient " + gradientName + "."
						);
						gradientName = null;
						gradientShape = null;
						gradientColor = null;
					}
				} else if (line.startsWith("@")) {
					// color
					String[] fields = line.split("[ ,]+");
					double at = parseDouble(fields[0].substring(1));
					double[] ccc = parseDoubles(fields, 1, 3);
					if (gradientColor == null) gradientColor = new GradientColorMap(gradientName);
					gradientColor.add(new GradientColorStop(at, new GradientColor.RGB16(
						(int)Math.round(ccc[0]), (int)Math.round(ccc[1]), (int)Math.round(ccc[2])
					)));
				} else {
					if (gradientName != null) {
						if (gradientShape != null && gradientColor != null) {
							GradientPreset gradientPreset = new GradientPreset(gradientShape, gradientColor, gradientName);
							gradientPresets.put(gradientName, gradientPreset);
						} else if (gradientShape != null) {
							gradientShapes.put(gradientName, gradientShape);
						} else if (gradientColor != null) {
							gradientColors.put(gradientName, gradientColor);
						}
					}
					gradientName = line;
					gradientShape = null;
					gradientColor = null;
				}
			}
			if (gradientName != null) {
				if (gradientShape != null && gradientColor != null) {
					GradientPreset gradientPreset = new GradientPreset(gradientShape, gradientColor, gradientName);
					gradientPresets.put(gradientName, gradientPreset);
				} else if (gradientShape != null) {
					gradientShapes.put(gradientName, gradientShape);
				} else if (gradientColor != null) {
					gradientColors.put(gradientName, gradientColor);
				}
			}
			sc.close();
		}
		
		private static double parseDouble(String string) {
			try { return Double.parseDouble(string); }
			catch (NumberFormatException nfe) { return 0; }
		}
		
		private static double[] parseDoubles(String[] strings, int offset, int length) {
			double[] d = new double[length];
			for (int di = 0, si = offset; di < length && si < strings.length; di++, si++) {
				try { d[di] = Double.parseDouble(strings[si]); }
				catch (NumberFormatException nfe) { d[di] = 0; }
			}
			return d;
		}
	}
}
