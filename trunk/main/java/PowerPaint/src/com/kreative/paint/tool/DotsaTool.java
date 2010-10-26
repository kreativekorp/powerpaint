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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.util.CursorUtils;

public class DotsaTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,K,K,K,0,0,K,0,
					0,0,K,0,0,0,0,0,K,0,0,0,K,0,K,0,
					0,K,0,0,0,0,0,0,K,0,0,0,K,0,0,K,
					0,K,0,0,0,0,0,0,K,0,0,0,K,0,0,K,
					0,K,0,0,0,0,0,0,0,K,K,K,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}

	private double dotSize;
	private Area dotShape;
	private Cursor dotCursor;
	private float lastX, lastY;
	
	public boolean toolSelected(ToolEvent e) {
		setDotSize(e.tc().getCustom(DotsaTool.class, "dotSize", Double.class, 20.0));
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		setDotSize(e.tc().getCustom(DotsaTool.class, "dotSize", Double.class, 20.0));
		return false;
	}
	
	private void setDotSize(double d) {
		dotSize = d;
		Shape outside = new Ellipse2D.Double(-d/2.0, -d/2.0, d, d);
		Shape inside = new Ellipse2D.Double(0, -d/4.0, d/4.0, d/4.0);
		dotShape = new Area(outside);
		dotShape.subtract(new Area(inside));
		Shape ooutside = new Ellipse2D.Double(-(d+2.0)/2.0, -(d+2.0)/2.0, d+2.0, d+2.0);
		Shape oinside = new Ellipse2D.Double(1.0, 1.0-d/4.0, d/4.0-2.0, d/4.0-2.0);
		Area outline = new Area(ooutside);
		outline.subtract(new Area(oinside));
		BufferedImage img = new BufferedImage((int)Math.ceil(d+2.0), (int)Math.ceil(d+2.0), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		AffineTransform at = AffineTransform.getTranslateInstance(Math.ceil(d+2.0)/2.0, Math.ceil(d+2.0)/2.0);
		g.setColor(Color.white);
		g.fill(at.createTransformedShape(outline));
		g.setColor(Color.black);
		g.fill(at.createTransformedShape(dotShape));
		dotCursor = CursorUtils.makeCursor(img, img.getWidth()/2, img.getHeight()/2, "Dotsa");
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float x = e.getX();
		float y = e.getY();
		lastX = x;
		lastY = y;
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		g.fill(at.createTransformedShape(dotShape));
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float x = e.getX();
		float y = e.getY();
		if (Math.hypot(x-lastX, y-lastY) > dotSize*3/4) {
			lastX = x;
			lastY = y;
			AffineTransform at = AffineTransform.getTranslateInstance(x, y);
			g.fill(at.createTransformedShape(dotShape));
			return true;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.commitTransaction();
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			if (dotSize >= 5.0) e.tc().setCustom(DotsaTool.class, "dotSize", dotSize-1.0);
			break;
		case KeyEvent.VK_DOWN:
			if (dotSize >= 8.0) e.tc().setCustom(DotsaTool.class, "dotSize", dotSize/2.0);
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().setCustom(DotsaTool.class, "dotSize", dotSize+1.0);
			break;
		case KeyEvent.VK_UP:
			e.tc().setCustom(DotsaTool.class, "dotSize", dotSize*2.0);
			break;
		case KeyEvent.VK_1:
			e.tc().setCustom(DotsaTool.class, "dotSize", 5.0);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(DotsaTool.class, "dotSize", 10.0);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(DotsaTool.class, "dotSize", 15.0);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(DotsaTool.class, "dotSize", 20.0);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(DotsaTool.class, "dotSize", 30.0);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(DotsaTool.class, "dotSize", 4.0);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(DotsaTool.class, "dotSize", 8.0);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(DotsaTool.class, "dotSize", 16.0);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(DotsaTool.class, "dotSize", 24.0);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(DotsaTool.class, "dotSize", 32.0);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return dotCursor;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("dotsa.options.DotSize"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return 1; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(DotsaTool.class, "dotSize", Double.class, 20.0); }
			public void setValue(double v) { tc.setCustom(DotsaTool.class, "dotSize", v); }
		});
		return f;
	}
}
