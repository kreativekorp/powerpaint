package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.material.dither.DiffusionDitherAlgorithm;
import com.kreative.paint.material.dither.DitherAlgorithm;
import com.kreative.paint.material.dither.DitherAlgorithmList;
import com.kreative.paint.material.dither.DitherAlgorithmParser;

public class DitherLoader {
	private final MaterialLoader loader;
	private final MaterialList<DitherAlgorithm> algorithms;
	
	public DitherLoader(MaterialLoader loader) {
		this.loader = loader;
		this.algorithms = new MaterialList<DitherAlgorithm>();
	}
	
	public MaterialList<DitherAlgorithm> getDitherAlgorithms() {
		if (algorithms.isEmpty()) loadResources();
		if (algorithms.isEmpty()) createAlgorithms();
		return algorithms;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("ditx", false)) {
				try {
					InputStream in = r.getInputStream();
					DitherAlgorithmList list = DitherAlgorithmParser.parse(r.getResourceName(), in);
					in.close();
					for (DitherAlgorithm algorithm : list) {
						algorithms.add(algorithm.name, algorithm);
					}
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile dither algorithm set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createAlgorithms() {
		System.err.println("Notice: No dither algorithms found. Generating generic dither algorithms.");
		algorithms.add("Threshold", DiffusionDitherAlgorithm.THRESHOLD);
		algorithms.add("Floyd-Steinberg", DiffusionDitherAlgorithm.FLOYD_STEINBERG);
	}
}
