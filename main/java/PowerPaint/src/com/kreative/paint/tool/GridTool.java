package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.TextAlignment;
import com.kreative.paint.draw.GridDrawObject;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.form.PreviewGenerator;

public class GridTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,0,K,0,0,K,0,0,0,K,0,0,0,0,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public static final int NONE = GridDrawObject.NONE;
	public static final int LINEAR = GridDrawObject.LINEAR;
	public static final int LOGARITHMIC = GridDrawObject.LOGARITHMIC;
	
	private GridDrawObject makeDrawObject(ToolEvent e) {
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
		int horizGridType = e.tc().getCustom(GridTool.class, "hgt", Integer.class, LINEAR);
		float horizGridSpacing = e.tc().getCustom(GridTool.class, "hgs", Float.class, 10f);
		int vertGridType = e.tc().getCustom(GridTool.class, "vgt", Integer.class, LINEAR);
		float vertGridSpacing = e.tc().getCustom(GridTool.class, "vgs", Float.class, 10f);
		return new GridDrawObject(
			e.getPaintSettings(),
			Math.min(sx,x),
			Math.min(sy,y),
			Math.abs(x-sx),
			Math.abs(y-sy),
			(e.isCtrlDown() ? (
					(horizGridType == LINEAR) ? LOGARITHMIC :
					(horizGridType == LOGARITHMIC) ? LINEAR :
					horizGridType
			) : horizGridType),
			horizGridSpacing,
			(e.isCtrlDown() ? (
					(vertGridType == LINEAR) ? LOGARITHMIC :
					(vertGridType == LOGARITHMIC) ? LINEAR :
					vertGridType
			) : vertGridType),
			vertGridSpacing
		);
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.beginTransaction(getName());
		GridDrawObject wsh = makeDrawObject(e);
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			GridDrawObject wsh = makeDrawObject(e);
			wsh.paint(g);
			return true;
		}
		else return false;
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
				int horizGridType = tc.getCustom(GridTool.class, "hgt", Integer.class, LINEAR);
				float horizGridSpacing = tc.getCustom(GridTool.class, "hgs", Float.class, 10f);
				int vertGridType = tc.getCustom(GridTool.class, "vgt", Integer.class, LINEAR);
				float vertGridSpacing = tc.getCustom(GridTool.class, "vgs", Float.class, 10f);
				new GridDrawObject(
					new PaintSettings(
						Color.white, AlphaComposite.SrcOver, false,
						Color.black, AlphaComposite.SrcOver, new BasicStroke(1), false,
						new Font("SansSerif", 0, 12), TextAlignment.LEFT, false
					),
					rec.x,
					rec.y,
					rec.width,
					rec.height,
					horizGridType,
					horizGridSpacing,
					vertGridType,
					vertGridSpacing
				).paint(g);
			}
		});
		f.add(new IntegerEnumOption() {
			public String getName() { return ToolUtilities.messages.getString("grid.options.HorizGridType"); }
			public int getValue() { return tc.getCustom(GridTool.class, "hgt", Integer.class, LINEAR); }
			public void setValue(int v) { tc.setCustom(GridTool.class, "hgt", v); }
			public int[] values() { return new int[]{ NONE, LINEAR, LOGARITHMIC }; }
			public String getLabel(int v) {
				switch (v) {
				case NONE: return ToolUtilities.messages.getString("grid.options.Type.None");
				case LINEAR: return ToolUtilities.messages.getString("grid.options.Type.Linear");
				case LOGARITHMIC: return ToolUtilities.messages.getString("grid.options.Type.Logarithmic");
				default: return null;
				}
			}
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("grid.options.HorizGridSpacing"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return Double.MIN_VALUE; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(GridTool.class, "hgs", Float.class, 10f); }
			public void setValue(double v) { tc.setCustom(GridTool.class, "hgs", (float)v); }
		});
		f.add(new IntegerEnumOption() {
			public String getName() { return ToolUtilities.messages.getString("grid.options.VertGridType"); }
			public int getValue() { return tc.getCustom(GridTool.class, "vgt", Integer.class, LINEAR); }
			public void setValue(int v) { tc.setCustom(GridTool.class, "vgt", v); }
			public int[] values() { return new int[]{ NONE, LINEAR, LOGARITHMIC }; }
			public String getLabel(int v) {
				switch (v) {
				case NONE: return ToolUtilities.messages.getString("grid.options.Type.None");
				case LINEAR: return ToolUtilities.messages.getString("grid.options.Type.Linear");
				case LOGARITHMIC: return ToolUtilities.messages.getString("grid.options.Type.Logarithmic");
				default: return null;
				}
			}
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("grid.options.VertGridSpacing"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return Double.MIN_VALUE; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(GridTool.class, "vgs", Float.class, 10f); }
			public void setValue(double v) { tc.setCustom(GridTool.class, "vgs", (float)v); }
		});
		return f;
	}
}
