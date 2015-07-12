package com.kreative.paint.gradient;

import java.util.ArrayList;
import java.util.List;

public class GradientList {
	public final List<GradientPreset> presets;
	public final List<GradientShape> shapes;
	public final List<GradientColorMap> colorMaps;
	public final String name;
	
	public GradientList(String name) {
		this.presets = new ArrayList<GradientPreset>();
		this.shapes = new ArrayList<GradientShape>();
		this.colorMaps = new ArrayList<GradientColorMap>();
		this.name = name;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof GradientList) {
			return this.equals((GradientList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(GradientList that, boolean withName) {
		if (!this.presets.equals(that.presets)) return false;
		if (!this.shapes.equals(that.shapes)) return false;
		if (!this.colorMaps.equals(that.colorMaps)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return presets.hashCode() ^ shapes.hashCode() ^ colorMaps.hashCode();
	}
}
