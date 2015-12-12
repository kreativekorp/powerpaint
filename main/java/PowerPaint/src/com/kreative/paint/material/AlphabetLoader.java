package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.material.alphabet.Alphabet;
import com.kreative.paint.material.alphabet.AlphabetList;
import com.kreative.paint.material.alphabet.AlphabetParser;

public class AlphabetLoader {
	private final MaterialLoader loader;
	private final MaterialList<Alphabet> alphabets;
	
	public AlphabetLoader(MaterialLoader loader) {
		this.loader = loader;
		this.alphabets = new MaterialList<Alphabet>();
	}
	
	public MaterialList<Alphabet> getAlphabets() {
		if (alphabets.isEmpty()) loadResources();
		if (alphabets.isEmpty()) createAlphabets();
		return alphabets;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("alpx", false)) {
				try {
					InputStream in = r.getInputStream();
					AlphabetList list = AlphabetParser.parse(r.getResourceName(), in);
					in.close();
					for (Alphabet a : list) {
						if (a.letters.length > 0) {
							alphabets.add(a.name, a);
						}
					}
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile alphabet set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createAlphabets() {
		System.err.println("Notice: No alphabets found. Generating generic alphabet.");
		alphabets.add(Alphabet.DEFAULT_ALPHABET.name, Alphabet.DEFAULT_ALPHABET);
	}
}
