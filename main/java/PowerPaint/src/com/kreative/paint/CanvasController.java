package com.kreative.paint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;

import com.kreative.paint.draw.ControlPoint;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolCommand;
import com.kreative.paint.tool.ToolEvent;
import com.kreative.paint.util.InputUtils;

public class CanvasController
implements MouseListener, MouseMotionListener, KeyListener,
ToolContextListener, PaintContextListener, CanvasPaintListener {
	private Canvas c;
	private CanvasView cv;
	private ToolContext tc;
	private PaintContext pc;
	private ToolEvent lastTE;
	private Collection<CanvasControllerListener> listeners;
	
	// SETUP
	
	public CanvasController(Canvas c, CanvasView cv, ToolContext tc, PaintContext pc) {
		this.c = c;
		this.cv = cv;
		this.tc = tc;
		this.pc = pc;
		this.lastTE = null;
		this.listeners = new HashSet<CanvasControllerListener>();
		if (cv != null) {
			cv.setCanvas(c);
			cv.addMouseListener(this);
			cv.addMouseMotionListener(this);
			cv.addKeyListener(this);
			cv.addPaintListener(this);
		}
		if (tc != null) {
			tc.addToolContextListener(this);
		}
		if (pc != null) {
			pc.addPaintContextListener(this);
		}
		new AnimationThread().start();
	}
	
	public synchronized Canvas getCanvas() {
		return c;
	}
	
	public synchronized void setCanvas(Canvas c) {
		this.c = c;
		if (cv != null) {
			cv.setCanvas(c);
		}
	}
	
	public synchronized CanvasView getCanvasView() {
		return cv;
	}
	
	public synchronized void setCanvasView(CanvasView cv) {
		if (this.cv != null) {
			this.cv.removeMouseListener(this);
			this.cv.removeMouseMotionListener(this);
			this.cv.removeKeyListener(this);
			this.cv.removePaintListener(this);
		}
		this.cv = cv;
		if (cv != null) {
			cv.setCanvas(c);
			cv.addMouseListener(this);
			cv.addMouseMotionListener(this);
			cv.addKeyListener(this);
			cv.addPaintListener(this);
		}
	}
	
	public synchronized void setCanvasAndView(Canvas c, CanvasView cv) {
		if (this.cv != null) {
			this.cv.removeMouseListener(this);
			this.cv.removeMouseMotionListener(this);
			this.cv.removeKeyListener(this);
			this.cv.removePaintListener(this);
		}
		this.c = c;
		this.cv = cv;
		if (cv != null) {
			cv.setCanvas(c);
			cv.addMouseListener(this);
			cv.addMouseMotionListener(this);
			cv.addKeyListener(this);
			cv.addPaintListener(this);
		}
	}
	
	public synchronized ToolContext getToolContext() {
		return tc;
	}
	
	public synchronized void setToolContext(ToolContext tc) {
		if (this.tc != null) {
			this.tc.removeToolContextListener(this);
		}
		this.tc = tc;
		if (tc != null) {
			tc.addToolContextListener(this);
		}
	}
	
	public synchronized PaintContext getPaintContext() {
		return pc;
	}
	
	public synchronized void setPaintContext(PaintContext pc) {
		if (this.pc != null) {
			this.pc.removePaintContextListener(this);
		}
		this.pc = pc;
		if (pc != null) {
			pc.addPaintContextListener(this);
		}
	}
	
	// EXTERNAL INTERFACE
	
	public synchronized boolean respondsToCommand(ToolCommand cmd) {
		return tc != null && dispatchToolEvent(tc.getTool(), makeToolCommandEvent(tc.getTool(), cmd, ToolEvent.RESPONDS_TO_COMMAND));
	}
	
	public synchronized boolean enableCommand(ToolCommand cmd) {
		return tc != null && dispatchToolEvent(tc.getTool(), makeToolCommandEvent(tc.getTool(), cmd, ToolEvent.ENABLE_COMMAND));
	}
	
	public synchronized void doCommand(ToolCommand cmd) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolCommandEvent(t, cmd, ToolEvent.DO_COMMAND));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void addCanvasControllerListener(CanvasControllerListener ccl) {
		listeners.add(ccl);
	}
	
	public synchronized void removeCanvasControllerListener(CanvasControllerListener ccl) {
		listeners.remove(ccl);
	}
	
	public synchronized CanvasControllerListener[] getCanvasControllerListeners() {
		return listeners.toArray(new CanvasControllerListener[0]);
	}
	
	public synchronized void notifyCanvasControllerListeners() {
		for (CanvasControllerListener ccl : listeners) ccl.canvasControllerEvent();
	}
	
	// EVENTS

	public synchronized void mouseEntered(MouseEvent e) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}

	public synchronized void mouseMoved(MouseEvent e) {
		if (e.getX() == canvasLastX && e.getY() == canvasLastY) return;
		if (mmpthr != null) {
			mmpthr.interrupt();
			mmpthr = null;
		}
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
		}
	}

	public synchronized void mousePressed(MouseEvent e) {
		if (mmpthr != null) {
			mmpthr.interrupt();
			mmpthr = null;
		}
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			if (t != null) {
				int sleep = t.getMouseHeldInterval();
				if (sleep > 0) {
					mmpthr = new MouseHeldThread(sleep, e);
					mmpthr.start();
				}
			}
		}
	}

	public synchronized void mouseHeld(MouseEvent e) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, true));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
		}
	}

	public synchronized void mouseClicked(MouseEvent e) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
		}
	}

	public synchronized void mouseDragged(MouseEvent e) {
		if (e.getX() == canvasLastX && e.getY() == canvasLastY) return;
		if (mmpthr != null) {
			mmpthr.interrupt();
			mmpthr = null;
		}
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			if (t != null) {
				int sleep = t.getMouseHeldInterval();
				if (sleep > 0) {
					mmpthr = new MouseHeldThread(sleep, e);
					mmpthr.start();
				}
			}
		}
	}

	public synchronized void mouseReleased(MouseEvent e) {
		if (mmpthr != null) {
			mmpthr.interrupt();
			mmpthr = null;
		}
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void mouseExited(MouseEvent e) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolMouseEvent(t, e, false));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void keyPressed(KeyEvent e) {
		if (tc != null && cv != null && !isMenuShortcut(e)) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolKeyEvent(t, e));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
		}
	}

	public synchronized void keyTyped(KeyEvent e) {
		if (tc != null && cv != null && !isMenuShortcut(e)) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolKeyEvent(t, e));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
		}
	}

	public synchronized void keyReleased(KeyEvent e) {
		if (tc != null && cv != null && !isMenuShortcut(e)) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolKeyEvent(t, e));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void modeChanged(ToolContext src, boolean drawMode) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {
		if (tc != null && cv != null) {
			dispatchToolEvent(previousTool, makeToolChangeEvent(previousTool, nextTool, ToolEvent.TOOL_DESELECTED));
			dispatchToolEvent(nextTool, makeToolChangeEvent(previousTool, nextTool, ToolEvent.TOOL_SELECTED));
			cv.paintNow();
			cv.setCursor(nextTool.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void toolDoubleClicked(ToolContext src, Tool clickedTool) {
		if (tc != null && cv != null) {
			Tool currentTool = tc.getTool();
			if (!clickedTool.equals(currentTool)) {
				dispatchToolEvent(currentTool, makeToolChangeEvent(currentTool, clickedTool, ToolEvent.TOOL_DESELECTED));
				dispatchToolEvent(clickedTool, makeToolChangeEvent(currentTool, clickedTool, ToolEvent.TOOL_SELECTED));
			}
			dispatchToolEvent(clickedTool, makeToolGenericEvent(clickedTool, ToolEvent.TOOL_DOUBLE_CLICKED));
			if (!clickedTool.equals(currentTool)) {
				dispatchToolEvent(clickedTool, makeToolChangeEvent(clickedTool, currentTool, ToolEvent.TOOL_DESELECTED));
				dispatchToolEvent(currentTool, makeToolChangeEvent(clickedTool, currentTool, ToolEvent.TOOL_SELECTED));
			}
			cv.paintNow();
			cv.setCursor(currentTool.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void toolSettingsChanged(ToolContext src, long delta) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolTSCEvent(t, delta));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
		if (tc != null && cv != null) {
			Tool t = tc.getTool();
			dispatchToolEvent(t, makeToolPSCEvent(t, ps, delta));
			cv.paintNow();
			cv.setCursor(t.getCursor(lastTE));
			notifyCanvasControllerListeners();
		}
	}
	
	public synchronized void editingChanged(PaintContext src, boolean stroke, boolean bkgnd) {
		// nothing
	}
	
	private int animationPhase = 0;
	private int animationAntiPhase = 4;
	public synchronized void canvasPainted(Graphics2D g) {
		// see also CanvasView.paintComponent(Graphics g)
		if (lastTE != null) {
			ToolContext q = lastTE.tc();
			PaintContext p = lastTE.getPaintContext();
			if (q != null && tc != null && q.equals(tc) && p != null && pc != null && p.equals(pc)) {
				Tool t = q.getTool();
				if (t != null && t.equals(tc.getTool())) {
					// tool-specific stuff
					Layer l = lastTE.getLayer();
					if (l != null && !t.paintIntermediateUsesCanvasCoordinates()) {
						AffineTransform tx = g.getTransform();
						g.translate(l.getX(), l.getY());
						t.paintIntermediate(lastTE, g);
						g.setTransform(tx);
					} else {
						t.paintIntermediate(lastTE, g);
					}
					// selections
					if (c != null && cv != null) {
						// marquee
						if (t.showPaintSelection(lastTE)) {
							Shape s = c.getPaintSelection();
							if (s != null) {
								Stroke stk = new BasicStroke(1.0f/cv.getScale(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f/cv.getScale(),4.0f/cv.getScale()}, (float)animationPhase/cv.getScale());
								Stroke astk = new BasicStroke(1.0f/cv.getScale(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f/cv.getScale(),4.0f/cv.getScale()}, (float)animationAntiPhase/cv.getScale());
								g.setComposite(AlphaComposite.SrcOver);
								g.setPaint(Color.white);
								g.setStroke(astk);
								g.draw(s);
								g.setPaint(Color.black);
								g.setStroke(stk);
								g.draw(s);
							}
						}
						// grabby handles
						for (Layer lyr : c) {
							if (t.showDrawSelection(lastTE, lyr)) {
								AffineTransform tx = g.getTransform();
								g.translate(lyr.getX(), lyr.getY());
								Stroke st = new BasicStroke(1.0f/cv.getScale(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
								g.setComposite(AlphaComposite.SrcOver);
								g.setColor(Color.blue);
								g.setStroke(st);
								for (DrawObject o : lyr) {
									if (o.isSelected()) {
										if (o.isLocked()) g.setColor(Color.red);
										for (double[] li : o.getControlLines()) {
											g.draw(new Line2D.Double(li[0], li[1], li[2], li[3]));
										}
										for (ControlPoint c : o.getControlPoints()) {
											g.draw(c.getShape());
										}
										if (o.isLocked()) g.setColor(Color.blue);
									}
								}
								g.setTransform(tx);
							}
						}
					}
				}
			}
		}
	}
	
	// PRIVATE METHODS
	
	private MouseHeldThread mmpthr = null;
	
	private class MouseHeldThread extends Thread {
		private int sleep;
		private MouseEvent me;
		public MouseHeldThread(int sleep, MouseEvent me) {
			this.sleep = sleep;
			this.me = me;
		}
		public void run() {
			while (!Thread.interrupted()) try {
				sleep(sleep);
				if (mmpthr == this) {
					mouseHeld(me);
				}
			} catch (InterruptedException ie) {
				break;
			}
		}
	}
	
	private boolean isMenuShortcut(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_META:
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_ALT_GRAPH:
		case KeyEvent.VK_CAPS_LOCK:
		case KeyEvent.VK_NUM_LOCK:
		case KeyEvent.VK_SCROLL_LOCK:
			return false;
		default:
			return (e.getModifiers() & InputUtils.META_MASK) != 0;
		}
	}
	
	private float canvasClickX = 0, canvasClickY = 0, canvasLastX = 0, canvasLastY = 0;
	private float layerClickX = 0, layerClickY = 0, layerLastX = 0, layerLastY = 0;
	private boolean snapX = false, snapY = false, mouse = false, mouseIn = false;
	private int lastModifiers = 0;
	
	private ToolEvent makeToolMouseEvent(Tool t, MouseEvent e, boolean held) {
		// get layer, coordinates within canvas (cp), coordinates within layer (lp), tool
		Layer layer = c.getPaintDrawLayer();
		Point2D cp = cv.viewCoordinateToCanvasGraphicsCoordinate(e.getPoint());
		Point2D lp = (layer == null) ? new Point2D.Float(0, 0) :
			cv.viewCoordinateToLayerGraphicsCoordinate(e.getPoint(), layer);
		Tool tool = tc.getTool();
		// get tool event id
		// handle constrained coordinates
		// handle click and last coordinates
		int toolEventId;
		switch (e.getID()) {
		case MouseEvent.MOUSE_ENTERED:
			toolEventId = ToolEvent.MOUSE_ENTERED;
			mouseIn = true;
			break;
		case MouseEvent.MOUSE_MOVED:
			toolEventId = ToolEvent.MOUSE_MOVED;
			if (tool != null && tool.shiftConstrainsCoordinates() && (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
				if (!(snapX || snapY)) {
					if (Math.abs(cp.getX() - canvasLastX) < Math.abs(cp.getY() - canvasLastY)) snapX = true;
					else snapY = true;
				}
				if (snapX) {
					cp = new Point2D.Double(canvasLastX, cp.getY());
					lp = new Point2D.Double(layerLastX, lp.getY());
				}
				if (snapY) {
					cp = new Point2D.Double(cp.getX(), canvasLastY);
					lp = new Point2D.Double(lp.getX(), layerLastY);
				}
			} else {
				snapX = false;
				snapY = false;
			}
			mouse = false;
			break;
		case MouseEvent.MOUSE_PRESSED:
			toolEventId = held ? ToolEvent.MOUSE_HELD : ToolEvent.MOUSE_PRESSED;
			canvasClickX = canvasLastX = (float)cp.getX();
			canvasClickY = canvasLastY = (float)cp.getY();
			layerClickX = layerLastX = (float)lp.getX();
			layerClickY = layerLastY = (float)lp.getY();
			snapX = false;
			snapY = false;
			mouse = true;
			break;
		case MouseEvent.MOUSE_CLICKED:
			toolEventId = ToolEvent.MOUSE_CLICKED;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			toolEventId = held ? ToolEvent.MOUSE_HELD : ToolEvent.MOUSE_DRAGGED;
			if (tool != null && tool.shiftConstrainsCoordinates() && (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
				if (!(snapX || snapY)) {
					if (Math.abs(cp.getX() - canvasLastX) < Math.abs(cp.getY() - canvasLastY)) snapX = true;
					else snapY = true;
				}
				if (snapX) {
					cp = new Point2D.Double(canvasLastX, cp.getY());
					lp = new Point2D.Double(layerLastX, lp.getY());
				}
				if (snapY) {
					cp = new Point2D.Double(cp.getX(), canvasLastY);
					lp = new Point2D.Double(lp.getX(), layerLastY);
				}
			} else {
				snapX = false;
				snapY = false;
			}
			mouse = true;
			break;
		case MouseEvent.MOUSE_RELEASED:
			toolEventId = ToolEvent.MOUSE_RELEASED;
			if (tool != null && tool.shiftConstrainsCoordinates() && (e.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
				if (!(snapX || snapY)) {
					if (Math.abs(cp.getX() - canvasLastX) < Math.abs(cp.getY() - canvasLastY)) snapX = true;
					else snapY = true;
				}
				if (snapX) {
					cp = new Point2D.Double(canvasLastX, cp.getY());
					lp = new Point2D.Double(layerLastX, lp.getY());
				}
				if (snapY) {
					cp = new Point2D.Double(cp.getX(), canvasLastY);
					lp = new Point2D.Double(lp.getX(), layerLastY);
				}
			}
			snapX = false;
			snapY = false;
			mouse = false;
			break;
		case MouseEvent.MOUSE_EXITED:
			toolEventId = ToolEvent.MOUSE_EXITED;
			mouseIn = false;
			break;
		default:
			return null;
		}
		// create the tool event
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				0,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				toolEventId,
				(float)cp.getX(),
				(float)cp.getY(),
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				(float)lp.getX(),
				(float)lp.getY(),
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				e.getClickCount(),
				e.getModifiers(),
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				null
		);
		// set last coordinates to these coordinates before returning
		canvasLastX = (float)cp.getX();
		canvasLastY = (float)cp.getY();
		layerLastX = (float)lp.getX();
		layerLastY = (float)lp.getY();
		lastModifiers = e.getModifiers();
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolKeyEvent(Tool t, KeyEvent e) {
		int toolEventId;
		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			toolEventId = ToolEvent.KEY_PRESSED;
			break;
		case KeyEvent.KEY_TYPED:
			toolEventId = ToolEvent.KEY_TYPED;
			break;
		case KeyEvent.KEY_RELEASED:
			toolEventId = ToolEvent.KEY_RELEASED;
			break;
		default:
			return null;
		}
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				0,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				toolEventId,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				e.getModifiers(),
				e.getKeyCode(),
				e.getKeyChar(),
				null
		);
		// set last modifiers to these modifiers before returning
		lastModifiers = e.getModifiers();
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolTSCEvent(Tool t, long tcdelta) {
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				tcdelta,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				ToolEvent.TOOL_SETTINGS_CHANGED,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				lastModifiers,
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				null
		);
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolPSCEvent(Tool t, PaintSettings ps, int psdelta) {
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				0,
				pc,
				ps,
				psdelta,
				cv,
				c,
				c.getPaintDrawLayer(),
				ToolEvent.PAINT_SETTINGS_CHANGED,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				lastModifiers,
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				null
		);
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolCommandEvent(Tool t, ToolCommand cmd, int toolEventId) {
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				0,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				toolEventId,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				lastModifiers,
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				cmd
		);
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolChangeEvent(Tool pt, Tool nt, int toolEventId) {
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				pt,
				nt,
				0,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				toolEventId,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				lastModifiers,
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				null
		);
		lastTE = te;
		return te;
	}
	
	private ToolEvent makeToolGenericEvent(Tool t, int toolEventId) {
		ToolEvent te = new ToolEvent(
				c.getHistory(),
				tc,
				t,
				t,
				0,
				pc,
				pc.getPaintSettings(),
				0,
				cv,
				c,
				c.getPaintDrawLayer(),
				toolEventId,
				canvasLastX,
				canvasLastY,
				canvasLastX,
				canvasLastY,
				canvasClickX,
				canvasClickY,
				layerLastX,
				layerLastY,
				layerLastX,
				layerLastY,
				layerClickX,
				layerClickY,
				mouse,
				mouseIn,
				0,
				lastModifiers,
				KeyEvent.VK_UNDEFINED,
				'\uFFFF',
				null
		);
		lastTE = te;
		return te;
	}
	
	private boolean dispatchToolEvent(Tool t, ToolEvent te) {
		if (t == null) return false;
		switch (te.getEventType()) {
			case ToolEvent.TOOL_SELECTED: return t.toolSelected(te);
			case ToolEvent.TOOL_DOUBLE_CLICKED: return t.toolDoubleClicked(te);
			case ToolEvent.TOOL_DESELECTED: return t.toolDeselected(te);
			case ToolEvent.TOOL_SETTINGS_CHANGED: return t.toolSettingsChanged(te);
			case ToolEvent.PAINT_SETTINGS_CHANGED: return t.paintSettingsChanged(te);
			case ToolEvent.MOUSE_ENTERED: return t.mouseEntered(te);
			case ToolEvent.MOUSE_MOVED: return t.mouseMoved(te);
			case ToolEvent.MOUSE_PRESSED: return t.mousePressed(te);
			case ToolEvent.MOUSE_HELD: return t.mouseHeld(te);
			case ToolEvent.MOUSE_CLICKED: return t.mouseClicked(te);
			case ToolEvent.MOUSE_DRAGGED: return t.mouseDragged(te);
			case ToolEvent.MOUSE_RELEASED: return t.mouseReleased(te);
			case ToolEvent.MOUSE_EXITED: return t.mouseExited(te);
			case ToolEvent.KEY_PRESSED: return t.keyPressed(te);
			case ToolEvent.KEY_TYPED: return t.keyTyped(te);
			case ToolEvent.KEY_RELEASED: return t.keyReleased(te);
			case ToolEvent.RESPONDS_TO_COMMAND: return t.respondsToCommand(te);
			case ToolEvent.ENABLE_COMMAND: return t.enableCommand(te);
			case ToolEvent.DO_COMMAND: return t.doCommand(te);
			default: return false;
		}
	}
	
	private class AnimationThread extends Thread {
		public void run() {
			while (true) {
				try {
					sleep(125);
					if (c != null && cv != null && c.getPaintSelection() != null) {
						cv.paintNow();
						animationPhase = (animationPhase+1)&7;
						animationAntiPhase = (animationAntiPhase+1)&7;
					}
				} catch (Exception e) {}
			}
		}
	}
}
