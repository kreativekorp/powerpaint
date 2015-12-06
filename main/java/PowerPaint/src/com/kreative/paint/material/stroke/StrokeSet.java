package com.kreative.paint.material.stroke;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class StrokeSet {
	public final Set<PowerStroke> strokes;
	public final SortedSet<Float> widths;
	public final SortedSet<Integer> multiplicities;
	public final Set<float[]> dashes;
	public final Set<Arrowhead> arrowheads;
	public final String name;
	
	public StrokeSet(String name) {
		this.strokes = new LinkedHashSet<PowerStroke>();
		this.widths = new TreeSet<Float>();
		this.multiplicities = new TreeSet<Integer>();
		this.dashes = new LinkedHashSet<float[]>();
		this.arrowheads = new LinkedHashSet<Arrowhead>();
		this.name = name;
	}
}
