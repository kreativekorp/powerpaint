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
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.undo.Atom;
import com.kreative.paint.undo.History;

public abstract class AbstractDrawObject implements DrawObject {
	private static Color COLOR_CLEAR = new Color(0, true);
	
	private History history;
	private Composite drawComposite;
	private Paint drawPaint;
	private Composite fillComposite;
	private Paint fillPaint;
	private Stroke stroke;
	private Font font;
	private int textAlignment;
	private boolean antiAliased;
	private AffineTransform transform;
	private boolean visible;
	private boolean locked;
	private boolean selected;
	
	public AbstractDrawObject() {
		this.history = null;
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = Color.black;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = Color.black;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
	}
	
	public AbstractDrawObject(PaintSettings ps) {
		this.history = null;
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
	}
	
	public abstract AbstractDrawObject clone();
	
	public final History getHistory() {
		return history;
	}
	
	public final void setHistory(History h) {
		this.history = h;
	}
	
	protected void edited() {}
	
	public final PaintSettings getPaintSettings() {
		return new PaintSettings(
				drawComposite,
				drawPaint,
				fillComposite,
				fillPaint,
				stroke,
				font,
				textAlignment,
				antiAliased
		);
	}
	
	private static class PaintSettingsAtom implements Atom {
		private AbstractDrawObject d;
		private PaintSettings oldps;
		private PaintSettings newps;
		public PaintSettingsAtom(AbstractDrawObject d, PaintSettings ps) {
			this.d = d;
			this.oldps = d.getPaintSettings();
			this.newps = ps;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldps = ((PaintSettingsAtom)previousAtom).oldps;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof PaintSettingsAtom) && ((PaintSettingsAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawComposite = newps.getDrawComposite();
			d.drawPaint = newps.getDrawPaint();
			d.fillComposite = newps.getFillComposite();
			d.fillPaint = newps.getFillPaint();
			d.stroke = newps.getStroke();
			d.font = newps.getFont();
			d.textAlignment = newps.getTextAlignment();
			d.antiAliased = newps.isAntiAliased();
			d.edited();
		}
		public void undo() {
			d.drawComposite = oldps.getDrawComposite();
			d.drawPaint = oldps.getDrawPaint();
			d.fillComposite = oldps.getFillComposite();
			d.fillPaint = oldps.getFillPaint();
			d.stroke = oldps.getStroke();
			d.font = oldps.getFont();
			d.textAlignment = oldps.getTextAlignment();
			d.antiAliased = oldps.isAntiAliased();
			d.edited();
		}
	}
	
	public final void setPaintSettings(PaintSettings ps) {
		if (history != null) history.add(new PaintSettingsAtom(this, ps));
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		this.edited();
	}
	
	public final boolean isDrawn() {
		return drawPaint != null;
	}
	
	public final boolean isFilled() {
		return fillPaint != null;
	}

	public final Composite getDrawComposite() {
		return drawComposite;
	}
	
	private static class DrawCompositeAtom implements Atom {
		private AbstractDrawObject d;
		private Composite olddc;
		private Composite newdc;
		public DrawCompositeAtom(AbstractDrawObject d, Composite dc) {
			this.d = d;
			this.olddc = d.drawComposite;
			this.newdc = dc;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.olddc = ((DrawCompositeAtom)previousAtom).olddc;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof DrawCompositeAtom) && ((DrawCompositeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawComposite = newdc;
			d.edited();
		}
		public void undo() {
			d.drawComposite = olddc;
			d.edited();
		}
	}

	public final void setDrawComposite(Composite drawComposite) {
		if (drawComposite == null) drawComposite = AlphaComposite.SrcOver;
		if (history != null) history.add(new DrawCompositeAtom(this, drawComposite));
		this.drawComposite = drawComposite;
		this.edited();
	}

	public final Paint getDrawPaint() {
		return drawPaint;
	}
	
	private static class DrawPaintAtom implements Atom {
		private AbstractDrawObject d;
		private Paint olddp;
		private Paint newdp;
		public DrawPaintAtom(AbstractDrawObject d, Paint dp) {
			this.d = d;
			this.olddp = d.drawPaint;
			this.newdp = dp;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.olddp = ((DrawPaintAtom)previousAtom).olddp;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof DrawPaintAtom) && ((DrawPaintAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawPaint = newdp;
			d.edited();
		}
		public void undo() {
			d.drawPaint = olddp;
			d.edited();
		}
	}

	public final void setDrawPaint(Paint drawPaint) {
		if (history != null) history.add(new DrawPaintAtom(this, drawPaint));
		this.drawPaint = drawPaint;
		this.edited();
	}

	public final Composite getFillComposite() {
		return fillComposite;
	}

	private static class FillCompositeAtom implements Atom {
		private AbstractDrawObject d;
		private Composite oldfc;
		private Composite newfc;
		public FillCompositeAtom(AbstractDrawObject d, Composite fc) {
			this.d = d;
			this.oldfc = d.fillComposite;
			this.newfc = fc;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldfc = ((FillCompositeAtom)previousAtom).oldfc;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FillCompositeAtom) && ((FillCompositeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.fillComposite = newfc;
			d.edited();
		}
		public void undo() {
			d.fillComposite = oldfc;
			d.edited();
		}
	}

	public final void setFillComposite(Composite fillComposite) {
		if (fillComposite == null) fillComposite = AlphaComposite.SrcOver;
		if (history != null) history.add(new FillCompositeAtom(this, fillComposite));
		this.fillComposite = fillComposite;
		this.edited();
	}

	public final Paint getFillPaint() {
		return fillPaint;
	}

	private static class FillPaintAtom implements Atom {
		private AbstractDrawObject d;
		private Paint oldfp;
		private Paint newfp;
		public FillPaintAtom(AbstractDrawObject d, Paint fp) {
			this.d = d;
			this.oldfp = d.fillPaint;
			this.newfp = fp;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldfp = ((FillPaintAtom)previousAtom).oldfp;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FillPaintAtom) && ((FillPaintAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.fillPaint = newfp;
			d.edited();
		}
		public void undo() {
			d.fillPaint = oldfp;
			d.edited();
		}
	}

	public final void setFillPaint(Paint fillPaint) {
		if (history != null) history.add(new FillPaintAtom(this, fillPaint));
		this.fillPaint = fillPaint;
		this.edited();
	}

	public final Stroke getStroke() {
		return stroke;
	}
	
	private static class StrokeAtom implements Atom {
		private AbstractDrawObject d;
		private Stroke oldst;
		private Stroke newst;
		public StrokeAtom(AbstractDrawObject d, Stroke st) {
			this.d = d;
			this.oldst = d.stroke;
			this.newst = st;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldst = ((StrokeAtom)previousAtom).oldst;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof StrokeAtom) && ((StrokeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.stroke = newst;
			d.edited();
		}
		public void undo() {
			d.stroke = oldst;
			d.edited();
		}
	}

	public final void setStroke(Stroke stroke) {
		if (stroke == null) stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		if (history != null) history.add(new StrokeAtom(this, stroke));
		this.stroke = stroke;
		this.edited();
	}
	
	public final Font getFont() {
		return font;
	}
	
	private static class FontAtom implements Atom {
		private AbstractDrawObject d;
		private Font oldf;
		private Font newf;
		public FontAtom(AbstractDrawObject d, Font f) {
			this.d = d;
			this.oldf = d.font;
			this.newf = f;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldf = ((FontAtom)previousAtom).oldf;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FontAtom) && ((FontAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.font = newf;
			d.edited();
		}
		public void undo() {
			d.font = oldf;
			d.edited();
		}
	}

	public final void setFont(Font font) {
		if (font == null) font = new Font("SansSerif", Font.PLAIN, 12);
		if (history != null) history.add(new FontAtom(this, font));
		this.font = font;
		this.edited();
	}
	
	public final int getTextAlignment() {
		return textAlignment;
	}
	
	private static class TextAlignmentAtom implements Atom {
		private AbstractDrawObject d;
		private int oldta;
		private int newta;
		public TextAlignmentAtom(AbstractDrawObject d, int ta) {
			this.d = d;
			this.oldta = d.textAlignment;
			this.newta = ta;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldta = ((TextAlignmentAtom)previousAtom).oldta;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TextAlignmentAtom) && ((TextAlignmentAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.textAlignment = newta;
			d.edited();
		}
		public void undo() {
			d.textAlignment = oldta;
			d.edited();
		}
	}
	
	public final void setTextAlignment(int textAlignment) {
		if (history != null) history.add(new TextAlignmentAtom(this, textAlignment));
		this.textAlignment = textAlignment;
		this.edited();
	}
	
	public final boolean isAntiAliased() {
		return antiAliased;
	}
	
	private static class AntiAliasedAtom implements Atom {
		private AbstractDrawObject d;
		private boolean oldaa;
		private boolean newaa;
		public AntiAliasedAtom(AbstractDrawObject d, boolean aa) {
			this.d = d;
			this.oldaa = d.antiAliased;
			this.newaa = aa;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldaa = ((AntiAliasedAtom)previousAtom).oldaa;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof AntiAliasedAtom) && ((AntiAliasedAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.antiAliased = newaa;
			d.edited();
		}
		public void undo() {
			d.antiAliased = oldaa;
			d.edited();
		}
	}
	
	public final void setAntiAliased(boolean antiAliased) {
		if (history != null) history.add(new AntiAliasedAtom(this, antiAliased));
		this.antiAliased = antiAliased;
		this.edited();
	}

	public final AffineTransform getTransform() {
		return transform;
	}
	
	private static class TransformAtom implements Atom {
		private AbstractDrawObject d;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public TransformAtom(AbstractDrawObject d, AffineTransform tx) {
			this.d = d;
			this.oldtx = d.transform;
			this.newtx = tx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((TransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TransformAtom) && ((TransformAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.transform = newtx;
			d.edited();
		}
		public void undo() {
			d.transform = oldtx;
			d.edited();
		}
	}

	public final void setTransform(AffineTransform transform) {
		if (history != null) history.add(new TransformAtom(this, transform));
		this.transform = transform;
		this.edited();
	}
	
	public final boolean isVisible() {
		return visible;
	}
	
	public final boolean isLocked() {
		return locked;
	}
	
	public final boolean isSelected() {
		return selected;
	}
	
	private static class AttributeAtom implements Atom {
		private AbstractDrawObject d;
		private boolean oldv, oldl, olds;
		private boolean newv, newl, news;
		public AttributeAtom(AbstractDrawObject d, boolean vis, boolean lok, boolean sel) {
			this.d = d;
			this.oldv = d.visible;
			this.oldl = d.locked;
			this.olds = d.selected;
			this.newv = vis;
			this.newl = lok;
			this.news = sel;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldv = ((AttributeAtom)previousAtom).oldv;
			this.oldl = ((AttributeAtom)previousAtom).oldl;
			this.olds = ((AttributeAtom)previousAtom).olds;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof AttributeAtom) && ((AttributeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.visible = newv;
			d.locked = newl;
			d.selected = news;
		}
		public void undo() {
			d.visible = oldv;
			d.locked = oldl;
			d.selected = olds;
		}
	}
	
	public void setVisible(boolean visible) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.visible = visible;
	}
	
	public void setLocked(boolean locked) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.locked = locked;
	}
	
	public void setSelected(boolean selected) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.selected = selected;
	}
	
	public abstract int getControlPointCount();
	
	protected abstract ControlPoint getControlPointImpl(int i);
	
	protected ControlPoint[] getControlPointsImpl() {
		// this implementation is provided,
		// but concrete classes may override it
		// if a more efficient method is available
		ControlPoint[] cp = new ControlPoint[getControlPointCount()];
		for (int i = 0; i < cp.length; i++) {
			cp[i] = getControlPointImpl(i);
		}
		return cp;
	}
	
	private static final double[][] EMPTY_LINE_LIST = new double[0][];
	protected double[][] getControlLinesImpl() {
		return EMPTY_LINE_LIST;
	}
	
	protected abstract int setControlPointImpl(int i, Point2D p);
	
	public final ControlPoint getControlPoint(int i) {
		ControlPoint cp = getControlPointImpl(i);
		if (transform != null) {
			Point2D.Double p = new Point2D.Double(cp.getX(), cp.getY());
			transform.transform(p, p);
			cp = new ControlPoint.Double(p, cp.getType());
		}
		return cp;
	}
	
	public final ControlPoint[] getControlPoints() {
		ControlPoint[] cp = getControlPointsImpl();
		if (transform != null) {
			for (int i = 0; i < cp.length; i++) {
				Point2D.Double p = new Point2D.Double(cp[i].getX(), cp[i].getY());
				transform.transform(p, p);
				cp[i] = new ControlPoint.Double(p, cp[i].getType());
			}
		}
		return cp;
	}
	
	public final double[][] getControlLines() {
		double[][] cl = getControlLinesImpl();
		if (cl != null && transform != null) {
			for (int i = 0; i < cl.length; i++) {
				Point2D.Double p1 = new Point2D.Double(cl[i][0], cl[i][1]);
				Point2D.Double p2 = new Point2D.Double(cl[i][2], cl[i][3]);
				transform.transform(p1, p1);
				transform.transform(p2, p2);
				cl[i][0] = p1.x; cl[i][1] = p1.y;
				cl[i][2] = p2.x; cl[i][3] = p2.y;
			}
		}
		return cl;
	}
	
	private static class ControlPointAtom implements Atom {
		private AbstractDrawObject d;
		private int cpIndex;
		private Point2D oldPt;
		private Point2D newPt;
		public ControlPointAtom(AbstractDrawObject d, int i, Point2D p) {
			this.d = d;
			this.cpIndex = i;
			ControlPoint cp = d.getControlPointImpl(i);
			this.oldPt = new Point2D.Double(cp.getX(), cp.getY());
			this.newPt = new Point2D.Double(p.getX(), p.getY());
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldPt = ((ControlPointAtom)previousAtom).oldPt;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ControlPointAtom)
				&& (((ControlPointAtom)previousAtom).d == this.d)
				&& (((ControlPointAtom)previousAtom).cpIndex == this.cpIndex);
		}
		public void redo() {
			d.setControlPointImpl(cpIndex, newPt);
			d.edited();
		}
		public void undo() {
			d.setControlPointImpl(cpIndex, oldPt);
			d.edited();
		}
	}
	
	public final int setControlPoint(int i, Point2D p) {
		if (transform != null) {
			try {
				Point2D p2 = new Point2D.Double(p.getX(), p.getY());
				p = transform.inverseTransform(p2, p2);
			} catch (NoninvertibleTransformException e) {
				return i;
			}
		}
		if (history != null) history.add(new ControlPointAtom(this, i, p));
		int ret = setControlPointImpl(i, p);
		this.edited();
		return ret;
	}
	
	protected abstract Point2D getAnchorImpl();
	protected abstract void setAnchorImpl(Point2D p);
	
	public final Point2D getAnchor() {
		Point2D a = getAnchorImpl();
		if (transform != null) {
			Point2D.Double p = new Point2D.Double(a.getX(), a.getY());
			a = transform.transform(p, p);
		}
		return a;
	}
	
	private static class AnchorAtom implements Atom {
		private AbstractDrawObject d;
		private Point2D oldPt;
		private Point2D newPt;
		public AnchorAtom(AbstractDrawObject d, Point2D p) {
			this.d = d;
			Point2D a = d.getAnchorImpl();
			this.oldPt = new Point2D.Double(a.getX(), a.getY());
			this.newPt = new Point2D.Double(p.getX(), p.getY());
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldPt = ((ControlPointAtom)previousAtom).oldPt;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ControlPointAtom)
				&& (((ControlPointAtom)previousAtom).d == this.d);
		}
		public void redo() {
			d.setAnchorImpl(newPt);
			d.edited();
		}
		public void undo() {
			d.setAnchorImpl(oldPt);
			d.edited();
		}
	}
	
	public final void setAnchor(Point2D p) {
		if (transform != null) {
			try {
				Point2D p2 = new Point2D.Double(p.getX(), p.getY());
				p = transform.inverseTransform(p2, p2);
			} catch (NoninvertibleTransformException e) {
				return;
			}
		}
		if (history != null) history.add(new AnchorAtom(this, p));
		setAnchorImpl(p);
		this.edited();
	}
	
	private Composite c = null;
	private Paint p = null;
	private Stroke s = null;
	private Font f = null;
	private RenderingHints h = null;
	
	protected final void push(Graphics2D g) {
		c = g.getComposite();
		p = g.getPaint();
		s = g.getStroke();
		f = g.getFont();
		h = g.getRenderingHints();
	}
	
	protected final void pop(Graphics2D g) {
		if (c != null) g.setComposite(c);
		if (p != null) g.setPaint(p);
		if (s != null) g.setStroke(s);
		if (f != null) g.setFont(f);
		if (h != null) g.setRenderingHints(h);
	}
	
	protected final void applyDraw(Graphics2D g) {
		g.setComposite(drawComposite);
		g.setPaint((drawPaint == null) ? COLOR_CLEAR : drawPaint);
		g.setStroke(stroke);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	protected final void applyFill(Graphics2D g) {
		g.setComposite(fillComposite);
		g.setPaint((fillPaint == null) ? COLOR_CLEAR : fillPaint);
		g.setStroke(stroke);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	public String toString() {
		return "com.kreative.paint.objects.AbstractDrawObject["+drawComposite+","+drawPaint+","+fillComposite+","+fillPaint+","+stroke+","+font+","+textAlignment+","+antiAliased+","+transform+"]";
	}
}
