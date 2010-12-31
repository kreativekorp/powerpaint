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

import java.awt.Cursor;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.util.CursorUtils;

public enum ControlPointType {
	GENERIC(fcc("gnrl")),
	CENTER(fcc("cntr")),
	NORTHWEST(fcc("nwcr")),
	NORTH(fcc("nedg")),
	NORTHEAST(fcc("necr")),
	EAST(fcc("eedg")),
	SOUTHEAST(fcc("secr")),
	SOUTH(fcc("sedg")),
	SOUTHWEST(fcc("swcr")),
	WEST(fcc("wedg")),
	RADIUS(fcc("radi")),
	ANGLE(fcc("angl")),
	BASELINE(fcc("base")),
	ENDPOINT(fcc("endp")),
	CURVED_MIDPOINT(fcc("cmid")),
	STRAIGHT_MIDPOINT(fcc("smid")),
	CONTROL_POINT(fcc("ctrl")),
	PULLTAB(fcc("pull"));
	
	private static int fcc(String s) {
		int a = (s.length() > 0) ? (s.charAt(0) & 0xFF) : 0x20;
		int b = (s.length() > 1) ? (s.charAt(1) & 0xFF) : 0x20;
		int c = (s.length() > 2) ? (s.charAt(2) & 0xFF) : 0x20;
		int d = (s.length() > 3) ? (s.charAt(3) & 0xFF) : 0x20;
		return ((a << 24) | (b << 16) | (c << 8) | d);
	}
	
	private int serializedForm;
	
	private ControlPointType(int serializedForm) {
		this.serializedForm = serializedForm;
	}
	
	public int getSerializedForm() {
		return serializedForm;
	}
	
	public static ControlPointType forSerializedForm(int i) {
		for (ControlPointType c : values()) {
			if (c.serializedForm == i) return c;
		}
		return null;
	}
	
	public Shape getShape(float x, float y) {
		switch (this) {
		case GENERIC:
		case NORTHWEST:
		case NORTH:
		case NORTHEAST:
		case EAST:
		case SOUTHEAST:
		case SOUTH:
		case SOUTHWEST:
		case WEST:
		case BASELINE:
		case ENDPOINT:
		case STRAIGHT_MIDPOINT:
			return new Rectangle2D.Float(x-2.0f, y-2.0f, 4.0f, 4.0f);
		case CENTER:
		case RADIUS:
		case ANGLE:
		case CURVED_MIDPOINT:
			return new Ellipse2D.Float(x-3.0f, y-3.0f, 6.0f, 6.0f);
		case CONTROL_POINT:
		case PULLTAB:
			GeneralPath p = new GeneralPath();
			p.moveTo(x-3.0f, y);
			p.lineTo(x, y-3.0f);
			p.lineTo(x+3.0f, y);
			p.lineTo(x, y+3.0f);
			p.closePath();
			return p;
		default:
			return new Rectangle2D.Float(x-2.0f, y-2.0f, 4.0f, 4.0f);
		}
	}
	
	public Cursor getCursor() {
		switch (this) {
		case GENERIC:
		case CENTER:
		case RADIUS:
		case ANGLE:
		case BASELINE:
			return CursorUtils.CURSOR_MOVE;
		case ENDPOINT:
		case CURVED_MIDPOINT:
		case STRAIGHT_MIDPOINT:
		case CONTROL_POINT:
		case PULLTAB:
			return CursorUtils.CURSOR_ARROW_HALF;
		case NORTHWEST:
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		case NORTH:
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		case NORTHEAST:
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		case EAST:
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		case SOUTHEAST:
			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		case SOUTH:
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		case SOUTHWEST:
			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		case WEST:
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		default:
			return CursorUtils.CURSOR_MOVE;
		}
	}
}
