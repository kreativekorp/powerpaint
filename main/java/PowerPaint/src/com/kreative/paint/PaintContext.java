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

package com.kreative.paint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashSet;
import com.kreative.paint.awt.PatternPaint;

public class PaintContext implements PaintContextConstants {
	private static final Color COLOR_CLEAR = new Color(0, true);
	
	private Collection<PaintContextListener> pslisteners;
	
	private Composite drawComposite;
	private Paint drawPaint;
	private Composite fillComposite;
	private Paint fillPaint;
	private Stroke stroke;
	private Font font;
	private int textAlignment;
	private boolean antiAliased;
	
	private boolean editingStroke;
	private boolean editingBkgnd;
	
	public PaintContext() {
		this.pslisteners = new HashSet<PaintContextListener>();
		
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = Color.black;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = Color.black;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
		
		this.editingStroke = false;
		this.editingBkgnd = false;
	}
	
	public boolean isEditingFill() {
		return !editingStroke;
	}
	
	public boolean isEditingStroke() {
		return editingStroke;
	}
	
	public boolean isEditingForeground() {
		return !editingBkgnd;
	}
	
	public boolean isEditingBackground() {
		return editingBkgnd;
	}
	
	public void setEditingFill(boolean fill) {
		editingStroke = !fill;
		for (PaintContextListener psl : pslisteners) {
			psl.editingChanged(this, editingStroke, editingBkgnd);
		}
	}
	
	public void setEditingStroke(boolean stroke) {
		editingStroke = stroke;
		for (PaintContextListener psl : pslisteners) {
			psl.editingChanged(this, editingStroke, editingBkgnd);
		}
	}
	
	public void setEditingForeground(boolean fore) {
		editingBkgnd = !fore;
		for (PaintContextListener psl : pslisteners) {
			psl.editingChanged(this, editingStroke, editingBkgnd);
		}
	}
	
	public void setEditingBackground(boolean back) {
		editingBkgnd = back;
		for (PaintContextListener psl : pslisteners) {
			psl.editingChanged(this, editingStroke, editingBkgnd);
		}
	}
	
	public void setEditing(boolean stroke, boolean bkgnd) {
		editingStroke = stroke;
		editingBkgnd = bkgnd;
		for (PaintContextListener psl : pslisteners) {
			psl.editingChanged(this, editingStroke, editingBkgnd);
		}
	}
	
	public PaintSettings getPaintSettings() {
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
	
	public void setPaintSettings(PaintSettings ps) {
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		notifyPaintContextListenersPaintSettingsChanged(-1);
	}
	
	public boolean isDrawn() {
		return drawPaint != null;
	}
	
	public boolean isFilled() {
		return fillPaint != null;
	}

	public Composite getDrawComposite() {
		return drawComposite;
	}

	public Paint getDrawPaint() {
		return drawPaint;
	}
	
	public Paint getDrawForeground() {
		if (drawPaint == null) {
			return null;
		} else if (drawPaint instanceof PatternPaint) {
			return ((PatternPaint)drawPaint).getForeground();
		} else {
			return drawPaint;
		}
	}
	
	public Paint getDrawBackground() {
		if (drawPaint == null) {
			return null;
		} else if (drawPaint instanceof PatternPaint) {
			return ((PatternPaint)drawPaint).getBackground();
		} else {
			return Color.white;
		}
	}
	
	public long getDrawPattern() {
		if (drawPaint instanceof PatternPaint) {
			return ((PatternPaint)drawPaint).getPattern();
		} else {
			return -1;
		}
	}

	public Composite getFillComposite() {
		return fillComposite;
	}

	public Paint getFillPaint() {
		return fillPaint;
	}
	
	public Paint getFillForeground() {
		if (fillPaint == null) {
			return null;
		} else if (fillPaint instanceof PatternPaint) {
			return ((PatternPaint)fillPaint).getForeground();
		} else {
			return fillPaint;
		}
	}
	
	public Paint getFillBackground() {
		if (fillPaint == null) {
			return null;
		} else if (fillPaint instanceof PatternPaint) {
			return ((PatternPaint)fillPaint).getBackground();
		} else {
			return Color.white;
		}
	}
	
	public long getFillPattern() {
		if (fillPaint instanceof PatternPaint) {
			return ((PatternPaint)fillPaint).getPattern();
		} else {
			return -1;
		}
	}
	
	public Composite getEditedComposite() {
		return editingStroke ? getDrawComposite() : getFillComposite();
	}
	
	public Paint getEditedPaint() {
		return editingStroke ? getDrawPaint() : getFillPaint();
	}
	
	public Paint getEditedForeground() {
		return editingStroke ? getDrawForeground() : getFillForeground();
	}
	
	public Paint getEditedBackground() {
		return editingStroke ? getDrawBackground() : getFillBackground();
	}
	
	public Paint getEditedEditedground() {
		return editingStroke ? (editingBkgnd ? getDrawBackground() : getDrawForeground()) : (editingBkgnd ? getFillBackground() : getFillForeground());
	}
	
	public long getEditedPattern() {
		return editingStroke ? getDrawPattern() : getFillPattern();
	}

	public void setDrawComposite(Composite drawComposite) {
		this.drawComposite = (drawComposite == null) ? AlphaComposite.SrcOver : drawComposite;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_DRAW_COMPOSITE);
	}

	public void setDrawPaint(Paint drawPaint) {
		this.drawPaint = drawPaint;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_DRAW_PAINT);
	}
	
	public void setDrawForeground(Paint fore) {
		Paint back = getDrawBackground();
		if (fore == null && back == null) {
			setDrawPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setDrawPaint(new PatternPaint(fore, back, getDrawPattern()));
		}
	}
	
	public void setDrawBackground(Paint back) {
		Paint fore = getDrawForeground();
		if (fore == null && back == null) {
			setDrawPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setDrawPaint(new PatternPaint(fore, back, getDrawPattern()));
		}
	}
	
	public void setDrawPattern(long pattern) {
		Paint fore = getDrawForeground();
		Paint back = getDrawBackground();
		if (fore == null && back == null) {
			setDrawPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setDrawPaint(new PatternPaint(fore, back, pattern));
		}
	}

	public void setFillComposite(Composite fillComposite) {
		this.fillComposite = (fillComposite == null) ? AlphaComposite.SrcOver : fillComposite;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_FILL_COMPOSITE);
	}

	public void setFillPaint(Paint fillPaint) {
		this.fillPaint = fillPaint;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_FILL_PAINT);
	}
	
	public void setFillForeground(Paint fore) {
		Paint back = getFillBackground();
		if (fore == null && back == null) {
			setFillPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setFillPaint(new PatternPaint(fore, back, getFillPattern()));
		}
	}
	
	public void setFillBackground(Paint back) {
		Paint fore = getFillForeground();
		if (fore == null && back == null) {
			setFillPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setFillPaint(new PatternPaint(fore, back, getFillPattern()));
		}
	}
	
	public void setFillPattern(long pattern) {
		Paint fore = getFillForeground();
		Paint back = getFillBackground();
		if (fore == null && back == null) {
			setFillPaint(null);
		} else {
			if (fore == null) fore = COLOR_CLEAR;
			if (back == null) back = COLOR_CLEAR;
			setFillPaint(new PatternPaint(fore, back, pattern));
		}
	}
	
	public void setEditedComposite(Composite composite) {
		if (editingStroke) setDrawComposite(composite);
		else setFillComposite(composite);
	}
	
	public void setEditedPaint(Paint paint) {
		if (editingStroke) setDrawPaint(paint);
		else setFillPaint(paint);
	}
	
	public void setEditedForeground(Paint fore) {
		if (editingStroke) setDrawForeground(fore);
		else setFillForeground(fore);
	}
	
	public void setEditedBackground(Paint back) {
		if (editingStroke) setDrawBackground(back);
		else setFillBackground(back);
	}
	
	public void setEditedEditedground(Paint paint) {
		if (editingStroke) {
			if (editingBkgnd) setDrawBackground(paint);
			else setDrawForeground(paint);
		} else {
			if (editingBkgnd) setFillBackground(paint);
			else setFillForeground(paint);
		}
	}
	
	public void setEditedPattern(long pattern) {
		if (editingStroke) setDrawPattern(pattern);
		else setFillPattern(pattern);
	}
	
	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = (stroke == null) ? new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) : stroke;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_STROKE);
	}
	
	public Font getFont() {
		return font;
	}
	
	public int getTextAlignment() {
		return textAlignment;
	}
	
	public void setFont(Font font) {
		this.font = (font == null) ? new Font("SansSerif", Font.PLAIN, 12) : font;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_FONT);
	}
	
	public void setTextAlignment(int textAlignment) {
		this.textAlignment = textAlignment;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_TEXT_ALIGNMENT);
	}
	
	public boolean isAntiAliased() {
		return antiAliased;
	}
	
	public void setAntiAliased(boolean antiAliased) {
		this.antiAliased = antiAliased;
		notifyPaintContextListenersPaintSettingsChanged(CHANGED_ANTI_ALIASED);
	}
	
	public void addPaintContextListener(PaintContextListener psl) {
		pslisteners.add(psl);
	}
	
	public void removePaintContextListener(PaintContextListener psl) {
		pslisteners.add(psl);
	}
	
	public PaintContextListener[] getPaintContextListeners() {
		return pslisteners.toArray(new PaintContextListener[0]);
	}
	
	protected void notifyPaintContextListenersPaintSettingsChanged(int delta) {
		for (PaintContextListener psl : pslisteners) {
			psl.paintSettingsChanged(this, getPaintSettings(), delta);
		}
	}
	
	public void applyDraw(Graphics2D g) {
		g.setComposite(drawComposite);
		g.setPaint((drawPaint == null) ? COLOR_CLEAR : drawPaint);
		g.setStroke(stroke);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	public void applyFill(Graphics2D g) {
		g.setComposite(fillComposite);
		g.setPaint((fillPaint == null) ? COLOR_CLEAR : fillPaint);
		g.setStroke(stroke);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}
