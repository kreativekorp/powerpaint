package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.TextAlignment;
import com.kreative.paint.draw.PerspectiveGridDrawObject;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;

public class PerspectiveGridTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,0,0,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,0,0,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,K,0,0,0,0,0,K,0,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,K,0,0,0,0,0,K,0,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,K,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,K,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,K,0,0,0,0,0,0,K,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private PerspectiveGridDrawObject makeDrawObject(ToolEvent e) {
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
		return new PerspectiveGridDrawObject(
				e.getPaintSettings(),
				Math.min(sx,x),
				Math.min(sy,y),
				Math.abs(x-sx),
				Math.abs(y-sy),
				e.tc().getCustom(PerspectiveGridTool.class, "nt", Integer.class, 20),
				e.tc().getCustom(PerspectiveGridTool.class, "nb", Integer.class, 10),
				e.tc().getCustom(PerspectiveGridTool.class, "nh", Integer.class, 10)
		);
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.beginTransaction(getName());
		PerspectiveGridDrawObject wsh = makeDrawObject(e);
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			PerspectiveGridDrawObject wsh = makeDrawObject(e);
			wsh.paint(g);
			return true;
		}
		else return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			e.tc().incrementCustom(PerspectiveGridTool.class, "nh", Integer.class, 20);
			break;
		case KeyEvent.VK_DOWN:
			e.tc().decrementCustom(PerspectiveGridTool.class, "nh", Integer.class, 20, 1);
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().decrementCustom(PerspectiveGridTool.class, "nt", Integer.class, 10, 1);
			break;
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_ADD:
			e.tc().incrementCustom(PerspectiveGridTool.class, "nt", Integer.class, 10);
			break;
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().decrementCustom(PerspectiveGridTool.class, "nb", Integer.class, 10, 1);
			break;
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().incrementCustom(PerspectiveGridTool.class, "nb", Integer.class, 10);
			break;
		case KeyEvent.VK_LEFT:
			e.tc().decrementCustom(PerspectiveGridTool.class, "nt", Integer.class, 10, 1);
			e.tc().decrementCustom(PerspectiveGridTool.class, "nb", Integer.class, 10, 1);
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().incrementCustom(PerspectiveGridTool.class, "nt", Integer.class, 10);
			e.tc().incrementCustom(PerspectiveGridTool.class, "nb", Integer.class, 10);
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
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle rec) {
				new PerspectiveGridDrawObject(
					new PaintSettings(
						Color.white, AlphaComposite.SrcOver, false,
						Color.black, AlphaComposite.SrcOver, new BasicStroke(1), false,
						new Font("SansSerif", 0, 12), TextAlignment.LEFT, false
					),
					rec.x,
					rec.y,
					rec.width,
					rec.height,
					tc.getCustom(PerspectiveGridTool.class, "nt", Integer.class, 20),
					tc.getCustom(PerspectiveGridTool.class, "nb", Integer.class, 10),
					tc.getCustom(PerspectiveGridTool.class, "nh", Integer.class, 10)
				).paint(g);
			}
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("pgrid.options.NumTop"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(PerspectiveGridTool.class, "nt", Integer.class, 20); }
			public void setValue(int v) { tc.setCustom(PerspectiveGridTool.class, "nt", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("pgrid.options.NumBottom"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(PerspectiveGridTool.class, "nb", Integer.class, 10); }
			public void setValue(int v) { tc.setCustom(PerspectiveGridTool.class, "nb", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("pgrid.options.NumHeight"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(PerspectiveGridTool.class, "nh", Integer.class, 10); }
			public void setValue(int v) { tc.setCustom(PerspectiveGridTool.class, "nh", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
