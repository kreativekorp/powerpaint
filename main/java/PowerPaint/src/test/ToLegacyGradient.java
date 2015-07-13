package test;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.kreative.paint.gradient.GradientColor;
import com.kreative.paint.gradient.GradientColorMap;
import com.kreative.paint.gradient.GradientColorStop;
import com.kreative.paint.gradient.GradientList;
import com.kreative.paint.gradient.GradientParser;
import com.kreative.paint.gradient.GradientPreset;
import com.kreative.paint.gradient.GradientShape;

public class ToLegacyGradient {
	public static void main(String[] args) throws IOException {
		boolean first = true;
		for (String arg : args) {
			File file = new File(arg);
			InputStream in = new FileInputStream(file);
			GradientList list = GradientParser.parse(file.getName(), in);
			in.close();
			for (GradientShape shape : list.shapes) {
				if (first) first = false; else System.out.println();
				System.out.println(shape.name);
				System.out.println(shapeToString(shape));
			}
			for (GradientColorMap map : list.colorMaps) {
				if (first) first = false; else System.out.println();
				System.out.println(map.name);
				for (GradientColorStop stop : map) {
					System.out.println("@" + stop.position + " " + colorToString(stop.color));
				}
			}
			for (GradientPreset preset : list.presets) {
				if (first) first = false; else System.out.println();
				System.out.println(preset.name);
				System.out.println(shapeToString(preset.shape));
				for (GradientColorStop stop : preset.colorMap) {
					System.out.println("@" + stop.position + " " + colorToString(stop.color));
				}
			}
		}
	}
	
	private static String shapeToString(GradientShape shape) {
		StringBuffer s = new StringBuffer();
		if (shape instanceof GradientShape.Linear) {
			GradientShape.Linear sh = (GradientShape.Linear)shape;
			s.append("$Lin " + sh.x0 + "," + sh.y0
			           + " " + sh.x1 + "," + sh.y1);
		} else if (shape instanceof GradientShape.Angular) {
			GradientShape.Angular sh = (GradientShape.Angular)shape;
			s.append("$Ang " + sh.cx + "," + sh.cy
			           + " " + sh.px + "," + sh.py);
		} else if (shape instanceof GradientShape.Radial) {
			GradientShape.Radial sh = (GradientShape.Radial)shape;
			s.append("$Rad " + sh.cx + "," + sh.cy
			           + " " + sh.x0 + "," + sh.y0
			           + " " + sh.x1 + "," + sh.y1);
		} else if (shape instanceof GradientShape.Rectangular) {
			GradientShape.Rectangular sh = (GradientShape.Rectangular)shape;
			s.append("$Rec " + sh.l0 + "," + sh.t0
			           + " " + sh.r0 + "," + sh.b0
			           + " " + sh.l1 + "," + sh.t1
			           + " " + sh.r1 + "," + sh.b1);
		} else {
			s.append("$" + shape.getClass().getSimpleName());
		}
		if (shape.repeat) s.append(" Rep");
		if (shape.reflect) s.append(" Ref");
		if (shape.reverse) s.append(" Rev");
		return s.toString();
	}
	
	private static String colorToString(GradientColor color) {
		if (color instanceof GradientColor.RGB) {
			GradientColor.RGB c = (GradientColor.RGB)color;
			return (c.r*257) + "," + (c.g*257) + "," + (c.b*257);
		} else if (color instanceof GradientColor.RGB16) {
			GradientColor.RGB16 c = (GradientColor.RGB16)color;
			return c.r + "," + c.g + "," + c.b;
		} else if (color instanceof GradientColor.RGBA) {
			GradientColor.RGBA c = (GradientColor.RGBA)color;
			return (c.r*257) + "," + (c.g*257) + "," + (c.b*257) + "," + (c.a*257);
		} else if (color instanceof GradientColor.RGBA16) {
			GradientColor.RGBA16 c = (GradientColor.RGBA16)color;
			return c.r + "," + c.g + "," + c.b + "," + c.a;
		} else if (color instanceof GradientColor.HSV) {
			Color c = color.awtColor();
			return (c.getRed()*257) + "," + (c.getGreen()*257) + "," + (c.getBlue()*257);
		} else {
			Color c = color.awtColor();
			return (c.getRed()*257) + "," + (c.getGreen()*257) + "," + (c.getBlue()*257) + "," + (c.getAlpha()*257);
		}
	}
}
