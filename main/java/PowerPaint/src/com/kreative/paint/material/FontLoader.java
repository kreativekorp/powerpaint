package com.kreative.paint.material;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;
import com.kreative.paint.material.fontlist.FontList;
import com.kreative.paint.material.fontlist.FontListParser;

public class FontLoader {
	private final MaterialLoader loader;
	private final SortedMap<String,Font> fonts;
	private final MaterialList<FontList> lists;
	
	public FontLoader(MaterialLoader loader) {
		this.loader = loader;
		this.fonts = new TreeMap<String,Font>();
		this.lists = new MaterialList<FontList>();
	}
	
	public SortedMap<String,Font> getFonts() {
		if (isEmpty()) loadResources();
		return fonts;
	}
	
	public MaterialList<FontList> getFontLists() {
		if (isEmpty()) loadResources();
		if (lists.isEmpty()) createLists();
		return lists;
	}
	
	private boolean isEmpty() {
		return fonts.isEmpty() && lists.isEmpty();
	}
	
	private void loadResources() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontFamilyNames = ge.getAvailableFontFamilyNames();
		for (String fontName : fontFamilyNames) {
			Font font = new Font(fontName, Font.PLAIN, 1);
			fonts.put(fontName, font);
		}
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("ttf", false)) {
				try {
					InputStream in = r.getInputStream();
					Font font = Font.createFont(Font.TRUETYPE_FONT, in);
					in.close();
					String fontName = font.getFamily();
					fonts.put(fontName, font);
				} catch (IOException e) {
					System.err.println("Warning: Failed to load font " + r.getResourceName() + ".");
					e.printStackTrace();
				} catch (FontFormatException e) {
					System.err.println("Warning: Failed to load font " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			} else if (r.isFormat("rfpx", false)) {
				try {
					InputStream in = r.getInputStream();
					FontList list = FontListParser.parse(r.getResourceName(), in);
					in.close();
					String name = (list.name != null) ? list.name : r.getResourceName();
					lists.add(name, list);
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile font list " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createLists() {
		System.err.println("Notice: No font lists found. Generating generic font lists.");
		lists.add("CSS", new FontList("CSS",
			"serif", "sans-serif", "monospace", "cursive", "fantasy"
		));
		lists.add("General", new FontList("General",
			"Courier", "Helvetica", "Palatino", "Symbol", "Times"
		));
		lists.add("Java", new FontList("Java",
			"Dialog", "DialogInput", "Monospaced", "SansSerif", "Serif"
		));
	}
}
