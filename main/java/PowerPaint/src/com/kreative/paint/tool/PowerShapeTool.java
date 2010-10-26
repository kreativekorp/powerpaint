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

import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import com.kreative.paint.geom.ParameterizedShape;
import com.kreative.paint.geom.ScaledShape;

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
	
	protected Shape makeShape(ToolEvent e, float sx, float sy, float x, float y) {
		return new ScaledShape(sx, sy, x-sx, y-sy, new ParameterizedShape(e.tc().getPowerShape()));
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
