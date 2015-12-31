package com.kreative.paint.tool;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.geom.Cycloid;
import com.kreative.paint.geom.draw.CycloidDrawObject;

public class CycloidTool extends CircularShapeTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,0,
					0,0,K,K,0,0,K,0,K,0,0,K,K,0,0,0,
					0,0,K,0,K,0,K,0,K,0,K,0,K,0,0,0,
					0,0,0,K,0,K,K,0,K,K,0,K,0,0,0,0,
					0,0,0,0,K,0,K,0,K,0,K,0,0,0,0,0,
					0,K,K,K,K,K,0,K,0,K,K,K,K,K,0,0,
					K,0,0,0,0,0,K,0,K,0,0,0,0,0,K,0,
					0,K,K,K,K,K,0,K,0,K,K,K,K,K,0,0,
					0,0,0,0,K,0,K,0,K,0,K,0,0,0,0,0,
					0,0,0,K,0,K,K,0,K,K,0,K,0,0,0,0,
					0,0,K,0,K,0,K,0,K,0,K,0,K,0,0,0,
					0,0,K,K,0,0,K,0,K,0,0,K,K,0,0,0,
					0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
			}
	);

	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public ShapeDrawObject makeShape(ToolEvent e, float cx, float cy, float ex, float ey) {
		return new CycloidDrawObject(
			e.getPaintSettings(),
			e.tc().getCustom(CycloidTool.class, "epi", Boolean.class, false),
			e.tc().getCustom(CycloidTool.class, "smoothness", Integer.class, Cycloid.DEFAULT_SMOOTHNESS),
			e.tc().getCustom(CycloidTool.class, "begin", Integer.class, Cycloid.DEFAULT_BEGIN),
			e.tc().getCustom(CycloidTool.class, "end", Integer.class, Cycloid.DEFAULT_END),
			e.tc().getCustom(CycloidTool.class, "R", Double.class, Cycloid.DEFAULT_R_FOR_HYPOCYCLOID),
			e.tc().getCustom(CycloidTool.class, "r", Double.class, Cycloid.DEFAULT_r),
			e.tc().getCustom(CycloidTool.class, "d", Double.class, Cycloid.DEFAULT_d),
			cx, cy, ex, ey
		);
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle rec) {
				g.draw(new Cycloid(
						tc.getCustom(CycloidTool.class, "epi", Boolean.class, false),
						tc.getCustom(CycloidTool.class, "smoothness", Integer.class, Cycloid.DEFAULT_SMOOTHNESS),
						tc.getCustom(CycloidTool.class, "begin", Integer.class, Cycloid.DEFAULT_BEGIN),
						tc.getCustom(CycloidTool.class, "end", Integer.class, Cycloid.DEFAULT_END),
						tc.getCustom(CycloidTool.class, "R", Double.class, Cycloid.DEFAULT_R_FOR_HYPOCYCLOID),
						tc.getCustom(CycloidTool.class, "r", Double.class, Cycloid.DEFAULT_r),
						tc.getCustom(CycloidTool.class, "d", Double.class, Cycloid.DEFAULT_d),
						(float)rec.getCenterX(), (float)rec.getCenterY(), (float)rec.getMaxX(), (float)rec.getCenterY()
				));
			}
		});
		f.add(new BooleanOption() {
			public String getName() { return ToolUtilities.messages.getString("cycloid.options.Type"); }
			public boolean getValue() { return tc.getCustom(CycloidTool.class, "epi", Boolean.class, false); }
			public void setValue(boolean v) { tc.setCustom(CycloidTool.class, "epi", v); }
			public boolean useTrueFalseLabels() { return true; }
			public String getLabel(boolean v) { return ToolUtilities.messages.getString(v ? "cycloid.options.Type.Epi" : "cycloid.options.Type.Hyp"); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("cycloid.options.BigR"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return Double.MIN_VALUE; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(CycloidTool.class, "R", Double.class, Cycloid.DEFAULT_R_FOR_HYPOCYCLOID); }
			public void setValue(double v) { tc.setCustom(CycloidTool.class, "R", v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("cycloid.options.LittleR"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return Double.MIN_VALUE; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(CycloidTool.class, "r", Double.class, Cycloid.DEFAULT_r); }
			public void setValue(double v) { tc.setCustom(CycloidTool.class, "r", v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("cycloid.options.D"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return -Double.MAX_VALUE; }
			public double getStep() { return 1; }
			public double getValue() { return tc.getCustom(CycloidTool.class, "d", Double.class, Cycloid.DEFAULT_d); }
			public void setValue(double v) { tc.setCustom(CycloidTool.class, "d", v); }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("cycloid.options.Smoothness"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(CycloidTool.class, "smoothness", Integer.class, Cycloid.DEFAULT_SMOOTHNESS); }
			public void setValue(int v) { tc.setCustom(CycloidTool.class, "smoothness", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
