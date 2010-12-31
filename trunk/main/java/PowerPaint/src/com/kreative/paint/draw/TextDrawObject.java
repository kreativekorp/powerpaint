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

package com.kreative.paint.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.undo.Atom;

public class TextDrawObject extends AbstractDrawObject {
	private float x, y;
	private float a, w, h;
	private StringBuffer text;
	private int cursorStart;
	private int cursorEnd;
	private float wrapWidth;
	
	public TextDrawObject(float x, float y) {
		super();
		this.x = x;
		this.y = y;
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.text = new StringBuffer();
		this.cursorStart = -1;
		this.cursorEnd = -1;
		this.wrapWidth = Integer.MAX_VALUE;
	}
	
	public TextDrawObject(float x, float y, PaintSettings ps) {
		super(ps);
		this.x = x;
		this.y = y;
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.text = new StringBuffer();
		this.cursorStart = -1;
		this.cursorEnd = -1;
		this.wrapWidth = Integer.MAX_VALUE;
	}
	
	public TextDrawObject(float x, float y, String text) {
		super();
		this.x = x;
		this.y = y;
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.text = new StringBuffer(text);
		this.cursorStart = -1;
		this.cursorEnd = -1;
		this.wrapWidth = Integer.MAX_VALUE;
	}
	
	public TextDrawObject(float x, float y, String text, PaintSettings ps) {
		super(ps);
		this.x = x;
		this.y = y;
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.text = new StringBuffer(text);
		this.cursorStart = -1;
		this.cursorEnd = -1;
		this.wrapWidth = Integer.MAX_VALUE;
	}
	
	public TextDrawObject clone() {
		TextDrawObject o = new TextDrawObject(x,y,text.toString(),getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		o.wrapWidth = this.wrapWidth;
		return o;
	}
	
	public synchronized float getX() { return x; }
	public synchronized float getY() { return y; }
	public synchronized Point2D getLocation() { return new Point2D.Float(x, y); }
	public synchronized String getText() { return text.toString(); }
	public synchronized int getCursorStart() { return cursorStart; }
	public synchronized int getCursorEnd() { return cursorEnd; }
	public synchronized float getWrapWidth() { return wrapWidth; }
	
	private static class LocationAtom implements Atom {
		private TextDrawObject d;
		private float oldX, oldY;
		private float newX, newY;
		public LocationAtom(TextDrawObject d, float nx, float ny) {
			this.d = d;
			this.oldX = d.x;
			this.oldY = d.y;
			this.newX = nx;
			this.newY = ny;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldX = ((LocationAtom)previousAtom).oldX;
			this.oldY = ((LocationAtom)previousAtom).oldY;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof LocationAtom) && ((LocationAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.x = newX;
			d.y = newY;
		}
		public void undo() {
			d.x = oldX;
			d.y = oldY;
		}
	}
	
	public synchronized void setX(float x) {
		if (getHistory() != null) getHistory().add(new LocationAtom(this, x, y));
		this.x = x;
	}
	public synchronized void setY(float y) {
		if (getHistory() != null) getHistory().add(new LocationAtom(this, x, y));
		this.y = y;
	}
	public synchronized void setLocation(float x, float y) {
		if (getHistory() != null) getHistory().add(new LocationAtom(this, x, y));
		this.x = x; this.y = y;
	}
	public synchronized void setLocation(Point2D p) {
		if (getHistory() != null) getHistory().add(new LocationAtom(this, (float)p.getX(), (float)p.getY()));
		this.x = (float)p.getX(); this.y = (float)p.getY();
	}
	
	private static class TextAtom implements Atom {
		private TextDrawObject d;
		private String oldText;
		private int oldcs, oldce;
		private String newText;
		private int newcs, newce;
		public TextAtom(TextDrawObject d, String newText) {
			this.d = d;
			this.oldText = d.text.toString();
			this.oldcs = d.cursorStart;
			this.oldce = d.cursorEnd;
			this.newText = newText;
			this.newcs = -1;
			this.newce = -1;
		}
		public TextAtom(TextDrawObject d, String newText, int cs, int ce) {
			this.d = d;
			this.oldText = d.text.toString();
			this.oldcs = d.cursorStart;
			this.oldce = d.cursorEnd;
			this.newText = newText;
			this.newcs = cs;
			this.newce = ce;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldText = ((TextAtom)previousAtom).oldText;
			this.oldcs = ((TextAtom)previousAtom).oldcs;
			this.oldce = ((TextAtom)previousAtom).oldce;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TextAtom) && ((TextAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.a = 0;
			d.w = 0;
			d.h = 0;
			d.text = new StringBuffer(newText);
			d.cursorStart = newcs;
			d.cursorEnd = newce;
		}
		public void undo() {
			d.a = 0;
			d.w = 0;
			d.h = 0;
			d.text = new StringBuffer(oldText);
			d.cursorStart = oldcs;
			d.cursorEnd = oldce;
		}
	}
	
	public synchronized void setText(String text) {
		if (getHistory() != null) getHistory().add(new TextAtom(this, text));
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.text = new StringBuffer(text);
		this.cursorStart = -1;
		this.cursorEnd = -1;
	}
	
	private static class SelectionAtom implements Atom {
		private TextDrawObject d;
		private int oldcs, oldce;
		private int newcs, newce;
		public SelectionAtom(TextDrawObject d, int cs, int ce) {
			this.d = d;
			this.oldcs = d.cursorStart;
			this.oldce = d.cursorEnd;
			this.newcs = cs;
			this.newce = ce;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldcs = ((SelectionAtom)previousAtom).oldcs;
			this.oldce = ((SelectionAtom)previousAtom).oldce;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof SelectionAtom) && ((SelectionAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.cursorStart = newcs;
			d.cursorEnd = newce;
		}
		public void undo() {
			d.cursorStart = oldcs;
			d.cursorEnd = oldce;
		}
	}
	
	public synchronized void setCursorStart(int cursorStart) {
		if (getHistory() != null) getHistory().add(new SelectionAtom(this, cursorStart, cursorEnd));
		this.cursorStart = cursorStart;
	}
	public synchronized void setCursorEnd(int cursorEnd) {
		if (getHistory() != null) getHistory().add(new SelectionAtom(this, cursorStart, cursorEnd));
		this.cursorEnd = cursorEnd;
	}
	public synchronized void setCursor(int start, int end) {
		if (getHistory() != null) getHistory().add(new SelectionAtom(this, start, end));
		this.cursorStart = start; this.cursorEnd = end;
	}
	
	private static class WrapWidthAtom implements Atom {
		private TextDrawObject d;
		private float oldww;
		private float newww;
		public WrapWidthAtom(TextDrawObject d, float ww) {
			this.d = d;
			this.oldww = d.wrapWidth;
			this.newww = ww;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldww = ((WrapWidthAtom)previousAtom).oldww;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof WrapWidthAtom) && ((WrapWidthAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.a = 0;
			d.w = 0;
			d.h = 0;
			d.wrapWidth = newww;
		}
		public void undo() {
			d.a = 0;
			d.w = 0;
			d.h = 0;
			d.wrapWidth = oldww;
		}
	}
	
	public synchronized void setWrapWidth(float wrapWidth) {
		if (getHistory() != null) getHistory().add(new WrapWidthAtom(this, wrapWidth));
		this.a = 0;
		this.w = 0;
		this.h = 0;
		this.wrapWidth = wrapWidth;
	}
	
	public synchronized String getSelectedText() {
		if (cursorStart < 0 || cursorEnd < 0) return text.toString();
		else {
			int s = Math.min(cursorStart, cursorEnd);
			int e = Math.max(cursorStart, cursorEnd);
			if (s < 0) s = 0;
			if (s > text.length()) s = text.length();
			if (e < 0) e = 0;
			if (e > text.length()) e = text.length();
			return text.substring(s, e);
		}
	}
	
	public synchronized void setSelectedText(String text) {
		if (cursorStart < 0 || cursorEnd < 0) {
			if (getHistory() != null) getHistory().add(new TextAtom(this, text));
			this.a = 0;
			this.w = 0;
			this.h = 0;
			this.text = new StringBuffer(text);
		}
		else {
			int s = Math.min(cursorStart, cursorEnd);
			int e = Math.max(cursorStart, cursorEnd);
			if (s < 0) s = 0;
			if (s > this.text.length()) s = this.text.length();
			if (e < 0) e = 0;
			if (e > this.text.length()) e = this.text.length();
			if (getHistory() != null) {
				String rt = this.text.toString().substring(0, s) + text + this.text.toString().substring(e);
				getHistory().add(new TextAtom(this, rt, s+text.length(), s+text.length()));
			}
			this.a = 0;
			this.w = 0;
			this.h = 0;
			this.text.replace(s, e, text);
			cursorStart = s + text.length();
			cursorEnd = s + text.length();
		}
	}
	
	public synchronized void backspace() {
		if (cursorStart < 0 || cursorEnd < 0) {
			if (getHistory() != null) getHistory().add(new TextAtom(this, ""));
			a = 0;
			w = 0;
			h = 0;
			text = new StringBuffer();
		}
		else if (cursorStart == cursorEnd) {
			if (cursorStart > 0) {
				if (getHistory() != null) {
					String rt = this.text.toString().substring(0, cursorStart-1) + this.text.toString().substring(cursorStart);
					getHistory().add(new TextAtom(this, rt, cursorStart-1, cursorEnd-1));
				}
				a = 0;
				w = 0;
				h = 0;
				text.deleteCharAt(cursorStart-1);
				cursorStart--;
				cursorEnd--;
			}
		}
		else {
			int s = Math.min(cursorStart, cursorEnd);
			int e = Math.max(cursorStart, cursorEnd);
			if (s < 0) s = 0;
			if (s > text.length()) s = text.length();
			if (e < 0) e = 0;
			if (e > text.length()) e = text.length();
			if (getHistory() != null) {
				String rt = this.text.toString().substring(0, s) + this.text.toString().substring(e);
				getHistory().add(new TextAtom(this, rt, s, s));
			}
			a = 0;
			w = 0;
			h = 0;
			text.delete(s, e);
			cursorStart = s;
			cursorEnd = s;
		}
	}
	
	public synchronized void delete() {
		if (cursorStart < 0 || cursorEnd < 0) {
			if (getHistory() != null) getHistory().add(new TextAtom(this, ""));
			a = 0;
			w = 0;
			h = 0;
			text = new StringBuffer();
		}
		else if (cursorStart == cursorEnd) {
			if (cursorStart < text.length()) {
				if (getHistory() != null) {
					String rt = this.text.toString().substring(0, cursorStart) + this.text.toString().substring(cursorStart+1);
					getHistory().add(new TextAtom(this, rt, cursorStart, cursorEnd));
				}
				a = 0;
				w = 0;
				h = 0;
				text.deleteCharAt(cursorStart);
			}
		}
		else {
			int s = Math.min(cursorStart, cursorEnd);
			int e = Math.max(cursorStart, cursorEnd);
			if (s < 0) s = 0;
			if (s > text.length()) s = text.length();
			if (e < 0) e = 0;
			if (e > text.length()) e = text.length();
			if (getHistory() != null) {
				String rt = this.text.toString().substring(0, s) + this.text.toString().substring(e);
				getHistory().add(new TextAtom(this, rt, s, s));
			}
			a = 0;
			w = 0;
			h = 0;
			text.delete(s, e);
			cursorStart = s;
			cursorEnd = s;
		}
	}
	
	private int getCursorIndexOfLocationImpl(Graphics2D gr, float mx, float my) {
		FontRenderContext frc = gr.getFontRenderContext();
		FontMetrics fm = gr.getFontMetrics(getFont());
		if (my < -fm.getAscent()) return 0;
		float lx, ly = 0.0f; int cidx = 0;
		String[] ss = text.toString().split("\r|\n");
		for (String s : ss) {
			if (s.length() < 1) {
				if (my <= ly+fm.getDescent()) return cidx;
				ly += fm.getHeight();
				cidx++;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, getFont());
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout(wrapWidth);
					int smax = lbm.getPosition();
					if (my <= ly+tl.getDescent()) {
						switch (getTextAlignment()) {
						case LEFT:
							lx = 0.0f;
							break;
						case CENTER:
							lx = -tl.getAdvance()/2.0f;
							break;
						case RIGHT:
							lx = -tl.getAdvance();
							break;
						default:
							lx = 0.0f;
						break;
						}
						if (mx <= lx) return cidx+smin;
						else if (mx >= lx+tl.getAdvance()) return cidx+smax;
						else {
							TextHitInfo thi = tl.hitTestChar(mx, ly, new Rectangle2D.Float(lx, ly-tl.getAscent(), tl.getAdvance(), tl.getAscent()+tl.getDescent()));
							return cidx+thi.getInsertionIndex();
						}
					}
					ly += tl.getAscent()+tl.getDescent()+tl.getLeading();
				}
				cidx += s.length()+1;
			}
		}
		while (cidx <= text.length()) {
			if (my <= ly+fm.getDescent()) return cidx;
			ly += fm.getHeight();
			cidx++;
		}
		return text.length();
	}
	
	private Point2D getLocationOfCursorIndexImpl(Graphics2D gr, int ci) {
		FontRenderContext frc = gr.getFontRenderContext();
		FontMetrics fm = gr.getFontMetrics(getFont());
		float lx, ly = 0.0f;
		String[] ss = text.toString().split("\r|\n");
		for (String s : ss) {
			if (s.length() < 1) {
				if (ci == 0) {
					return new Point2D.Float(0.0f, ly);
				}
				ly += fm.getHeight();
				ci--;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, getFont());
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout(wrapWidth);
					int smax = lbm.getPosition();
					switch (getTextAlignment()) {
					case LEFT:
						lx = 0.0f;
						break;
					case CENTER:
						lx = -tl.getAdvance()/2.0f;
						break;
					case RIGHT:
						lx = -tl.getAdvance();
						break;
					default:
						lx = 0.0f;
						break;
					}
					if ((ci >= smin) && (ci <= smax)) {
						int lcs = Math.min(Math.max(smin, ci), smax);
						Shape[] carets = tl.getCaretShapes(lcs-smin);
						for (Shape caret : carets) {
							if (caret != null) {
								Shape sh = AffineTransform.getTranslateInstance(lx, ly).createTransformedShape(caret);
								float cx = (float)sh.getBounds2D().getCenterX();
								return new Point2D.Float(cx, ly);
							}
						}
					}
					ly += tl.getAscent()+tl.getDescent()+tl.getLeading();
				}
				ci -= s.length()+1;
			}
		}
		ly += fm.getHeight() * ci;
		return new Point2D.Float(0.0f, ly);
	}
	
	private static final Color CURSOR_COLOR = new Color(0x80808080, true);
	private static final Color HIGHLIGHT_COLOR = new Color(SystemColor.textHighlight.getRGB());
	private void paintText(Graphics2D g) {
		FontRenderContext frc = g.getFontRenderContext();
		FontMetrics fm = g.getFontMetrics(getFont());
		float lx, ly = 0.0f;
		int cs = Math.min(cursorStart, cursorEnd);
		int ce = Math.max(cursorStart, cursorEnd);
		String[] ss = text.toString().split("\r|\n");
		a = 0;
		w = 0;
		h = 0;
		for (String s : ss) {
			if (s.length() < 1) {
				if (cs == 0 && ce == 0) {
					g.setComposite(AlphaComposite.SrcOver);
					g.setPaint(CURSOR_COLOR);
					g.setStroke(new BasicStroke(1));
					g.drawLine(0, (int)ly-fm.getAscent(), 0, (int)ly+fm.getDescent());
				}
				ly += fm.getHeight();
				a = Math.max(a, fm.getAscent());
				h += fm.getHeight();
				cs--;
				ce--;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, getFont());
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout(wrapWidth);
					int smax = lbm.getPosition();
					switch (getTextAlignment()) {
					case LEFT:
						lx = 0.0f;
						break;
					case CENTER:
						lx = -tl.getAdvance()/2.0f;
						break;
					case RIGHT:
						lx = -tl.getAdvance();
						break;
					default:
						lx = 0.0f;
						break;
					}
					if ((ce >= smin) && (cs <= smax)) {
						if (cs == ce) {
							int lcs = Math.min(Math.max(smin, cs), smax);
							g.setComposite(AlphaComposite.SrcOver);
							g.setPaint(CURSOR_COLOR);
							g.setStroke(new BasicStroke(1));
							Shape[] carets = tl.getCaretShapes(lcs-smin);
							for (Shape caret : carets) {
								if (caret != null) {
									Shape sh = AffineTransform.getTranslateInstance(lx, ly).createTransformedShape(caret);
									g.draw(sh);
								}
							}
						} else {
							int lcs = Math.min(Math.max(smin, cs), smax);
							int lce = Math.min(Math.max(smin, ce), smax);
							if (lcs != lce) {
								g.setComposite(AlphaComposite.SrcOver);
								g.setPaint(HIGHLIGHT_COLOR);
								g.setStroke(new BasicStroke(1));
								Shape highlight = tl.getLogicalHighlightShape(lcs-smin, lce-smin);
								if (highlight != null) {
									Shape sh = AffineTransform.getTranslateInstance(lx, ly).createTransformedShape(highlight);
									g.fill(sh);
								}
							}
						}
					}
					applyFill(g);
					tl.draw(g, lx, ly);
					ly += tl.getAscent()+tl.getDescent()+tl.getLeading();
					a = Math.max(a, tl.getAscent());
					w = Math.max(w, tl.getAdvance());
					h += tl.getAscent()+tl.getDescent()+tl.getLeading();
				}
				cs -= s.length()+1;
				ce -= s.length()+1;
			}
		}
		if (cs == ce && cs >= 0) {
			ly += fm.getHeight() * cs;
			a = Math.max(a, fm.getAscent());
			h += fm.getHeight() * cs;
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(CURSOR_COLOR);
			g.setStroke(new BasicStroke(1));
			g.drawLine(0, (int)ly-fm.getAscent(), 0, (int)ly+fm.getDescent());
		}
	}

	public void paint(Graphics2D g) {
		AffineTransform svtx = g.getTransform();
		if (getTransform() != null) g.transform(getTransform());
		g.translate(this.x, this.y);
		paintText(g);
		g.setTransform(svtx);
	}

	public void paint(Graphics2D g, int tx, int ty) {
		AffineTransform svtx = g.getTransform();
		g.translate(tx, ty);
		if (getTransform() != null) g.transform(getTransform());
		g.translate(this.x, this.y);
		paintText(g);
		g.setTransform(svtx);
	}
	
	public int getCursorIndexOfLocation(Graphics2D g, float x, float y) {
		return getCursorIndexOfLocationImpl(g, x-this.x, y-this.y);
	}
	
	public Point2D getLocationOfCursorIndex(Graphics2D g, int idx) {
		Point2D p = getLocationOfCursorIndexImpl(g, idx);
		return new Point2D.Double(this.x+p.getX(), this.y+p.getY());
	}
	
	private Shape getTransformedBounds() {
		double ax;
		switch (getTextAlignment()) {
		case LEFT: ax = x; break;
		case CENTER: ax = x-w/2.0; break;
		case RIGHT: ax = x-w; break;
		default: ax = x; break;
		}
		Rectangle2D.Double r = new Rectangle2D.Double(ax, y-a, w, h);
		AffineTransform tx = getTransform();
		if (tx != null) return tx.createTransformedShape(r);
		else return r;
	}

	public boolean contains(Point2D p) {
		return getTransformedBounds().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return getTransformedBounds().contains(r);
	}

	public boolean contains(double x, double y) {
		return getTransformedBounds().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getTransformedBounds().contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return getTransformedBounds().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return getTransformedBounds().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getTransformedBounds().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getTransformedBounds().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return getTransformedBounds().intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return getTransformedBounds().intersects(x, y, w, h);
	}
	
	public int getControlPointCount() {
		return 1;
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		return new ControlPoint.Float(x, y, ControlPointType.BASELINE);
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		x = (float)p.getX();
		y = (float)p.getY();
		return i;
	}
	
	protected Point2D getAnchorImpl() {
		return new ControlPoint.Float(x, y, ControlPointType.BASELINE);
	}
	
	protected void setAnchorImpl(Point2D p) {
		x = (float)p.getX();
		y = (float)p.getY();
	}
	
	protected void edited() {
		a = 0;
		w = 0;
		h = 0;
	}
}
