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

package com.kreative.paint.ui.about;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import com.kreative.paint.ui.UIUtilities;

public class CreditsRoller extends JPanel {
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private StarField sf;
	private JLabel cr;
	private long crTime;
	private int crDelay;
	private RollerThread crThr;
	
	public CreditsRoller() {
		width = 300;
		height = 180;
		sf = new StarField();
		cr = new JLabel(UIUtilities.messages.getString("about.credits"));
		cr.setOpaque(false);
		cr.setForeground(Color.white);
		crTime = System.currentTimeMillis();
		crDelay = 6000;
		crThr = null;
		add(cr);
		add(sf);
		setOpaque(true);
		setBackground(Color.black);
	}
	
	public void start() {
		if (crThr == null) {
			crThr = new RollerThread();
			crThr.start();
		}
	}
	
	public void stop() {
		if (crThr != null) {
			crThr.interrupt();
			crThr = null;
		}
	}

	public Dimension getMinimumSize() {
		Insets i = getInsets();
		return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
	}
	
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
	}
	
	public Dimension getMaximumSize() {
		Insets i = getInsets();
		return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
	}
	
	private class RollerThread extends Thread {
		public void run() {
			crTime = System.currentTimeMillis();
			while (true) {
				try {
					int w = getWidth();
					int h = getHeight();
					Insets i = getInsets();
					sf.setSize(sf.getPreferredSize());
					sf.setLocation(i.left+(w-i.left-i.right-sf.getWidth())/2, i.top+(h-i.top-i.bottom-sf.getHeight())/2);
					int ccrTime = (int)((System.currentTimeMillis()-crTime-crDelay)/38);
					if (ccrTime < 0) ccrTime = 0;
					else if (ccrTime > cr.getHeight()) {
						ccrTime = 0;
						crTime = System.currentTimeMillis();
					}
					cr.setSize(cr.getPreferredSize());
					cr.setLocation(i.left+(w-i.left-i.right-cr.getWidth())/2, i.top+(h-i.top-i.bottom-sf.getHeight())/2-ccrTime);
					repaint();
					sleep(5);
				} catch (InterruptedException ie) {
					break;
				}
			}
		}
	}
	
	private static class StarField extends JComponent {
		private static final long serialVersionUID = 1L;
		private int width;
		private int height;
		private int starCount;
		private int starLoop;
		private int[] starLoc;
		private int starBlinkProbability;
		private int starBlinkTiming;
		private int shootingStarCount;
		private int shootingStarLoop;
		private int[] shootingStarLoc;
		public StarField() {
			width = 300;
			height = 180;
			Random r = new Random();
			starCount = 200;
			starLoop = 4000;
			starLoc = new int[starCount*3];
			starBlinkProbability = 20;
			starBlinkTiming = 100;
			for (int i = 0; i < starCount*3; i += 3) {
				starLoc[i] = r.nextInt(width);
				starLoc[i+1] = r.nextInt(height);
				starLoc[i+2] = (r.nextInt(starBlinkProbability) == 1) ? r.nextInt(starLoop/100)*100 : Integer.MIN_VALUE;
			}
			shootingStarCount = 4;
			shootingStarLoop = 10000;
			shootingStarLoc = new int[shootingStarCount*3];
			for (int i = 0; i < shootingStarCount*3; i += 3) {
				shootingStarLoc[i] = r.nextInt(width)-height/2;
				shootingStarLoc[i+1] = -1;
				shootingStarLoc[i+2] = r.nextInt((shootingStarLoop-width)/50)*50;
			}
		}
		public Dimension getMinimumSize() {
			Insets i = getInsets();
			return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
		}
		public Dimension getPreferredSize() {
			Insets i = getInsets();
			return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
		}
		public Dimension getMaximumSize() {
			Insets i = getInsets();
			return new Dimension(width+i.left+i.right, height+i.top+i.bottom);
		}
		protected void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			Insets i = getInsets();
			Shape clip = g.getClip();
			g.clipRect(i.left, i.top, w-i.left-i.right, h-i.top-i.bottom);
			g.setColor(Color.black);
			g.fillRect(i.left, i.top, w-i.left-i.right, h-i.top-i.bottom);
			g.setColor(Color.white);
			int starIt = (int)(System.currentTimeMillis() % starLoop);
			int shootingStarIt = (int)(System.currentTimeMillis() % shootingStarLoop);
			for (int j = 0; j < starCount*3; j += 3) {
				if (Math.abs(starIt-starLoc[j+2]) > starBlinkTiming) {
					g.fillRect(i.left+starLoc[j], i.top+starLoc[j+1], 1, 1);
				}
			}
			for (int j = 0; j < shootingStarCount*3; j += 3) {
				int d = (shootingStarIt-shootingStarLoc[j+2])/10;
				g.fillRect(i.left+shootingStarLoc[j]+d, i.top+shootingStarLoc[j+1]+d, 1, 1);
			}
			g.setClip(clip);
		}
	}
}
