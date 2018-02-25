package com.kreative.paint.material;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.kreative.paint.material.colorpalette.PaletteReader;
import com.kreative.paint.material.colorpalette.RCPXBorder;
import com.kreative.paint.material.colorpalette.RCPXColor;
import com.kreative.paint.material.colorpalette.RCPXLayout;
import com.kreative.paint.material.colorpalette.RCPXOrientation;
import com.kreative.paint.material.colorpalette.RCPXPalette;
import com.kreative.paint.material.colorpalette.RCPXSwatch;

public class ColorPaletteLoader {
	private final MaterialLoader loader;
	private final MaterialList<RCPXPalette> palettes;
	private final MaterialList<MaterialList<Color>> lists;
	private final MaterialList<int[]> arrays;
	private final MaterialList<int[]> orderedArrays;
	
	public ColorPaletteLoader(MaterialLoader loader) {
		this.loader = loader;
		this.palettes = new MaterialList<RCPXPalette>();
		this.lists = new MaterialList<MaterialList<Color>>();
		this.arrays = new MaterialList<int[]>();
		this.orderedArrays = new MaterialList<int[]>();
	}
	
	public MaterialList<RCPXPalette> getColorPalettes() {
		if (isEmpty()) loadResources();
		if (palettes.isEmpty()) createPalettes();
		return palettes;
	}
	
	public MaterialList<MaterialList<Color>> getColorLists() {
		if (isEmpty()) loadResources();
		if (lists.isEmpty()) createLists();
		return lists;
	}
	
	public MaterialList<int[]> getColorArrays() {
		if (isEmpty()) loadResources();
		if (arrays.isEmpty()) createArrays();
		return arrays;
	}
	
	public MaterialList<int[]> getOrderedArrays() {
		if (isEmpty()) loadResources();
		if (orderedArrays.isEmpty()) createOrderedArrays();
		return orderedArrays;
	}
	
	private boolean isEmpty() {
		return palettes.isEmpty()
		    && lists.isEmpty()
		    && arrays.isEmpty()
		    && orderedArrays.isEmpty();
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			PaletteReader pr;
			if (r.isFormat("rcpx", false)) pr = new PaletteReader.RCPXReader();
			else if (r.isFormat("act", false)) pr = new PaletteReader.ACTReader();
			else if (r.isFormat("aco", false)) pr = new PaletteReader.ACOReader();
			else if (r.isFormat("ase", false)) pr = new PaletteReader.ASEReader();
			else continue;
			try {
				InputStream in = r.getInputStream();
				RCPXPalette rcpx = pr.read(r.getResourceName(), in);
				in.close();
				MaterialList<Color> list = new MaterialList<Color>();
				List<Integer> array = new ArrayList<Integer>();
				for (RCPXColor color : rcpx.colors) {
					String n = color.name();
					Color c = color.awtColor();
					if (n != null) list.add(n, c);
					array.add(c.getRGB());
				}
				String name = (rcpx.name != null) ? rcpx.name : r.getResourceName();
				palettes.add(name, rcpx);
				if (!list.isEmpty()) lists.add(name, list);
				if (!array.isEmpty()) {
					int n = array.size();
					int[] c = new int[n];
					int p = 0;
					for (int color : array) c[p++] = color;
					arrays.add(name, c);
					if (rcpx.colorsOrdered) orderedArrays.add(name, c);
				}
			} catch (IOException e) {
				System.err.println("Warning: Failed to compile color palette " + r.getResourceName() + ".");
				e.printStackTrace();
			}
		}
	}
	
	private void createPalettes() {
		System.err.println("Notice: No color palettes found. Generating generic color palette.");
		List<RCPXColor> colors = new ArrayList<RCPXColor>();
		colors.add(new RCPXColor.RGB(255,   0,   0, "Red"       ));
		colors.add(new RCPXColor.RGB(255, 128,   0, "Orange"    ));
		colors.add(new RCPXColor.RGB(255, 255,   0, "Yellow"    ));
		colors.add(new RCPXColor.RGB(128, 255,   0, "Chartreuse"));
		colors.add(new RCPXColor.RGB(  0, 255,   0, "Green"     ));
		colors.add(new RCPXColor.RGB(  0, 255, 128, "Aquamarine"));
		colors.add(new RCPXColor.RGB(  0, 255, 255, "Cyan"      ));
		colors.add(new RCPXColor.RGB(  0, 128, 255, "Azure"     ));
		colors.add(new RCPXColor.RGB(  0,   0, 255, "Blue"      ));
		colors.add(new RCPXColor.RGB(128,   0, 255, "Violet"    ));
		colors.add(new RCPXColor.RGB(255,   0, 255, "Magenta"   ));
		colors.add(new RCPXColor.RGB(255,   0, 128, "Rose"      ));
		colors.add(new RCPXColor.RGB(128,  64,   0, "Brown"     ));
		colors.add(new RCPXColor.RGB(  0,   0,   0, "Black"     ));
		colors.add(new RCPXColor.RGB(128, 128, 128, "Gray"      ));
		colors.add(new RCPXColor.RGB(255, 255, 255, "White"     ));
		RCPXLayout.Row r1 = new RCPXLayout.Row();
		r1.add(new RCPXSwatch.Range(0, 8, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Row r2 = new RCPXLayout.Row();
		r2.add(new RCPXSwatch.Range(8, 16, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Column hlayout = new RCPXLayout.Column();
		hlayout.add(r1); hlayout.add(r2);
		RCPXLayout.Row r3 = new RCPXLayout.Row();
		r3.add(new RCPXSwatch.Range(0, 4, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Row r4 = new RCPXLayout.Row();
		r4.add(new RCPXSwatch.Range(4, 8, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Row r5 = new RCPXLayout.Row();
		r5.add(new RCPXSwatch.Range(8, 12, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Row r6 = new RCPXLayout.Row();
		r6.add(new RCPXSwatch.Range(12, 16, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Column slayout = new RCPXLayout.Column();
		slayout.add(r3); slayout.add(r4); slayout.add(r5); slayout.add(r6);
		RCPXLayout.Column c1 = new RCPXLayout.Column();
		c1.add(new RCPXSwatch.Range(0, 8, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Column c2 = new RCPXLayout.Column();
		c2.add(new RCPXSwatch.Range(8, 16, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL, RCPXBorder.ALL));
		RCPXLayout.Row vlayout = new RCPXLayout.Row();
		vlayout.add(c1); vlayout.add(c2);
		RCPXLayout.Oriented layout = new RCPXLayout.Oriented(hlayout, slayout, vlayout);
		RCPXPalette rcpx = new RCPXPalette(
			"Simple", RCPXOrientation.HORIZONTAL,
			289, 73, 145, 145, 73, 289,
			colors, false, layout
		);
		palettes.add("Simple", rcpx);
	}
	
	private void createLists() {
		System.err.println("Notice: No color lists found. Generating generic color list.");
		MaterialList<Color> list = new MaterialList<Color>();
		list.add("Pink"      , Color.pink     );
		list.add("Red"       , Color.red      );
		list.add("Orange"    , Color.orange   );
		list.add("Yellow"    , Color.yellow   );
		list.add("Green"     , Color.green    );
		list.add("Cyan"      , Color.cyan     );
		list.add("Blue"      , Color.blue     );
		list.add("Magenta"   , Color.magenta  );
		list.add("White"     , Color.white    );
		list.add("Light Gray", Color.lightGray);
		list.add("Gray"      , Color.gray     );
		list.add("Dark Gray" , Color.darkGray );
		list.add("Black"     , Color.black    );
		lists.add("Simple", list);
	}
	
	private void createArrays() {
		System.err.println("Notice: No color arrays found. Generating generic color arrays.");
		arrays.add("Black & White", new int[]{
			0xFF000000, 0xFFFFFFFF
		});
		arrays.add("Process", new int[]{
			0xFF000000, 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00,
			0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFFFFFF
		});
	}
	
	private void createOrderedArrays() {
		System.err.println("Notice: No ordered color arrays found. Generating generic ordered color arrays.");
		orderedArrays.add("Black & White (Black First)", new int[]{
			0xFF000000, 0xFFFFFFFF
		});
		orderedArrays.add("Black & White (White First)", new int[]{
			0xFFFFFFFF, 0xFF000000
		});
		orderedArrays.add("Process (RGB)", new int[]{
			0xFF000000, 0xFFFF0000, 0xFF00FF00, 0xFFFFFF00,
			0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF
		});
		orderedArrays.add("Process (BGR)", new int[]{
			0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
			0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF
		});
	}
}
