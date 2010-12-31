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

package com.kreative.paint.awt;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

public interface DerivableStroke extends Stroke {
	public static final int CAP_BUTT = BasicStroke.CAP_BUTT;
	public static final int CAP_ROUND = BasicStroke.CAP_ROUND;
	public static final int CAP_SQUARE = BasicStroke.CAP_SQUARE;
	public static final int JOIN_BEVEL = BasicStroke.JOIN_BEVEL;
	public static final int JOIN_MITER = BasicStroke.JOIN_MITER;
	public static final int JOIN_ROUND = BasicStroke.JOIN_ROUND;
	
	public DerivableStroke deriveStroke(float width);
	public DerivableStroke deriveStroke(float width, int cap, int join, float miterlimit);
	public DerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase);
	public DerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity);
	public DerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity, Arrowhead start, Arrowhead end);
	public DerivableStroke deriveStroke(int cap, int join, float miterlimit);
	public DerivableStroke deriveStroke(float[] dash, float dashphase);
	public DerivableStroke deriveStroke(int multiplicity);
	public DerivableStroke deriveStroke(Arrowhead start, Arrowhead end);
	public Shape createStrokedShape(Shape s);
	public float getLineWidth();
	public int getEndCap();
	public int getLineJoin();
	public float getMiterLimit();
	public float[] getDashArray();
	public float getDashPhase();
	public int getMultiplicity();
	public Arrowhead getArrowOnStart();
	public Arrowhead getArrowOnEnd();
}
