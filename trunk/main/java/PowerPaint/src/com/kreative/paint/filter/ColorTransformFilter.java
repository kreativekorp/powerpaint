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

package com.kreative.paint.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import com.kreative.paint.form.CustomOption;
import com.kreative.paint.form.Form;

public class ColorTransformFilter extends RGBFilter {
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(Image src) {
		Form f = new Form();
		f.add(new CustomOption<JPanel>() {
			public String getName() { return null; }
			public JPanel makeUI(boolean mini) {
				JTextField aa, ar, ag, ab, ac;
				JTextField ra, rr, rg, rb, rc;
				JTextField ga, gr, gg, gb, gc;
				JTextField ba, br, bg, bb, bc;
				JPanel form = new JPanel(new GridLayout(5,5,0,5));
				form.add(prefix("A\u2032=", aa = new JTextField("1")));
				form.add(prefix("A +", ar = new JTextField("0")));
				form.add(prefix("R +", ag = new JTextField("0")));
				form.add(prefix("G +", ab = new JTextField("0")));
				form.add(prefix("B +", ac = new JTextField("0")));
				form.add(prefix("R\u2032=", ra = new JTextField("0")));
				form.add(prefix("A +", rr = new JTextField("1")));
				form.add(prefix("R +", rg = new JTextField("0")));
				form.add(prefix("G +", rb = new JTextField("0")));
				form.add(prefix("B +", rc = new JTextField("0")));
				form.add(prefix("G\u2032=", ga = new JTextField("0")));
				form.add(prefix("A +", gr = new JTextField("0")));
				form.add(prefix("R +", gg = new JTextField("1")));
				form.add(prefix("G +", gb = new JTextField("0")));
				form.add(prefix("B +", gc = new JTextField("0")));
				form.add(prefix("B\u2032=", ba = new JTextField("0")));
				form.add(prefix("A +", br = new JTextField("0")));
				form.add(prefix("R +", bg = new JTextField("0")));
				form.add(prefix("G +", bb = new JTextField("1")));
				form.add(prefix("B +", bc = new JTextField("0")));
				form.add(prefix("1 =", makeRO(new JTextField("0"))));
				form.add(prefix("A +", makeRO(new JTextField("0"))));
				form.add(prefix("R +", makeRO(new JTextField("0"))));
				form.add(prefix("G +", makeRO(new JTextField("0"))));
				form.add(prefix("B +", makeRO(new JTextField("1"))));
				aa.getDocument().addDocumentListener(new TxParamListener(0, 0));
				ar.getDocument().addDocumentListener(new TxParamListener(0, 1));
				ag.getDocument().addDocumentListener(new TxParamListener(0, 2));
				ab.getDocument().addDocumentListener(new TxParamListener(0, 3));
				ac.getDocument().addDocumentListener(new TxParamListener(0, 4));
				ra.getDocument().addDocumentListener(new TxParamListener(1, 0));
				rr.getDocument().addDocumentListener(new TxParamListener(1, 1));
				rg.getDocument().addDocumentListener(new TxParamListener(1, 2));
				rb.getDocument().addDocumentListener(new TxParamListener(1, 3));
				rc.getDocument().addDocumentListener(new TxParamListener(1, 4));
				ga.getDocument().addDocumentListener(new TxParamListener(2, 0));
				gr.getDocument().addDocumentListener(new TxParamListener(2, 1));
				gg.getDocument().addDocumentListener(new TxParamListener(2, 2));
				gb.getDocument().addDocumentListener(new TxParamListener(2, 3));
				gc.getDocument().addDocumentListener(new TxParamListener(2, 4));
				ba.getDocument().addDocumentListener(new TxParamListener(3, 0));
				br.getDocument().addDocumentListener(new TxParamListener(3, 1));
				bg.getDocument().addDocumentListener(new TxParamListener(3, 2));
				bb.getDocument().addDocumentListener(new TxParamListener(3, 3));
				bc.getDocument().addDocumentListener(new TxParamListener(3, 4));
				return form;
			}
			public void update(JPanel ui) {
				// not needed
			}
		});
		return f;
	}
	
	private double aa = 1, ar = 0, ag = 0, ab = 0, ac = 0;
	private double ra = 0, rr = 1, rg = 0, rb = 0, rc = 0;
	private double ga = 0, gr = 0, gg = 1, gb = 0, gc = 0;
	private double ba = 0, br = 0, bg = 0, bb = 1, bc = 0;
	
	protected int filterRGB(int x, int y, int c) {
		double a = ((c >>> 24) & 0xFF) / 255.0;
		double r = ((c >>> 16) & 0xFF) / 255.0;
		double g = ((c >>>  8) & 0xFF) / 255.0;
		double b = ((c >>>  0) & 0xFF) / 255.0;
		int a2 = (int)Math.round((aa*a + ar*r + ag*g + ab*b + ac) * 255.0);
		int r2 = (int)Math.round((ra*a + rr*r + rg*g + rb*b + rc) * 255.0);
		int g2 = (int)Math.round((ga*a + gr*r + gg*g + gb*b + gc) * 255.0);
		int b2 = (int)Math.round((ba*a + br*r + bg*g + bb*b + bc) * 255.0);
		int a3 = (a2 < 0) ? 0 : (a2 > 255) ? 255 : a2;
		int r3 = (r2 < 0) ? 0 : (r2 > 255) ? 255 : r2;
		int g3 = (g2 < 0) ? 0 : (g2 > 255) ? 255 : g2;
		int b3 = (b2 < 0) ? 0 : (b2 > 255) ? 255 : b2;
		return (a3 << 24) | (r3 << 16) | (g3 << 8) | b3;
	}

	private Component prefix(String pref, JTextField c) {
		JPanel p = new JPanel(new BorderLayout());
		JLabel l = new JLabel(pref);
		l.setMinimumSize(new Dimension(30, l.getMinimumSize().width));
		l.setPreferredSize(new Dimension(30, l.getPreferredSize().width));
		l.setMaximumSize(new Dimension(30, l.getMaximumSize().width));
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.setHorizontalAlignment(JLabel.CENTER);
		l.setHorizontalTextPosition(JLabel.CENTER);
		c.setMinimumSize(new Dimension(60, c.getMinimumSize().width));
		c.setPreferredSize(new Dimension(60, c.getPreferredSize().width));
		c.setMaximumSize(new Dimension(60, c.getMaximumSize().width));
		c.setAlignmentX(JTextField.RIGHT_ALIGNMENT);
		c.setHorizontalAlignment(JTextField.RIGHT);
		p.add(l, BorderLayout.WEST);
		p.add(c, BorderLayout.CENTER);
		return p;
	}
	
	private JTextField makeRO(JTextField t) {
		t.setEditable(false);
		t.setOpaque(false);
		t.setFocusable(false);
		t.setRequestFocusEnabled(false);
		return t;
	}
	
	private class TxParamListener implements DocumentListener {
		private int channel;
		private int field;
		public TxParamListener(int channel, int field) {
			this.channel = channel;
			this.field = field;
		}
		public void changedUpdate(DocumentEvent e) { doIt(e); }
		public void insertUpdate(DocumentEvent e) { doIt(e); }
		public void removeUpdate(DocumentEvent e) { doIt(e); }
		private void doIt(DocumentEvent e) {
			try {
				Document d = e.getDocument();
				int l = d.getLength();
				String s = d.getText(0, l);
				double v = Double.parseDouble(s);
				switch (channel) {
				case 0:
					switch (field) {
					case 0: aa = v; break;
					case 1: ar = v; break;
					case 2: ag = v; break;
					case 3: ab = v; break;
					case 4: ac = v; break;
					}
					break;
				case 1:
					switch (field) {
					case 0: ra = v; break;
					case 1: rr = v; break;
					case 2: rg = v; break;
					case 3: rb = v; break;
					case 4: rc = v; break;
					}
					break;
				case 2:
					switch (field) {
					case 0: ga = v; break;
					case 1: gr = v; break;
					case 2: gg = v; break;
					case 3: gb = v; break;
					case 4: gc = v; break;
					}
					break;
				case 3:
					switch (field) {
					case 0: ba = v; break;
					case 1: br = v; break;
					case 2: bg = v; break;
					case 3: bb = v; break;
					case 4: bc = v; break;
					}
					break;
				}
			} catch (Exception ex) {}
		}
	}
}
