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
import javax.swing.SwingConstants;

public class PaintSettings {
	private static final Color COLOR_CLEAR = new Color(0, true);
	
	public static final int LEFT = SwingConstants.LEFT;
	public static final int CENTER = SwingConstants.CENTER;
	public static final int RIGHT = SwingConstants.RIGHT;
	
	private Composite drawComposite;
	private Paint drawPaint;
	private Composite fillComposite;
	private Paint fillPaint;
	private Stroke stroke;
	private Font font;
	private int textAlignment;
	private boolean antiAliased;
	
	public PaintSettings() {
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = Color.black;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = Color.black;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
	}
	
	public PaintSettings(Paint draw, Paint fill) {
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = draw;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = fill;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
	}
	
	public PaintSettings(Composite drawComposite, Paint drawPaint, Composite fillComposite, Paint fillPaint, Stroke stroke, Font font, int textAlignment, boolean antiAliased) {
		this.drawComposite = (drawComposite == null) ? AlphaComposite.SrcOver : drawComposite;
		this.drawPaint = drawPaint;
		this.fillComposite = (fillComposite == null) ? AlphaComposite.SrcOver : fillComposite;
		this.fillPaint = fillPaint;
		this.stroke = (stroke == null) ? new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER) : stroke;
		this.font = (font == null) ? new Font("SansSerif", Font.PLAIN, 12) : font;
		this.textAlignment = textAlignment;
		this.antiAliased = antiAliased;
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

	public Composite getFillComposite() {
		return fillComposite;
	}

	public Paint getFillPaint() {
		return fillPaint;
	}

	public Stroke getStroke() {
		return stroke;
	}
	
	public Font getFont() {
		return font;
	}
	
	public int getTextAlignment() {
		return textAlignment;
	}
	
	public boolean isAntiAliased() {
		return antiAliased;
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
	
	public String toString() {
		return "com.kreative.paint.PaintSettings["+drawComposite+","+drawPaint+","+fillComposite+","+fillPaint+","+stroke+","+font+","+textAlignment+","+antiAliased+"]";
	}
}
