/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.geom.Spiral;

public class SpiralTool extends CircularShapeTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,
					0,0,0,K,K,0,0,0,0,0,K,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,K,0,0,0,K,K,K,0,0,0,K,0,0,0,
					0,K,0,0,0,K,0,0,0,K,0,0,K,0,0,0,
					0,K,0,0,K,0,0,0,0,K,0,0,K,0,0,0,
					0,K,0,0,K,0,0,K,0,K,0,0,K,0,0,K,
					0,K,0,0,K,0,0,0,K,0,0,0,K,0,0,K,
					0,K,0,0,K,0,0,0,0,0,0,K,0,0,0,K,
					0,K,0,0,0,K,0,0,0,0,K,0,0,0,K,0,
					0,0,K,0,0,0,K,K,K,K,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,K,K,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,K,K,K,K,K,K,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public Shape makeShape(ToolEvent e, float cx, float cy, float ex, float ey) {
		int sides = e.tc().getCustom(SpiralTool.class, "sides", Integer.class, 32);
		double spacing = e.tc().getCustom(SpiralTool.class, "spacing", Double.class, 20.0);
		boolean spokes = e.tc().getCustom(SpiralTool.class, "spokes", Boolean.class, false);
		return new Spiral(sides, spacing, spokes, cx, cy, ex, ey);
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().decrementCustom(SpiralTool.class, "sides", Integer.class, 32, 3);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().incrementCustom(SpiralTool.class, "sides", Integer.class, 32);
			break;
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().setCustom(SpiralTool.class, "sides", 3);
			break;
		case KeyEvent.VK_SEMICOLON:
			e.tc().setCustom(SpiralTool.class, "sides", 4);
			break;
		case KeyEvent.VK_PERIOD:
			e.tc().setCustom(SpiralTool.class, "sides", 5);
			break;
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().setCustom(SpiralTool.class, "sides", 6);
			break;
		case KeyEvent.VK_QUOTE:
		case KeyEvent.VK_QUOTEDBL:
			e.tc().setCustom(SpiralTool.class, "sides", 8);
			break;
		case KeyEvent.VK_SLASH:
			e.tc().setCustom(SpiralTool.class, "sides", 32);
			break;
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_EQUALS:
			e.tc().incrementCustom(SpiralTool.class, "spacing", Double.class, 20.0);
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().decrementCustom(SpiralTool.class, "spacing", Double.class, 20.0, 1.0);
			break;
		case KeyEvent.VK_1:
			e.tc().setCustom(SpiralTool.class, "spacing", 5.0);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(SpiralTool.class, "spacing", 10.0);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(SpiralTool.class, "spacing", 15.0);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(SpiralTool.class, "spacing", 20.0);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(SpiralTool.class, "spacing", 25.0);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(SpiralTool.class, "spacing", 30.0);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(SpiralTool.class, "spacing", 35.0);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(SpiralTool.class, "spacing", 40.0);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(SpiralTool.class, "spacing", 45.0);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(SpiralTool.class, "spacing", 50.0);
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleCustom(SpiralTool.class, "spokes", false);
			break;
		}
		return false;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle rec) {
				int sides = tc.getCustom(SpiralTool.class, "sides", Integer.class, 32);
				double spacing = tc.getCustom(SpiralTool.class, "spacing", Double.class, 20.0);
				boolean spokes = tc.getCustom(SpiralTool.class, "spokes", Boolean.class, false);
				g.draw(new Spiral(sides, spacing, spokes, (float)rec.getCenterX(), (float)rec.getCenterY(), (float)rec.getMaxX(), (float)rec.getCenterY()));
			}
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("spiral.options.Sides"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 3; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(SpiralTool.class, "sides", Integer.class, 32); }
			public void setValue(int v) { tc.setCustom(SpiralTool.class, "sides", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("spiral.options.Spacing"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return 1; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(SpiralTool.class, "spacing", Double.class, 20.0); }
			public void setValue(double v) { tc.setCustom(SpiralTool.class, "spacing", v); }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public boolean getValue() { return tc.getCustom(SpiralTool.class, "spokes", Boolean.class, false); }
			public void setValue(boolean v) { tc.setCustom(SpiralTool.class, "spokes", v); }
			public boolean useTrueFalseLabels() { return false; }
			public String getLabel(boolean v) { return ToolUtilities.messages.getString("spiral.options.Spokes"); }
		});
		return f;
	}
}
