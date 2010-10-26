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

package com.kreative.paint.palette;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ResourceBundle;

public class PaletteUtilities {
	static final ResourceBundle messages = ResourceBundle.getBundle("com.kreative.paint.messages.PaletteMessages");
	
	private static final Class<?> cl = AbstractPalette.class;
	private static final Toolkit tk = Toolkit.getDefaultToolkit();
	static final Image ARROW_4U = tk.createImage(cl.getResource("Arrow4U.png"));
	static final Image ARROW_4UH = tk.createImage(cl.getResource("Arrow4UH.png"));
	static final Image ARROW_4D = tk.createImage(cl.getResource("Arrow4D.png"));
	static final Image ARROW_4DH = tk.createImage(cl.getResource("Arrow4DH.png"));
	static final Image ARROW_4L = tk.createImage(cl.getResource("Arrow4L.png"));
	static final Image ARROW_4LH = tk.createImage(cl.getResource("Arrow4LH.png"));
	static final Image ARROW_4R = tk.createImage(cl.getResource("Arrow4R.png"));
	static final Image ARROW_4RH = tk.createImage(cl.getResource("Arrow4RH.png"));
	static final Image ARROW_5U = tk.createImage(cl.getResource("Arrow5U.png"));
	static final Image ARROW_5UH = tk.createImage(cl.getResource("Arrow5UH.png"));
	static final Image ARROW_5D = tk.createImage(cl.getResource("Arrow5D.png"));
	static final Image ARROW_5DH = tk.createImage(cl.getResource("Arrow5DH.png"));
	static final Image ARROW_5L = tk.createImage(cl.getResource("Arrow5L.png"));
	static final Image ARROW_5LH = tk.createImage(cl.getResource("Arrow5LH.png"));
	static final Image ARROW_5R = tk.createImage(cl.getResource("Arrow5R.png"));
	static final Image ARROW_5RH = tk.createImage(cl.getResource("Arrow5RH.png"));
	static {
		tk.prepareImage(ARROW_4U, -1, -1, null);
		tk.prepareImage(ARROW_4UH, -1, -1, null);
		tk.prepareImage(ARROW_4D, -1, -1, null);
		tk.prepareImage(ARROW_4DH, -1, -1, null);
		tk.prepareImage(ARROW_4L, -1, -1, null);
		tk.prepareImage(ARROW_4LH, -1, -1, null);
		tk.prepareImage(ARROW_4R, -1, -1, null);
		tk.prepareImage(ARROW_4RH, -1, -1, null);
		tk.prepareImage(ARROW_5U, -1, -1, null);
		tk.prepareImage(ARROW_5UH, -1, -1, null);
		tk.prepareImage(ARROW_5D, -1, -1, null);
		tk.prepareImage(ARROW_5DH, -1, -1, null);
		tk.prepareImage(ARROW_5L, -1, -1, null);
		tk.prepareImage(ARROW_5LH, -1, -1, null);
		tk.prepareImage(ARROW_5R, -1, -1, null);
		tk.prepareImage(ARROW_5RH, -1, -1, null);
	}
	
	private PaletteUtilities() {}
}
