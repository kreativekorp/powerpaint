package com.kreative.paint.pattern;

import java.util.regex.Matcher;

public class Pattern {
	public static final Pattern BACKGROUND = new Pattern(0L, "Background");
	public static final Pattern BG_75_FG_25 = new Pattern(0xAA005500AA005500L, null);
	public static final Pattern BG_50_FG_50 = new Pattern(0xAA55AA55AA55AA55L, null);
	public static final Pattern BG_25_FG_75 = new Pattern(0xAAFF55FFAAFF55FFL, null);
	public static final Pattern FOREGROUND = new Pattern(-1L, "Foreground");
	
	public final int width;
	public final int height;
	public final int denominator;
	public final int[] values;
	public final String name;
	
	public Pattern(long pattern, String name) {
		this.width = 8;
		this.height = 8;
		this.denominator = 1;
		this.values = new int[64];
		this.name = name;
		for (int i = 0; i < 64; i++) {
			this.values[i] = (pattern < 0) ? 1 : 0;
			pattern <<= 1;
		}
	}
	
	public Pattern(int width, int height, int denominator, String name) {
		this.width = (width < 1) ? 1 : width;
		this.height = (height < 1) ? 1 : height;
		this.denominator = (denominator < 1) ? 1 : denominator;
		this.values = new int[this.width * this.height];
		this.name = name;
	}
	
	private static final java.util.regex.Pattern VALUE_PATTERN =
		java.util.regex.Pattern.compile("([._@#$%&*]|[0-9]+)");
	
	public Pattern(int width, int height, int denominator, String text, String name) {
		this(width, height, denominator, name);
		int i = 0, n = this.values.length;
		Matcher m = VALUE_PATTERN.matcher(text);
		while (i < n && m.find()) {
			String valueText = m.group();
			switch (valueText.charAt(0)) {
			case '.': case '_':
				this.values[i++] = 0;
				break;
			case '@': case '#': case '$':
			case '%': case '&': case '*':
				this.values[i++] = this.denominator;
				break;
			default:
				try {
					int value = Integer.parseInt(valueText);
					this.values[i++] = value;
				} catch (NumberFormatException nfe) {
					// ignored
				}
				break;
			}
		}
	}
	
	public int getIndex(int x, int y) {
		y %= height;
		if (y < 0) y += height;
		x %= width;
		if (x < 0) x += width;
		return (y * width) + x;
	}
	
	public int getRGB(int x, int y, int bg, int fg) {
		int value = values[getIndex(x, y)];
		if (value <= 0) return bg;
		if (value >= denominator) return fg;
		int bgA = (bg >> 24) & 0xFF;
		int bgR = (bg >> 16) & 0xFF;
		int bgG = (bg >>  8) & 0xFF;
		int bgB = (bg >>  0) & 0xFF;
		int fgA = (fg >> 24) & 0xFF;
		int fgR = (fg >> 16) & 0xFF;
		int fgG = (fg >>  8) & 0xFF;
		int fgB = (fg >>  0) & 0xFF;
		int a = bgA + (fgA - bgA) * value / denominator;
		int r = bgR + (fgR - bgR) * value / denominator;
		int g = bgG + (fgG - bgG) * value / denominator;
		int b = bgB + (fgB - bgB) * value / denominator;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public boolean isBackground() {
		for (int value : values) {
			if (value > 0) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isForeground() {
		for (int value : values) {
			if (value < denominator) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Pattern) {
			return this.equals((Pattern)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(Pattern that, boolean withName) {
		if (this.width != that.width) return false;
		if (this.height != that.height) return false;
		if (this.denominator != that.denominator) return false;
		if (this.values.length != that.values.length) return false;
		for (int i = 0; i < this.values.length; i++) {
			if (this.values[i] != that.values[i]) return false;
		}
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = width + height + denominator;
		for (int value : values) hashCode += value;
		return hashCode;
	}
}
