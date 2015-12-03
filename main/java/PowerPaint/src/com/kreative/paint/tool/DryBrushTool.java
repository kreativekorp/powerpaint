package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import com.kreative.paint.sprite.ColorTransform;
import com.kreative.paint.sprite.Sprite;

public class DryBrushTool extends AbstractPaintTool implements ToolOptions.Brushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					0,K,K,K,K,K,K,K,K,0,K,K,0,K,K,0,
					K,K,K,K,K,K,K,K,0,K,K,K,K,0,0,0,
					K,K,K,K,K,0,K,0,K,K,K,0,K,K,0,0,
					K,K,K,K,K,K,K,0,0,0,K,K,K,K,K,K,
					K,K,K,K,K,K,K,K,K,K,0,K,K,K,K,0,
					K,K,K,K,K,K,0,0,K,K,K,0,K,0,0,0,
					0,K,K,K,K,K,K,K,0,0,K,K,K,K,0,0,
					0,0,K,K,K,K,K,K,K,K,0,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private Random random = new Random();
	private int w, h, hx, hy;
	private ColorTransform tx;
	private int[] rgb;
	
	private void unmakeBrush(Sprite brush) {
		w = brush.getWidth();
		h = brush.getHeight();
		hx = brush.getHotspotX();
		hy = brush.getHotspotY();
		tx = brush.getColorTransform();
		int[] raw = brush.getRawPixels();
		rgb = new int[raw.length];
		for (int i = 0; i < raw.length; i++) {
			rgb[i] = raw[i];
		}
	}
	
	private Sprite makeBrush() {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, rgb, 0, w);
		return new Sprite(img, hx, hy, tx);
	}
	
	public boolean mousePressed(ToolEvent e) {
		unmakeBrush(e.tc().getBrush());
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		makeBrush().paint(g, (int)e.getX(), (int)e.getY());
		return true;
	}
	
	private void drag(Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			rgb[random.nextInt(rgb.length)] = 0;
			makeBrush().paint(g, (int)x, (int)y);
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float lastX = e.getPreviousX();
		float lastY = e.getPreviousY();
		float x = e.getX();
		float y = e.getY();
		drag(g, lastX, lastY, x, y);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		mouseDragged(e);
		e.commitTransaction();
		return true;
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
