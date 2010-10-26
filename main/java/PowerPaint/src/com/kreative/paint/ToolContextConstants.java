/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

public interface ToolContextConstants {
	public static final long CHANGED_DRAW_PERPENDICULAR     = 0x0000000000000001L;
	public static final long CHANGED_DRAW_SQUARE            = 0x0000000000000002L;
	public static final long CHANGED_DRAW_CENTER            = 0x0000000000000004L;
	public static final long CHANGED_DRAW_FILLED            = 0x0000000000000008L;
	public static final long CHANGED_DRAW_MULTIPLE          = 0x0000000000000010L;
	public static final long CHANGED_DRAW_OPTIONS           = 0x00000000000000FFL;
	public static final long CHANGED_CORNER_RADIUS_X        = 0x0000000000000100L;
	public static final long CHANGED_CORNER_RADIUS_Y        = 0x0000000000000200L;
	public static final long CHANGED_CORNER_RADIUS          = 0x0000000000000300L;
	public static final long CHANGED_QUICKSHADOW_TYPE       = 0x0000000000001000L;
	public static final long CHANGED_QUICKSHADOW_OPACITY    = 0x0000000000002000L;
	public static final long CHANGED_QUICKSHADOW_X          = 0x0000000000004000L;
	public static final long CHANGED_QUICKSHADOW_Y          = 0x0000000000008000L;
	public static final long CHANGED_QUICKSHADOW            = 0x000000000000F000L;
	public static final long CHANGED_POWERBRUSH_SHAPE       = 0x0000000000010000L;
	public static final long CHANGED_POWERBRUSH_OUTER_SIZE  = 0x0000000000020000L;
	public static final long CHANGED_POWERBRUSH_INNER_SIZE  = 0x0000000000040000L;
	public static final long CHANGED_POWERBRUSH_FLOW_RATE   = 0x0000000000080000L;
	public static final long CHANGED_POWERBRUSH             = 0x00000000000F0000L;
	public static final long CHANGED_CURL_RADIUS            = 0x0000000000100000L;
	public static final long CHANGED_CURL_SPACING           = 0x0000000000200000L;
	public static final long CHANGED_CURL                   = 0x0000000000300000L;
	public static final long CHANGED_POLYGON_SIDES          = 0x0000000000400000L;
	public static final long CHANGED_POLYGON_STELLATION     = 0x0000000000800000L;
	public static final long CHANGED_POLYGON                = 0x0000000000C00000L;
	public static final long CHANGED_ALPHABET_SET           = 0x0000000100000000L;
	public static final long CHANGED_ALPHABET_LETTER        = 0x0000000200000000L;
	public static final long CHANGED_ALPHABET_FONT          = 0x0000000400000000L;
	public static final long CHANGED_BRUSH_SET              = 0x0000001000000000L;
	public static final long CHANGED_BRUSH                  = 0x0000002000000000L;
	public static final long CHANGED_CALLIGRAPHY_BRUSH      = 0x0000004000000000L;
	public static final long CHANGED_CALLIGRAPHY_CONTINUOUS = 0x0000008000000000L;
	public static final long CHANGED_CHARCOAL_BRUSH         = 0x0000010000000000L;
	public static final long CHANGED_FRAME                  = 0x0000020000000000L;
	public static final long CHANGED_STAMP_SET              = 0x0000040000000000L;
	public static final long CHANGED_STAMP                  = 0x0000080000000000L;
	public static final long CHANGED_SHAPE_SET              = 0x0000100000000000L;
	public static final long CHANGED_SHAPE                  = 0x0000200000000000L;
	public static final long CHANGED_SPRINKLE_SET           = 0x0001000000000000L;
	public static final long CHANGED_SPRINKLE               = 0x0002000000000000L;
	public static final long CHANGED_SPRINKLE_BRUSH_MODE    = 0x0004000000000000L;
	public static final long CHANGED_CUSTOM                 = 0x1000000000000000L;
	public static final long CHANGED_MODE                   = 0x2000000000000000L;
	public static final long CHANGED_TOOL                   = 0x4000000000000000L;
	public static final long DOUBLE_CLICKED_TOOL            = 0x8000000000000000L;
}
