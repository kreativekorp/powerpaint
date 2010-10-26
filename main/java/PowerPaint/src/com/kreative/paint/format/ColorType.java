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

package com.kreative.paint.format;

/**
 * Indicates how a file format supports color.
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum ColorType {
	/**
	 * Indicates that a file format supports black and white only.
	 */
	BLACK_AND_WHITE,
	/**
	 * Indicates that a file format supports four levels of gray only.
	 */
	GRAYSCALE_4,
	/**
	 * Indicates that a file format supports 16 levels of gray only.
	 */
	GRAYSCALE_16,
	/**
	 * Indicates that a file format supports 256 levels of gray only.
	 */
	GRAYSCALE_256,
	/**
	 * Indicates that a file format supports some other number of levels of gray only.
	 */
	GRAYSCALE_OTHER,
	/**
	 * Indicates that a file format supports four indexed colors only.
	 */
	INDEXED_4,
	/**
	 * Indicates that a file format supports 16 indexed colors only.
	 */
	INDEXED_16,
	/**
	 * Indicates that a file format supports 256 indexed colors only.
	 */
	INDEXED_256,
	/**
	 * Indicates that a file format supports some other number of indexed colors only.
	 */
	INDEXED_OTHER,
	/**
	 * Indicates that a file format supports color, but limited in some other way.
	 */
	LIMITED_COLOR,
	/**
	 * Indicates that a file format supports RGB with 4 bits per channel.
	 */
	RGB_4,
	/**
	 * Indicates that a file format supports RGB with 8 bits per channel.
	 */
	RGB_8,
	/**
	 * Indicates that a file format supports RGB with 16 bits per channel.
	 */
	RGB_16,
	/**
	 * Indicates that a file format supports arbitrary color models.
	 */
	ARBITRARY_COLOR;
}
