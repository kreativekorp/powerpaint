/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.res;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import com.kreative.paint.awt.Arrowhead;
import com.kreative.paint.awt.CircleArrowhead;
import com.kreative.paint.awt.PolygonArrowhead;
import com.kreative.paint.filter.Filter;
import com.kreative.paint.format.Format;
import com.kreative.paint.geom.ParameterPoint;
import com.kreative.paint.geom.ParameterizedPath;
import com.kreative.paint.gradient.GradientColorMap;
import com.kreative.paint.gradient.GradientList;
import com.kreative.paint.gradient.GradientParser;
import com.kreative.paint.gradient.GradientPreset;
import com.kreative.paint.gradient.GradientShape;
import com.kreative.paint.rcp.RCPXBorder;
import com.kreative.paint.rcp.RCPXColor;
import com.kreative.paint.rcp.RCPXLayout;
import com.kreative.paint.rcp.RCPXOrientation;
import com.kreative.paint.rcp.RCPXPalette;
import com.kreative.paint.rcp.RCPXParser;
import com.kreative.paint.rcp.RCPXSwatch;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.DitherAlgorithm;
import com.kreative.paint.util.Frame;
import com.kreative.paint.util.ImageUtils;
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
	public PairList<String,RCPXPalette> getColorPalettes() {
		if (colorPalettes == null) {
			colorPalettes = new PairList<String,RCPXPalette>();
			for (Resource r : rm.getResources(ResourceCategory.COLORS)) {
				RCPXPalette rcpx = loadColorPalette(r);
				if (rcpx == null) continue;
				String n = (rcpx.name == null) ? r.name() : rcpx.name;
				colorPalettes.add(n, rcpx);
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
		}
		return colorPalettes;
	}
	
	private PairList<String,PairList<Color,String>> colorLists = null;
	public PairList<String,PairList<Color,String>> getColorLists() {
		if (colorLists == null) {
			colorLists = new PairList<String,PairList<Color,String>>();
			for (Resource r : rm.getResources(ResourceCategory.COLORS)) {
				RCPXPalette rcpx = loadColorPalette(r);
				if (rcpx == null) continue;
				String n = (rcpx.name == null) ? r.name() : rcpx.name;
				PairList<Color,String> v = new PairList<Color,String>();
				for (RCPXColor color : rcpx.colors) {
					if (color.name() != null) {
						v.add(color.awtColor(), color.name());
					}
				}
				if (!v.isEmpty()) colorLists.add(n, v);
			}
			if (colorLists.isEmpty()) {
				System.err.println("Notice: No color lists found. Generating generic color list.");
				PairList<Color,String> v = new PairList<Color,String>();
				v.add(Color.pink, "Pink");
				v.add(Color.red, "Red");
				v.add(Color.orange, "Orange");
				v.add(Color.yellow, "Yellow");
				v.add(Color.green, "Green");
				v.add(Color.cyan, "Cyan");
				v.add(Color.blue, "Blue");
				v.add(Color.magenta, "Magenta");
				v.add(Color.white, "White");
				v.add(Color.lightGray, "Light Gray");
				v.add(Color.gray, "Gray");
				v.add(Color.darkGray, "Dark Gray");
				v.add(Color.black, "Black");
				colorLists.add("Simple", v);
			}
		}
		return colorLists;
	}
	
	private PairList<String,int[]> colorArrays = null;
	public PairList<String,int[]> getColorArrays() {
		if (colorArrays == null) {
			colorArrays = new PairList<String,int[]>();
			for (Resource r : rm.getResources(ResourceCategory.COLORS)) {
				RCPXPalette rcpx = loadColorPalette(r);
				if (rcpx == null) continue;
				String n = (rcpx.name == null) ? r.name() : rcpx.name;
				Set<Integer> colors = new HashSet<Integer>();
				for (RCPXColor color : rcpx.colors) {
					colors.add(color.awtColor().getRGB());
				}
				if (!colors.isEmpty()) {
					Integer[] c2 = colors.toArray(new Integer[colors.size()]);
					int[] c3 = new int[c2.length];
					for (int i = 0; i < c2.length; i++) c3[i] = c2[i];
					colorArrays.add(n, c3);
				}
			}
			if (colorArrays.isEmpty()) {
				System.err.println("Notice: No color arrays found. Generating generic color arrays.");
				colorArrays.add("Black & White", new int[]{ 0xFF000000, 0xFFFFFFFF });
				colorArrays.add("Process", new int[]{ 0xFF000000, 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFFFFFF });
			}
		}
		return colorArrays;
	}
	
	private TreeMap<String,Font> fonts = null;
	public TreeMap<String,Font> getFonts() {
		if (fonts == null) {
			fonts = new TreeMap<String,Font>();
			for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
				fonts.put(f, new Font(f, Font.PLAIN, 1));
			}
		}
		return fonts;
	}
	
	private PairList<String,TreeMap<String,Font>> fontLists = null;
	public PairList<String,TreeMap<String,Font>> getFontLists() {
		if (fontLists == null) {
			getFonts();
			fontLists = new PairList<String,TreeMap<String,Font>>();
			for (Resource r : rm.getResources(ResourceCategory.FONTSETS)) {
				TreeMap<String,Font> fs = new TreeMap<String,Font>();
				Scanner sc = new Scanner(new ByteArrayInputStream(r.data()), "UTF-8");
				while (sc.hasNextLine()) {
					String s = sc.nextLine();
					if (s.length() > 0 && fonts.containsKey(s)) {
						fs.put(s, fonts.get(s));
					}
				}
				sc.close();
				if (!fs.isEmpty()) {
					fontLists.add(r.name(), fs);
				}
			}
		}
		return fontLists;
	}
	
	private PairList<String,Vector<Long>> patterns = null;
	public PairList<String,Vector<Long>> getPatterns() {
		if (patterns == null) {
			patterns = new PairList<String,Vector<Long>>();
			for (Resource r : rm.getResources(ResourceCategory.PATTERNS)) {
				Vector<Long> pats = new Vector<Long>();
				try {
					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(r.data()));
					int n = dis.readUnsignedShort();
					for (int i = 0; i < n; i++) pats.add(dis.readLong());
					dis.close();
				} catch (IOException ioe) {
					if (pats.isEmpty()) {
						pats.add(0L);
						pats.add(-1L);
					}
				}
				patterns.add(r.name(), pats);
			}
			if (patterns.isEmpty()) {
				System.err.println("Notice: No patterns found. Generating generic patterns.");
				Vector<Long> pats = new Vector<Long>();
				pats.add(0L);
				pats.add(0xAA005500AA005500L);
				pats.add(0xAA55AA55AA55AA55L);
				pats.add(0xAAFF55FFAAFF55FFL);
				pats.add(-1L);
				patterns.add("Simple", pats);
			}
		}
		return patterns;
	}
	
	private PairList<String,char[]> alphabets = null;
	public PairList<String,char[]> getAlphabets() {
		if (alphabets == null) {
			alphabets = new PairList<String,char[]>();
			for (Resource r : rm.getResources(ResourceCategory.ALPHABETS)) {
				try {
					String s1 = new String(r.data(), "UTF-8");
					StringBuffer s2 = new StringBuffer();
					CharacterIterator i = new StringCharacterIterator(s1);
					for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
						if (!(Character.isWhitespace(ch) || Character.isSpaceChar(ch) || Character.isISOControl(ch) || ch == 0xFFEF || ch == 0xFFFE)) {
							s2.append(ch);
						}
					}
					if (s2.length() > 0) {
						alphabets.add(r.name(), s2.toString().toCharArray());
					}
				} catch (IOException ioe) {
					System.err.println("Warning: Ignoring invalid alphabet: "+r.name());
				}
			}
			if (alphabets.isEmpty()) {
				System.err.println("Notice: No alphabets found. Generating generic alphabet.");
				alphabets.add("Latin", new char[] {
						'A','B','C','D','E','F','G','H','I','J','K','L','M','N',
						'O','P','Q','R','S','T','U','V','W','X','Y','Z','!','?',
						'0','1','2','3','4','5','6','7','8','9','+','-','=','&',
						'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
						'o','p','q','r','s','t','u','v','w','x','y','z','\u00A1','\u00BF',
						';',':',',','.','@','#','\'','"','*','/','(',')','[',']',
						'\u00C0','\u00C1','\u00C2','\u00C3','\u00C4','\u00C5','\u00C6',
						'\u00C7','\u00C8','\u00C9','\u00CA','\u00CB','\u00CC','\u00CD',
						'\u00CE','\u00CF','\u00D1','\u00D2','\u00D3','\u00D4','\u00D5',
						'\u00D6','\u00D8','\u00D9','\u00DA','\u00DB','\u00DC','\u00DD',
						'\u00E0','\u00E1','\u00E2','\u00E3','\u00E4','\u00E5','\u00E6',
						'\u00E7','\u00E8','\u00E9','\u00EA','\u00EB','\u00EC','\u00ED',
						'\u00EE','\u00EF','\u00F1','\u00F2','\u00F3','\u00F4','\u00F5',
						'\u00F6','\u00F8','\u00F9','\u00FA','\u00FB','\u00FC','\u00FD',
						'\u00D0','\u00F0','\u00DE','\u00FE','\u0152','\u0153','\u0178',
						'\u00FF','\u00DF','\u00D7','\u00F7','%','^','_',
						'$','\u00A2','\u00A3','\u00A5','\u00A7','\u00B6','\u00A9',
						'\u00AE','{','}','<','>','\u00AB','\u00BB',
						'\\','`','~','\u00A8','|','\u00A6','\u00A4',
						'\u00AA','\u00BA','\u00B0','\u00B1','\u00B5','\u00AC','\u00B7'
				});
			}
		}
		return alphabets;
	}
	
	private PairList<String,Vector<Bitmap>> brushes = null;
	public PairList<String,Vector<Bitmap>> getBrushes() {
		if (brushes == null) {
			brushes = getBitmaps(ResourceCategory.BRUSHES);
			if (brushes.isEmpty()) {
				System.err.println("Notice: No brushes found. Generating generic brushes.");
				Vector<Bitmap> v = new Vector<Bitmap>();
				for (int x = 6, w = 4; x >= 0 && w <= 16; x -= 2, w += 4) {
					BufferedImage i = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = i.createGraphics();
					g.setColor(Color.black);
					g.fillOval(x, x, w, w);
					g.dispose();
					int[] rgb = new int[256];
					i.getRGB(0, 0, 16, 16, rgb, 0, 16);
					v.add(new Bitmap(16, 16, rgb));
				}
				for (int x = 6, w = 4; x >= 0 && w <= 16; x -= 2, w += 4) {
					BufferedImage i = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = i.createGraphics();
					g.setColor(Color.black);
					g.fillRect(x, x, w, w);
					g.dispose();
					int[] rgb = new int[256];
					i.getRGB(0, 0, 16, 16, rgb, 0, 16);
					v.add(new Bitmap(16, 16, rgb));
				}
				brushes.add("Simple", v);
			}
		}
		return brushes;
	}
	
	private PairList<String,Vector<Bitmap>> calligraphyBrushes = null;
	public PairList<String,Vector<Bitmap>> getCalligraphyBrushes() {
		if (calligraphyBrushes == null) {
			calligraphyBrushes = getBitmaps(ResourceCategory.CALLIGRAPHY);
			if (calligraphyBrushes.isEmpty()) {
				System.err.println("Notice: No calligraphy brushes found. Generating generic calligraphy brushes.");
				Vector<Bitmap> v1 = new Vector<Bitmap>();
				Vector<Bitmap> v2 = new Vector<Bitmap>();
				for (int d = 16; d > 0; d--) {
					BufferedImage i1 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					BufferedImage i2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Graphics g1 = i1.createGraphics();
					Graphics g2 = i2.createGraphics();
					g1.setColor(Color.black);
					g2.setColor(Color.black);
					g1.fillOval(8-d/2, 8-d/2, d, d);
					g2.fillRect(8-d/2, 8-d/2, d, d);
					g1.dispose();
					g2.dispose();
					int[] rgb1 = new int[256];
					int[] rgb2 = new int[256];
					i1.getRGB(0, 0, 16, 16, rgb1, 0, 16);
					i2.getRGB(0, 0, 16, 16, rgb2, 0, 16);
					v1.add(new Bitmap(16, 16, rgb1));
					v2.add(new Bitmap(16, 16, rgb2));
				}
				calligraphyBrushes.add("Round", v1);
				calligraphyBrushes.add("Square", v2);
			}
		}
		return calligraphyBrushes;
	}
	
	private PairList<String,Vector<Bitmap>> charcoalBrushes = null;
	public PairList<String,Vector<Bitmap>> getCharcoalBrushes() {
		if (charcoalBrushes == null) {
			charcoalBrushes = getBitmaps(ResourceCategory.CHARCOALS);
			if (charcoalBrushes.isEmpty()) {
				System.err.println("Notice: No charcoal brushes found. Generating generic charcoal brushes.");
				Vector<Bitmap> v = new Vector<Bitmap>();
				for (int i = 0; i < 8; i++) {
					int[] rgb = new int[256];
					Random r = new Random();
					for (int j = 0; j < rgb.length; j++) {
						rgb[j] = r.nextBoolean() ? 0xFF000000 : 0;
					}
					v.add(new Bitmap(16, 16, rgb));
				}
				charcoalBrushes.add("Square", v);
			}
		}
		return charcoalBrushes;
	}
	
	private PairList<String,DitherAlgorithm> ditherAlgorithms = null;
	public PairList<String,DitherAlgorithm> getDitherAlgorithms() {
		if (ditherAlgorithms == null) {
			ditherAlgorithms = new PairList<String,DitherAlgorithm>();
			for (Resource r : rm.getResources(ResourceCategory.DITHERERS)) {
				Scanner sc = new Scanner(new ByteArrayInputStream(r.data()));
				DitherAlgorithm d = new DitherAlgorithm(sc);
				sc.close();
				ditherAlgorithms.add(r.name(), d);
			}
			if (ditherAlgorithms.isEmpty()) {
				System.err.println("Notice: No dither algorithms found. Generating generic dither algorithms.");
				ditherAlgorithms.add("Threshold", new DitherAlgorithm(new int[][]{new int[]{0}}, 1));
				ditherAlgorithms.add("Floyd-Steinberg", new DitherAlgorithm(new int[][]{new int[]{0,0,7}, new int[]{3,5,1}}, 16));
			}
		}
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
	public PairList<String,Frame> getFrames() {
		if (frames == null) {
			frames = new PairList<String,Frame>();
			for (Resource r : rm.getResources(ResourceCategory.FRAMES)) {
				try {
					Frame f = new Frame(r.data());
					frames.add(r.name(), f);
				} catch (Exception e) {
					System.err.println("Warning: Ignoring invalid image: "+r.name());
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
				frames.add("Simple Black", new Frame(i1));
				frames.add("Simple Gray", new Frame(i2));
				frames.add("Simple White", new Frame(i3));
			}
		}
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
	
	private TreeSet<Float> lineWidths;
	private TreeSet<Integer> lineMultiplicities;
	private LinkedHashSet<float[]> lineDashes;
	private LinkedHashSet<Arrowhead> lineArrowheads;
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
	private void loadLines() {
		lineWidths = new TreeSet<Float>();
		lineMultiplicities = new TreeSet<Integer>();
		lineDashes = new LinkedHashSet<float[]>();
		lineArrowheads = new LinkedHashSet<Arrowhead>();
		for (Resource r : rm.getResources(ResourceCategory.LINES)) {
			Scanner sc = new Scanner(new ByteArrayInputStream(r.data()));
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line.length() > 0 && !line.startsWith("#")) {
					String[] fields = line.split("[ ,;]");
					if (fields[0].length() > 0 && !fields[0].startsWith("#")) {
						switch (fields[0].charAt(0)) {
						case '|':
							for (int i = 1; i < fields.length; i++) {
								try {
									lineWidths.add(Float.parseFloat(fields[i]));
								} catch (NumberFormatException nfe) {}
							}
							break;
						case '-':
							if (fields[0].contains("0")) {
								lineDashes.add(null);
							} else {
								float[] a = new float[fields.length-1];
								for (int i = 1; i < fields.length; i++) {
									try {
										a[i-1] = Float.parseFloat(fields[i]);
									}  catch (NumberFormatException nfe) {}
								}
								lineDashes.add(a);
							}
							break;
						case 'x':
							for (int i = 1; i < fields.length; i++) {
								try {
									lineMultiplicities.add(Integer.parseInt(fields[i]));
								} catch (NumberFormatException nfe) {}
							}
							break;
						case '>':
							if (fields[0].contains("0")) {
								lineArrowheads.add(null);
							} else {
								float[] a = new float[fields.length-1];
								for (int i = 1; i < fields.length; i++) {
									try {
										a[i-1] = Float.parseFloat(fields[i]);
									}  catch (NumberFormatException nfe) {}
								}
								lineArrowheads.add(new PolygonArrowhead(
										a,
										fields[0].contains("f"),
										fields[0].contains("s")
								));
							}
							break;
						case '*':
							if (fields[0].contains("0")) {
								lineArrowheads.add(null);
							} else if (fields.length >= 5) {
								try {
									lineArrowheads.add(new CircleArrowhead(
											Float.parseFloat(fields[1]),
											Float.parseFloat(fields[2]),
											Float.parseFloat(fields[3]),
											Float.parseFloat(fields[4]),
											fields[0].contains("f"),
											fields[0].contains("s")
									));
								} catch (NumberFormatException nfe) {}
							}
							break;
						}
					}
				}
			}
			sc.close();
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
			lineArrowheads.add(new PolygonArrowhead(new float[]{6,0,0,6,0,-6,6,0}, true, true));
			lineArrowheads.add(new PolygonArrowhead(new float[]{6,0,0,6,0,-6,6,0}, false, true));
			lineArrowheads.add(new PolygonArrowhead(new float[]{0,6,6,0,0,0,6,0,0,-6}, false, true));
			lineArrowheads.add(new PolygonArrowhead(new float[]{0,6,0,-6}, false, true));
			lineArrowheads.add(new PolygonArrowhead(new float[]{4,2,0,2,0,-2,4,-2,4,2}, true, true));
			lineArrowheads.add(new PolygonArrowhead(new float[]{4,2,0,2,0,-2,4,-2,4,2}, false, true));
			lineArrowheads.add(new CircleArrowhead(2,0,4,4, true, true));
			lineArrowheads.add(new CircleArrowhead(2,0,4,4, false, true));
		}
	}
	
	private PairList<String,PairList<String,ParameterizedPath>> shapes = null;
	public PairList<String,PairList<String,ParameterizedPath>> getShapes() {
		if (shapes == null) {
			shapes = new PairList<String,PairList<String,ParameterizedPath>>();
			for (Resource r : rm.getResources(ResourceCategory.SHAPES)) {
				String collectionName = r.name();
				PairList<String,ParameterizedPath> collectionShapes = new PairList<String,ParameterizedPath>();
				String lastName = null;
				ParameterizedPath lastPath = null;
				Scanner sc = new Scanner(new ByteArrayInputStream(r.data()), "UTF-8");
				while (sc.hasNextLine()) {
					String s = sc.nextLine();
					if (s.trim().length() > 0 && !s.trim().startsWith("#")) {
						if (s.startsWith("\t")) {
							if (lastPath != null) {
								String[] fields = s.trim().split("\\s+");
								try {
									switch (fields[0].charAt(0)) {
									case 'p':
										boolean polar = fields[4].trim().toLowerCase().startsWith("p");
										ParameterPoint pp = new ParameterPoint(
												fields[1].trim(),
												Double.parseDouble(fields[2]), Double.parseDouble(fields[3]),
												polar,
												Double.parseDouble(fields[5]), (polar ? Math.toRadians(Double.parseDouble(fields[6])) : Double.parseDouble(fields[6])),
												Double.parseDouble(fields[7]), (polar ? Math.toRadians(Double.parseDouble(fields[8])) : Double.parseDouble(fields[8])),
												Double.parseDouble(fields[9]), (polar ? Math.toRadians(Double.parseDouble(fields[10])) : Double.parseDouble(fields[10]))
										);
										lastPath.addParameterPoint(pp);
										break;
									case 'm':
										lastPath.moveTo(fields[1], fields[2]);
										break;
									case 'l':
										lastPath.lineTo(fields[1], fields[2]);
										break;
									case 'q':
										lastPath.quadTo(fields[1], fields[2], fields[3], fields[4]);
										break;
									case 'c':
										lastPath.curveTo(fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
										break;
									case 'a':
										lastPath.arcTo(fields[1], fields[2], fields[3], fields[4]);
										break;
									case 'x':
										lastPath.closePath();
										break;
									case 'w':
										String rule = fields[1].toLowerCase().replaceAll("[^a-z]", "");
										if (rule.equals("eo") || rule.equals("evenodd")) lastPath.setWindingRule(ParameterizedPath.WIND_EVEN_ODD);
										if (rule.equals("nz") || rule.equals("nonzero")) lastPath.setWindingRule(ParameterizedPath.WIND_NON_ZERO);
										break;
									case 'r':
										lastPath.appendRectangle(fields[1], fields[2], fields[3], fields[4], (fields.length > 5) && fields[5].trim().toLowerCase().equals("connect"));
										break;
									case 'd':
										lastPath.appendRoundRectangle(fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], (fields.length > 7) && fields[7].trim().toLowerCase().equals("connect"));
										break;
									case 'e':
										lastPath.appendEllipse(fields[1], fields[2], fields[3], fields[4], (fields.length > 5) && fields[5].trim().toLowerCase().equals("connect"));
										break;
									case 'h':
										lastPath.appendArc(fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], (fields.length > 7) && fields[7].trim().toLowerCase().equals("connect"));
										break;
									default:
										System.err.println("Warning: Invalid instruction '" + fields[0] + "' in shape " + lastName + " in shape resource " + r.name() + ".");
										break;
									}
								} catch (NumberFormatException nfe) {
									System.err.println("Warning: Invalid numeric value in shape " + lastName + " in shape resource " + r.name() + ".");
								} catch (IndexOutOfBoundsException ioobe) {
									System.err.println("Warning: Too few parameters in shape " + lastName + " in shape resource " + r.name() + ".");
								}
							} else {
								System.err.println("Warning: Shape resource " + r.name() + " put the cart before the horse.");
							}
						} else {
							lastName = s.trim();
							lastPath = new ParameterizedPath();
							collectionShapes.add(lastName, lastPath);
						}
					}
				}
				sc.close();
				if (!collectionShapes.isEmpty()) {
					shapes.add(collectionName, collectionShapes);
				}
			}
			if (shapes.isEmpty()) {
				System.err.println("Notice: No shapes found. Generating generic shapes.");
				PairList<String,ParameterizedPath> collectionShapes = new PairList<String,ParameterizedPath>();
				collectionShapes.add("Line", new ParameterizedPath(new Line2D.Float(0,0,1,1)));
				collectionShapes.add("Rectangle", new ParameterizedPath(new Rectangle2D.Float(0,0,1,1)));
				collectionShapes.add("Round Rectangle", new ParameterizedPath(new RoundRectangle2D.Float(0,0,1,1,0.25f,0.25f)));
				collectionShapes.add("Ellipse", new ParameterizedPath(new Ellipse2D.Float(0,0,1,1)));
				collectionShapes.add("Arc", new ParameterizedPath(new Arc2D.Float(-1,0,2,2,0,90,Arc2D.PIE)));
				shapes.add("Basic", collectionShapes);
			}
		}
		return shapes;
	}
	
	private PairList<String,Vector<Bitmap>> sprinkles = null;
	public PairList<String,Vector<Bitmap>> getSprinkles() {
		if (sprinkles == null) {
			sprinkles = getBitmaps(ResourceCategory.SPRINKLES);
			if (sprinkles.isEmpty()) {
				System.err.println("Notice: No sprinkles found. Generating generic sprinkles.");
				Vector<Bitmap> v = new Vector<Bitmap>();
				int k = 0xFF000000;
				int[] rgb = new int[]{
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
				v.add(new Bitmap(16, 16, rgb));
				sprinkles.add("Smiley", v);
			}
		}
		return sprinkles;
	}
	
	private PairList<String,Vector<Image>> rubberStamps = null;
	public PairList<String,Vector<Image>> getRubberStamps() {
		if (rubberStamps == null) {
			rubberStamps = getImages(ResourceCategory.STAMPS);
			if (rubberStamps.isEmpty()) {
				System.err.println("Notice: No rubber stamps found. Generating generic rubber stamps.");
				Vector<Image> v = new Vector<Image>();
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
				v.add(bi);
				rubberStamps.add("Smiley", v);
			}
		}
		return rubberStamps;
	}
	
	private LinkedHashMap<String,PairList<String,TexturePaint>> textures = null;
	public LinkedHashMap<String,PairList<String,TexturePaint>> getTextures() {
		if (textures == null) {
			textures = new LinkedHashMap<String,PairList<String,TexturePaint>>();
			for (Resource r : rm.getResources(ResourceCategory.TEXTURES)) {
				String[] ss = r.name().split("/", 2);
				String categoryName = (ss.length >= 2) ? ss[0] : "Textures";
				String textureName = (ss.length >= 2) ? ss[1] : ss[0];
				Image i = Toolkit.getDefaultToolkit().createImage(r.data());
				boolean prepd = (i == null) ? false : ImageUtils.prepImage(i);
				BufferedImage bi = (i == null) ? null : ImageUtils.toBufferedImage(i, false);
				if (i == null || !prepd || bi == null) {
					System.err.println("Warning: Ignoring invalid image: "+r.name());
				} else {
					TexturePaint t = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
					if (textures.containsKey(categoryName)) {
						textures.get(categoryName).add(textureName, t);
					} else {
						PairList<String,TexturePaint> cat = new PairList<String,TexturePaint>();
						cat.add(textureName, t);
						textures.put(categoryName, cat);
					}
				}
			}
		}
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
	
	private RCPXPalette loadColorPalette(Resource r) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(r.data());
			RCPXPalette rcpx = RCPXParser.parse(r.name(), bin);
			return rcpx;
		} catch (IOException ioe) {
			System.err.println("Warning: Failed to compile color palette " + r.name() + ".");
			ioe.printStackTrace();
			return null;
		}
	}
	
	private PairList<String,Vector<Bitmap>> getBitmaps(ResourceCategory cat) {
		PairList<String,Vector<Bitmap>> bmps = new PairList<String,Vector<Bitmap>>();
		Toolkit tk = Toolkit.getDefaultToolkit();
		for (Resource r : rm.getResources(cat)) {
			Image i = tk.createImage(r.data());
			boolean prepd = (i == null) ? false : ImageUtils.prepImage(tk, i);
			BufferedImage bi = (i == null) ? null : ImageUtils.toBufferedImage(i, false);
			if (i == null || !prepd || bi == null) {
				System.err.println("Warning: Ignoring invalid image: "+r.name());
			} else {
				Vector<Bitmap> v = new Vector<Bitmap>();
				int size = bi.getHeight();
				for (int x = 0; x+size <= bi.getWidth(); x += size) {
					int[] rgb = new int[size*size];
					bi.getRGB(x, 0, size, size, rgb, 0, size);
					v.add(new Bitmap(size, size, rgb));
				}
				if (!v.isEmpty()) bmps.add(r.name(), v);
			}
		}
		return bmps;
	}
	
	private PairList<String,Vector<Image>> getImages(ResourceCategory cat) {
		PairList<String,Vector<Image>> imgs = new PairList<String,Vector<Image>>();
		Toolkit tk = Toolkit.getDefaultToolkit();
		for (Resource r : rm.getResources(cat)) {
			Image i = tk.createImage(r.data());
			boolean prepd = (i == null) ? false : ImageUtils.prepImage(tk, i);
			BufferedImage bi = (i == null) ? null : ImageUtils.toBufferedImage(i, false);
			if (i == null || !prepd || bi == null) {
				System.err.println("Warning: Ignoring invalid image: "+r.name());
			} else {
				Vector<Image> v = new Vector<Image>();
				int size = bi.getHeight();
				int[] rgb = new int[size*size];
				for (int x = 0; x+size <= bi.getWidth(); x += size) {
					bi.getRGB(x, 0, size, size, rgb, 0, size);
					BufferedImage e = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
					e.setRGB(0, 0, size, size, rgb, 0, size);
					v.add(e);
				}
				if (!v.isEmpty()) imgs.add(r.name(), v);
			}
		}
		return imgs;
	}
}
