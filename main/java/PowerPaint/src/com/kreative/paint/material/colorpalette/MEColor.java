package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.io.IOException;

public abstract class MEColor {
	public abstract Color toColor();
	public abstract RCPXColor toRCPXColor(String name);
	public abstract String toString();
	public abstract void archiveTo(MEArchiver arc) throws IOException;
	
	public static class CalibratedRGB extends MEColor {
		public float r, g, b, a;
		public Color toColor() {
			return new Color(r, g, b, a);
		}
		public RCPXColor toRCPXColor(String name) {
			return new RCPXColor.RGBAD(r, g, b, a, name);
		}
		public String toString() {
			return CalibratedRGB.class.getName() + "[r=" + r + ",g=" + g + ",b=" + b + ",a=" + a + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 1);
			arc.writeValuesOfTypes("ffff", r, g, b, a);
		}
	}
	
	public static class DeviceRGB extends MEColor {
		public float r, g, b, a;
		public Color toColor() {
			return new Color(r, g, b, a);
		}
		public RCPXColor toRCPXColor(String name) {
			return new RCPXColor.RGBAD(r, g, b, a, name);
		}
		public String toString() {
			return DeviceRGB.class.getName() + "[r=" + r + ",g=" + g + ",b=" + b + ",a=" + a + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 2);
			arc.writeValuesOfTypes("ffff", r, g, b, a);
		}
	}
	
	public static class CalibratedGray extends MEColor {
		public float gray, alpha;
		public Color toColor() {
			return new Color(gray, gray, gray, alpha);
		}
		public RCPXColor toRCPXColor(String name) {
			return new RCPXColor.GrayAlpha(gray * 100f, alpha * 100f, name);
		}
		public String toString() {
			return CalibratedGray.class.getName() + "[gray=" + gray + ",alpha=" + alpha + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 3);
			arc.writeValuesOfTypes("ff", gray, alpha);
		}
	}
	
	public static class DeviceGray extends MEColor {
		public float gray, alpha;
		public Color toColor() {
			return new Color(gray, gray, gray, alpha);
		}
		public RCPXColor toRCPXColor(String name) {
			return new RCPXColor.GrayAlpha(gray * 100f, alpha * 100f, name);
		}
		public String toString() {
			return DeviceGray.class.getName() + "[gray=" + gray + ",alpha=" + alpha + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 4);
			arc.writeValuesOfTypes("ff", gray, alpha);
		}
	}
	
	public static class CMYK extends MEColor {
		public float c, m, y, k, a;
		public Color toColor() {
			return new Color(RCPXColor.CMYK_COLOR_SPACE, new float[]{c, m, y, k}, a);
		}
		public RCPXColor toRCPXColor(String name) {
			return new RCPXColor.CMYKA(c * 100f, m * 100f, y * 100f, k * 100f, a * 100f, name);
		}
		public String toString() {
			return CMYK.class.getName() + "[c=" + c + ",m=" + m + ",y=" + y + ",k=" + k + ",a=" + a + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 5);
			arc.writeValuesOfTypes("fffff", c, m, y, k, a);
		}
	}
	
	public static class Named extends MEColor {
		public String colorSpace;
		public String colorName;
		public MEColor fallback;
		public Color toColor() {
			return fallback.toColor();
		}
		public RCPXColor toRCPXColor(String name) {
			return fallback.toRCPXColor(name);
		}
		public String toString() {
			return Named.class.getName() + "[cs=" + colorSpace + ",cn=" + colorName + ",fb=" + fallback + "]";
		}
		public void archiveTo(MEArchiver arc) throws IOException {
			arc.writeValueOfType("c", 6);
			arc.writeValuesOfTypes("@@@", colorSpace, colorName, fallback);
		}
	}
	
	public static MEColor fromRCPXColor(RCPXColor rc) {
		if (rc instanceof RCPXColor.CMYK) {
			RCPXColor.CMYK cmyk = (RCPXColor.CMYK)rc;
			MEColor.CMYK me = new MEColor.CMYK();
			me.c = cmyk.c / 100f;
			me.m = cmyk.m / 100f;
			me.y = cmyk.y / 100f;
			me.k = cmyk.k / 100f;
			me.a = 1;
			return me;
		} else if (rc instanceof RCPXColor.CMYKA) {
			RCPXColor.CMYKA cmyka = (RCPXColor.CMYKA)rc;
			MEColor.CMYK me = new MEColor.CMYK();
			me.c = cmyka.c / 100f;
			me.m = cmyka.m / 100f;
			me.y = cmyka.y / 100f;
			me.k = cmyka.k / 100f;
			me.a = cmyka.a / 100f;
			return me;
		} else if (rc instanceof RCPXColor.Gray) {
			RCPXColor.Gray gray = (RCPXColor.Gray)rc;
			MEColor.CalibratedGray me = new MEColor.CalibratedGray();
			me.gray = gray.gray / 100f;
			me.alpha = 1;
			return me;
		} else if (rc instanceof RCPXColor.GrayAlpha) {
			RCPXColor.GrayAlpha gray = (RCPXColor.GrayAlpha)rc;
			MEColor.CalibratedGray me = new MEColor.CalibratedGray();
			me.gray = gray.gray / 100f;
			me.alpha = gray.alpha / 100f;
			return me;
		} else {
			float[] rgba = rc.awtColor().getRGBComponents(null);
			MEColor.CalibratedRGB me = new MEColor.CalibratedRGB();
			me.r = rgba[0]; me.g = rgba[1]; me.b = rgba[2]; me.a = rgba[3];
			return me;
		}
	}
	
	public static MEColor unarchiveFrom(MEUnarchiver u) throws IOException {
		int cs = ((Number)u.readValueOfType("c")).intValue();
		switch (cs) {
			case 1:
				Object[] crgbv = u.readValuesOfTypes("ffff");
				CalibratedRGB crgb = new CalibratedRGB();
				crgb.r = ((Number)crgbv[0]).floatValue();
				crgb.g = ((Number)crgbv[1]).floatValue();
				crgb.b = ((Number)crgbv[2]).floatValue();
				crgb.a = ((Number)crgbv[3]).floatValue();
				return crgb;
			case 2:
				Object[] drgbv = u.readValuesOfTypes("ffff");
				DeviceRGB drgb = new DeviceRGB();
				drgb.r = ((Number)drgbv[0]).floatValue();
				drgb.g = ((Number)drgbv[1]).floatValue();
				drgb.b = ((Number)drgbv[2]).floatValue();
				drgb.a = ((Number)drgbv[3]).floatValue();
				return drgb;
			case 3:
				Object[] cgrayv = u.readValuesOfTypes("ff");
				CalibratedGray cgray = new CalibratedGray();
				cgray.gray = ((Number)cgrayv[0]).floatValue();
				cgray.alpha = ((Number)cgrayv[1]).floatValue();
				return cgray;
			case 4:
				Object[] dgrayv = u.readValuesOfTypes("ff");
				DeviceGray dgray = new DeviceGray();
				dgray.gray = ((Number)dgrayv[0]).floatValue();
				dgray.alpha = ((Number)dgrayv[1]).floatValue();
				return dgray;
			case 5:
				Object[] cmykv = u.readValuesOfTypes("fffff");
				CMYK cmyk = new CMYK();
				cmyk.c = ((Number)cmykv[0]).floatValue();
				cmyk.m = ((Number)cmykv[1]).floatValue();
				cmyk.y = ((Number)cmykv[2]).floatValue();
				cmyk.k = ((Number)cmykv[3]).floatValue();
				cmyk.a = ((Number)cmykv[4]).floatValue();
				return cmyk;
			case 6:
				Object[] namedv = u.readValuesOfTypes("@@@");
				Named named = new Named();
				named.colorSpace = namedv[0].toString();
				named.colorName = namedv[1].toString();
				named.fallback = (MEColor)namedv[2];
				return named;
			default:
				throw new IOException("Unknown color space " + cs);
		}
	}
}
