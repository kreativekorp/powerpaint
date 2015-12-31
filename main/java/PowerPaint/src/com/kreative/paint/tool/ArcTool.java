package com.kreative.paint.tool;

import java.awt.Image;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.geom.draw.RightArcDrawObject;

public class ArcTool extends SimpleShapeTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.SHAPE;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	protected ShapeDrawObject makeShape(ToolEvent e, float sx, float sy, float x, float y) {
		return new RightArcDrawObject(e.getPaintSettings(), sx, sy, x-sx, y-sy);
	}
}
