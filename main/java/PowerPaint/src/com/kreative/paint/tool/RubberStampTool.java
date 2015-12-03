package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import com.kreative.paint.sprite.ColorTransform;
import com.kreative.paint.sprite.Sprite;
import com.kreative.paint.util.CursorUtils;

public class RubberStampTool extends AbstractPaintDrawTool implements ToolOptions.RubberStamps {
	private static final int K = 0xFF000000;
	private static final int H = 0x80808080;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,K,K,K,0,K,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,K,K,K,K,K,0,K,K,K,K,K,0,0,
					0,0,K,0,0,0,0,K,K,K,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
			}
	);
	private static final Cursor pickupCursor = CursorUtils.makeCursor(
			32, 32,
			new int[] {
					H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,H,
					H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,H,
			},
			16, 16,
			"StampPickup"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean stamp(ToolEvent e) {
		if (e.isInPaintMode() && e.isAltDown()) {
			float x = e.getX() - 16;
			float y = e.getY() - 16;
			int[] rgb = new int[1024];
			e.getPaintSurface().getRGB((int)x, (int)y, 32, 32, rgb, 0, 32);
			BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, 32, 32, rgb, 0, 32);
			e.tc().setRubberStamp(new Sprite(img, 16, 16, ColorTransform.NONE));
			return false;
		} else {
			Graphics2D g = e.isInDrawMode() ? e.getDrawGraphics() : e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			e.tc().getRubberStamp().paint(g, (int)e.getX(), (int)e.getY());
			return true;
		}
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		return stamp(e);
	}
	
	public boolean mouseDragged(ToolEvent e) {
		return e.isCtrlDown() && stamp(e);
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.commitTransaction();
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().prevRubberStamp();
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().nextRubberStamp();
			break;
		case KeyEvent.VK_UP:
			e.tc().prevRubberStampSet();
			break;
		case KeyEvent.VK_DOWN:
			e.tc().nextRubberStampSet();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return (e.isInPaintMode() && e.isAltDown()) ? pickupCursor :
			e.tc().getRubberStamp().getPreparedCursor(false);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
