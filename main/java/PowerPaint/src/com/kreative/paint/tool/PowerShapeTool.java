package com.kreative.paint.tool;

import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.geom.draw.PowerShapeDrawObject;

public class PowerShapeTool extends SimpleShapeTool implements ToolOptions.PowerShapes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,
					0,0,0,K,K,K,K,0,0,0,0,K,0,K,K,0,
					0,0,K,0,0,0,0,0,0,0,0,K,K,0,0,K,
					0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,K,0,
					0,0,K,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,0,
					K,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
					K,0,0,0,0,K,K,K,0,0,0,0,K,0,0,0,
					0,K,0,0,K,0,0,0,K,0,0,K,0,0,0,0,
					0,0,K,K,0,0,0,0,0,K,K,0,0,0,0,0,
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
		return new PowerShapeDrawObject(e.getPaintSettings(), e.tc().getPowerShape().clone(), sx, sy, x-sx, y-sy);
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().prevPowerShape();
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().nextPowerShape();
			break;
		case KeyEvent.VK_UP:
			e.tc().prevPowerShapeSet();
			break;
		case KeyEvent.VK_DOWN:
			e.tc().nextPowerShapeSet();
			break;
		case KeyEvent.VK_1:
			e.tc().setPowerShapeIndex(0);
			break;
		case KeyEvent.VK_2:
			e.tc().setPowerShapeIndex(1);
			break;
		case KeyEvent.VK_3:
			e.tc().setPowerShapeIndex(2);
			break;
		case KeyEvent.VK_4:
			e.tc().setPowerShapeIndex(3);
			break;
		case KeyEvent.VK_5:
			e.tc().setPowerShapeIndex(4);
			break;
		case KeyEvent.VK_6:
			e.tc().setPowerShapeIndex(5);
			break;
		case KeyEvent.VK_7:
			e.tc().setPowerShapeIndex(6);
			break;
		case KeyEvent.VK_8:
			e.tc().setPowerShapeIndex(7);
			break;
		case KeyEvent.VK_9:
			e.tc().setPowerShapeIndex(8);
			break;
		case KeyEvent.VK_0:
			e.tc().setPowerShapeIndex(9);
			break;
		}
		return super.keyPressed(e);
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
