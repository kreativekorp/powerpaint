package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import com.kreative.paint.powershape.Parameter;
import com.kreative.paint.powershape.ParameterizedPath;
import com.kreative.paint.powershape.ParameterizedShape;
import com.kreative.paint.powershape.ParameterizedValue;
import com.kreative.paint.powershape.PowerShape;
import com.kreative.paint.powershape.PowerShapeList;
import com.kreative.paint.powershape.WindingRule;

public class ToXMLShape {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			String name = file.getName()
					.replaceAll("^#[0-9]+\\s+", "")
					.replaceAll("\\.shapes$", "");
			InputStream in  = new FileInputStream(file);
			PowerShapeList shapes = parseShapes(name, in);
			in.close();
			System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			System.out.println("<!DOCTYPE shapes PUBLIC \"-//Kreative//DTD PowerShape 1.0//EN\" \"shpx.dtd\">");
			System.out.println("<shapes name=\"" + xmls(name) + "\">");
			for (PowerShape shape : shapes) {
				System.out.print("\t<shape name=\"" + xmls(shape.name) + "\"");
				switch (shape.windingRule) {
				case EVEN_ODD: System.out.print(" winding=\"evenodd\""); break;
				case NON_ZERO: break;
				}
				System.out.println(">");
				for (String pn : shape.getParameterNames()) {
					Parameter param = shape.getParameter(pn);
					System.out.println("\t\t<param name=\"" + xmls(pn) + "\"");
					System.out.println("\t\t       origin-x=\"" + dbls(param.originX) + "\" origin-y=\"" + dbls(param.originY) + "\"");
					if (param.polar) {
						System.out.println("\t\t       coords=\"polar\"");
						System.out.println("\t\t       min-r=\"" + dbls(param.minR) + "\" min-a=\"" + dbls(Math.toDegrees(param.minA)) + "\"");
						System.out.println("\t\t       def-r=\"" + dbls(param.defR) + "\" def-a=\"" + dbls(Math.toDegrees(param.defA)) + "\"");
						System.out.println("\t\t       max-r=\"" + dbls(param.maxR) + "\" max-a=\"" + dbls(Math.toDegrees(param.maxA)) + "\"/>");
					} else {
						System.out.println("\t\t       min-x=\"" + dbls(param.minX) + "\" min-y=\"" + dbls(param.minY) + "\"");
						System.out.println("\t\t       def-x=\"" + dbls(param.defX) + "\" def-y=\"" + dbls(param.defY) + "\"");
						System.out.println("\t\t       max-x=\"" + dbls(param.maxX) + "\" max-y=\"" + dbls(param.maxY) + "\"/>");
					}
				}
				for (ParameterizedShape ss : shape.getShapes()) {
					if (ss instanceof ParameterizedPath) {
						ParameterizedPath pp = (ParameterizedPath)ss;
						for (int i = 0, n = pp.size(); i < n; i++) {
							if (i == 0) System.out.print("\t\t<path d=\"");
							else System.out.print("\t\t         ");
							System.out.print(pp.getOpcode(i));
							for (ParameterizedValue pv : pp.getOperands(i)) {
								System.out.print(" " + pvs(pv));
							}
							if (i == n-1) System.out.println("\"/>");
							else System.out.println();
						}
					}
				}
				System.out.println("\t</shape>");
			}
			System.out.println("</shapes>");
		}
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;")
		        .replaceAll("\u2264", "&#8804;")
		        .replaceAll("\u2265", "&#8805;")
		        .replaceAll("\u2122", "&#8482;");
	}
	
	private static String dbls(double d) {
		if (d == (long)d) return Long.toString((long)d);
		else return Double.toString(d);
	}
	
	private static String pvs(ParameterizedValue v) {
		String s = v.source;
		s = s.replaceAll("toDeg(rees)?\\(0(\\.0)?\\)", "0");
		s = s.replaceAll("toDeg(rees)?\\(pi\\)", "180");
		return xmls(s);
	}
	
	private static PowerShapeList parseShapes(String name, InputStream in) {
		PowerShapeList shapes = new PowerShapeList(name);
		String lastName = null;
		WindingRule lastWinding = null;
		List<Parameter> lastParams = null;
		ParameterizedPath lastPath = null;
		Scanner sc = new Scanner(in, "UTF-8");
		while (sc.hasNextLine()) {
			String s = sc.nextLine();
			if (s.trim().length() > 0 && !s.trim().startsWith("#")) {
				if (s.startsWith("\t")) {
					if (lastPath != null) {
						String[] fields = s.trim().split("\\s+");
						try {
							switch (fields[0].charAt(0)) {
							case 'p':
								lastParams.add(new Parameter(
									fields[1].trim(),
									Double.parseDouble(fields[2]), Double.parseDouble(fields[3]),
									fields[4].trim().toLowerCase().startsWith("p"),
									Double.parseDouble(fields[5]), Double.parseDouble(fields[6]),
									Double.parseDouble(fields[5]), Math.toRadians(Double.parseDouble(fields[6])),
									Double.parseDouble(fields[7]), Double.parseDouble(fields[8]),
									Double.parseDouble(fields[7]), Math.toRadians(Double.parseDouble(fields[8])),
									Double.parseDouble(fields[9]), Double.parseDouble(fields[10]),
									Double.parseDouble(fields[9]), Math.toRadians(Double.parseDouble(fields[10]))
								));
								break;
							case 'm':
								lastPath.add(
									'M',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2])
								);
								break;
							case 'l':
								lastPath.add(
									'L',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2])
								);
								break;
							case 'q':
								lastPath.add(
									'Q',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4])
								);
								break;
							case 'c':
								lastPath.add(
									'C',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4]),
									new ParameterizedValue(fields[5]),
									new ParameterizedValue(fields[6])
								);
								break;
							case 'a':
								lastPath.add(
									'G',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4])
								);
								break;
							case 'x':
								lastPath.add('Z');
								break;
							case 'w':
								String rule = fields[1].toLowerCase().replaceAll("[^a-z]", "");
								if (rule.equals("eo") || rule.equals("evenodd")) lastWinding = WindingRule.EVEN_ODD;
								if (rule.equals("nz") || rule.equals("nonzero")) lastWinding = WindingRule.NON_ZERO;
								break;
							case 'r':
								lastPath.add(
									'R',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4]),
									new ParameterizedValue(0.0),
									new ParameterizedValue(0.0)
								);
								break;
							case 'd':
								lastPath.add(
									'R',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4]),
									new ParameterizedValue(fields[5]),
									new ParameterizedValue(fields[6])
								);
								break;
							case 'e':
								lastPath.add(
									'E',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4]),
									new ParameterizedValue(0.0),
									new ParameterizedValue(360.0),
									new ParameterizedValue(0.0)
								);
								break;
							case 'h':
								boolean connect = (fields.length > 7) && fields[7].trim().toLowerCase().equals("connect");
								lastPath.add(
									'E',
									new ParameterizedValue(fields[1]),
									new ParameterizedValue(fields[2]),
									new ParameterizedValue(fields[3]),
									new ParameterizedValue(fields[4]),
									new ParameterizedValue("toDeg("+fields[5]+")"),
									new ParameterizedValue("toDeg("+fields[6]+")"),
									new ParameterizedValue(connect ? 4.0 : 3.0)
								);
								break;
							default:
								System.err.println("Warning: Invalid instruction '" + fields[0] + "' in shape " + lastName + " in shape resource " + name + ".");
								break;
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Warning: Invalid numeric value in shape " + lastName + " in shape resource " + name + ".");
						} catch (IndexOutOfBoundsException ioobe) {
							System.err.println("Warning: Too few parameters in shape " + lastName + " in shape resource " + name + ".");
						}
					} else {
						System.err.println("Warning: Shape resource " + name + " put the cart before the horse.");
					}
				} else {
					if (lastPath != null) {
						PowerShape ps = new PowerShape(lastWinding, lastName);
						for (Parameter p : lastParams) ps.addParameter(p);
						ps.addShape(lastPath);
						shapes.add(ps);
					}
					lastName = s.trim();
					lastWinding = WindingRule.NON_ZERO;
					lastParams = new Vector<Parameter>();
					lastPath = new ParameterizedPath();
				}
			}
		}
		sc.close();
		if (lastPath != null) {
			PowerShape ps = new PowerShape(lastWinding, lastName);
			for (Parameter p : lastParams) ps.addParameter(p);
			ps.addShape(lastPath);
			shapes.add(ps);
		}
		return shapes;
	}
}
