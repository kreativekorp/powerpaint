package com.kreative.paint.util;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

public class ICCColorModel extends ColorModel {
	private final String name;
	private final ColorSpace colorspace;
	private final int components;
	private final ColorChannel[] channels;
	
	public ICCColorModel(String name, ICC_Profile profile) {
		this.name = name;
		this.colorspace = new ICC_ColorSpace(profile);
		this.components = colorspace.getNumComponents();
		this.channels = new ColorChannel[components + 1];
		for (int i = 0; i < components; i++) {
			String cn = colorspace.getName(i);
			String cs = abbreviate(cn);
			float min = colorspace.getMinValue(i);
			float max = colorspace.getMaxValue(i);
			float step = ((max - min) < 10f) ? 0.01f : 1f;
			channels[i] = new ColorChannel(cs, cn, min, max, step);
		}
		channels[components] = new ColorChannel("A", "Alpha", 0, 255, 1);
	}
	
	@Override public String getName() { return name; }
	@Override public ColorChannel[] getChannels() { return channels; }
	
	@Override
	public Color makeColor(float[] channels) {
		return new Color(colorspace, channels, channels[components] / 255f);
	}
	
	@Override
	public float[] unmakeColor(Color color, float[] channels) {
		if (channels == null) channels = new float[components + 1];
		channels = color.getColorComponents(colorspace, channels);
		channels[components] = color.getAlpha();
		return channels;
	}
	
	private static String abbreviate(String channelName) {
		if (channelName.equalsIgnoreCase("Black")) return "K";
		return channelName.substring(0, 1);
	}
}
