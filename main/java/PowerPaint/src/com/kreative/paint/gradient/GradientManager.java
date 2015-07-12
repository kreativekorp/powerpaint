package com.kreative.paint.gradient;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class GradientManager {
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
