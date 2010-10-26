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

package com.kreative.paint.ui.progress;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DocumentIconProgressIndicator extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static Image DocIcon = null;
	private static Image DocIconInverse = null;
	private static Image DocIconFrame = null;
	static {
		Class<?> cl = DocumentIconProgressIndicator.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		DocIcon = tk.createImage(cl.getResource("DocumentIcon.png"));
		DocIconInverse = tk.createImage(cl.getResource("DocumentIconInverse.png"));
		DocIconFrame = tk.createImage(cl.getResource("DocumentIconFrame.png"));
		tk.prepareImage(DocIcon, -1, -1, null);
		tk.prepareImage(DocIconInverse, -1, -1, null);
		tk.prepareImage(DocIconFrame, -1, -1, null);
	}
	
	private int min = 0;
	private int max = 0;
	private int val = 0;
	private BufferedImage docicon = null;
	private BufferedImage dociconinverse = null;
	
	public DocumentIconProgressIndicator(String ext, int min, int max, int val) {
		this.min = min;
		this.max = max;
		this.val = val;
		docicon = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g1 = docicon.createGraphics();
		while (!g1.drawImage(DocIcon, 0, 0, null));
		g1.setFont(new Font("SansSerif", Font.PLAIN, 8));
		g1.setPaint(Color.black);
		int sw1 = g1.getFontMetrics().stringWidth(ext.toUpperCase());
		g1.drawString(ext.toUpperCase(), 26-sw1/2, 40);
		while (!g1.drawImage(DocIconFrame, 0, 0, null));
		g1.dispose();
		dociconinverse = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dociconinverse.createGraphics();
		while (!g2.drawImage(DocIconInverse, 0, 0, null));
		g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
		g2.setPaint(Color.white);
		int sw2 = g2.getFontMetrics().stringWidth(ext.toUpperCase());
		g2.drawString(ext.toUpperCase(), 26-sw2/2, 40);
		while (!g2.drawImage(DocIconFrame, 0, 0, null));
		g2.dispose();
	}
	
	public int getMinimum() { return min; }
	public int getMaximum() { return max; }
	public int getValue() { return val; }
	
	public void setMinimum(int min) { this.min = min; repaint(); }
	public void setMaximum(int max) { this.max = max; repaint(); }
	public void setValue(int val) { this.val = val; repaint(); }
	
	public Dimension getMinimumSize() {
		Insets i = getInsets();
		return new Dimension(i.left+i.right+48, i.top+i.bottom+48);
	}
	
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		return new Dimension(i.left+i.right+48, i.top+i.bottom+48);
	}
	
	public Dimension getMaximumSize() {
		Insets i = getInsets();
		return new Dimension(i.left+i.right+48, i.top+i.bottom+48);
	}
	
	protected void paintComponent(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		Insets i = getInsets();
		int x = i.left + (w-i.left-i.right-48)/2;
		int y = i.top + (h-i.top-i.bottom-48)/2;
		g.clearRect(i.left, i.top, w-i.left-i.right, h-i.top-i.bottom);
		if (val <= min) {
			while (!g.drawImage(docicon, x, y, null));
		} else if (val >= max) {
			while (!g.drawImage(dociconinverse, x, y, null));
		} else {
			int k = 1 + 46*(val-min)/(max-min);
			while (!g.drawImage(docicon, x, y, x+48, y+48-k, 0, 0, 48, 48-k, null));
			while (!g.drawImage(dociconinverse, x, y+48-k, x+48, y+48, 0, 48-k, 48, 48, null));
		}
	}
}
