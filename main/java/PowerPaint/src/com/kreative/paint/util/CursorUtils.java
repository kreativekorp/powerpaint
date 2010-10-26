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

package com.kreative.paint.util;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class CursorUtils {
	private CursorUtils() {}
	
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	
	public static final Cursor CURSOR_MOVE = makeCursor(
			17, 17,
			new int[] {
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,W,K,W,0,0,W,K,W,0,0,W,K,W,0,0,
					0,W,K,K,W,W,W,W,K,W,W,W,W,K,K,W,0,
					W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,
					0,W,K,K,W,W,W,W,K,W,W,W,W,K,K,W,0,
					0,0,W,K,W,0,0,W,K,W,0,0,W,K,W,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
			},
			8, 8,
			"Move"
	);
	
	public static final Cursor CURSOR_ARROW = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,W,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,W,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,W,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,W,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,W,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,
					W,K,K,K,K,K,W,W,W,W,W,0,0,0,0,0,
					W,K,K,W,K,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,0,W,K,K,W,0,0,0,0,0,0,0,0,
					W,W,0,0,W,K,K,W,0,0,0,0,0,0,0,0,
					W,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
			},
			1, 1,
			"Arrow"
	);
	
	public static final Cursor CURSOR_ARROW_HALLOW = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,K,W,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,W,K,W,0,0,0,0,0,0,0,0,0,0,
					W,K,W,W,W,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,W,W,W,K,W,0,0,0,0,0,0,0,0,
					W,K,W,W,W,W,W,K,W,0,0,0,0,0,0,0,
					W,K,W,W,W,W,K,K,K,W,0,0,0,0,0,0,
					W,K,W,K,K,K,W,W,W,W,W,0,0,0,0,0,
					W,K,K,W,K,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,0,W,K,K,W,0,0,0,0,0,0,0,0,
					W,W,0,0,W,K,K,W,0,0,0,0,0,0,0,0,
					W,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
			},
			1, 1,
			"HollowArrow"
	);
	
	public static final Cursor CURSOR_ARROW_HALF = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,W,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,W,0,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,W,0,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,W,0,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,W,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,
					W,K,K,K,W,W,W,W,W,W,W,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			1, 1,
			"HalfArrow"
	);
	
	public static final Cursor CURSOR_ARROW_HALF_HALLOW = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,K,W,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,W,K,W,0,0,0,0,0,0,0,0,0,0,
					W,K,W,W,W,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,W,W,W,K,W,0,0,0,0,0,0,0,0,
					W,K,W,W,W,W,W,K,W,0,0,0,0,0,0,0,
					W,K,W,W,K,K,K,K,K,W,0,0,0,0,0,0,
					W,K,W,K,W,W,W,W,W,W,W,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			1, 1,
			"HollowHalfArrow"
	);
	
	public static final Cursor CURSOR_ARROW_HALF_DOUBLE = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,W,W,0,0,0,0,0,0,0,0,0,
					W,K,K,K,W,W,K,W,0,0,0,0,0,0,0,0,
					W,K,K,K,K,W,K,K,W,0,0,0,0,0,0,0,
					W,K,K,K,K,K,W,K,K,W,0,0,0,0,0,0,
					W,K,K,K,K,K,K,W,K,K,W,0,0,0,0,0,
					W,K,K,K,K,K,K,K,W,K,K,W,0,0,0,0,
					W,K,K,K,K,K,K,K,K,W,K,K,W,0,0,0,
					W,K,K,K,W,W,W,W,W,W,W,K,K,W,0,0,
					W,K,K,W,0,W,K,K,K,K,K,K,K,K,W,0,
					W,K,W,0,0,W,K,K,K,W,W,W,W,W,W,W,
					W,W,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					W,0,0,0,0,W,K,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,0,0,0,0,0,0,0,0,0,0,
			},
			1, 1,
			"DoubleHalfArrow"
	);
	
	public static final Cursor CURSOR_ARROW_HALF_DOUBLE_HALLOW = makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,W,W,0,0,0,0,0,0,0,0,0,
					W,K,W,K,W,W,K,W,0,0,0,0,0,0,0,0,
					W,K,W,W,K,W,K,K,W,0,0,0,0,0,0,0,
					W,K,W,W,W,K,W,W,K,W,0,0,0,0,0,0,
					W,K,W,W,W,W,K,W,W,K,W,0,0,0,0,0,
					W,K,W,W,W,W,W,K,W,W,K,W,0,0,0,0,
					W,K,W,W,K,K,K,K,K,W,W,K,W,0,0,0,
					W,K,W,K,W,W,W,W,W,W,W,W,K,W,0,0,
					W,K,K,W,0,W,K,W,W,K,K,K,K,K,W,0,
					W,K,W,0,0,W,K,W,K,W,W,W,W,W,W,W,
					W,W,0,0,0,W,K,K,W,0,0,0,0,0,0,0,
					W,0,0,0,0,W,K,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,0,0,0,0,0,0,0,0,0,0,
			},
			1, 1,
			"HollowDoubleHalfArrow"
	);
	
	public static final Cursor CURSOR_SELECT = makeCursor(
			17, 17,
			new int[] {
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
					W,W,W,W,W,W,W,W,K,W,W,W,W,W,W,W,W,
					W,K,K,W,K,K,W,K,K,K,W,K,K,W,K,K,W,
					W,W,W,W,W,W,W,W,K,W,W,W,W,W,W,W,W,
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,
			},
			8, 8,
			"Select"
	);
	
	public static final Cursor CURSOR_MOVE_SELECT = makeCursor(
			17, 17,
			new int[] {
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,K,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,0,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,W,K,W,0,0,W,W,W,0,0,W,K,W,0,0,
					0,W,K,W,W,W,W,W,K,W,W,W,W,W,K,W,0,
					W,K,W,W,K,K,W,K,K,K,W,K,K,W,W,K,W,
					0,W,K,W,W,W,W,W,K,W,W,W,W,W,K,W,0,
					0,0,W,K,W,0,0,W,W,W,0,0,W,K,W,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,0,0,0,0,
					0,0,0,0,0,0,W,K,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
			},
			8, 8,
			"MoveSelect"
	);
	
	public static final Cursor CURSOR_DROPPER = makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,K,K,
					0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,K,W,K,K,K,0,0,0,
					0,0,0,0,0,0,0,K,W,W,W,K,K,0,0,0,
					0,0,0,0,0,0,K,W,W,W,K,0,K,0,0,0,
					0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,0,
					0,0,0,K,W,W,W,K,0,0,0,0,0,0,0,0,
					0,0,K,W,W,W,K,0,0,0,0,0,0,0,0,0,
					0,K,W,W,W,K,0,0,0,0,0,0,0,0,0,0,
					0,K,W,W,K,0,0,0,0,0,0,0,0,0,0,0,
					K,W,K,K,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			0, 15,
			"Eyedropper"
	);
	
	public static final Cursor CURSOR_BUCKET = makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,0,K,W,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,W,W,K,W,K,K,0,0,0,0,
					0,0,0,0,K,W,W,W,K,W,W,K,K,K,0,0,
					0,0,0,K,W,W,W,W,K,W,W,W,K,K,K,0,
					0,0,K,W,W,W,W,K,W,K,W,W,W,K,K,K,
					0,K,W,W,W,W,W,W,K,W,W,W,W,K,K,K,
					K,W,W,W,W,W,W,W,W,W,W,W,K,K,K,K,
					K,W,W,W,W,W,W,W,W,W,W,K,0,K,K,K,
					0,K,W,W,W,W,W,W,W,W,K,0,0,K,K,K,
					0,0,K,W,W,W,W,W,W,K,0,0,0,K,K,K,
					0,0,0,K,W,W,W,W,K,0,0,0,0,K,K,K,
					0,0,0,0,K,W,W,K,0,0,0,0,0,K,K,0,
					0,0,0,0,0,K,K,0,0,0,0,0,0,K,0,0,
			},
			13, 15,
			"PaintBucket"
	);
	
	public static final Cursor CURSOR_HAND_OPEN = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,K,K,0,K,W,W,K,K,K,0,0,0,0,
					0,0,K,W,W,K,K,W,W,K,W,W,K,0,0,0,
					0,0,K,W,W,K,K,W,W,K,W,W,K,0,K,0,
					0,0,0,K,W,W,K,W,W,K,W,W,K,K,W,K,
					0,0,0,K,W,W,K,W,W,K,W,W,K,W,W,K,
					0,K,K,0,K,W,W,W,W,W,W,W,K,W,W,K,
					K,W,W,K,K,W,W,W,W,W,W,W,W,W,W,K,
					K,W,W,W,K,W,W,W,W,W,W,W,W,W,K,0,
					0,K,W,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,0,K,W,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
			},
			6, 6,
			"Hand"
	);
	
	public static final Cursor CURSOR_HAND_CLOSED = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,K,K,0,K,K,0,0,0,0,
					0,0,0,K,W,W,K,W,W,K,W,W,K,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,K,W,K,0,
					0,0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,
					0,0,0,K,K,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,0,K,W,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
			},
			6, 6,
			"HandClosed"
	);
	
	public static Cursor makeCursor(int width, int height, int[] rgb, int hotx, int hoty, String name) {
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = tk.getBestCursorSize(width, height);
			if (d.width <= 0 || d.height <= 0) {
				System.err.println("Notice: System does not support custom cursors. Returning generic cursor.");
				return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			}
			if (d.width < width || d.height < height) {
				d = tk.getBestCursorSize(width*2, height*2);
				if (d.width < width || d.height < height) {
					System.err.println("Notice: Tool requested a cursor larger than possible on this system. Returning generic cursor.");
					return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
				}
			}
			BufferedImage img2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			img2.setRGB(0, 0, width, height, rgb, 0, width);
			return tk.createCustomCursor(img2, new Point(hotx, hoty), name);
		} catch (Exception e) {
			e.printStackTrace();
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
	}
	
	public static Cursor makeCursor(BufferedImage img, int hotx, int hoty, String name) {
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = tk.getBestCursorSize(img.getWidth(), img.getHeight());
			if (d.width <= 0 || d.height <= 0) {
				System.err.println("Notice: System does not support custom cursors. Returning generic cursor.");
				return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			}
			if (d.width < img.getWidth() || d.height < img.getHeight()) {
				d = tk.getBestCursorSize(img.getWidth()*2, img.getHeight()*2);
				if (d.width < img.getWidth() || d.height < img.getHeight()) {
					System.err.println("Notice: Tool requested a cursor larger than possible on this system. Returning generic cursor.");
					return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
				}
			}
			BufferedImage img2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img2.createGraphics();
			g2.drawImage(img, null, 0, 0);
			g2.dispose();
			return tk.createCustomCursor(img2, new Point(hotx, hoty), name);
		} catch (Exception e) {
			e.printStackTrace();
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
	}
}
