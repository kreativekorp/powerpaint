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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.util.ShapeUtils;

public class QuickShadowDrawObject extends ShapeDrawObject {
	public static final int SHADOW_TYPE_NONE = 0;
	public static final int SHADOW_TYPE_STROKE = 1;
	public static final int SHADOW_TYPE_FILL = 2;
	public static final int SHADOW_TYPE_BLACK = 3;
	public static final int SHADOW_TYPE_GRAY = 4;
	public static final int SHADOW_TYPE_WHITE = 5;
	
	private int shadowType, shadowOpacity, xOffset, yOffset;
	
	public QuickShadowDrawObject(Shape originalShape, int shadowType, int shadowOpacity, int xOffset, int yOffset) {
		super(originalShape);
		this.shadowType = shadowType;
		this.shadowOpacity = shadowOpacity;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public QuickShadowDrawObject(Shape originalShape, int shadowType, int shadowOpacity, int xOffset, int yOffset, PaintSettings ps) {
		super(originalShape, ps);
		this.shadowType = shadowType;
		this.shadowOpacity = shadowOpacity;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public QuickShadowDrawObject clone() {
		QuickShadowDrawObject o = new QuickShadowDrawObject(ShapeUtils.cloneShape(originalShape), shadowType, shadowOpacity, xOffset, yOffset, getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public int getShadowType() {
		return shadowType;
	}
	
	public int getShadowOpacity() {
		return shadowOpacity;
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	protected void paint(Graphics2D g, Shape ts, Shape tss) {
		push(g);
		Shape shs = AffineTransform.getTranslateInstance(xOffset, yOffset).createTransformedShape(ts);
		if (shadowType < 0 || shadowType >= 0x01000000) {
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(new Color(shadowType, true));
			g.fill(shs);
		} else {
			switch (shadowType) {
			case SHADOW_TYPE_STROKE:
				applyDraw(g);
				if (shadowOpacity < 255 && g.getComposite() instanceof AlphaComposite) {
					AlphaComposite cx = (AlphaComposite)g.getComposite();
					g.setComposite(AlphaComposite.getInstance(cx.getRule(), cx.getAlpha() * shadowOpacity / 255.0f));
				}
				g.fill(shs);
				break;
			case SHADOW_TYPE_FILL:
				applyFill(g);
				if (shadowOpacity < 255 && g.getComposite() instanceof AlphaComposite) {
					AlphaComposite cx = (AlphaComposite)g.getComposite();
					g.setComposite(AlphaComposite.getInstance(cx.getRule(), cx.getAlpha() * shadowOpacity / 255.0f));
				}
				g.fill(shs);
				break;
			case SHADOW_TYPE_BLACK:
				g.setComposite(AlphaComposite.SrcOver);
				g.setPaint(new Color((shadowOpacity << 24), true));
				g.fill(shs);
				break;
			case SHADOW_TYPE_GRAY:
				g.setComposite(AlphaComposite.SrcOver);
				g.setPaint(new Color(((shadowOpacity << 24) | 0x808080), true));
				g.fill(shs);
				break;
			case SHADOW_TYPE_WHITE:
				g.setComposite(AlphaComposite.SrcOver);
				g.setPaint(new Color(((shadowOpacity << 24) | 0xFFFFFF), true));
				g.fill(shs);
				break;
			}
		}
		if (isFilled()) {
			applyFill(g);
			g.fill(ts);
		}
		if (isDrawn()) {
			applyDraw(g);
			g.fill(tss);
		}
		pop(g);
	}

	public String toString() {
		return "com.kreative.paint.objects.QuickShadowDrawObject["+shadowType+","+shadowOpacity+","+xOffset+","+yOffset+","+super.toString()+"]";
	}
}
