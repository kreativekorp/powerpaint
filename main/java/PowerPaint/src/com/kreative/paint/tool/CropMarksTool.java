package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.draw.CropMarkDrawObject;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;

public class CropMarksTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.DrawFilled, ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					K,K,K,K,K,0,0,K,0,K,0,K,0,K,0,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private DrawObject makeObject(ToolEvent e) {
		int divH = e.tc().getCustom(CropMarksTool.class, "divH", Integer.class, 1);
		int divV = e.tc().getCustom(CropMarksTool.class, "divV", Integer.class, 1);
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.isAltDown() != e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		PaintSettings ps = e.getPaintSettings();
		if (e.isCtrlDown() == e.tc().drawFilled()) {
			ps = ps.deriveFillPaint(null);
		}
		return new CropMarkDrawObject(ps, sx, sy, x, y, divH, divV);
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.beginTransaction(getName());
		DrawObject o = makeObject(e);
		if (e.isInDrawMode()) e.getDrawSurface().add(o);
		else o.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			DrawObject o = makeObject(e);
			o.paint(g);
			return true;
		}
		else return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().decrementCustom(CropMarksTool.class, "divH", Integer.class, 1, 1);
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().incrementCustom(CropMarksTool.class, "divH", Integer.class, 1);
			break;
		case KeyEvent.VK_DOWN:
			e.tc().decrementCustom(CropMarksTool.class, "divV", Integer.class, 1, 1);
			break;
		case KeyEvent.VK_UP:
			e.tc().incrementCustom(CropMarksTool.class, "divV", Integer.class, 1);
			break;
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
			public String getName() { return ToolUtilities.messages.getString("cropmarks.options.HorizDiv"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(CropMarksTool.class, "divH", Integer.class, 1); }
			public void setValue(int v) { tc.setCustom(CropMarksTool.class, "divH", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("cropmarks.options.VertDiv"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(CropMarksTool.class, "divV", Integer.class, 1); }
			public void setValue(int v) { tc.setCustom(CropMarksTool.class, "divV", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
