/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.tool.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class AlphabetsUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private static final Font smallfont = new Font("Helvetica", Font.BOLD, 18);
	private static final Font font = new Font("Helvetica", Font.BOLD, 36);
	private static final Font smallpuafont = new Font("Constructium", Font.BOLD, 18);
	private static final Font puafont = new Font("Constructium", Font.BOLD, 36);
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JComboBox apop;
	private JPanel apanel;
	private CardLayout alyt;
	private Set<LetterPanel> apanels;
	private JTextField afld;
	
	public AlphabetsUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		
		apop = new JComboBox(tc.getAlphabets().toFormerArray(new String[0]));
		apop.setEditable(false);
		apop.setMaximumRowCount(48);
		if (mini) SwingUtils.shrink(apop);
		apop.setSelectedIndex(tc.getAlphabetIndex());
		apop.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (u.lock()) {
					AlphabetsUI.this.tc.setAlphabetIndex(apop.getSelectedIndex());
					alyt.show(apanel, Integer.toString(AlphabetsUI.this.tc.getAlphabetIndex()));
					afld.setText(Character.toString(AlphabetsUI.this.tc.getLetter()));
					u.unlock();
				}
			}
		});
		
		apanel = new JPanel(alyt = new CardLayout());
		apanels = new HashSet<LetterPanel>();
		for (int n = 0; n < tc.getAlphabets().size(); n++) {
			LetterPanel a = new LetterPanel(tc.getAlphabets().getLatter(n), mini);
			JScrollPane s = new JScrollPane(a, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			apanel.add(s, Integer.toString(n));
			apanels.add(a);
		}
		
		afld = new JTextField(Character.toString(tc.getLetter()), 2);
		afld.setEditable(false);
		if (mini) SwingUtils.shrink(afld);
		afld.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) { e.consume(); }
			public void keyReleased(KeyEvent e) { e.consume(); }
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() > 0x20 && e.getKeyChar() < 0x7F) || (e.getKeyChar() >= 0xA0 && e.getKeyChar() < 0xFFFD)) {
					if (u.lock()) {
						afld.setText(Character.toString(e.getKeyChar()));
						AlphabetsUI.this.tc.setLetter(e.getKeyChar());
						for (LetterPanel a : apanels) a.updateSelection();
						u.unlock();
					}
				}
				e.consume();
			}
		});
		JPanel afp = new JPanel(new FlowLayout());
		afp.add(afld);
		
		setLayout(new BorderLayout());
		add(apop, BorderLayout.PAGE_START);
		add(apanel, BorderLayout.CENTER);
		add(afp, BorderLayout.PAGE_END);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_ALPHABET_SET) != 0L) {
			if (u.lock()) {
				apop.setSelectedIndex(tc.getAlphabetIndex());
				alyt.show(apanel, Integer.toString(tc.getAlphabetIndex()));
				afld.setText(Character.toString(tc.getLetter()));
				u.unlock();
			}
			for (LetterPanel a : apanels) a.updateSelection();
		} else if ((delta & ToolContextConstants.CHANGED_ALPHABET_LETTER) != 0L) {
			if (u.lock()) {
				afld.setText(Character.toString(tc.getLetter()));
				u.unlock();
			}
			for (LetterPanel a : apanels) a.updateSelection();
		}
	}
	
	private class LetterPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1L;
		private Set<LetterLabel> labels;
		public LetterPanel(char[] ch, boolean mini) {
			super(new GridLayout(0,14,-1,-1));
			labels = new HashSet<LetterLabel>();
			for (char c : ch) {
				LetterLabel l = new LetterLabel(c, mini);
				add(l);
				labels.add(l);
			}
		}
		public void updateSelection() {
			for (LetterLabel l : labels) l.updateSelection();
		}
		public Dimension getPreferredScrollableViewportSize() {
			if (!labels.isEmpty()) {
				int h = (labels.iterator().next().getHeight()-1)*6 + 1;
				Dimension p = getPreferredSize();
				if (p.height < h) return p;
				else return new Dimension(p.width, h);
			} else {
				return getPreferredSize();
			}
		}
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			if (!labels.isEmpty()) {
				return (labels.iterator().next().getHeight()-1)*6;
			} else {
				return visibleRect.height;
			}
		}
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			if (!labels.isEmpty()) {
				return labels.iterator().next().getHeight()-1;
			} else {
				return 1;
			}
		}
	}
	
	private class LetterLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private char ch;
		public LetterLabel(char ch, boolean mini) {
			super(Character.toString(ch));
			setOpaque(true);
			setBackground(Color.white);
			setForeground(Color.black);
			setFont(
					(Character.getType(ch) == Character.PRIVATE_USE)
					? (mini ? smallpuafont : puafont)
					: (mini ? smallfont : font)
			);
			setHorizontalAlignment(JLabel.CENTER);
			this.ch = ch;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.setLetter(LetterLabel.this.ch);
				}
			});
		}
		public void updateSelection() {
			if (tc.getLetter() == ch) {
				Border inner = BorderFactory.createEmptyBorder(4, 2, 0, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 3);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				Border inner = BorderFactory.createEmptyBorder(6, 4, 2, 4);
				Border outer = BorderFactory.createLineBorder(Color.black, 1);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			}
		}
	}
}
