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

package com.kreative.paint.draw;

import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import com.kreative.paint.Paintable;
import com.kreative.paint.PaintContextConstants;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.undo.Recordable;

public interface DrawObject extends Paintable, Shape, Cloneable, Recordable, PaintContextConstants {
	public DrawObject clone();
	public PaintSettings getPaintSettings();
	public void setPaintSettings(PaintSettings ps);
	public boolean isDrawn();
	public boolean isFilled();
	public Composite getDrawComposite();
	public void setDrawComposite(Composite drawComposite);
	public Paint getDrawPaint();
	public void setDrawPaint(Paint drawPaint);
	public Composite getFillComposite();
	public void setFillComposite(Composite fillComposite);
	public Paint getFillPaint();
	public void setFillPaint(Paint fillPaint);
	public Stroke getStroke();
	public void setStroke(Stroke stroke);
	public Font getFont();
	public void setFont(Font font);
	public int getTextAlignment();
	public void setTextAlignment(int textAlignment);
	public boolean isAntiAliased();
	public void setAntiAliased(boolean antiAliased);
	public AffineTransform getTransform();
	public void setTransform(AffineTransform transform);
	public boolean isVisible();
	public void setVisible(boolean vis);
	public boolean isLocked();
	public void setLocked(boolean lock);
	public boolean isSelected();
	public void setSelected(boolean sel);
	public int getControlPointCount();
	public ControlPoint getControlPoint(int i);
	public ControlPoint[] getControlPoints();
	public double[][] getControlLines();
	public int setControlPoint(int i, Point2D p);
	public Point2D getAnchor();
	public void setAnchor(Point2D p);
}
