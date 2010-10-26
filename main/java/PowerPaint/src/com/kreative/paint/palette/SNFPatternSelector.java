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

package com.kreative.paint.palette;

import java.awt.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.awt.PatternPaint;

public class SNFPatternSelector extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	public SNFPatternSelector(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		setToolTipText(PaletteUtilities.messages.getString("snf.pattern"));
	}
	
	public void update() {
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		long pat = pc.getEditedPattern();
		g.setColor(Color.black);
		g.fillRect(x+1, y+1, w-1, h-1);
		g.fillRect(x, y, w-1, h-1);
		g.setColor(Color.white);
		g.fillRect(x+1, y+1, w-3, h-3);
		((Graphics2D)g).setPaint(new PatternPaint(Color.black, Color.white, pat));
		g.fillRect(x+2, y+2, w-5, h-5);
	}
}
