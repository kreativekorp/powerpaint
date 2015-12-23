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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.util.CursorUtils;

public class EraserTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,K,K,
					0,0,0,0,0,0,K,0,0,0,0,0,0,K,0,K,
					0,0,0,0,0,K,0,0,0,0,0,0,K,0,K,K,
					0,0,0,0,K,0,0,0,0,0,0,K,0,K,K,0,
					0,0,0,K,0,0,0,0,0,0,K,0,K,K,0,0,
					0,0,K,0,0,0,0,0,0,K,0,K,K,0,0,0,
					0,K,0,0,0,0,0,0,K,0,K,K,0,0,0,0,
					K,K,K,K,K,K,K,K,0,K,K,0,0,0,0,0,
					K,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					K,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int eraserSize;
	private Cursor eraserCursor;
	
	public boolean toolSelected(ToolEvent e) {
		setEraserSize(e.tc().getCustom(EraserTool.class, "size", Integer.class, 16));
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		setEraserSize(e.tc().getCustom(EraserTool.class, "size", Integer.class, 16));
		return false;
	}
	
	private void setEraserSize(int size) {
		eraserSize = size;
		if (size < 3) size = 3;
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, size, size);
		g.setColor(Color.white);
		g.fillRect(1, 1, size-2, size-2);
		g.dispose();
		eraserCursor = CursorUtils.makeCursor(img, size/2, size/2, "Eraser");
	}
	
	public boolean toolDoubleClicked(ToolEvent e) {
		e.beginTransaction(getName());
		e.getPaintSurface().clearAll();
		e.commitTransaction();
		return true;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		e.getPaintSurface().clear((int)e.getX()-eraserSize/2, (int)e.getY()-eraserSize/2, eraserSize, eraserSize);
		return true;
	}
	
	private void drag(PaintSurface p, int sx, int sy, int dx, int dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			int x = sx + ((dx-sx)*i)/m;
			int y = sy + ((dy-sy)*i)/m;
			p.clear(x-eraserSize/2, y-eraserSize/2, eraserSize, eraserSize);
		}
	}

	public boolean mouseDragged(ToolEvent e) {
		drag(e.getPaintSurface(), (int)e.getPreviousX(), (int)e.getPreviousY(), (int)e.getX(), (int)e.getY());
		return true;
	}

	public boolean mouseReleased(ToolEvent e) {
		drag(e.getPaintSurface(), (int)e.getPreviousX(), (int)e.getPreviousY(), (int)e.getX(), (int)e.getY());
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().decrementCustom(EraserTool.class, "size", Integer.class, 16, 1);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().incrementCustom(EraserTool.class, "size", Integer.class, 16);
			break;
		case KeyEvent.VK_ALT:
			if (eraserSize > 1) e.tc().setCustom(EraserTool.class, "size", eraserSize/2);
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().setCustom(EraserTool.class, "size", 1);
			break;
		case KeyEvent.VK_1:
			e.tc().setCustom(EraserTool.class, "size", 2);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(EraserTool.class, "size", 4);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(EraserTool.class, "size", 8);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(EraserTool.class, "size", 16);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(EraserTool.class, "size", 32);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(EraserTool.class, "size", 3);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(EraserTool.class, "size", 6);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(EraserTool.class, "size", 12);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(EraserTool.class, "size", 24);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(EraserTool.class, "size", 5);
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().setCustom(EraserTool.class, "size", 10);
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_PLUS:
			e.tc().setCustom(EraserTool.class, "size", 20);
			break;
		}
		return false;
	}
	
	public boolean keyReleased(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			e.tc().setCustom(EraserTool.class, "size", eraserSize*2);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return eraserCursor;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("eraser.options.EraserSize"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(EraserTool.class, "size", Integer.class, 16); }
			public void setValue(int v) { tc.setCustom(EraserTool.class, "size", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
