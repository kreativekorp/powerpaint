package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import com.kreative.paint.material.gradient.GradientColorMap;
import com.kreative.paint.material.gradient.GradientList;
import com.kreative.paint.material.gradient.GradientParser;
import com.kreative.paint.material.gradient.GradientPreset;
import com.kreative.paint.material.gradient.GradientShape;

public class GradientLoader {
	private final MaterialLoader loader;
	private final LinkedHashMap<String,GradientPreset> presets;
	private final LinkedHashMap<String,GradientShape> shapes;
	private final LinkedHashMap<String,GradientColorMap> colorMaps;
	
	public GradientLoader(MaterialLoader loader) {
		this.loader = loader;
		this.presets = new LinkedHashMap<String,GradientPreset>();
		this.shapes = new LinkedHashMap<String,GradientShape>();
		this.colorMaps = new LinkedHashMap<String,GradientColorMap>();
	}
	
	public LinkedHashMap<String,GradientPreset> getGradientPresets() {
		if (isEmpty()) loadResources();
		if (presets.isEmpty()) createPresets();
		return presets;
	}
	
	public LinkedHashMap<String,GradientShape> getGradientShapes() {
		if (isEmpty()) loadResources();
		if (shapes.isEmpty()) createShapes();
		return shapes;
	}
	
	public LinkedHashMap<String,GradientColorMap> getGradientColorMaps() {
		if (isEmpty()) loadResources();
		if (colorMaps.isEmpty()) createColorMaps();
		return colorMaps;
	}
	
	private boolean isEmpty() {
		return presets.isEmpty()
		    && shapes.isEmpty()
		    && colorMaps.isEmpty();
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("grdx", false)) {
				try {
					InputStream in = r.getInputStream();
					GradientList list = GradientParser.parse(r.getResourceName(), in);
					in.close();
					for (GradientPreset preset : list.presets) presets.put(preset.name, preset);
					for (GradientShape shape : list.shapes) shapes.put(shape.name, shape);
					for (GradientColorMap map : list.colorMaps) colorMaps.put(map.name, map);
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile gradient set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createPresets() {
		System.err.println("Notice: No gradient presets found. Generating generic gradient presets.");
		presets.put(GradientPreset.BLACK_TO_WHITE.name, GradientPreset.BLACK_TO_WHITE);
		presets.put(GradientPreset.WHITE_TO_BLACK.name, GradientPreset.WHITE_TO_BLACK);
		presets.put(GradientPreset.RGB_SPECTRUM.name, GradientPreset.RGB_SPECTRUM);
		presets.put(GradientPreset.RGB_WHEEL.name, GradientPreset.RGB_WHEEL);
	}
	
	private void createShapes() {
		System.err.println("Notice: No gradient shapes found. Generating generic gradient shapes.");
		shapes.put(GradientShape.SIMPLE_LINEAR.name, GradientShape.SIMPLE_LINEAR);
		shapes.put(GradientShape.REVERSE_LINEAR.name, GradientShape.REVERSE_LINEAR);
		shapes.put(GradientShape.SIMPLE_ANGULAR.name, GradientShape.SIMPLE_ANGULAR);
		shapes.put(GradientShape.REVERSE_ANGULAR.name, GradientShape.REVERSE_ANGULAR);
	}
	
	private void createColorMaps() {
		System.err.println("Notice: No gradient color maps found. Generating generic gradient color maps.");
		colorMaps.put(GradientColorMap.BLACK_TO_WHITE.name, GradientColorMap.BLACK_TO_WHITE);
		colorMaps.put(GradientColorMap.WHITE_TO_BLACK.name, GradientColorMap.WHITE_TO_BLACK);
		colorMaps.put(GradientColorMap.RGB_SPECTRUM.name, GradientColorMap.RGB_SPECTRUM);
	}
}
