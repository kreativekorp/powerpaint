package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import com.kreative.paint.material.stroke.Arrowhead;
import com.kreative.paint.material.stroke.StrokeParser;
import com.kreative.paint.material.stroke.StrokeSet;

public class StrokeLoader {
	private final MaterialLoader loader;
	private final SortedSet<Float> widths;
	private final SortedSet<Integer> multiplicities;
	private final LinkedHashSet<float[]> dashes;
	private final LinkedHashSet<Arrowhead> arrowheads;
	
	public StrokeLoader(MaterialLoader loader) {
		this.loader = loader;
		this.widths = new TreeSet<Float>();
		this.multiplicities = new TreeSet<Integer>();
		this.dashes = new LinkedHashSet<float[]>();
		this.arrowheads = new LinkedHashSet<Arrowhead>();
	}
	
	public SortedSet<Float> getLineWidths() {
		if (isEmpty()) loadResources();
		if (widths.isEmpty()) createWidths();
		return widths;
	}
	
	public SortedSet<Integer> getLineMultiplicities() {
		if (isEmpty()) loadResources();
		if (multiplicities.isEmpty()) createMultiplicities();
		return multiplicities;
	}
	
	public LinkedHashSet<float[]> getLineDashes() {
		if (isEmpty()) loadResources();
		if (dashes.isEmpty()) createDashes();
		return dashes;
	}
	
	public LinkedHashSet<Arrowhead> getLineArrowheads() {
		if (isEmpty()) loadResources();
		if (arrowheads.isEmpty()) createArrowheads();
		return arrowheads;
	}
	
	private boolean isEmpty() {
		return widths.isEmpty()
		    && multiplicities.isEmpty()
		    && dashes.isEmpty()
		    && arrowheads.isEmpty();
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("lnsx", false)) {
				try {
					InputStream in = r.getInputStream();
					StrokeSet ss = StrokeParser.parse(r.getResourceName(), in);
					in.close();
					for (float width : ss.widths) widths.add(width);
					for (int multiplicity : ss.multiplicities) multiplicities.add(multiplicity);
					for (float[] dash : ss.dashes) dashes.add(dash);
					for (Arrowhead arrowhead : ss.arrowheads) arrowheads.add(arrowhead);
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile stroke set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createWidths() {
		System.err.println("Notice: No line widths found. Generating generic line widths.");
		for (int i = 0; i <= 12; i++) {
			widths.add((float)i);
		}
	}
	
	private void createMultiplicities() {
		System.err.println("Notice: No line multiplicies found. Generating generic line multiplicities.");
		for (int i = 1; i <= 3; i++) {
			multiplicities.add(i);
		}
	}
	
	private void createDashes() {
		System.err.println("Notice: No dashes found. Generating generic dashes.");
		dashes.add(null);
		for (int i = 1; i <= 5; i++) {
			dashes.add(new float[]{i,i});
		}
	}
	
	private void createArrowheads() {
		System.err.println("Notice: No arrowheads found. Generating generic arrowheads.");
		arrowheads.add(null);
		arrowheads.add(Arrowhead.GENERAL_FILLED_ARROW);
		arrowheads.add(Arrowhead.GENERAL_STROKED_ARROW);
		arrowheads.add(Arrowhead.GENERAL_FILLED_CIRCLE);
		arrowheads.add(Arrowhead.GENERAL_STROKED_CIRCLE);
	}
}
