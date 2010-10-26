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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.Layer;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;

public class MovingVanTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,
					K,0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					K,0,0,0,0,0,0,0,0,0,K,K,K,K,0,0,
					K,0,0,0,0,0,0,0,0,0,K,0,K,0,K,0,
					K,0,0,0,0,0,0,0,0,0,K,0,K,0,K,0,
					K,0,0,0,0,0,0,0,0,0,K,0,K,K,K,K,
					K,0,0,0,0,0,0,0,0,0,K,0,0,0,0,K,
					K,0,K,K,0,0,0,K,K,0,K,0,K,K,0,K,
					K,K,0,0,K,K,K,0,0,K,K,K,0,0,K,K,
					0,K,0,0,K,0,K,0,0,K,0,K,0,0,K,0,
					0,0,K,K,0,0,0,K,K,0,0,0,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		Layer l = e.getLayer();
		if (l != null) {
			e.beginTransaction(getName());
			int width = e.tc().getCustom(MovingVanTool.class, "vanWidth", Integer.class, 32);
			int height = e.tc().getCustom(MovingVanTool.class, "vanHeight", Integer.class, 32);
			Shape s = new Rectangle2D.Float(e.getX()-width/2.0f, e.getY()-height/2.0f, width, height);
			l.popImage(s, e.isAltDown());
			return true;
		}
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Layer l = e.getLayer();
		if (l != null) {
			l.transformPoppedImage(AffineTransform.getTranslateInstance(e.getX()-e.getPreviousX(), e.getY()-e.getPreviousY()));
			return true;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		Layer l = e.getLayer();
		if (l != null) {
			l.pushImage();
			e.commitTransaction();
			return true;
		}
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseOnCanvas()) {
			g.setStroke(new BasicStroke(1));
			g.setPaint(new Color(0x66666666, true));
			g.setComposite(AlphaComposite.SrcOver);
			int width = e.tc().getCustom(MovingVanTool.class, "vanWidth", Integer.class, 32);
			int height = e.tc().getCustom(MovingVanTool.class, "vanHeight", Integer.class, 32);
			Shape s = new Rectangle2D.Float(e.getX()-width/2.0f, e.getY()-height/2.0f, width-1, height-1);
			g.draw(s);
			return true;
		}
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_1: e.tc().setCustom(MovingVanTool.class, "vanWidth", 8); break;
		case KeyEvent.VK_2: e.tc().setCustom(MovingVanTool.class, "vanWidth", 16); break;
		case KeyEvent.VK_3: e.tc().setCustom(MovingVanTool.class, "vanWidth", 32); break;
		case KeyEvent.VK_4: e.tc().setCustom(MovingVanTool.class, "vanWidth", 64); break;
		case KeyEvent.VK_5: e.tc().setCustom(MovingVanTool.class, "vanWidth", 128); break;
		case KeyEvent.VK_6: e.tc().setCustom(MovingVanTool.class, "vanHeight", 8); break;
		case KeyEvent.VK_7: e.tc().setCustom(MovingVanTool.class, "vanHeight", 16); break;
		case KeyEvent.VK_8: e.tc().setCustom(MovingVanTool.class, "vanHeight", 32); break;
		case KeyEvent.VK_9: e.tc().setCustom(MovingVanTool.class, "vanHeight", 64); break;
		case KeyEvent.VK_0: e.tc().setCustom(MovingVanTool.class, "vanHeight", 128); break;
		case KeyEvent.VK_LEFT: e.tc().decrementCustom(MovingVanTool.class, "vanWidth", Integer.class, 32, 1); break;
		case KeyEvent.VK_RIGHT: e.tc().incrementCustom(MovingVanTool.class, "vanWidth", Integer.class, 32); break;
		case KeyEvent.VK_DOWN: e.tc().decrementCustom(MovingVanTool.class, "vanHeight", Integer.class, 32, 1); break;
		case KeyEvent.VK_UP: e.tc().incrementCustom(MovingVanTool.class, "vanHeight", Integer.class, 32); break;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("movingvan.options.Width"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(MovingVanTool.class, "vanWidth", Integer.class, 32); }
			public void setValue(int v) { tc.setCustom(MovingVanTool.class, "vanWidth", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("movingvan.options.Height"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(MovingVanTool.class, "vanHeight", Integer.class, 32); }
			public void setValue(int v) { tc.setCustom(MovingVanTool.class, "vanHeight", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
