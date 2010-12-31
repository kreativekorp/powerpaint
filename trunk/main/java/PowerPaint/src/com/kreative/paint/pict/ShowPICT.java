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

package com.kreative.paint.pict;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;

public class ShowPICT {
	public static void main(String[] args) throws IOException {
		System.setProperty("apple.awt.graphics.UseQuartz", "false");
		for (String arg : args) {
			File f = new File(arg);
			PICTInputStream in = new PICTInputStream(new FileInputStream(f));
			in.skip(512);
			in.readUnsignedShort();
			Rect bounds = in.readRect();
			BufferedImage bi = new BufferedImage(bounds.right-bounds.left, bounds.bottom-bounds.top, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bg = bi.createGraphics();
			bg.translate(-bounds.left, -bounds.top);
			PICTGraphics pg = new PICTGraphics(bg);
			while (true) {
				PICTInstruction inst = in.readInstruction();
				pg.executeInstruction(inst);
				if (inst instanceof PICTInstruction.OpEndPic) break;
			}
			bg.dispose();
			JLabel l = new JLabel(new ImageIcon(bi));
			JScrollPane sp = new JScrollPane(l, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			sp.getViewport().setBackground(Color.lightGray);
			JFrame fr = new JFrame(f.getName());
			fr.setContentPane(sp);
			fr.pack();
			fr.setLocationRelativeTo(null);
			fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			fr.setVisible(true);
			in.close();
		}
	}
}
