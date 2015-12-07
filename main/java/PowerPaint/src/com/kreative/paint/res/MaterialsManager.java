package com.kreative.paint.res;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.imageio.ImageIO;
import com.kreative.paint.filter.Filter;
import com.kreative.paint.format.Format;
import com.kreative.paint.material.alphabet.Alphabet;
import com.kreative.paint.material.alphabet.AlphabetList;
import com.kreative.paint.material.alphabet.AlphabetParser;
import com.kreative.paint.material.colorpalette.RCPXBorder;
import com.kreative.paint.material.colorpalette.RCPXColor;
import com.kreative.paint.material.colorpalette.RCPXLayout;
import com.kreative.paint.material.colorpalette.RCPXOrientation;
import com.kreative.paint.material.colorpalette.RCPXPalette;
import com.kreative.paint.material.colorpalette.RCPXParser;
import com.kreative.paint.material.colorpalette.RCPXSwatch;
import com.kreative.paint.material.dither.DiffusionDitherAlgorithm;
import com.kreative.paint.material.dither.DitherAlgorithm;
import com.kreative.paint.material.dither.DitherAlgorithmList;
import com.kreative.paint.material.dither.DitherAlgorithmParser;
import com.kreative.paint.material.dither.RandomDitherAlgorithm;
import com.kreative.paint.material.fontlist.FontList;
import com.kreative.paint.material.fontlist.FontListParser;
import com.kreative.paint.material.frame.Frame;
import com.kreative.paint.material.frame.FrameReader;
import com.kreative.paint.material.gradient.GradientColorMap;
import com.kreative.paint.material.gradient.GradientList;
import com.kreative.paint.material.gradient.GradientParser;
import com.kreative.paint.material.gradient.GradientPreset;
import com.kreative.paint.material.gradient.GradientShape;
import com.kreative.paint.material.pattern.Pattern;
import com.kreative.paint.material.pattern.PatternList;
import com.kreative.paint.material.pattern.PatternParser;
import com.kreative.paint.material.shape.ArcType;
import com.kreative.paint.material.shape.ParameterizedShape;
import com.kreative.paint.material.shape.ParameterizedValue;
import com.kreative.paint.material.shape.PowerShape;
import com.kreative.paint.material.shape.PowerShapeList;
import com.kreative.paint.material.shape.PowerShapeParser;
import com.kreative.paint.material.shape.WindingRule;
import com.kreative.paint.material.sprite.ArrayOrdering;
import com.kreative.paint.material.sprite.ColorTransform;
import com.kreative.paint.material.sprite.SpriteIntent;
import com.kreative.paint.material.sprite.SpriteSheet;
import com.kreative.paint.material.sprite.SpriteSheetReader;
import com.kreative.paint.material.sprite.SpriteSheetSlice;
import com.kreative.paint.material.sprite.SpriteTreeNode;
import com.kreative.paint.material.stroke.Arrowhead;
import com.kreative.paint.material.stroke.StrokeParser;
import com.kreative.paint.material.stroke.StrokeSet;
import com.kreative.paint.material.texture.Texture;
import com.kreative.paint.material.texture.TextureList;
import com.kreative.paint.material.texture.TextureParser;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.PairList;

/*
 * Whenever possible, use MaterialsManager instead of ResourceManager.
 * ResourceManager is too low-level for any code written to use it to end well.
 */
public class MaterialsManager {
	private ResourceManager rm;
	public MaterialsManager(ResourceManager rm) {
		this.rm = rm;
	}
	public ResourceManager getResourceManager() {
		return rm;
	}
	
	private PairList<String,RCPXPalette> colorPalettes = null;
	private PairList<String,PairList<Color,String>> colorLists = null;
	private PairList<String,int[]> colorArrays = null;
	private void loadColorPalettes() {
		colorPalettes = new PairList<String,RCPXPalette>();
		colorLists = new PairList<String,PairList<Color,String>>();
		colorArrays = new PairList<String,int[]>();
		for (Resource r : rm.getResources(ResourceCategory.COLORS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				RCPXPalette rcpx = RCPXParser.parse(r.name(), bin);
				bin.close();
				Set<Integer> colors = new HashSet<Integer>();
				PairList<Color,String> list = new PairList<Color,String>();
				for (RCPXColor color : rcpx.colors) {
					Color c = color.awtColor();
					colors.add(c.getRGB());
					String n = color.name();
					if (n != null) list.add(c, n);
				}
				String name = (rcpx.name != null) ? rcpx.name : r.name();
				colorPalettes.add(name, rcpx);
				if (!list.isEmpty()) colorLists.add(name, list);
				if (!colors.isEmpty()) {
					int n = colors.size();
					int[] c = new int[n];
					int p = 0;
					for (int color : colors) c[p++] = color;
					colorArrays.add(name, c);
				}
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile color palette " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (colorPalettes.isEmpty()) {
			System.err.println("Notice: No color palettes found. Generating generic color palette.");
			List<RCPXColor> colors = new Vector<RCPXColor>();
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
			colorPalettes.add("Simple", rcpx);
		}
		if (colorLists.isEmpty()) {
			System.err.println("Notice: No color lists found. Generating generic color list.");
			PairList<Color,String> list = new PairList<Color,String>();
			list.add(Color.pink     , "Pink"      );
			list.add(Color.red      , "Red"       );
			list.add(Color.orange   , "Orange"    );
			list.add(Color.yellow   , "Yellow"    );
			list.add(Color.green    , "Green"     );
			list.add(Color.cyan     , "Cyan"      );
			list.add(Color.blue     , "Blue"      );
			list.add(Color.magenta  , "Magenta"   );
			list.add(Color.white    , "White"     );
			list.add(Color.lightGray, "Light Gray");
			list.add(Color.gray     , "Gray"      );
			list.add(Color.darkGray , "Dark Gray" );
			list.add(Color.black    , "Black"     );
			colorLists.add("Simple", list);
		}
		if (colorArrays.isEmpty()) {
			System.err.println("Notice: No color arrays found. Generating generic color arrays.");
			colorArrays.add("Black & White", new int[]{
				0xFF000000, 0xFFFFFFFF
			});
			colorArrays.add("Process", new int[]{
				0xFF000000, 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00,
				0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFFFFFF
			});
		}
	}
	public PairList<String,RCPXPalette> getColorPalettes() {
		if (colorPalettes == null) loadColorPalettes();
		return colorPalettes;
	}
	public PairList<String,PairList<Color,String>> getColorLists() {
		if (colorLists == null) loadColorPalettes();
		return colorLists;
	}
	public PairList<String,int[]> getColorArrays() {
		if (colorArrays == null) loadColorPalettes();
		return colorArrays;
	}
	
	private SortedMap<String,Font> fonts = null;
	private void loadFonts() {
		fonts = new TreeMap<String,Font>();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] ffn = ge.getAvailableFontFamilyNames();
		for (String fontName : ffn) {
			Font font = new Font(fontName, Font.PLAIN, 1);
			fonts.put(fontName, font);
		}
	}
	public SortedMap<String,Font> getFonts() {
		if (fonts == null) loadFonts();
		return fonts;
	}
	
	private PairList<String,FontList> fontLists = null;
	private void loadFontLists() {
		fontLists = new PairList<String,FontList>();
		for (Resource r : rm.getResources(ResourceCategory.FONTSETS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				FontList list = FontListParser.parse(r.name(), bin);
				bin.close();
				String name = (list.name != null) ? list.name : r.name();
				fontLists.add(name, list);
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile font list " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (fontLists.isEmpty()) {
			System.err.println("Notice: No font lists found. Generating generic font lists.");
			fontLists.add("CSS", new FontList("CSS",
				"serif", "sans-serif", "monospace", "cursive", "fantasy"
			));
			fontLists.add("General", new FontList("General",
				"Courier", "Helvetica", "Palatino", "Symbol", "Times"
			));
			fontLists.add("Java", new FontList("Java",
				"Dialog", "DialogInput", "Monospaced", "SansSerif", "Serif"
			));
		}
	}
	public PairList<String,FontList> getFontLists() {
		if (fontLists == null) loadFontLists();
		return fontLists;
	}
	
	private PairList<String,PatternList> patterns = null;
	private void loadPatterns() {
		patterns = new PairList<String,PatternList>();
		for (Resource r : rm.getResources(ResourceCategory.PATTERNS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				PatternList list = PatternParser.parse(r.name(), bin);
				bin.close();
				String name = (list.name != null) ? list.name : r.name();
				patterns.add(name, list);
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile pattern set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (patterns.isEmpty()) {
			System.err.println("Notice: No patterns found. Generating generic patterns.");
			PatternList list = new PatternList("Simple");
			list.add(Pattern.FOREGROUND);
			list.add(Pattern.BG_25_FG_75);
			list.add(Pattern.BG_50_FG_50);
			list.add(Pattern.BG_75_FG_25);
			list.add(Pattern.BACKGROUND);
			patterns.add("Simple", list);
		}
	}
	public PairList<String,PatternList> getPatterns() {
		if (patterns == null) loadPatterns();
		return patterns;
	}
	
	private PairList<String,Alphabet> alphabets = null;
	private void loadAlphabets() {
		alphabets = new PairList<String,Alphabet>();
		for (Resource r : rm.getResources(ResourceCategory.ALPHABETS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				AlphabetList list = AlphabetParser.parse(r.name(), bin);
				bin.close();
				for (Alphabet a : list) {
					if (a.letters.length > 0) {
						alphabets.add(a.name, a);
					}
				}
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile alphabet set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (alphabets.isEmpty()) {
			System.err.println("Notice: No alphabets found. Generating generic alphabet.");
			alphabets.add(Alphabet.DEFAULT_ALPHABET.name, Alphabet.DEFAULT_ALPHABET);
		}
	}
	public PairList<String,Alphabet> getAlphabets() {
		if (alphabets == null) loadAlphabets();
		return alphabets;
	}
	
	private PairList<String,SpriteSheet> brushes = null;
	private PairList<String,SpriteSheet> calligraphyBrushes = null;
	private PairList<String,SpriteSheet> charcoalBrushes = null;
	private PairList<String,SpriteSheet> sprinkles = null;
	private PairList<String,SpriteSheet> rubberStamps = null;
	private void loadSprites(ResourceCategory rc, PairList<String,SpriteSheet> spriteSets) {
		SpriteSheetReader.Options o = new SpriteSheetReader.Options();
		if (spriteSets == brushes) o.setDefaultPresentation(12, -1, ArrayOrdering.LTR_TTB);
		if (spriteSets == calligraphyBrushes) o.setDefaultStructureSingleParent(false);
		if (spriteSets == charcoalBrushes) o.setDefaultStructureSingleParent(true);
		if (spriteSets == sprinkles) o.setDefaultStructureSingleParent(false);
		if (spriteSets != rubberStamps) o.setDefaultColorTransform(ColorTransform.ALL);
		for (Resource r : rm.getResources(rc)) {
			try {
				SpriteSheet ss = SpriteSheetReader.readSpriteSheet(r.name(), r.data(), o);
				String name = (ss.name != null) ? ss.name : r.name();
				switch (ss.intent) {
					case SpriteIntent.STATIC_BRUSH:      spriteSets = brushes;            break;
					case SpriteIntent.ACCELERATED_BRUSH: spriteSets = calligraphyBrushes; break;
					case SpriteIntent.SPRAYED_BRUSH:     spriteSets = charcoalBrushes;    break;
					case SpriteIntent.STAMPED_BRUSH:     spriteSets = sprinkles;          break;
					case SpriteIntent.RUBBER_STAMPS:     spriteSets = rubberStamps;       break;
					case SpriteIntent.ANIMATED_STAMPS:   spriteSets = rubberStamps;       break;
				}
				spriteSets.add(name, ss);
			} catch (IOException ioe) {
				System.err.println("Warning: Ignoring invalid image: " + r.name());
			}
		}
	}
	private void loadSprites() {
		brushes            = new PairList<String,SpriteSheet>();
		calligraphyBrushes = new PairList<String,SpriteSheet>();
		charcoalBrushes    = new PairList<String,SpriteSheet>();
		sprinkles          = new PairList<String,SpriteSheet>();
		rubberStamps       = new PairList<String,SpriteSheet>();
		loadSprites(ResourceCategory.BRUSHES,     brushes           );
		loadSprites(ResourceCategory.CALLIGRAPHY, calligraphyBrushes);
		loadSprites(ResourceCategory.CHARCOALS,   charcoalBrushes   );
		loadSprites(ResourceCategory.SPRINKLES,   sprinkles         );
		loadSprites(ResourceCategory.STAMPS,      rubberStamps      );
		if (brushes.isEmpty()) {
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
		if (calligraphyBrushes.isEmpty()) {
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
		if (charcoalBrushes.isEmpty()) {
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
		if (sprinkles.isEmpty()) {
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
		if (rubberStamps.isEmpty()) {
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
	public PairList<String,SpriteSheet> getBrushes() {
		if (brushes == null) loadSprites();
		return brushes;
	}
	public PairList<String,SpriteSheet> getCalligraphyBrushes() {
		if (calligraphyBrushes == null) loadSprites();
		return calligraphyBrushes;
	}
	public PairList<String,SpriteSheet> getCharcoalBrushes() {
		if (charcoalBrushes == null) loadSprites();
		return charcoalBrushes;
	}
	public PairList<String,SpriteSheet> getSprinkles() {
		if (sprinkles == null) loadSprites();
		return sprinkles;
	}
	public PairList<String,SpriteSheet> getRubberStamps() {
		if (rubberStamps == null) loadSprites();
		return rubberStamps;
	}
	
	private PairList<String,DitherAlgorithm> ditherAlgorithms = null;
	private void loadDitherAlgorithms() {
		ditherAlgorithms = new PairList<String,DitherAlgorithm>();
		for (Resource r : rm.getResources(ResourceCategory.DITHERERS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				DitherAlgorithmList list = DitherAlgorithmParser.parse(r.name(), bin);
				bin.close();
				for (DitherAlgorithm da : list) {
					ditherAlgorithms.add(da.name, da);
				}
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile dither algorithm set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (ditherAlgorithms.isEmpty()) {
			System.err.println("Notice: No dither algorithms found. Generating generic dither algorithms.");
			ditherAlgorithms.add("Threshold", DiffusionDitherAlgorithm.THRESHOLD);
			ditherAlgorithms.add("Floyd-Steinberg", DiffusionDitherAlgorithm.FLOYD_STEINBERG);
		}
		ditherAlgorithms.add("Random", RandomDitherAlgorithm.RANDOM);
	}
	public PairList<String,DitherAlgorithm> getDitherAlgorithms() {
		if (ditherAlgorithms == null) loadDitherAlgorithms();
		return ditherAlgorithms;
	}
	
	private Collection<Format> pluginFormats = null;
	public Collection<Format> getPluginFormats() {
		if (pluginFormats == null) {
			pluginFormats = new HashSet<Format>();
			ClassLoader tcl = rm.getClassLoader(ResourceCategory.FORMATS);
			List<String> trn = rm.getResourceNames(ResourceCategory.FORMATS);
			for (String name : trn) {
				if (!name.contains("$")) {
					try {
						Class<? extends Format> t = tcl.loadClass(name).asSubclass(Format.class);
						try {
							Constructor<? extends Format> cons = t.getConstructor(MaterialsManager.class);
							pluginFormats.add(cons.newInstance(this));
						} catch (NoSuchMethodException nsme1) {
							try {
								Constructor<? extends Format> cons = t.getConstructor(ResourceManager.class);
								pluginFormats.add(cons.newInstance(rm));
							} catch (NoSuchMethodException nsme2) {
								Constructor<? extends Format> cons = t.getConstructor();
								pluginFormats.add(cons.newInstance());
							}
						}
					} catch (Exception e) {
						System.err.println("Warning: Failed to load Format class "+name+". Ignoring this class.");
					}
				}
			}
		}
		return pluginFormats;
	}
	
	private Collection<Filter> pluginFilters = null;
	public Collection<Filter> getPluginFilters() {
		if (pluginFilters == null) {
			pluginFilters = new HashSet<Filter>();
			ClassLoader tcl = rm.getClassLoader(ResourceCategory.FILTERS);
			List<String> trn = rm.getResourceNames(ResourceCategory.FILTERS);
			for (String name : trn) {
				if (!name.contains("$")) {
					try {
						Class<? extends Filter> t = tcl.loadClass(name).asSubclass(Filter.class);
						try {
							Constructor<? extends Filter> cons = t.getConstructor(MaterialsManager.class);
							pluginFilters.add(cons.newInstance(this));
						} catch (NoSuchMethodException nsme1) {
							try {
								Constructor<? extends Filter> cons = t.getConstructor(ResourceManager.class);
								pluginFilters.add(cons.newInstance(rm));
							} catch (NoSuchMethodException nsme2) {
								Constructor<? extends Filter> cons = t.getConstructor();
								pluginFilters.add(cons.newInstance());
							}
						}
					} catch (Exception e) {
						System.err.println("Warning: Failed to load Filter class "+name+". Ignoring this class.");
					}
				}
			}
		}
		return pluginFilters;
	}
	
	private PairList<String,Frame> frames = null;
	private void loadFrames() {
		frames = new PairList<String,Frame>();
		for (Resource r : rm.getResources(ResourceCategory.FRAMES)) {
			try {
				Frame frame = FrameReader.readFrame(r.name(), r.data());
				String name = (frame.name != null) ? frame.name : r.name();
				frames.add(name, frame);
			} catch (IOException ioe) {
				System.err.println("Warning: Ignoring invalid image: " + r.name());
			}
		}
		if (frames.isEmpty()) {
			System.err.println("Notice: No frames found. Generating generic frames.");
			BufferedImage i1 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			BufferedImage i2 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			BufferedImage i3 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			for (int j = 0; j < 4; j++) {
				i1.setRGB(0, j, 0xFF000000);
				i1.setRGB(3, j, 0xFF000000);
				i1.setRGB(j, 0, 0xFF000000);
				i1.setRGB(j, 3, 0xFF000000);
				i2.setRGB(0, j, 0xFF808080);
				i2.setRGB(3, j, 0xFF808080);
				i2.setRGB(j, 0, 0xFF808080);
				i2.setRGB(j, 3, 0xFF808080);
				i3.setRGB(0, j, 0xFFFFFFFF);
				i3.setRGB(3, j, 0xFFFFFFFF);
				i3.setRGB(j, 0, 0xFFFFFFFF);
				i3.setRGB(j, 3, 0xFFFFFFFF);
			}
			frames.add("Simple Black", new Frame(i1, "Simple Black"));
			frames.add("Simple Gray", new Frame(i2, "Simple Gray"));
			frames.add("Simple White", new Frame(i3, "Simple White"));
		}
	}
	public PairList<String,Frame> getFrames() {
		if (frames == null) loadFrames();
		return frames;
	}
	
	private LinkedHashMap<String,GradientPreset> gradientPresets = null;
	private LinkedHashMap<String,GradientShape> gradientShapes = null;
	private LinkedHashMap<String,GradientColorMap> gradientColorMaps = null;
	private void loadGradients() {
		gradientPresets = new LinkedHashMap<String,GradientPreset>();
		gradientShapes = new LinkedHashMap<String,GradientShape>();
		gradientColorMaps = new LinkedHashMap<String,GradientColorMap>();
		for (Resource r : rm.getResources(ResourceCategory.GRADIENTS)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				GradientList list = GradientParser.parse(r.name(), bin);
				bin.close();
				for (GradientPreset preset : list.presets) gradientPresets.put(preset.name, preset);
				for (GradientShape shape : list.shapes) gradientShapes.put(shape.name, shape);
				for (GradientColorMap map : list.colorMaps) gradientColorMaps.put(map.name, map);
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile gradient set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (gradientPresets.isEmpty()) {
			System.err.println("Notice: No gradient presets found. Generating generic gradient presets.");
			gradientPresets.put(GradientPreset.BLACK_TO_WHITE.name, GradientPreset.BLACK_TO_WHITE);
			gradientPresets.put(GradientPreset.WHITE_TO_BLACK.name, GradientPreset.WHITE_TO_BLACK);
			gradientPresets.put(GradientPreset.RGB_SPECTRUM.name, GradientPreset.RGB_SPECTRUM);
			gradientPresets.put(GradientPreset.RGB_WHEEL.name, GradientPreset.RGB_WHEEL);
		}
		if (gradientShapes.isEmpty()) {
			System.err.println("Notice: No gradient shapes found. Generating generic gradient shapes.");
			gradientShapes.put(GradientShape.SIMPLE_LINEAR.name, GradientShape.SIMPLE_LINEAR);
			gradientShapes.put(GradientShape.REVERSE_LINEAR.name, GradientShape.REVERSE_LINEAR);
			gradientShapes.put(GradientShape.SIMPLE_ANGULAR.name, GradientShape.SIMPLE_ANGULAR);
			gradientShapes.put(GradientShape.REVERSE_ANGULAR.name, GradientShape.REVERSE_ANGULAR);
		}
		if (gradientColorMaps.isEmpty()) {
			System.err.println("Notice: No gradient color maps found. Generating generic gradient color maps.");
			gradientColorMaps.put(GradientColorMap.BLACK_TO_WHITE.name, GradientColorMap.BLACK_TO_WHITE);
			gradientColorMaps.put(GradientColorMap.WHITE_TO_BLACK.name, GradientColorMap.WHITE_TO_BLACK);
			gradientColorMaps.put(GradientColorMap.RGB_SPECTRUM.name, GradientColorMap.RGB_SPECTRUM);
		}
	}
	public LinkedHashMap<String,GradientPreset> getGradientPresets() {
		if (gradientPresets == null) loadGradients();
		return gradientPresets;
	}
	public LinkedHashMap<String,GradientShape> getGradientShapes() {
		if (gradientShapes == null) loadGradients();
		return gradientShapes;
	}
	public LinkedHashMap<String,GradientColorMap> getGradientColors() {
		if (gradientColorMaps == null) loadGradients();
		return gradientColorMaps;
	}
	
	private TreeSet<Float> lineWidths = null;
	private TreeSet<Integer> lineMultiplicities = null;
	private LinkedHashSet<float[]> lineDashes = null;
	private LinkedHashSet<Arrowhead> lineArrowheads = null;
	private void loadLines() {
		lineWidths = new TreeSet<Float>();
		lineMultiplicities = new TreeSet<Integer>();
		lineDashes = new LinkedHashSet<float[]>();
		lineArrowheads = new LinkedHashSet<Arrowhead>();
		for (Resource r : rm.getResources(ResourceCategory.LINES)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				StrokeSet ss = StrokeParser.parse(r.name(), bin);
				bin.close();
				for (float width : ss.widths) lineWidths.add(width);
				for (int multiplicity : ss.multiplicities) lineMultiplicities.add(multiplicity);
				for (float[] dashes : ss.dashes) lineDashes.add(dashes);
				for (Arrowhead arrowhead : ss.arrowheads) lineArrowheads.add(arrowhead);
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile stroke set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (lineWidths.isEmpty()) {
			System.err.println("Notice: No line widths found. Generating generic line widths.");
			for (int i = 0; i <= 12; i++) {
				lineWidths.add((float)i);
			}
		}
		if (lineMultiplicities.isEmpty()) {
			System.err.println("Notice: No line multiplicies found. Generating generic line multiplicities.");
			for (int i = 1; i <= 3; i++) {
				lineMultiplicities.add(i);
			}
		}
		if (lineDashes.isEmpty()) {
			System.err.println("Notice: No dashes found. Generating generic dashes.");
			lineDashes.add(null);
			for (int i = 1; i <= 5; i++) {
				lineDashes.add(new float[]{i,i});
			}
		}
		if (lineArrowheads.isEmpty()) {
			System.err.println("Notice: No arrowheads found. Generating generic arrowheads.");
			lineArrowheads.add(null);
			lineArrowheads.add(Arrowhead.GENERAL_FILLED_ARROW);
			lineArrowheads.add(Arrowhead.GENERAL_STROKED_ARROW);
			lineArrowheads.add(Arrowhead.GENERAL_FILLED_CIRCLE);
			lineArrowheads.add(Arrowhead.GENERAL_STROKED_CIRCLE);
		}
	}
	public TreeSet<Float> getLineWidths() {
		if (lineWidths == null) loadLines();
		return lineWidths;
	}
	public TreeSet<Integer> getLineMultiplicies() {
		if (lineMultiplicities == null) loadLines();
		return lineMultiplicities;
	}
	public LinkedHashSet<float[]> getLineDashes() {
		if (lineDashes == null) loadLines();
		return lineDashes;
	}
	public LinkedHashSet<Arrowhead> getLineArrowheads() {
		if (lineArrowheads == null) loadLines();
		return lineArrowheads;
	}
	
	private PairList<String,PowerShapeList> shapes = null;
	private void loadShapes() {
		shapes = new PairList<String,PowerShapeList>();
		for (Resource r : rm.getResources(ResourceCategory.SHAPES)) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
				PowerShapeList list = PowerShapeParser.parse(r.name(), bin);
				bin.close();
				String name = (list.name != null) ? list.name : r.name();
				shapes.add(name, list);
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile shape set " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (shapes.isEmpty()) {
			System.err.println("Notice: No shapes found. Generating generic shapes.");
			PowerShapeList list = new PowerShapeList("Basic");
			PowerShape line = new PowerShape(WindingRule.NON_ZERO, "Line");
			line.addShape(new ParameterizedShape.Line(
				new ParameterizedValue(0.0), new ParameterizedValue(0.0),
				new ParameterizedValue(1.0), new ParameterizedValue(1.0)
			));
			list.add(line);
			PowerShape rect = new PowerShape(WindingRule.NON_ZERO, "Rectangle");
			rect.addShape(new ParameterizedShape.Rect(
				new ParameterizedValue(0.0), new ParameterizedValue(0.0),
				new ParameterizedValue(1.0), new ParameterizedValue(1.0),
				new ParameterizedValue(0.0), new ParameterizedValue(0.0)
			));
			list.add(rect);
			PowerShape rrect = new PowerShape(WindingRule.NON_ZERO, "Round Rectangle");
			rrect.addShape(new ParameterizedShape.Rect(
				new ParameterizedValue(0.0), new ParameterizedValue(0.0),
				new ParameterizedValue(1.0), new ParameterizedValue(1.0),
				new ParameterizedValue(0.25), new ParameterizedValue(0.25)
			));
			list.add(rrect);
			PowerShape ellipse = new PowerShape(WindingRule.NON_ZERO, "Ellipse");
			ellipse.addShape(new ParameterizedShape.Ellipse(
				new ParameterizedValue(0.5), new ParameterizedValue(0.5),
				new ParameterizedValue(0.5), new ParameterizedValue(0.5)
			));
			list.add(ellipse);
			PowerShape arc = new PowerShape(WindingRule.NON_ZERO, "Arc");
			arc.addShape(new ParameterizedShape.Arc(
				new ParameterizedValue(0.0), new ParameterizedValue(1.0),
				new ParameterizedValue(1.0), new ParameterizedValue(1.0),
				new ParameterizedValue(0.0), new ParameterizedValue(90.0),
				ArcType.OPEN
			));
			list.add(arc);
			shapes.add("Basic", list);
		}
	}
	public PairList<String,PowerShapeList> getShapes() {
		if (shapes == null) loadShapes();
		return shapes;
	}
	
	private LinkedHashMap<String,PairList<String,TexturePaint>> textures = null;
	private void loadTextures() {
		textures = new LinkedHashMap<String,PairList<String,TexturePaint>>();
		final List<Resource> rr = rm.getResources(ResourceCategory.TEXTURES);
		for (Resource r : rr) {
			String name = r.name();
			byte[] data = r.data();
			if (name.contains("/") && data[0] == '<') try {
				final String prefix = name.substring(0, name.lastIndexOf("/") + 1);
				TextureParser.HrefResolver hr = new TextureParser.HrefResolver() {
					@Override
					public BufferedImage resolveHref(String href) throws IOException {
						href = href.replaceFirst("^#[0-9]+ ", "");
						href = href.replaceFirst("\\.[a-zA-Z0-9]+$", "");
						href = prefix + href.trim();
						for (Resource r : rr) {
							if (r.name().equals(href)) {
								InputStream in = new ByteArrayInputStream(r.data());
								BufferedImage image = ImageIO.read(in);
								in.close();
								return image;
							}
						}
						throw new IOException("Resource not found.");
					}
				};
				ByteArrayInputStream bin = new ByteArrayInputStream(data);
				TextureList list = TextureParser.parse(hr, name, bin);
				bin.close();
				if (textures.containsKey(list.name)) {
					PairList<String,TexturePaint> cat = textures.get(list.name);
					for (Texture t : list) cat.add(t.name, t.paint);
				} else {
					PairList<String,TexturePaint> cat = new PairList<String,TexturePaint>();
					for (Texture t : list) cat.add(t.name, t.paint);
					textures.put(list.name, cat);
				}
			} catch (IOException ioe) {
				System.err.println("Warning: Failed to compile texture list " + r.name() + ".");
				ioe.printStackTrace();
			}
		}
		if (textures.isEmpty()) {
			System.err.println("Notice: No textures found. Generating generic textures.");
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 8, 8);
			g.fillRect(8, 8, 8, 8);
			g.setColor(Color.white);
			g.fillRect(8, 0, 8, 8);
			g.fillRect(0, 8, 8, 8);
			g.dispose();
			Texture t = new Texture("Checkerboard", image);
			PairList<String,TexturePaint> cat = new PairList<String,TexturePaint>();
			cat.add(t.name, t.paint);
			textures.put("Simple", cat);
		}
	}
	public LinkedHashMap<String,PairList<String,TexturePaint>> getTextures() {
		if (textures == null) loadTextures();
		return textures;
	}
	
	private Collection<Tool> pluginTools = null;
	public Collection<Tool> getPluginTools() {
		if (pluginTools == null) {
			pluginTools = new HashSet<Tool>();
			ClassLoader tcl = rm.getClassLoader(ResourceCategory.TOOLS);
			List<String> trn = rm.getResourceNames(ResourceCategory.TOOLS);
			for (String name : trn) {
				if (!name.contains("$")) {
					try {
						Class<? extends Tool> t = tcl.loadClass(name).asSubclass(Tool.class);
						pluginTools.add(t.newInstance());
					} catch (Exception e) {
						System.err.println("Warning: Failed to load Tool class "+name+". Ignoring this class.");
					}
				}
			}
		}
		return pluginTools;
	}
}
