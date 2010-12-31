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

package com.kreative.paint.format;

/**
 * Indicates how a file format supports layers.
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum LayerType {
	/**
	 * Indicates that a file format does not support layers.
	 */
	FLAT,
	/**
	 * Indicates that a file format supports one bitmap layer and one vector layer.
	 */
	SUPERPAINT,
	/**
	 * Indicates that a file format supports layers,
	 * but each layer can only contain either a bitmap or a vector.
	 */
	PHOTOSHOP,
	/**
	 * Indicates that a file format supports layers,
	 * and each layer can contain both bitmaps and vectors.
	 */
	POWERPAINT;
}
