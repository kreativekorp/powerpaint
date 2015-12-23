package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.material.sprite.Sprite;

public class SmudgeTool extends AbstractPaintTool implements ToolOptions.Brushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,K,0,0,0,0,0,0,K,0,0,
					0,0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,K,0,K,0,0,0,0,0,0,0,0,K,0,
					0,0,K,0,K,0,K,0,0,0,0,0,0,0,K,0,
					0,0,K,K,0,K,0,K,0,0,0,0,0,0,K,0,
					0,0,K,0,K,0,K,0,0,0,K,0,0,0,K,0,
					0,0,K,K,0,K,0,0,0,K,K,0,0,0,K,0,
					0,0,0,K,0,K,0,0,K,0,K,0,0,0,K,0,
					0,0,0,K,K,0,0,K,K,K,0,0,0,K,0,0,
					0,0,0,0,K,0,0,K,0,0,0,0,K,0,0,0,
					0,0,0,K,0,0,K,K,K,K,K,K,0,0,0,0,
					0,0,0,K,0,0,K,0,0,0,0,0,0,0,0,0,
					0,0,K,0,0,K,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int w, h, hx, hy;
	private int[] rgb;
	
	private void unmakeBrush(Sprite brush) {
		w = brush.getWidth();
		h = brush.getHeight();
		hx = brush.getHotspotX();
		hy = brush.getHotspotY();
		rgb = brush.getPreparedPixels();
	}
	
	public boolean mousePressed(ToolEvent e) {
		unmakeBrush(e.tc().getBrush());
		e.beginTransaction(getName());
		float x = e.getX() - hx;
		float y = e.getY() - hy;
		blurRGB(e.getPaintSurface(), (int)x, (int)y, w, h, rgb);
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		float lastX = e.getPreviousX();
		float lastY = e.getPreviousY();
		float x = e.getX();
		float y = e.getY();
		drag(e.getPaintSurface(), lastX, lastY, x, y);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		float lastX = e.getPreviousX();
		float lastY = e.getPreviousY();
		float x = e.getX();
		float y = e.getY();
		drag(e.getPaintSurface(), lastX, lastY, x, y);
		e.commitTransaction();
		return true;
	}
	
	private void drag(PaintSurface srf, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m - hx;
			float y = sy + ((dy-sy)*i)/m - hy;
			blurRGB(srf, (int)x, (int)y, w, h, rgb);
		}
	}
	
	private void blurRGB(PaintSurface srf, int x, int y, int w, int h, int[] argb) {
		int[] irgb = new int[(w+2)*(h+2)];
		int[] orgb = new int[(w+2)*(h+2)];
		srf.getRGB(x-1, y-1, w+2, h+2, irgb, 0, w+2);
		srf.getRGB(x-1, y-1, w+2, h+2, orgb, 0, w+2);
		for (int yy = 0, ay = 0, iy = w+3; yy < h; yy++, ay += w, iy += w+2) {
			for (int xx = 0, ax = ay, ix = iy; xx < w; xx++, ax++, ix++) {
				int c = argb[ax];
				if (((c & 0xFF000000) < 0) && ((c & 0xFF0000) < 0x800000) && ((c & 0xFF00) < 0x8000) && ((c & 0xFF) < 0x80)) {
					int c0 = irgb[ix];
					int c1 = irgb[ix-1];
					int c2 = irgb[ix+1];
					int c3 = irgb[ix-w-2];
					int c4 = irgb[ix+w+2];
					int aa = (((c0>>>24)&0xFF)*4+((c1>>>24)&0xFF)+((c2>>>24)&0xFF)+((c3>>>24)&0xFF)+((c4>>>24)&0xFF))/8;
					int ra = (((c0>>>16)&0xFF)*4+((c1>>>16)&0xFF)+((c2>>>16)&0xFF)+((c3>>>16)&0xFF)+((c4>>>16)&0xFF))/8;
					int ga = (((c0>>>8)&0xFF)*4+((c1>>>8)&0xFF)+((c2>>>8)&0xFF)+((c3>>>8)&0xFF)+((c4>>>8)&0xFF))/8;
					int ba = ((c0&0xFF)*4+(c1&0xFF)+(c2&0xFF)+(c3&0xFF)+(c4&0xFF))/8;
					int ca = ((aa&0xFF)<<24) | ((ra&0xFF)<<16) | ((ga&0xFF)<<8) | (ba&0xFF);
					orgb[ix] = ca;
				}
			}
		}
		srf.setRGB(x-1, y-1, w+2, h+2, orgb, 0, w+2);
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().prevBrush();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().nextBrush();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getBrush().getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
