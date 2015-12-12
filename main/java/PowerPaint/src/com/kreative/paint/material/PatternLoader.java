package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.material.pattern.Pattern;
import com.kreative.paint.material.pattern.PatternList;
import com.kreative.paint.material.pattern.PatternParser;

public class PatternLoader {
	private final MaterialLoader loader;
	private final MaterialList<PatternList> lists;
	
	public PatternLoader(MaterialLoader loader) {
		this.loader = loader;
		this.lists = new MaterialList<PatternList>();
	}
	
	public MaterialList<PatternList> getPatternLists() {
		if (lists.isEmpty()) loadResources();
		if (lists.isEmpty()) createLists();
		return lists;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("patx", false)) {
				try {
					InputStream in = r.getInputStream();
					PatternList list = PatternParser.parse(r.getResourceName(), in);
					in.close();
					String name = (list.name != null) ? list.name : r.getResourceName();
					lists.add(name, list);
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile pattern set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createLists() {
		System.err.println("Notice: No patterns found. Generating generic patterns.");
		PatternList list = new PatternList("Simple");
		list.add(Pattern.FOREGROUND);
		list.add(Pattern.BG_25_FG_75);
		list.add(Pattern.BG_50_FG_50);
		list.add(Pattern.BG_75_FG_25);
		list.add(Pattern.BACKGROUND);
		lists.add("Simple", list);
	}
}
