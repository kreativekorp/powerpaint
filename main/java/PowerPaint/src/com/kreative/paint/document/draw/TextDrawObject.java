package com.kreative.paint.document.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.paint.document.undo.Atom;

public class TextDrawObject extends DrawObject {
	private double x, y, wrapWidth;
	private String text;
	private int cursorStart, cursorEnd;
	private double a, w, h;
	
	public TextDrawObject(PaintSettings ps, double x, double y, double wrapWidth, String text) {
		super(ps);
		this.x = x;
		this.y = y;
		this.wrapWidth = wrapWidth;
		this.text = normalize(text);
		this.cursorStart = -1;
		this.cursorEnd = -1;
		a = w = h = 0;
	}
	
	private TextDrawObject(TextDrawObject o) {
		super(o);
		this.x = o.x;
		this.y = o.y;
		this.wrapWidth = o.wrapWidth;
		this.text = o.text;
		this.cursorStart = -1;
		this.cursorEnd = -1;
		a = w = h = 0;
	}
	
	@Override
	public TextDrawObject clone() {
		return new TextDrawObject(this);
	}
	
	@Override
	protected void notifyDrawObjectListeners(int id) {
		a = w = h = 0;
		super.notifyDrawObjectListeners(id);
	}
	
	@Override
	protected Shape getBoundaryImpl() {
		switch (ps.textAlignment) {
			case CENTER: return new Rectangle2D.Double(x - w / 2, y - a, w, h);
			case RIGHT: return new Rectangle2D.Double(x - w, y - a, w, h);
			default: return new Rectangle2D.Double(x, y - a, w, h);
		}
	}
	
	@Override
	protected Shape getHitAreaImpl() {
		switch (ps.textAlignment) {
			case CENTER: return new Rectangle2D.Double(x - w / 2, y - a, w, h);
			case RIGHT: return new Rectangle2D.Double(x - w, y - a, w, h);
			default: return new Rectangle2D.Double(x, y - a, w, h);
		}
	}
	
	@Override
	protected Object getControlState() {
		return new double[]{ x, y, wrapWidth };
	}
	
	@Override
	protected void setControlState(Object o) {
		double[] state = (double[])o;
		x = state[0];
		y = state[1];
		wrapWidth = state[2];
	}
	
	@Override
	public int getControlPointCount() {
		return 1;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		return new ControlPoint(ControlPointType.BASELINE, x, y);
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		cpts.add(new ControlPoint(ControlPointType.BASELINE, x, y));
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		return null;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		this.x = x;
		this.y = y;
		return i;
	}
	
	@Override
	protected Point2D getLocationImpl() {
		return new Point2D.Double(x, y);
	}
	
	@Override
	protected void setLocationImpl(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	private static final Color CURSOR_COLOR = new Color(0x80808080, true);
	private static final Color HIGHLIGHT_COLOR = new Color(SystemColor.textHighlight.getRGB());
	
	@Override
	protected void paintImpl(Graphics2D g) {
		g.translate(x, y);
		paintText(g);
	}
	
	private void paintText(Graphics2D g) {
		FontRenderContext frc = g.getFontRenderContext();
		FontMetrics fm = g.getFontMetrics(ps.textFont);
		double lx = 0, ly = 0;
		int cs = Math.min(cursorStart, cursorEnd);
		int ce = Math.max(cursorStart, cursorEnd);
		String[] ss = text.split("\n");
		a = w = h = 0;
		for (String s : ss) {
			if (s.length() < 1) {
				if (cs == 0 && ce == 0) {
					g.setPaint(CURSOR_COLOR);
					g.setComposite(AlphaComposite.SrcOver);
					g.setStroke(new BasicStroke(1));
					g.drawLine(
						0, (int)Math.round(ly - fm.getAscent()),
						0, (int)Math.round(ly + fm.getDescent())
					);
				}
				ly += fm.getHeight();
				a = Math.max(a, fm.getAscent());
				h += fm.getHeight();
				cs--; ce--;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, ps.textFont);
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout((float)wrapWidth);
					int smax = lbm.getPosition();
					switch (ps.textAlignment) {
						case CENTER: lx = -tl.getAdvance() / 2; break;
						case RIGHT: lx = -tl.getAdvance(); break;
						default: lx = 0; break;
					}
					if (ce >= smin && cs <= smax) {
						if (cs == ce) {
							int lcs = Math.min(Math.max(smin, cs), smax);
							g.setPaint(CURSOR_COLOR);
							g.setComposite(AlphaComposite.SrcOver);
							g.setStroke(new BasicStroke(1));
							Shape[] carets = tl.getCaretShapes(lcs - smin);
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
								g.setPaint(HIGHLIGHT_COLOR);
								g.setComposite(AlphaComposite.SrcOver);
								g.setStroke(new BasicStroke(1));
								Shape highlight = tl.getLogicalHighlightShape(lcs - smin, lce - smin);
								if (highlight != null) {
									Shape sh = AffineTransform.getTranslateInstance(lx, ly).createTransformedShape(highlight);
									g.fill(sh);
								}
							}
						}
					}
					ps.applyFill(g);
					tl.draw(g, (float)lx, (float)ly);
					ly += tl.getAscent() + tl.getDescent() + tl.getLeading();
					a = Math.max(a, tl.getAscent());
					w = Math.max(w, tl.getAdvance());
					h += tl.getAscent() + tl.getDescent() + tl.getLeading();
				}
				cs -= s.length() + 1;
				ce -= s.length() + 1;
			}
		}
		if (cs == ce && cs >= 0) {
			ly += fm.getHeight() * cs;
			a = Math.max(a, fm.getAscent());
			h += fm.getHeight() * cs;
			g.setPaint(CURSOR_COLOR);
			g.setComposite(AlphaComposite.SrcOver);
			g.setStroke(new BasicStroke(1));
			g.drawLine(
				0, (int)Math.round(ly - fm.getAscent()),
				0, (int)Math.round(ly + fm.getDescent())
			);
		}
	}
	
	public int getCursorIndexOfLocation(Graphics2D g, double x, double y) {
		if (tx != null) {
			try {
				Point2D p = new Point2D.Double(x, y);
				tx.inverseTransform(p, p);
				x = p.getX(); y = p.getY();
			} catch (Exception e) {
				return -1;
			}
		}
		return getCursorIndexOfLocationImpl(g, x - this.x, y - this.y);
	}
	
	private int getCursorIndexOfLocationImpl(Graphics2D gr, double mx, double my) {
		FontRenderContext frc = gr.getFontRenderContext();
		FontMetrics fm = gr.getFontMetrics(ps.textFont);
		if (my < -fm.getAscent()) return 0;
		double lx = 0, ly = 0; int cidx = 0;
		String[] ss = text.split("\n");
		for (String s : ss) {
			if (s.length() < 1) {
				if (my <= ly + fm.getDescent()) return cidx;
				ly += fm.getHeight();
				cidx++;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, ps.textFont);
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout((float)wrapWidth);
					int smax = lbm.getPosition();
					if (my <= ly + tl.getDescent()) {
						switch (ps.textAlignment) {
							case CENTER: lx = -tl.getAdvance() / 2; break;
							case RIGHT: lx = -tl.getAdvance(); break;
							default: lx = 0; break;
						}
						if (mx <= lx) return cidx + smin;
						if (mx >= lx + tl.getAdvance()) return cidx + smax;
						TextHitInfo thi = tl.hitTestChar(
							(float)mx, (float)ly,
							new Rectangle2D.Double(
								lx, ly - tl.getAscent(), tl.getAdvance(),
								tl.getAscent() + tl.getDescent()
							)
						);
						return cidx + thi.getInsertionIndex();
					}
					ly += tl.getAscent() + tl.getDescent() + tl.getLeading();
				}
				cidx += s.length() + 1;
			}
		}
		while (cidx <= text.length()) {
			if (my <= ly + fm.getDescent()) return cidx;
			ly += fm.getHeight();
			cidx++;
		}
		return text.length();
	}
	
	public Point2D getLocationOfCursorIndex(Graphics2D g, int index) {
		Point2D p = getLocationOfCursorIndexImpl(g, index);
		p.setLocation(p.getX() + x, p.getY() + y);
		if (tx != null) tx.transform(p, p);
		return p;
	}
	
	private Point2D getLocationOfCursorIndexImpl(Graphics2D gr, int ci) {
		FontRenderContext frc = gr.getFontRenderContext();
		FontMetrics fm = gr.getFontMetrics(ps.textFont);
		double lx = 0, ly = 0;
		String[] ss = text.split("\n");
		for (String s : ss) {
			if (s.length() < 1) {
				if (ci == 0) return new Point2D.Double(0, ly);
				ly += fm.getHeight();
				ci--;
			} else {
				AttributedString as = new AttributedString(s);
				as.addAttribute(TextAttribute.FONT, ps.textFont);
				AttributedCharacterIterator aci = as.getIterator();
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
				lbm.setPosition(aci.getBeginIndex());
				while (lbm.getPosition() < aci.getEndIndex()) {
					int smin = lbm.getPosition();
					TextLayout tl = lbm.nextLayout((float)wrapWidth);
					int smax = lbm.getPosition();
					switch (ps.textAlignment) {
						case CENTER: lx = -tl.getAdvance() / 2; break;
						case RIGHT: lx = -tl.getAdvance(); break;
						default: lx = 0; break;
					}
					if (ci >= smin && ci <= smax) {
						int lcs = Math.min(Math.max(smin, ci), smax);
						Shape[] carets = tl.getCaretShapes(lcs - smin);
						for (Shape caret : carets) {
							if (caret != null) {
								Shape sh = AffineTransform.getTranslateInstance(lx, ly).createTransformedShape(caret);
								double cx = sh.getBounds2D().getCenterX();
								return new Point2D.Double(cx, ly);
							}
						}
					}
					ly += tl.getAscent() + tl.getDescent() + tl.getLeading();
				}
				ci -= s.length() + 1;
			}
		}
		ly += fm.getHeight() * ci;
		return new Point2D.Double(0, ly);
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getWrapWidth() { return wrapWidth; }
	
	private static class WrapWidthAtom implements Atom {
		private TextDrawObject d;
		private double oldWrapWidth;
		private double newWrapWidth;
		public WrapWidthAtom(TextDrawObject d, double wrapWidth) {
			this.d = d;
			this.oldWrapWidth = d.wrapWidth;
			this.newWrapWidth = wrapWidth;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof WrapWidthAtom)
			    && (((WrapWidthAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldWrapWidth = ((WrapWidthAtom)prev).oldWrapWidth;
			return this;
		}
		@Override
		public void redo() {
			d.wrapWidth = newWrapWidth;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
		@Override
		public void undo() {
			d.wrapWidth = oldWrapWidth;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void setWrapWidth(double wrapWidth) {
		if (this.wrapWidth == wrapWidth) return;
		if (history != null) history.add(new WrapWidthAtom(this, wrapWidth));
		this.wrapWidth = wrapWidth;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	public String getText() {
		return text;
	}
	
	public String getSelectedText() {
		if (cursorStart < 0 || cursorEnd < 0) return text;
		int l = text.length();
		int s = Math.min(cursorStart, cursorEnd);
		int e = Math.max(cursorStart, cursorEnd);
		if (s < 0) s = 0; if (s > l) s = l;
		if (e < 0) e = 0; if (e > l) e = l;
		return text.substring(s, e);
	}
	
	public int getCursorStart() {
		return cursorStart;
	}
	
	public int getCursorEnd() {
		return cursorEnd;
	}
	
	public boolean hasSelection() {
		return (cursorStart >= 0 && cursorEnd >= 0);
	}
	
	private static class TextAtom implements Atom {
		private TextDrawObject d;
		private String oldText;
		private int oldSelStart, oldSelEnd;
		private String newText;
		private int newSelStart, newSelEnd;
		public TextAtom(TextDrawObject d, String text, int selStart, int selEnd) {
			this.d = d;
			this.oldText = d.text;
			this.oldSelStart = d.cursorStart;
			this.oldSelEnd = d.cursorEnd;
			this.newText = text;
			this.newSelStart = selStart;
			this.newSelEnd = selEnd;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof TextAtom)
			    && (((TextAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldText = ((TextAtom)prev).oldText;
			this.oldSelStart = ((TextAtom)prev).oldSelStart;
			this.oldSelEnd = ((TextAtom)prev).oldSelEnd;
			return this;
		}
		@Override
		public void redo() {
			d.text = newText;
			d.cursorStart = newSelStart;
			d.cursorEnd = newSelEnd;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
		@Override
		public void undo() {
			d.text = oldText;
			d.cursorStart = oldSelStart;
			d.cursorEnd = oldSelEnd;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void setText(String text) {
		text = normalize(text);
		if (this.text.equals(text)) return;
		if (history != null) history.add(new TextAtom(this, text, -1, -1));
		this.text = text;
		this.cursorStart = -1;
		this.cursorEnd = -1;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	public void setSelectedText(String text) {
		if (cursorStart < 0 || cursorEnd < 0) {
			setText(text);
		} else {
			int l = this.text.length();
			int s = Math.min(cursorStart, cursorEnd);
			int e = Math.max(cursorStart, cursorEnd);
			if (s < 0) s = 0; if (s > l) s = l;
			if (e < 0) e = 0; if (e > l) e = l;
			text = normalize(text);
			String newText = this.text.substring(0, s) + text + this.text.substring(e);
			if (history != null) history.add(new TextAtom(this, newText, s + text.length(), s + text.length()));
			this.text = newText;
			this.cursorStart = s + text.length();
			this.cursorEnd = s + text.length();
			this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void setCursorStart(int cursorStart) {
		if (this.cursorStart == cursorStart) return;
		if (history != null) history.add(new TextAtom(this, text, cursorStart, cursorEnd));
		this.cursorStart = cursorStart;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	public void setCursorEnd(int cursorEnd) {
		if (this.cursorEnd == cursorEnd) return;
		if (history != null) history.add(new TextAtom(this, text, cursorStart, cursorEnd));
		this.cursorEnd = cursorEnd;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	public void setCursor(int cursorStart, int cursorEnd) {
		if (this.cursorStart == cursorStart && this.cursorEnd == cursorEnd) return;
		if (history != null) history.add(new TextAtom(this, text, cursorStart, cursorEnd));
		this.cursorStart = cursorStart;
		this.cursorEnd = cursorEnd;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	public void clearSelection() {
		setCursor(-1, -1);
	}
	
	public void deleteBackward() {
		if (cursorStart < 0 || cursorEnd < 0) {
			setText(null);
		} else if (cursorStart != cursorEnd) {
			setSelectedText(null);
		} else if (cursorStart > 0) {
			String newText = text.substring(0, cursorStart - 1) + text.substring(cursorStart);
			if (history != null) history.add(new TextAtom(this, newText, cursorStart - 1, cursorStart - 1));
			this.text = newText;
			this.cursorStart--;
			this.cursorEnd--;
			this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void deleteForward() {
		if (cursorStart < 0 || cursorEnd < 0) {
			setText(null);
		} else if (cursorStart != cursorEnd) {
			setSelectedText(null);
		} else if (cursorStart < text.length()) {
			String newText = text.substring(0, cursorStart) + text.substring(cursorStart + 1);
			if (history != null) history.add(new TextAtom(this, newText, cursorStart, cursorStart));
			this.text = newText;
			this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void moveCursor(Graphics2D g, int lineDelta, int charDelta, boolean shiftDown) {
		if (cursorStart < 0 || cursorEnd < 0) return;
		if (lineDelta == 0 && charDelta == 0) return;
		boolean gTemp = (g == null);
		if (gTemp) g = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB).createGraphics();
		int lineHeight = g.getFontMetrics(ps.textFont).getHeight();
		int cs = cursorStart;
		int ce = cursorEnd;
		if (lineDelta != 0) {
			Point2D p = getLocationOfCursorIndexImpl(g, (lineDelta > 0 || shiftDown) ? ce : cs);
			ce = getCursorIndexOfLocationImpl(g, p.getX(), p.getY() + lineHeight * lineDelta);
			if (!shiftDown) cs = ce;
		}
		if (charDelta != 0) {
			ce = ((charDelta > 0 || shiftDown) ? ce : cs) + charDelta;
			if (ce < 0) ce = 0; if (ce > text.length()) ce = text.length();
			if (!shiftDown) cs = ce;
		}
		setCursor(cs, ce);
		if (gTemp) g.dispose();
	}
	
	private static String normalize(String text) {
		if (text == null) return "";
		return text.replaceAll("\r\n|\r|\n|\u2028|\u2029", "\n");
	}
}
