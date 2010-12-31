/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.CursorUtils;

public class SpraypaintTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,K,0,K,0,K,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,K,K,K,0,0,0,0,
					0,0,K,0,0,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,K,K,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,0,0,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,K,K,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,0,0,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,K,K,K,0,0,
					0,0,0,0,0,0,0,K,0,0,K,K,K,K,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,
			}
	);
	
	public static final int[] PATTERN_1x1 = new int[]{
		0,0,
	};
	
	public static final int[] PATTERN_6x6 = new int[]{
		-3,-2,	-3,0,	-2,1,	-1,-3,	-1,-1,
		0,0,	1,-2,	1,2,	2,0,
	};
	
	public static final int[] PATTERN_10x10 = new int[]{
		-5,-2,	-5,1,	-4,3,	-3,-3,	-3,0,	-2,2,
		-1,-5,	-1,-1,	-1,4,	0,2,	1,-2,	1,0,
		2,-4,	2,2,	2,4,	3,0,	4,-2,	4,2,
	};
	
	public static final int[] PATTERN_16x16 = new int[]{
		-8,-3,	-8,1,	-7,4,	-6,-6,	-5,-2,	-5,1,	-4,3,	-4,6,
		-3,-5,	-3,0,	-2,-3,	-2,2,	-1,-8,	-1,-1,	-1,4,	0,-6,
		0,7,	1,-2,	1,0,	2,-4,	2,2,	2,4,	3,-7,	4,-2,
		4,1,	5,5,	6,-4,	6,2,	7,-1,
	};
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int[] patcache = null;
	private int[] rgbcache = null;
	private Cursor ccache = null;
	
	public boolean toolSelected(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	private void makeCache(ToolEvent e) {
		patcache = e.tc().getCustom(SpraypaintTool.class, "sprayPattern", int[].class, PATTERN_16x16);
		
		rgbcache = new int[32*32];
		for (int i = 0; i < patcache.length; i+=2) {
			rgbcache[(16+patcache[i+1])*32 + (16+patcache[i])] = 0xFF000000;
		}
		
		BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		int[] rgb = new int[32*32];
		for (int i = 0; i < patcache.length; i+=2) {
			rgb[(16+patcache[i+1])*32 + (16+patcache[i])] = 0xFF000000;
		}
		for (int i = 33; i < rgb.length-33; i++) {
			if (rgb[i] == 0xFF000000) {
				if (rgb[i-1] == 0) rgb[i-1] = -1;
				if (rgb[i+1] == 0) rgb[i+1] = -1;
				if (rgb[i-32] == 0) rgb[i-32] = -1;
				if (rgb[i+32] == 0) rgb[i+32] = -1;
			}
		}
		img.setRGB(0, 0, 32, 32, rgb, 0, 32);
		ccache = CursorUtils.makeCursor(img, 16, 16, "Spraypaint");
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (rgbcache == null) makeCache(e);
		e.beginTransaction(getName());
		PaintSettings ps = e.getPaintSettings();
		PaintSurface srf = e.getPaintSurface();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		ps.applyFill(g);
		new Bitmap(32, 32, rgbcache).paint(srf, g, (int)x-16, (int)y-16);
		return true;
	}

	public boolean mouseDragged(ToolEvent e) {
		if (rgbcache == null) makeCache(e);
		PaintSettings ps = e.getPaintSettings();
		PaintSurface srf = e.getPaintSurface();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		ps.applyFill(g);
		new Bitmap(32, 32, rgbcache).paint(srf, g, (int)x-16, (int)y-16);
		return true;
	}

	public boolean mouseReleased(ToolEvent e) {
		if (rgbcache == null) makeCache(e);
		PaintSettings ps = e.getPaintSettings();
		PaintSurface srf = e.getPaintSurface();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		ps.applyFill(g);
		new Bitmap(32, 32, rgbcache).paint(srf, g, (int)x-16, (int)y-16);
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_1:
			e.tc().setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_1x1);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_6x6);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_10x10);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_16x16);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (ccache == null) makeCache(e);
		return ccache;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new IntegerEnumOption() {
			public String getName() {
				return ToolUtilities.messages.getString("spraypaint.options.Size");
			}
			public int getValue() {
				int[] sprayPattern = tc.getCustom(SpraypaintTool.class, "sprayPattern", int[].class, PATTERN_16x16);
				if (Arrays.equals(sprayPattern, PATTERN_1x1)) return 1;
				if (Arrays.equals(sprayPattern, PATTERN_6x6)) return 6;
				if (Arrays.equals(sprayPattern, PATTERN_10x10)) return 10;
				if (Arrays.equals(sprayPattern, PATTERN_16x16)) return 16;
				return 0;
			}
			public void setValue(int v) {
				switch (v) {
				case 1: tc.setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_1x1); break;
				case 6: tc.setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_6x6); break;
				case 10: tc.setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_10x10); break;
				case 16: tc.setCustom(SpraypaintTool.class, "sprayPattern", PATTERN_16x16); break;
				}
			}
			public int[] values() {
				return new int[]{ 1, 6, 10, 16 };
			}
			public String getLabel(int v) {
				switch (v) {
				case 1: return ToolUtilities.messages.getString("spraypaint.options.Size.1");
				case 6: return ToolUtilities.messages.getString("spraypaint.options.Size.6");
				case 10: return ToolUtilities.messages.getString("spraypaint.options.Size.10");
				case 16: return ToolUtilities.messages.getString("spraypaint.options.Size.16");
				default: return null;
				}
			}
		});
		return f;
	}
}
