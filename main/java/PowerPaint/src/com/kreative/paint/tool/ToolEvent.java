/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import com.kreative.paint.Canvas;
import com.kreative.paint.CanvasView;
import com.kreative.paint.DrawSurface;
import com.kreative.paint.Layer;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintContextConstants;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.draw.DrawObject;

public class ToolEvent implements PaintContextConstants {
	public static final int TOOL_SELECTED			= 1;
	public static final int TOOL_DOUBLE_CLICKED		= 2;
	public static final int TOOL_DESELECTED			= 3;
	public static final int TOOL_SETTINGS_CHANGED   = 4;
	public static final int PAINT_SETTINGS_CHANGED	= 5;
	public static final int MOUSE_ENTERED			= 6;
	public static final int MOUSE_MOVED				= 7;
	public static final int MOUSE_PRESSED			= 8;
	public static final int MOUSE_HELD				= 9;
	public static final int MOUSE_CLICKED			= 10;
	public static final int MOUSE_DRAGGED			= 11;
	public static final int MOUSE_RELEASED			= 12;
	public static final int MOUSE_EXITED			= 13;
	public static final int KEY_PRESSED				= 14;
	public static final int KEY_TYPED				= 15;
	public static final int KEY_RELEASED			= 16;
	public static final int RESPONDS_TO_COMMAND     = 17;
	public static final int ENABLE_COMMAND          = 18;
	public static final int DO_COMMAND              = 19;
	
	private History history;
	private ToolContext tc;
	private Tool prevTool;
	private Tool nextTool;
	private long tcdelta;
	private PaintContext pc;
	private PaintSettings ps;
	private int psdelta;
	private CanvasView canvasView;
	private Canvas canvas;
	private Layer layer;
	private int eventType;
	private float canvasX;
	private float canvasY;
	private float lastCanvasX;
	private float lastCanvasY;
	private float clickCanvasX;
	private float clickCanvasY;
	private float mouseX;
	private float mouseY;
	private float lastX;
	private float lastY;
	private float clickX;
	private float clickY;
	private boolean mouseDown;
	private boolean mouseOnCanvas;
	private int clickCount;
	private int modifiers;
	private int keyCode;
	private char keyChar;
	private ToolCommand command;
	
	public ToolEvent(
			History h,
			ToolContext tc,
			Tool pt,
			Tool nt,
			long tcdelta,
			PaintContext pc,
			PaintSettings ps,
			int psdelta,
			CanvasView cv,
			Canvas c,
			Layer l,
			int eventType,
			float cvx, float cvy,
			float lcx, float lcy,
			float ccx, float ccy,
			float mx, float my,
			float lx, float ly,
			float cx, float cy,
			boolean mouse, boolean mouseIn,
			int cc,
			int mod,
			int key, char ch,
			ToolCommand cmd
	) {
		this.history = h;
		this.tc = tc;
		this.prevTool = pt;
		this.nextTool = nt;
		this.tcdelta = tcdelta;
		this.pc = pc;
		this.ps = ps;
		this.psdelta = psdelta;
		this.canvasView = cv;
		this.canvas = c;
		this.layer = l;
		this.eventType = eventType;
		this.canvasX = cvx;
		this.canvasY = cvy;
		this.lastCanvasX = lcx;
		this.lastCanvasY = lcy;
		this.clickCanvasX = ccx;
		this.clickCanvasY = ccy;
		this.mouseX = mx;
		this.mouseY = my;
		this.lastX = lx;
		this.lastY = ly;
		this.clickX = cx;
		this.clickY = cy;
		this.mouseDown = mouse;
		this.mouseOnCanvas = mouseIn;
		this.clickCount = cc;
		this.modifiers = mod;
		this.keyCode = key;
		this.keyChar = ch;
		this.command = cmd;
	}
	
	public History getHistory() {
		return history;
	}
	
	public void beginTransaction(String name) {
		if (history == null) System.err.println("Warning: Transaction \""+(name==null?"":name)+"\" began with no History to record it.");
		else {
			history.begin(name);
			if (tc.getTool().pushPaintSelectionUponEntry())
				if (canvas.isPaintSelectionPopped())
					canvas.pushPaintSelection();
			if (tc.getTool().clearDrawSelectionUponEntry())
				for (Layer l : canvas)
					for (DrawObject o : l)
						if (o.isSelected())
							o.setSelected(false);
		}
	}
	
	public void renameTransaction(String name) {
		if (history == null) System.err.println("Warning: Transaction renamed to \""+(name==null?"":name)+"\" with no History to record it.");
		else history.rename(name);
	}
	
	public void commitTransaction() {
		if (history == null) System.err.println("Warning: Transaction committed with no History to record it.");
		else history.commit();
	}
	
	public void rollbackTransaction() {
		if (history == null) System.err.println("Warning: Transaction rolled back with no History to record it.");
		else history.rollback();
	}
	
	public boolean isTransactionInProgress() {
		if (history == null) return false;
		else return history.isInProgress();
	}
	
	// this has a short name to make code in Tool classes easier to read
	public ToolContext tc() {
		return tc;
	}
	
	public Tool getPreviousTool() {
		return prevTool;
	}
	
	public Tool getNextTool() {
		return nextTool;
	}
	
	public boolean isInPaintMode() {
		return tc.isInPaintMode();
	}
	
	public boolean isInDrawMode() {
		return tc.isInDrawMode();
	}
	
	public long getToolContextDelta() {
		return tcdelta;
	}
	
	public PaintContext getPaintContext() {
		return pc;
	}
	
	public PaintSettings getPaintSettings() {
		return ps;
	}
	
	public int getPaintSettingsDelta() {
		return psdelta;
	}
	
	public CanvasView getCanvasView() {
		return canvasView;
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public Layer getLayer() {
		return layer;
	}
	
	public PaintSurface getPaintSurface() {
		return layer;
	}
	
	public Graphics2D getPaintGraphics() {
		return (layer == null) ? null : layer.getCachedPaintGraphics();
	}
	
	public DrawSurface getDrawSurface() {
		return layer;
	}
	
	public Graphics2D getDrawGraphics() {
		return (layer == null) ? null : layer.getCachedDrawGraphics();
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public float getCanvasX() {
		return canvasX;
	}
	
	public float getCanvasY() {
		return canvasY;
	}
	
	public float getCanvasPreviousX() {
		return lastCanvasX;
	}
	
	public float getCanvasPreviousY() {
		return lastCanvasY;
	}
	
	public float getCanvasPreviousClickedX() {
		return clickCanvasX;
	}
	
	public float getCanvasPreviousClickedY() {
		return clickCanvasY;
	}
	
	public float getX() {
		return mouseX;
	}
	
	public float getY() {
		return mouseY;
	}
	
	public float getPreviousX() {
		return lastX;
	}
	
	public float getPreviousY() {
		return lastY;
	}
	
	public float getPreviousClickedX() {
		return clickX;
	}
	
	public float getPreviousClickedY() {
		return clickY;
	}
	
	public boolean isMouseDown() {
		return mouseDown;
	}
	
	public boolean isMouseOnCanvas() {
		return mouseOnCanvas;
	}
	
	public int getClickCount() {
		return clickCount;
	}
	
	public boolean isShiftDown() {
		return (modifiers & KeyEvent.SHIFT_MASK) != 0;
	}
	
	public boolean isAltDown() {
		return (modifiers & KeyEvent.ALT_MASK) != 0;
	}
	
	public boolean isCtrlDown() {
		return (modifiers & (KeyEvent.CTRL_MASK | KeyEvent.META_MASK)) != 0;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public char getChar() {
		return keyChar;
	}
	
	public ToolCommand getCommand() {
		return command;
	}
}
