package com.kreative.paint.material;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import com.kreative.paint.material.sprite.ArrayOrdering;
import com.kreative.paint.material.sprite.ColorTransform;
import com.kreative.paint.material.sprite.SPNXParser;
import com.kreative.paint.material.sprite.SpriteIntent;
import com.kreative.paint.material.sprite.SpriteSheet;
import com.kreative.paint.material.sprite.SpriteSheetReader;
import com.kreative.paint.material.sprite.SpriteSheetSlice;
import com.kreative.paint.material.sprite.SpriteTreeNode;

public class SpriteLoader {
	private final MaterialLoader loader;
	private final MaterialList<SpriteSheet> brushes;
	private final MaterialList<SpriteSheet> calligraphyBrushes;
	private final MaterialList<SpriteSheet> charcoalBrushes;
	private final MaterialList<SpriteSheet> sprinkles;
	private final MaterialList<SpriteSheet> rubberStamps;
	
	public SpriteLoader(MaterialLoader loader) {
		this.loader = loader;
		this.brushes = new MaterialList<SpriteSheet>();
		this.calligraphyBrushes = new MaterialList<SpriteSheet>();
		this.charcoalBrushes = new MaterialList<SpriteSheet>();
		this.sprinkles = new MaterialList<SpriteSheet>();
		this.rubberStamps = new MaterialList<SpriteSheet>();
	}
	
	public MaterialList<SpriteSheet> getBrushes() {
		if (isEmpty()) loadResources();
		if (brushes.isEmpty()) createBrushes();
		return brushes;
	}
	
	public MaterialList<SpriteSheet> getCalligraphyBrushes() {
		if (isEmpty()) loadResources();
		if (calligraphyBrushes.isEmpty()) createCalligraphyBrushes();
		return calligraphyBrushes;
	}
	
	public MaterialList<SpriteSheet> getCharcoalBrushes() {
		if (isEmpty()) loadResources();
		if (charcoalBrushes.isEmpty()) createCharcoalBrushes();
		return charcoalBrushes;
	}
	
	public MaterialList<SpriteSheet> getSprinkles() {
		if (isEmpty()) loadResources();
		if (sprinkles.isEmpty()) createSprinkles();
		return sprinkles;
	}
	
	public MaterialList<SpriteSheet> getRubberStamps() {
		if (isEmpty()) loadResources();
		if (rubberStamps.isEmpty()) createRubberStamps();
		return rubberStamps;
	}
	
	private boolean isEmpty() {
		return brushes.isEmpty()
		    && calligraphyBrushes.isEmpty()
		    && charcoalBrushes.isEmpty()
		    && sprinkles.isEmpty()
		    && rubberStamps.isEmpty();
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("png", false)) {
				MaterialList<SpriteSheet> dc = getDefaultCategory(r);
				if (dc == null) continue;
				SpriteSheetReader.Options o = getDefaultOptions(dc);
				try {
					InputStream in = r.getInputStream();
					BufferedImage image = ImageIO.read(in);
					in.close();
					in = r.getInputStream();
					SpriteSheet ss = SpriteSheetReader.readSpriteSheet(r.getResourceName(), in, image, o);
					in.close();
					MaterialList<SpriteSheet> category = getCategory(ss, dc);
					String name = (ss.name != null) ? ss.name : r.getResourceName();
					category.add(name, ss);
				} catch (IOException e) {
					System.err.println("Warning: Ignoring invalid image: " + r.getResourceName());
					e.printStackTrace();
				}
			} else if (r.isFormat("spnd", true)) {
				MaterialList<SpriteSheet> dc = getDefaultCategory(r);
				SpriteSheetReader.Options o = getDefaultOptions(dc);
				Map<String,MaterialResource> pngs = new TreeMap<String,MaterialResource>();
				Map<String,MaterialResource> spnxs = new TreeMap<String,MaterialResource>();
				for (MaterialResource child : r.listChildren()) {
					String name = child.getResourceName().replaceFirst("\\.[a-zA-Z0-9]+$", "").trim();
					if (child.isFormat("png", false)) pngs.put(name, child);
					if (child.isFormat("spnx", false)) spnxs.put(name, child);
				}
				for (Map.Entry<String,MaterialResource> e : pngs.entrySet()) {
					MaterialResource png = e.getValue();
					MaterialResource spnx = spnxs.get(e.getKey());
					try {
						InputStream in = png.getInputStream();
						BufferedImage image = ImageIO.read(in);
						in.close();
						SpriteSheet ss;
						if (spnx == null) {
							ss = SpriteSheetReader.createSpriteSheet(e.getKey(), image, o);
						} else {
							in = spnx.getInputStream();
							ss = SPNXParser.parse(e.getKey(), in, image);
							in.close();
						}
						MaterialList<SpriteSheet> category = getCategory(ss, dc);
						if (category == null) continue;
						String name = (ss.name != null) ? ss.name : e.getKey();
						category.add(name, ss);
					} catch (IOException ex) {
						System.err.println("Warning: Ignoring invalid sprite sheet: " + e.getKey());
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	private MaterialList<SpriteSheet> getDefaultCategory(MaterialResource r) {
		String branchName = r.getBranchName().toLowerCase();
		if (branchName.contains("stamps")) return rubberStamps;
		if (branchName.contains("sprinkles")) return sprinkles;
		if (branchName.contains("charcoal")) return charcoalBrushes;
		if (branchName.contains("calligraphy")) return calligraphyBrushes;
		if (branchName.contains("brushes")) return brushes;
		return null;
	}
	
	private SpriteSheetReader.Options getDefaultOptions(MaterialList<SpriteSheet> dc) {
		SpriteSheetReader.Options o = new SpriteSheetReader.Options();
		if (dc == brushes) o.setDefaultPresentation(12, -1, ArrayOrdering.LTR_TTB);
		if (dc == calligraphyBrushes) o.setDefaultStructureSingleParent(false);
		if (dc == charcoalBrushes) o.setDefaultStructureSingleParent(true);
		if (dc == sprinkles) o.setDefaultStructureSingleParent(false);
		if (dc != rubberStamps) o.setDefaultColorTransform(ColorTransform.ALL);
		return o;
	}
	
	private MaterialList<SpriteSheet> getCategory(SpriteSheet ss, MaterialList<SpriteSheet> dc) {
		switch (ss.intent) {
			case SpriteIntent.STATIC_BRUSH: return brushes;
			case SpriteIntent.ACCELERATED_BRUSH: return calligraphyBrushes;
			case SpriteIntent.SPRAYED_BRUSH: return charcoalBrushes;
			case SpriteIntent.STAMPED_BRUSH: return sprinkles;
			case SpriteIntent.RUBBER_STAMPS: return rubberStamps;
			case SpriteIntent.ANIMATED_STAMPS: return rubberStamps;
			default: return dc;
		}
	}
	
	private void createBrushes() {
		System.err.println("Notice: No brushes found. Generating generic brushes.");
		BufferedImage bi = new BufferedImage(128, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.black);
		for (
			int rx = 0, sx = 64, x = 6, w = 4;
			rx < 64 && sx < 128 && x >= 0 && w <= 16;
			rx += 16, sx += 16, x -= 2, w += 4
		) {
			g.fillOval(rx + x, x, w, w);
			g.fillRect(sx + x, x, w, w);
		}
		g.dispose();
		SpriteSheet ss = new SpriteSheet(bi, "Simple", 0, 2, 4, ArrayOrdering.TTB_LTR);
		ss.slices.add(new SpriteSheetSlice(0, 0, 16, 16, 8, 8, 16, 16, 8, 1, ArrayOrdering.LTR_TTB, ColorTransform.ALL));
		ss.root.children.add(new SpriteTreeNode.Leaf("", 0, 0, 8));
		brushes.add("Simple", ss);
	}
	
	private void createCalligraphyBrushes() {
		System.err.println("Notice: No calligraphy brushes found. Generating generic calligraphy brushes.");
		BufferedImage bi = new BufferedImage(256, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.black);
		for (int x = 8, d = 16; x < 256 && d > 0; x += 16, d--) {
			g.fillOval(x - d / 2,  8 - d / 2, d, d);
			g.fillRect(x - d / 2, 24 - d / 2, d, d);
		}
		g.dispose();
		SpriteSheet ss = new SpriteSheet(bi, "Simple", 0, 2, 1, ArrayOrdering.LTR_TTB);
		ss.slices.add(new SpriteSheetSlice(0, 0, 16, 16, 8, 8, 16, 16, 16, 2, ArrayOrdering.LTR_TTB, ColorTransform.ALL));
		SpriteTreeNode.Branch stb;
		stb = new SpriteTreeNode.Branch("Round", 0, 0);
		stb.children.add(new SpriteTreeNode.Leaf("", 0, 0, 16));
		ss.root.children.add(stb);
		stb = new SpriteTreeNode.Branch("Square", 16, 0);
		stb.children.add(new SpriteTreeNode.Leaf("", 16, 0, 16));
		ss.root.children.add(stb);
		calligraphyBrushes.add("Simple", ss);
	}
	
	private void createCharcoalBrushes() {
		System.err.println("Notice: No charcoal brushes found. Generating generic charcoal brushes.");
		BufferedImage bi = new BufferedImage(128, 16, BufferedImage.TYPE_INT_ARGB);
		int[] rgb = new int[256];
		Random r = new Random();
		for (int i = 0, x = 0; i < 8 && x < 128; i++, x += 16) {
			for (int j = 0; j < 256; j++) {
				rgb[j] = r.nextBoolean() ? 0xFF000000 : 0; 
			}
			bi.setRGB(x, 0, 16, 16, rgb, 0, 16);
		}
		SpriteSheet ss = new SpriteSheet(bi, "Simple", 0, 1, 1, ArrayOrdering.LTR_TTB);
		ss.slices.add(new SpriteSheetSlice(0, 0, 16, 16, 8, 8, 16, 16, 8, 1, ArrayOrdering.LTR_TTB, ColorTransform.ALL));
		SpriteTreeNode.Branch stb = new SpriteTreeNode.Branch("Square", 0, 0);
		stb.children.add(new SpriteTreeNode.Leaf("", 0, 0, 8));
		ss.root.children.add(stb);
		charcoalBrushes.add("Simple", ss);
	}
	
	private void createSprinkles() {
		System.err.println("Notice: No sprinkles found. Generating generic sprinkles.");
		int k = 0xFF000000;
		int[] rgb = new int[] {
			0,0,0,0,0,k,k,k,k,k,k,0,0,0,0,0,
			0,0,0,k,k,k,0,0,0,0,k,k,k,0,0,0,
			0,0,k,k,0,0,0,0,0,0,0,0,k,k,0,0,
			0,k,k,0,0,0,0,0,0,0,0,0,0,k,k,0,
			0,k,0,0,0,k,0,0,0,0,k,0,0,0,k,0,
			k,k,0,0,0,k,0,0,0,0,k,0,0,0,k,k,
			k,0,0,0,0,0,0,0,0,0,0,0,0,0,0,k,
			k,0,0,k,0,0,0,0,0,0,0,0,k,0,0,k,
			k,0,k,k,0,0,0,0,0,0,0,0,k,k,0,k,
			k,0,0,0,k,k,0,0,0,0,k,k,0,0,0,k,
			k,k,0,0,k,0,k,k,k,k,0,k,0,0,k,k,
			0,k,0,0,0,k,0,0,0,0,k,0,0,0,k,0,
			0,k,k,0,0,0,k,k,k,k,0,0,0,k,k,0,
			0,0,k,k,0,0,0,0,0,0,0,0,k,k,0,0,
			0,0,0,k,k,k,0,0,0,0,k,k,k,0,0,0,
			0,0,0,0,0,k,k,k,k,k,k,0,0,0,0,0
		};
		BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0, 0, 16, 16, rgb, 0, 16);
		SpriteSheet ss = new SpriteSheet(bi, "Smiley", 0, 1, 1, ArrayOrdering.LTR_TTB);
		ss.slices.add(new SpriteSheetSlice(0, 0, 16, 16, 8, 8, 16, 16, 1, 1, ArrayOrdering.LTR_TTB, ColorTransform.ALL));
		SpriteTreeNode.Branch stb = new SpriteTreeNode.Branch("Smiley", 0, 0);
		stb.children.add(new SpriteTreeNode.Leaf("", 0, 0, 1));
		ss.root.children.add(stb);
		sprinkles.add("Smiley", ss);
	}
	
	private void createRubberStamps() {
		System.err.println("Notice: No rubber stamps found. Generating generic rubber stamps.");
		BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.black);
		g.fillOval(0, 0, 32, 32);
		g.setColor(Color.yellow);
		g.fillOval(1, 1, 30, 30);
		Shape s = g.getClip();
		g.setClip(new Rectangle(0, 16, 32, 32));
		g.setColor(Color.black);
		g.fillOval(8, 8, 16, 16);
		g.setColor(Color.yellow);
		g.fillOval(9, 9, 14, 14);
		g.setClip(s);
		g.setColor(Color.black);
		g.fillRect(10, 10, 2, 2);
		g.fillRect(20, 10, 2, 2);
		g.dispose();
		SpriteSheet ss = new SpriteSheet(bi, "Smiley", 0, 1, 1, ArrayOrdering.LTR_TTB);
		ss.slices.add(new SpriteSheetSlice(0, 0, 32, 32, 16, 16, 32, 32, 1, 1, ArrayOrdering.LTR_TTB, ColorTransform.NONE));
		ss.root.children.add(new SpriteTreeNode.Leaf("", 0, 0, 1));
		rubberStamps.add("Smiley", ss);
	}
}
