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

import javax.swing.SwingConstants;

public interface PaintContextConstants {
	public static final int CHANGED_DRAW_COMPOSITE = 0x0001;
	public static final int CHANGED_DRAW_PAINT = 0x0002;
	public static final int CHANGED_FILL_COMPOSITE = 0x0004;
	public static final int CHANGED_FILL_PAINT = 0x0008;
	public static final int CHANGED_STROKE = 0x0010;
	public static final int CHANGED_FONT = 0x0100;
	public static final int CHANGED_TEXT_ALIGNMENT = 0x0200;
	public static final int CHANGED_ANTI_ALIASED = 0x1000;
	public static final int CHANGED_COMPOSITE = CHANGED_DRAW_COMPOSITE | CHANGED_FILL_COMPOSITE;
	public static final int CHANGED_PAINT = CHANGED_DRAW_PAINT | CHANGED_FILL_PAINT;
	public static final int CHANGED_EDITING_STROKE = 0x40000000;
	public static final int CHANGED_EDITING_BKGND = 0x80000000;
	public static final int CHANGED_EDITING = CHANGED_EDITING_STROKE | CHANGED_EDITING_BKGND;
	
	public static final int LEFT = SwingConstants.LEFT;
	public static final int CENTER = SwingConstants.CENTER;
	public static final int RIGHT = SwingConstants.RIGHT;
}
