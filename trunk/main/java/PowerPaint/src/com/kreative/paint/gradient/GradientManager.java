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

package com.kreative.paint.gradient;

import java.awt.Color;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class GradientManager {
	public static final LinearGradientShape DEFAULT_SHAPE = new LinearGradientShape(0.0,0.5,1.0,0.5);
	public static final GradientColorMap DEFAULT_COLORS = new GradientColorMap();
	static {
		DEFAULT_COLORS.put(0.0,Color.black);
		DEFAULT_COLORS.put(1.0, Color.white);
	}
	public static final Gradient DEFAULT_GRADIENT = new Gradient(DEFAULT_SHAPE, DEFAULT_COLORS);
	private LinkedHashMap<String,Gradient> gradientPresets = new LinkedHashMap<String,Gradient>();
	private LinkedHashMap<String,GradientShape> gradientShapes = new LinkedHashMap<String,GradientShape>();
	private LinkedHashMap<String,GradientColorMap> gradientColors = new LinkedHashMap<String,GradientColorMap>();
	
	public void loadGradients(InputStream in) {
		Scanner sc = new Scanner(in);
		
		String gradientName = null;
		GradientShape gradientShape = null;
		GradientColorMap gradientColor = null;

		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) {
				// blank line or comment
			}
			else if (line.startsWith("$")) {
				// shape
				String[] fields = line.split("[ ,]+");
				if (fields[0].startsWith("$Lin")) {
					double[] lf = parseDoubles(fields, 1, 4);
					gradientShape = new LinearGradientShape(lf[0], lf[1], lf[2], lf[3]);
					for (int i = 5; i < fields.length; i++) {
						if (fields[i].startsWith("Rep")) gradientShape.repeat = true;
						if (fields[i].startsWith("Ref")) gradientShape.reflect = true;
						if (fields[i].startsWith("Rev")) gradientShape.reverse = true;
					}
				}
				else if (fields[0].startsWith("$Ang")) {
					double[] af = parseDoubles(fields, 1, 4);
					gradientShape = new AngularGradientShape(af[0], af[1], af[2], af[3]);
					for (int i = 5; i < fields.length; i++) {
						if (fields[i].startsWith("Rep")) gradientShape.repeat = true;
						if (fields[i].startsWith("Ref")) gradientShape.reflect = true;
						if (fields[i].startsWith("Rev")) gradientShape.reverse = true;
					}
				}
				else if (fields[0].startsWith("$Rad")) {
					double[] rf = parseDoubles(fields, 1, 6);
					gradientShape = new RadialGradientShape(rf[0], rf[1], rf[2], rf[3], rf[4], rf[5]);
					for (int i = 7; i < fields.length; i++) {
						if (fields[i].startsWith("Rep")) gradientShape.repeat = true;
						if (fields[i].startsWith("Ref")) gradientShape.reflect = true;
						if (fields[i].startsWith("Rev")) gradientShape.reverse = true;
					}
				}
				else if (fields[0].startsWith("$Rec")) {
					double[] rf = parseDoubles(fields, 1, 8);
					gradientShape = new RectangularGradientShape(rf[0], rf[1], rf[2], rf[3], rf[4], rf[5], rf[6], rf[7]);
					for (int i = 9; i < fields.length; i++) {
						if (fields[i].startsWith("Rep")) gradientShape.repeat = true;
						if (fields[i].startsWith("Ref")) gradientShape.reflect = true;
						if (fields[i].startsWith("Rev")) gradientShape.reverse = true;
					}
				}
				else {
					System.err.println("Warning: Unknown gradient shape "+fields[0]+". Ignoring gradient "+gradientName+".");
					gradientName = null;
					gradientShape = null;
					gradientColor = null;
				}
			}
			else if (line.startsWith("@")) {
				// color
				String[] fields = line.split("[ ,]+");
				double at = parseDouble(fields[0].substring(1));
				double[] ccc = parseDoubles(fields, 1, 3);
				if (gradientColor == null) gradientColor = new GradientColorMap();
				gradientColor.put(at, new Color((float)ccc[0]/65535.0f, (float)ccc[1]/65535.0f, (float)ccc[2]/65535.0f));
			}
			else {
				if (gradientName != null) {
					if (gradientShape != null && gradientColor != null) {
						gradientPresets.put(gradientName, new Gradient(gradientShape, gradientColor));
					}
					else if (gradientShape != null) {
						gradientShapes.put(gradientName, gradientShape);
					}
					else if (gradientColor != null) {
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
				gradientPresets.put(gradientName, new Gradient(gradientShape, gradientColor));
			}
			else if (gradientShape != null) {
				gradientShapes.put(gradientName, gradientShape);
			}
			else if (gradientColor != null) {
				gradientColors.put(gradientName, gradientColor);
			}
		}

		sc.close();
	}
	
	public LinkedHashMap<String,Gradient> gradientPresets() {
		return gradientPresets;
	}
	
	public LinkedHashMap<String,GradientShape> gradientShapes() {
		return gradientShapes;
	}
	
	public LinkedHashMap<String,GradientColorMap> gradientColors() {
		return gradientColors;
	}
	
	private static double parseDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	private static double[] parseDoubles(String[] strings, int offset, int length) {
		double[] d = new double[length];
		for (int di = 0, si = offset; di < length && si < strings.length; di++, si++) {
			try {
				d[di] = Double.parseDouble(strings[si]);
			} catch (NumberFormatException nfe) {
				d[di] = 0;
			}
		}
		return d;
	}
}
